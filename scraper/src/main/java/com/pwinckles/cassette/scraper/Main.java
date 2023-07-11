package com.pwinckles.cassette.scraper;

import java.io.IOException;
import java.nio.file.Paths;

public final class Main {

    private static final String SCRAPE = "scrape";
    private static final String ASSEMBLE = "assemble";

    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length < 1 || args.length > 2) {
            die("Usage: scrape|assemble [PATH]");
        }

        var cmd = args[0];
        var destination = args.length == 2 ? Paths.get(args[1]) : Paths.get("data");

        switch (cmd) {
            case SCRAPE -> new WikiScraper().scrape(destination);
            case ASSEMBLE -> new Assembler().assemble(destination);
            default -> die("Unknown command: " + cmd);
        }
    }

    private static void die(String message) {
        System.err.println(message);
        System.exit(1);
    }
}
