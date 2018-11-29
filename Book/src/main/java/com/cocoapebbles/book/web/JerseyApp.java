package com.cocoapebbles.book.web;

import com.cocoapebbles.book.web.jersey.BookDAO;
import com.cocoapebbles.book.web.jersey.BooksResource;
import io.netty.channel.Channel;
import org.bukkit.plugin.java.JavaPlugin;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.netty.httpserver.NettyHttpContainerProvider;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;
import java.util.logging.Logger;

public class JerseyApp {
    private Integer port;
    private URI baseURI;
    private JavaPlugin p;
    private Logger logger;
    private Channel server;
    public JerseyApp(JavaPlugin p){
        this.p = p;
        logger = p.getLogger();
        String ip = p.getServer().getIp();
        if ((ip == null) || (ip.trim().length() == 0)) {
            ip = "http://0.0.0.0";
        }
        if(!(ip.startsWith("http://")||ip.startsWith("https://"))){
            ip="http://"+ip;
        }
        port = p.getConfig().getInt("api.port");
        baseURI = URI.create(ip+":"+port+"/");
        if(p.getConfig().getBoolean("api.enable")){
            initializeJersey();
        } else{
            logger.info("Jersey server disabled, skipping");
        }
    }

    public void shutDownServer(){
        try {
            if(server!=null) {
                logger.info("Shutting down jersey application running on port: "+port);
                server.close();
            }
        }catch(Exception e){
            logger.severe(e.getMessage());
        }
    }

    public void initializeJersey(){
        logger.info("Initializing jersey application");
        ResourceConfig resourceConfig = new ResourceConfig(BooksResource.class);
        BookDAO bookDAO = new BookDAO(p);
        Logger logger = p.getLogger();
        resourceConfig.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(logger).to(Logger.class);
                bind(bookDAO).to(BookDAO.class);
            }
        });
        server = NettyHttpContainerProvider.createHttp2Server(baseURI, resourceConfig, null);

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                server.close();
            }
        }));
        logger.info("Application started on port: "+ port+"!");
    }


}