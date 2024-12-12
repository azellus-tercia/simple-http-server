package com.azellustercia;

import com.azellustercia.database.Database;
import com.azellustercia.httpserver.config.Configuration;
import com.azellustercia.httpserver.config.ConfigurationManager;
import com.azellustercia.httpserver.core.ServerListenerThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 *
 * Основной класс для старта сервера
 *
 */
public class HttpServer {

    private final static Logger LOGGER = LoggerFactory.getLogger(HttpServer.class);

    public static void main(String[] args) {
        startServer(new File("src/main/resources/http.json"));
    }

    private static void startServer(File file) {
        if (file == null) {
            LOGGER.error("No configuration file provided.");
            return;
        }

        LOGGER.info("Server starting...");

        ConfigurationManager.getInstance().loadConfigurationFile(file);
        Configuration conf = ConfigurationManager.getInstance().getCurrentConfiguration();

        LOGGER.info("Using Port: {}", conf.getPort());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            synchronized (Database.class) {
                LOGGER.info("Saving database...");
                try {
                    Database.getInstance().saveDatabase("src/main/resources/database.dat");
                } catch (IOException e) {
                    LOGGER.info("Something went wrong while saving database");
                    throw new RuntimeException(e);
                }
                LOGGER.info("Done!");
            }
        }));

        try {
            LOGGER.info("Loading database...");
            Database.getInstance().loadDatabase("src/main/resources/database.dat");
            LOGGER.info("Done!");
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.info("Something went wrong while loading database");
            throw new RuntimeException(e);
        }

        try {
            ServerListenerThread serverListenerThread = new ServerListenerThread(conf.getPort());
            serverListenerThread.start();
        } catch (IOException e) {
            LOGGER.error("I/O Exception happened",e);
        }
    }
}
