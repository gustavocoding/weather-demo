package org.infinispan.demo.weather.service.rest;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.infinispan.demo.weather.dataloader.CachePopulator;
import org.infinispan.demo.weather.module.WeatherModule;
import org.infinispan.demo.weather.service.query.LuceneCountrySuggester;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jboss.resteasy.plugins.guice.GuiceResteasyBootstrapServletContextListener;
import org.jboss.resteasy.plugins.guice.ext.RequestScopeModule;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

import java.io.File;
import java.io.IOException;

public class MainServer {

    public static void main(String[] args) throws Exception {
        Injector injector = Guice.createInjector(new ServerModule(), new WeatherModule());
        initialize(injector);
        Server server = new Server(8080);
        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setDirectoriesListed(true);
        resource_handler.setWelcomeFiles(new String[]{"index.html"});
        resource_handler.setResourceBase(new File(MainServer.class.getClassLoader().getResource("ui/index.html").getFile()).getParentFile().getAbsolutePath());

        ServletContextHandler servletHandler = new ServletContextHandler();
        servletHandler.addEventListener(injector.getInstance(GuiceResteasyBootstrapServletContextListener.class));
        ServletHolder sh = new ServletHolder(HttpServletDispatcher.class);
        servletHandler.addServlet(sh, "/*");

        resource_handler.setHandler(servletHandler);

        server.setHandler(resource_handler);
        server.start();
        server.join();
    }

    private static void initialize(Injector injector) throws IOException {
        CachePopulator cachePopulator = injector.getInstance(CachePopulator.class);
        LuceneCountrySuggester luceneCountrySuggester = injector.getInstance(LuceneCountrySuggester.class);
        cachePopulator.indexData();
        luceneCountrySuggester.buildDictionary();
    }

    private static class ServerModule extends RequestScopeModule {

        @Override
        protected void configure() {
            super.configure();
            bind(WeatherResource.class);
            bind(GsonMessageBodyHandler.class);
        }
    }
}
