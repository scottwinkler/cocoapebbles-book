package com.cocoapebbles.book.web;

import org.bukkit.plugin.java.JavaPlugin;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.*;

import java.io.File;
import java.util.logging.Logger;

/**
 * Simple Jetty FileServer.
 * This is a simple example of Jetty configured as a FileServer.
 */
public class FileServer {
    private Logger logger;
    private Integer port;
    private JavaPlugin p;
    private Server server;
    public FileServer(JavaPlugin p) {
        logger = p.getLogger();
        this.p = p;
        port = p.getConfig().getInt("webapp.port");
        if (p.getConfig().getBoolean("webapp.enable")) {
            initializeFileServer();
        } else {
            logger.info("File server disabled, skipping");
        }

    }
    public void shutDownServer(){
        try {
            if(server!=null){
                logger.info("Shutting down file server running on port: "+port);
                server.stop();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void initializeFileServer() {
        logger.info("Initializing file server");
        try {
            File dataDir = p.getDataFolder();
            String ip = p.getServer().getIp();
            if ((ip == null) || (ip.trim().length() == 0)) {
                ip = "http://0.0.0.0";
            }
            if(!(ip.startsWith("http://")||ip.startsWith("https://"))){
                ip="http://"+ip;
            }
            server = new Server(port);
            server.setStopAtShutdown(true);
            ResourceHandler resource_handler = new ResourceHandler();
            resource_handler.setDirectoriesListed(true);
            resource_handler.setWelcomeFiles(new String[]{"index.html"});
            resource_handler.setResourceBase(dataDir.getAbsolutePath()+"/web");

            ContextHandler context = new ContextHandler();
            context.setContextPath("/");
            context.setHandler(resource_handler);

            ContextHandlerCollection contexts = new ContextHandlerCollection();
            contexts.setHandlers(new Handler[]{context});
            server.setHandler(contexts);

            server.start();
            logger.info("File server running on port: " + port + "!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}