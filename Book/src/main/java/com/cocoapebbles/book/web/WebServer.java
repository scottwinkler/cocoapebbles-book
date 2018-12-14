package com.cocoapebbles.book.web;

import com.cocoapebbles.book.web.jersey.BookDAO;
import com.cocoapebbles.book.web.jersey.BooksResource;
import org.bukkit.plugin.java.JavaPlugin;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.*;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import java.io.File;
import java.util.logging.Logger;

/**
 * Simple Jetty FileServer.
 * This is a simple example of Jetty configured as a FileServer.
 */
public class WebServer {
    private Logger logger;
    private Integer port;
    private JavaPlugin p;
    private Server server;
    public WebServer(JavaPlugin p) {
        logger = p.getLogger();
        this.p = p;
        port = p.getConfig().getInt("server.port");
        if (p.getConfig().getBoolean("server.enable")) {
            initializeFileServer();
        } else {
            logger.info("Server disabled, skipping");
        }

    }
    public void shutDownServer(){
        try {
            if(server!=null){
                logger.info("Shutting down server running on port: "+port);
                server.stop();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void initializeFileServer() {
        logger.info("Initializing server");
        try {
            File dataDir = p.getDataFolder();
            server = new Server(port);
            server.setStopAtShutdown(true);

            //file servlet
            ResourceHandler resource_handler = new ResourceHandler();
            resource_handler.setDirectoriesListed(true);
            resource_handler.setWelcomeFiles(new String[]{"index.html"});
            resource_handler.setResourceBase(dataDir.getAbsolutePath()+"/web");

            ContextHandler filesContext = new ContextHandler();
            filesContext.setContextPath("/");
            filesContext.setHandler(resource_handler);

            //jersey servlet
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
            ServletContainer container = new ServletContainer(resourceConfig);
            ServletHolder holder = new ServletHolder(container);
            ServletContextHandler jerseyContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
            jerseyContext.setContextPath("/books");
            jerseyContext.addServlet(holder,"/*");

            ContextHandlerCollection contexts = new ContextHandlerCollection();
            contexts.setHandlers(new Handler[]{filesContext,jerseyContext});
            server.setHandler(contexts);
            server.start();
            logger.info("Server running on port: " + port + "!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}