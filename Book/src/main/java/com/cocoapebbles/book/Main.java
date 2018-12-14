package com.cocoapebbles.book;

import com.cocoapebbles.book.commands.CommandHandler;
import com.cocoapebbles.book.web.WebServer;
import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.*;
import java.util.Enumeration;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Main extends JavaPlugin implements Listener{
    private Logger logger;
    private WebServer webServer;

    public Main(){
        logger = this.getLogger();
    }
    @Override
    public void onEnable(){
        registerCommands();
        registerEvents();
        initializeWebApp();
    }
    @Override
    public void onDisable(){
        webServer.shutDownServer();
    }

    public void registerCommands(){
        getCommand("book").setExecutor(new CommandHandler(this));
    }

    public void registerEvents()
    {
        PluginManager pm = getServer().getPluginManager();
    }

    public void initializeWebApp(){
        loadConfig();
        getStaticFiles();
        webServer = new WebServer(this);
    }

    private void getStaticFiles() {
        logger.info("checking version");
        File jarfile = this.getFile();
        if(jarfile == null) return;
        File df = this.getDataFolder();
        if(df.exists() == false) df.mkdirs();

        /* Exit if already have files */
        File versionFile = new File(df, "version.txt");
        String pluginVersion = this.getDescription().getVersion();
        if(versionFile.exists()) {
            try {
                String installedVersion = FileUtils.readFileToString(versionFile, "UTF-8");
                logger.info("Current version: "+ installedVersion+", plugin version: "+ pluginVersion);
                if (installedVersion.equals(pluginVersion)) {
                    logger.info("Skipping static files unpacking");
                    return;
                }
            }catch(IOException e){
                logger.severe(e.getMessage());
            }
        }
        try{
            logger.info("Clearing old files and bumping version");
            //clear directory of old static files and bump version
            File webDir = new File(df+"/web");
            FileUtils.cleanDirectory(webDir);
            FileUtils.forceDelete(versionFile);
            FileUtils.write(versionFile,pluginVersion,"UTF-8");
        }catch(IOException e){
            logger.severe(e.getMessage());
        }

        /* Open JAR as ZIP */
        ZipFile zf = null;
        FileOutputStream fos = null;
        InputStream ins = null;
        byte[] buf = new byte[2048];
        String n = null;
        logger.info("unpacking static files");
        try {
            File f;
            zf = new ZipFile(jarfile);
            Enumeration<? extends ZipEntry> e = zf.entries();
            while (e.hasMoreElements()) {
                ZipEntry ze = e.nextElement();
                n = ze.getName();
                if(!n.startsWith("web/")) continue;
                f = new File(df, n);
                if(ze.isDirectory()) {
                    f.mkdirs();
                }
                else {
                    f.getParentFile().mkdirs();
                    fos = new FileOutputStream(f);
                    ins = zf.getInputStream(ze);
                    int len;
                    while ((len = ins.read(buf)) >= 0) {
                        fos.write(buf,  0,  len);
                    }
                    ins.close();
                    ins = null;
                    fos.close();
                    fos = null;
                }
            }
        } catch (IOException iox) {
            logger.severe ("Error extracting file - " + n);
        } finally {
            if (ins != null) {
                try { ins.close(); } catch (IOException iox) {}
                ins = null;
            }
            if (fos != null) {
                try { fos.close(); } catch (IOException iox) {}
                fos = null;
            }
            if (zf != null) {
                try { zf.close(); } catch (IOException iox) {}
                zf = null;
            }
        }

        return;
    }

    private void loadConfig(){
        FileConfiguration config = this.getConfig();
        config.addDefault("server.port",8080);
        config.addDefault("server.enable",true);
        config.options().copyDefaults(true);
        this.saveConfig();
    }

}
