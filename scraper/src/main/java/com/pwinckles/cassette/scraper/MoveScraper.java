package com.pwinckles.cassette.scraper;

import com.pwinckles.cassette.common.model.CompatibleSpeciesBuilder;
import com.pwinckles.cassette.common.model.Move;
import com.pwinckles.cassette.common.model.MoveAccuracy;
import com.pwinckles.cassette.common.model.MoveBuilder;
import com.pwinckles.cassette.common.model.MoveCategory;
import com.pwinckles.cassette.common.model.MoveHits;
import com.pwinckles.cassette.common.model.MoveSource;
import com.pwinckles.cassette.common.model.MoveTarget;
import com.pwinckles.cassette.common.model.MoveType;
import com.pwinckles.cassette.common.model.StatusEffect;
import com.pwinckles.cassette.common.model.StatusEffectBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Consumer;

public class MoveScraper {

    private static final Logger log = LoggerFactory.getLogger(MoveScraper.class);

    public Move scrape(String url) throws IOException {
        log.info("Scraping move url: {}", url);
        var doc = Jsoup.connect(url).get();
        return scrape(doc);
    }

    public Move scrape(Path file) throws IOException {
        log.info("Scraping move file: {}", file);
        var doc = Jsoup.parse(file.toFile());
        return scrape(doc);
    }

    private Move scrape(Document doc) {
        var move = MoveBuilder.builder();

        var infoBox = doc.selectFirst("div.infobox");

        move.name(infoBox.selectFirst("div.infobox-title").text());
        move.addCategories(mapCategory(infoBox.selectFirst("div.infobox-subtitle").text()));

        var infoBoxDataRows = infoBox.selectFirst("div.infobox-data table").select("tr");
        infoBoxDataRows.forEach(row -> {
            var header = row.selectFirst("th").text();
            var value = row.selectFirst("td").text();

            switch (header.toLowerCase()) {
                case "type" -> move.type(MoveType.fromString(value));
                case "power" -> move.power(Integer.parseInt(value));
                case "num hits" -> move.numHits(mapHits(value));
                case "accuracy" -> move.accuracy(mapAccuracy(value));
                case "copyable" -> move.copyable(mapCopyable(value));
                case "priority" -> move.priority(mapPriority(value));
                case "targets" -> move.target(mapTarget(value));
                case "use cost" -> {
                    if ("passive".equalsIgnoreCase(value)) {
                        move.addCategories(MoveCategory.PASSIVE);
                    } else {
                        move.addCategories(MoveCategory.ACTIVE);
                        move.cost(mapCost(value));
                    }
                }
                default -> log.warn("Unmapped move info box data: {}", header);
            }
        });

        if (move.target() == null) {
            move.target(MoveTarget.SELF);
        }

        move.description(doc.selectFirst("h2:has(span#Description) + p").text());

        if (move.description().toLowerCase().contains("automatically")) {
            move.addCategories(MoveCategory.AUTOMATED);
        }

        var statusEffectsTable = doc.selectFirst("h2:has(span#Status_Effects) + table");
        if (statusEffectsTable != null) {
            processTable(statusEffectsTable, cells ->
                    move.addStatusEffects(StatusEffectBuilder.builder()
                            .name(cells.get(1).text())
                            .kind(StatusEffect.Kind.fromString(cells.get(2).text()))
                            .build()));
        }

        var compatibleTable = doc.selectFirst("h2:has(span#Compatible_Species) + table");
        if (compatibleTable != null) {
            processTable(compatibleTable, cells -> {
                var method = cells.get(4).text();
                MoveSource source;
                if ("Initial".equals(method)) {
                    source = MoveSource.INITIAL;
                } else if ("Sticker".equals(method)) {
                    source = MoveSource.STICKER;
                } else {
                    var stars = cells.get(4).select("img").size();
                    source = switch (stars) {
                        case 1 -> MoveSource.STAR_1;
                        case 2 -> MoveSource.STAR_2;
                        case 3 -> MoveSource.STAR_3;
                        case 4 -> MoveSource.STAR_4;
                        case 5 -> MoveSource.STAR_5;
                        default -> throw new IllegalArgumentException("Unexpected star count: " + stars);
                    };
                }
                move.addCompatibleSpecies(CompatibleSpeciesBuilder.builder()
                        .name(cells.get(1).text())
                        .source(source)
                        .build());
            });
        }

        return move.build();
    }

    private void processTable(Element table, Consumer<Elements> processor) {
        var rows = table.select("tr");
        rows.stream().skip(1)
                .forEach(row -> processor.accept(row.select("td")));
    }

    private MoveCategory mapCategory(String displayCategory) {
        return switch (displayCategory.toLowerCase()) {
            case "melee attack" -> MoveCategory.MELEE;
            case "ranged attack" -> MoveCategory.RANGED;
            case "status effect move" -> MoveCategory.STATUS;
            case "miscellaneous" -> MoveCategory.MISC;
            default -> throw new IllegalArgumentException("Unknown move category: " + displayCategory);
        };
    }

    private MoveAccuracy mapAccuracy(String value) {
        if ("Unavoidable".equalsIgnoreCase(value)) {
            return new MoveAccuracy.Unavoidable();
        }
        var intPart = value.substring(0, value.length() - 1);
        return new MoveAccuracy.Avoidable(Integer.parseInt(intPart));
    }

    private int mapCost(String value) {
        var intPart = value.substring(0, value.length() - 3);
        return Integer.parseInt(intPart);
    }

    private boolean mapCopyable(String value) {
        return "yes".equalsIgnoreCase(value);
    }

    private int mapPriority(String value) {
        var start = value.indexOf('(');
        var stop = value.indexOf(')');

        if (start == -1 || stop == -1) {
            throw new IllegalArgumentException("Unknown move priority: " + value);
        }

        return Integer.parseInt(value.substring(start + 1, stop));
    }

    private MoveTarget mapTarget(String value) {
        return switch (value.toLowerCase()) {
            case "single" -> MoveTarget.SINGLE;
            case "team" -> MoveTarget.TEAM;
            case "all except self" -> MoveTarget.ALL_EXCEPT_SELF;
            case "single ally" -> MoveTarget.SINGLE_ALLY;
            case "single ally (not self)" -> MoveTarget.SINGLE_ALLY;
            case "all" -> MoveTarget.ALL;
            default -> throw new IllegalArgumentException("Unknown move target: " + value);
        };
    }

    private MoveHits mapHits(String value) {
        try {
            var parsed = Integer.parseInt(value);
            return new MoveHits(parsed, parsed);
        } catch (NumberFormatException e) {
            var parts = value.split(" - ");
            return new MoveHits(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        }
    }

}
