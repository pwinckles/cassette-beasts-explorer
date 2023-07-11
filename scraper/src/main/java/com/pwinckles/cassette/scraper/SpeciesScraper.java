package com.pwinckles.cassette.scraper;

import com.pwinckles.cassette.common.model.Species;
import com.pwinckles.cassette.common.model.SpeciesBuilder;
import com.pwinckles.cassette.common.model.SpeciesMovesBuilder;
import com.pwinckles.cassette.common.model.SpeciesStatsBuilder;
import com.pwinckles.cassette.common.model.SpeciesType;
import java.io.IOException;
import java.nio.file.Path;
import java.util.function.BiConsumer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpeciesScraper {

    private static final Logger log = LoggerFactory.getLogger(SpeciesScraper.class);

    public Species scrape(String url) throws IOException {
        log.info("Scraping species url: {}", url);
        var doc = Jsoup.connect(url).get();
        return scrape(doc);
    }

    public Species scrape(Path file) throws IOException {
        log.info("Scraping species file: {}", file);
        var doc = Jsoup.parse(file.toFile());
        return scrape(doc);
    }

    private Species scrape(Document doc) {
        var species = SpeciesBuilder.builder();

        var infoBox = doc.selectFirst("div.infobox");

        var infoBoxTitle = infoBox.selectFirst("div.infobox-title").text();
        var parts = infoBoxTitle.split("#", 2);
        species.name(parts[0].trim()).number(Integer.parseInt(parts[1]));

        processTable(infoBox, "div.infobox-data table", (header, value) -> {
            switch (header.toLowerCase()) {
                case "type" -> species.type(SpeciesType.fromString(value));
                case "remaster from" -> species.remasterFrom(value);
                case "remaster to" -> {
                    var remasters = value.split(",");
                    for (var remaster : remasters) {
                        species.addRemasterTo(remaster.trim());
                    }
                }
                default -> log.warn("Unmapped species info box data: {}", header);
            }
        });

        var stats = SpeciesStatsBuilder.builder();

        processTable(doc, "h3:has(span#Base_Form_Stats) + table", (header, value) -> {
            int parsed = Integer.parseInt(value);
            switch (header.toLowerCase()) {
                case "max hp" -> stats.hp(parsed);
                case "m. atk" -> stats.meleeAttack(parsed);
                case "m. def" -> stats.meleeDefense(parsed);
                case "r. atk" -> stats.rangedAttack(parsed);
                case "r. def" -> stats.rangedDefense(parsed);
                case "speed" -> stats.speed(parsed);
                default -> log.warn("Unmapped species base stat: {}", header);
            }
        });

        stats.attributeSum(stats.hp()
                + stats.meleeAttack()
                + stats.meleeDefense()
                + stats.rangedAttack()
                + stats.rangedDefense()
                + stats.speed());

        processTable(doc, "h3:has(span#Additional_Stats) + table", (header, value) -> {
            var parsed = Integer.parseInt(value.split("/", 2)[0].trim());
            switch (header.toLowerCase()) {
                case "max ap" -> stats.ap(parsed);
                case "move slots" -> stats.moveSlots(parsed);
                default -> log.warn("Unmapped species additional state: {}", header);
            }
        });

        species.stats(stats.build());

        var moves = SpeciesMovesBuilder.builder();

        var learnedMovesTable = doc.selectFirst("h2:has(span#Moves) + table");
        var learnedMovesRows = learnedMovesTable.select("tr");

        learnedMovesRows.stream().skip(1).forEach(row -> {
            var cells = row.select("td");
            var move = cells.get(1).text();

            if ("Initial".equals(cells.get(0).text())) {
                moves.addInitial(move);
            } else {
                moves.addLearned(move);
            }
        });

        var compatibleMovesRows = learnedMovesTable.nextElementSibling().select("tr");
        compatibleMovesRows.stream().skip(2).forEach(row -> {
            var cells = row.select("td");
            moves.addCompatible(cells.get(1).text());
        });

        species.moves(moves.build());

        return species.build();
    }

    private void processTable(Element doc, String selector, BiConsumer<String, String> processor) {
        var baseStatsRows = doc.selectFirst(selector).select("tr");
        baseStatsRows.forEach(row -> {
            var header = row.selectFirst("th").text();
            var value = row.selectFirst("td").text();
            processor.accept(header, value);
        });
    }
}
