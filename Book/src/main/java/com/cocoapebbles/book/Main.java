package com.cocoapebbles.book;

import com.cocoapebbles.book.commands.CommandHandler;
import com.cocoapebbles.book.web.FileServer;
import com.cocoapebbles.book.web.JerseyApp;
import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.*;
import java.net.URL;
import java.util.Enumeration;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Main extends JavaPlugin implements Listener{
    private Logger logger;
    private FileServer fileServer;
    private JerseyApp restServer;

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
        fileServer.shutDownServer();
        restServer.shutDownServer();
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
        fileServer = new FileServer(this);
        restServer = new JerseyApp(this);
    }

    private void getStaticFiles() {
        File jarfile = this.getFile();
        if(jarfile == null) return;
        File df = this.getDataFolder();
        if(df.exists() == false) df.mkdirs();

        /* Exit if already have files */
        /*File config = new File(df, "web/index.html");
        if(config.exists()) {
           return;
        }*/

        /* Open JAR as ZIP */
        ZipFile zf = null;
        FileOutputStream fos = null;
        InputStream ins = null;
        byte[] buf = new byte[2048];
        String n = null;
        try {
            File f;
            zf = new ZipFile(jarfile);
            Enumeration<? extends ZipEntry> e = zf.entries();
            while (e.hasMoreElements()) {
                ZipEntry ze = e.nextElement();
                n = ze.getName();
                //logger.info(n);
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
                    //inject runtime environment variables to index.html
                    if(n.equals("web/index.html")){
                        String s = FileUtils.readFileToString(f,"UTF-8");
                        String ip = this.getServer().getIp();
                        if ((ip == null) || (ip.trim().length() == 0)) {
                            ip = "http://0.0.0.0";
                        }
                        if(!(ip.startsWith("http://")||ip.startsWith("https://"))){
                            logger.info("Padding ip with 'http://' to prevent internal error");
                            ip="http://"+ip;
                        }
                        //this nonsense is because my ip is showing up as localhost:8080/:9000 instead of localhost:9000
                        String API_URL = ip+":"+this.getConfig().getInt("api.port");

                        s=s.replace("__SERVER_DATA__","{\"API_URL\":\""+API_URL+"\"}");
                        FileUtils.forceDelete(f);
                        FileUtils.writeStringToFile(f,s,"UTF-8");
                    }
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
        config.addDefault("api.port",9000);
        config.addDefault("api.enable",true);
        config.addDefault("webapp.port",8080);
        config.addDefault("webapp.enable",true);
        config.options().copyDefaults(true);
        this.saveConfig();
    }

}
