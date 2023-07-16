package com.pwinckles.cassette.scraper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.pwinckles.cassette.common.model.MoveBuilder;
import com.pwinckles.cassette.common.model.SpeciesBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WikiScraper {

    private static final Logger log = LoggerFactory.getLogger(WikiScraper.class);

    private static final String BASE_URL = "https://wiki.cassettebeasts.com";
    private static final String SPECIES_PAGE = "/wiki/Species";
    private static final String MOVES_PAGE = "/wiki/Move";

    private final SpeciesScraper speciesScraper;
    private final MoveScraper moveScraper;

    private final ObjectMapper objectMapper;

    private final Duration wait;

    public WikiScraper() {
        this.speciesScraper = new SpeciesScraper();
        this.moveScraper = new MoveScraper();
        this.objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        this.wait = Duration.ofSeconds(1);
    }

    public void scrape(Path outputDir) throws IOException, InterruptedException {
        scrapeSpecies(Files.createDirectories(outputDir.resolve(Constants.SPECIES_DIR)));
        scrapeMoves(Files.createDirectories(outputDir.resolve(Constants.MOVES_DIR)));
    }

    private void scrapeSpecies(Path speciesDir) throws IOException, InterruptedException {
        log.info("Scraping species to {}", speciesDir);
        var doc = Jsoup.connect(BASE_URL + SPECIES_PAGE).get();

        var species = extractLinks(1, doc);

        log.info("Found {} species", species.size());

        for (var it = species.iterator(); it.hasNext(); ) {
            var s = it.next();
            var output = outputFile(s, speciesDir);
            if (Files.exists(output)) {
                log.info("Skipping {} because it has already been processed.", s.name);
            } else {
                var url = BASE_URL + s.ref;
                var scraped = SpeciesBuilder.from(speciesScraper.scrape(url)).withUrl(url);
                objectMapper.writeValue(output.toFile(), scraped);

                if (it.hasNext()) {
                    Thread.sleep(wait.toMillis());
                }
            }
        }
    }

    private void scrapeMoves(Path movesDir) throws IOException, InterruptedException {
        log.info("Scraping moves to {}", movesDir);
        var doc = Jsoup.connect(BASE_URL + MOVES_PAGE).get();

        var moves = extractLinks(0, doc);

        log.info("Found {} moves", moves.size());

        for (var it = moves.iterator(); it.hasNext(); ) {
            var move = it.next();
            var output = outputFile(move, movesDir);
            if (Files.exists(output)) {
                log.info("Skipping {} because it has already been processed.", move.name);
            } else {
                var url = BASE_URL + move.ref;
                var scraped = MoveBuilder.from(moveScraper.scrape(url)).withUrl(url);
                objectMapper.writeValue(output.toFile(), scraped);

                if (it.hasNext()) {
                    Thread.sleep(wait.toMillis());
                }
            }
        }
    }

    private Path outputFile(Link link, Path parent) {
        return parent.resolve(link.name.replace(" ", "_") + ".json");
    }

    private List<Link> extractLinks(int cellIndex, Document doc) {
        var rows = doc.selectFirst("table.wikitable").select("tr");
        return rows.stream()
                .skip(1)
                .map(row -> row.select("td").get(cellIndex))
                .map(cell -> new Link(cell.text(), cell.selectFirst("a").attr("href")))
                .toList();
    }

    private record Link(String name, String ref) {}
}
