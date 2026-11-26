package com.vizja.swp.lab2;

import com.vizja.swp.lab2.lib.FrontController;
import com.vizja.swp.lab2.lib.Server;

public class Application {
    public static void main(String[] args) {
        // Register routes
        FrontController.addRoute("/", new HomeController());
        FrontController.addRoute("/api/books", new BookController());

        try (final var server = new Server()) {
            server.start(8080);
        }
    }
}