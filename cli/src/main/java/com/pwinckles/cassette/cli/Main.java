package com.pwinckles.cassette.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pwinckles.cassette.common.model.Data;
import com.pwinckles.cassette.common.model.Move;
import com.pwinckles.cassette.common.model.MoveAccuracy;
import com.pwinckles.cassette.common.model.MoveCategory;
import com.pwinckles.cassette.common.model.MoveHits;
import com.pwinckles.cassette.common.model.Species;
import com.pwinckles.cassette.common.model.SpeciesType;
import com.pwinckles.cassette.index.Index;
import com.pwinckles.cassette.index.SearchResult;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;

public final class Main {

    public static void main(String[] args) throws Exception {
        var data = readData();
        var speciesMap = data.species().stream().collect(Collectors.toMap(Species::name, Function.identity()));
        var movesMap = data.moves().stream().collect(Collectors.toMap(Move::name, Function.identity()));

        try (var index = new Index();
                var scanner = new Scanner(System.in)) {
            index.index(data);
            var searcher = index.createSearcher();

            var exit = false;

            while (!exit) {
                try {
                    System.out.print("Query: ");
                    var input = readInput(scanner);
                    // TODO docs
                    // TODO changelog

                    System.out.println();

                    if (input instanceof Input.Exit) {
                        exit = true;
                    } else if (input instanceof Input.Help) {
                        printHelp();
                        System.out.println();
                    } else if (input instanceof Input.Query query) {
                        executeQuery(query.query(), searcher, speciesMap, movesMap);
                        System.out.println();
                    }
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                    System.out.println();
                }
            }
        }
    }

    private static Input readInput(Scanner scanner) {
        try {
            return Input.parse(scanner.nextLine());
        } catch (Exception e) {
            return new Input.Exit();
        }
    }

    private static void executeQuery(
            String query, Index.Searcher searcher, Map<String, Species> speciesMap, Map<String, Move> movesMap)
            throws QueryNodeException, IOException {
        var results = searcher.search(query);

        if (results.isEmpty()) {
            System.out.println("No matches found");
        } else {
            System.out.printf("Matches: %s%n%n", results.size());
        }

        results.forEach(result -> {
            if (result instanceof SearchResult.SpeciesResult s) {
                print(speciesMap.get(s.name()), s.bootleg(), s.type());
            } else if (result instanceof SearchResult.MoveResult m) {
                print(movesMap.get(m.name()));
            }
        });

        if (!results.isEmpty()) {
            System.out.println("=".repeat(80));
        }
    }

    private static void printHelp() {
        System.out.println(
                """
                ## Species indices ##
                | Index name      | Description                                                     |
                |-----------------|-----------------------------------------------------------------|
                | species_num     | The species' number, formatted as an int                        |
                | species_name    | The name of the species                                         |
                | species_type    | The type of the species                                         |
                | hp              | The base HP attribute value                                     |
                | matk            | The base melee attack attribute value                           |
                | mdef            | The base melee defense attribute value                          |
                | ratk            | The base ranged attack attribute value                          |
                | rdef            | The base ranged defense attribute value                         |
                | spd             | The base speed attribute value                                  |
                | attr_sum        | The sum of all of the base attribute values                     |
                | ap              | The number of available AP                                      |
                | slots           | The number of available move slots                              |
                | compatible_move | The name of a move that is compatible with the species          |
                | remaster_from   | The name of the prior form of the species, or `none`            |
                | remaster_to     | The name of the species this species may be remastered into, or |
                |                 | `none`.                                                         |

                ## Move indices ##
                | Index name         | Description                                                  |
                |--------------------|--------------------------------------------------------------|
                | move_name          | The name of the move                                         |
                | move_desc          | The description of the move                                  |
                | move_type          | The elemental type of the move                               |
                | move_cat           | The category of the move: `melee attack`, `ranged attack`,   |
                |                    | `status effect`, `misc`, `active`, `passive`, `automated`    |
                | power              | The moves base damage                                        |
                | min_hits           | The minimum number of hits the move can make                 |
                | max_hits           | The maximum number of hits the move can make                 |
                | avoidable          | Whether the move is avoidable: `true` or `false`             |
                | accuracy           | The percentage chance to hit, only if the move is avoidable  |
                | cost               | The number of AP the move costs                              |
                | target             | Who the move affects: `single`, `team`, `self`,              |
                |                    | `single ally`, `all`, `all except self`                      |
                | copyable           | Whether the move is copyable: `true` or `false`              |
                | priority           | The numeric move priority value. 0 is normal. Greater than 0 |
                |                    | is higher priority, and less is lower.                       |
                | status_effect      | The name of the status effect the move causes.               |
                |                    | See https://wiki.cassettebeasts.com/wiki/Status_Effects      |
                | status_effect_kind | The kind of the status effect: `buff`, `debuff`,             |
                |                    | `transmutation`, `misc`                                      |
                | compatible_species | The name of a species that is compatible with the move       |

                Query syntax: https://lucene.apache.org/core/9_7_0/queryparser/org/apache/lucene/queryparser/flexible/standard/StandardQueryParser.html""");
    }

    private static void print(Species species, boolean bootleg, SpeciesType type) {
        var stats = species.stats();

        var typeDisplay = titleCase(type.name());
        if (bootleg) {
            typeDisplay += " Bootleg";
        }

        System.out.println("=".repeat(80));
        System.out.printf(" #%03d %s [%s]%n", species.number(), species.name(), typeDisplay);
        System.out.printf(" %s%n", species.url());
        System.out.println("  HP | M. Atk | M. Def | R. Atk | R. Def | Speed | Total");
        System.out.printf(
                " %3d | %6d | %6d | %6d | %6d | %5d | %5d%n",
                stats.hp(),
                stats.meleeAttack(),
                stats.meleeDefense(),
                stats.rangedAttack(),
                stats.rangedDefense(),
                stats.speed(),
                stats.attributeSum());
    }

    private static void print(Move move) {
        System.out.println("=".repeat(80));
        System.out.printf(" %s [%s]%n", move.name(), displayCats(move.categories()));
        System.out.printf(" %s%n", move.url());
        printWithWrap(move.description(), 78);
        System.out.println("      Type | Cost | Power |  Hits |    Accuracy | Priority | Target");
        System.out.printf(
                " %9s | %4d | %5s | %5s | %11s | %8d | %s%n",
                titleCase(move.type().name()),
                move.cost(),
                nullToEmpty(move.power()),
                displayHits(move.numHits()),
                displayAccuracy(move.accuracy()),
                move.priority(),
                multiPartTitleCase(move.target().name()));

        if (!move.statusEffects().isEmpty()) {
            System.out.print(" Status Effects: ");
            move.statusEffects().stream()
                    .findFirst()
                    .ifPresent(effect -> System.out.printf(
                            "%s [%s]%n", effect.name(), titleCase(effect.kind().name())));
            move.statusEffects().stream()
                    .skip(1)
                    .forEach(effect -> System.out.printf(
                            "                 %s [%s]%n",
                            effect.name(), titleCase(effect.kind().name())));
        }
    }

    private static Data readData() throws IOException {
        var objectMapper = new ObjectMapper();
        try (var is = new BufferedInputStream(Main.class.getResourceAsStream("/data.json"))) {
            return objectMapper.readValue(is, Data.class);
        }
    }

    private static String displayHits(MoveHits hits) {
        if (hits == null) {
            return "";
        } else if (hits.min() == hits.max()) {
            return String.valueOf(hits.min());
        }
        return hits.min() + " - " + hits.max();
    }

    private static String displayAccuracy(MoveAccuracy accuracy) {
        if (accuracy instanceof MoveAccuracy.Avoidable a) {
            return String.valueOf(a.percentToHit());
        }
        return "Unavoidable";
    }

    private static String displayCats(Set<MoveCategory> cats) {
        return cats.stream()
                .filter(cat -> cat != MoveCategory.ACTIVE)
                .map(Main::displayCat)
                .collect(Collectors.joining(", "));
    }

    private static String displayCat(MoveCategory cat) {
        return switch (cat) {
            case MELEE -> "Melee Attack";
            case RANGED -> "Ranged Attack";
            case STATUS -> "Status Effect";
            case MISC -> "Misc";
            case ACTIVE -> "Active";
            case PASSIVE -> "Passive";
            case AUTOMATED -> "Automated";
        };
    }

    private static String multiPartTitleCase(String value) {
        var builder = new StringBuilder();
        var parts = value.split("_");
        for (var part : parts) {
            builder.append(titleCase(part)).append(" ");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    private static String titleCase(String value) {
        var lower = value.toLowerCase().substring(1);
        return String.valueOf(value.charAt(0)).toUpperCase() + lower;
    }

    private static void printWithWrap(String value, int width) {
        if (value.length() <= width) {
            System.out.printf(" %s%n", value);
        } else {
            for (int i = width; i >= 0; i--) {
                if (value.charAt(i) == ' ') {
                    System.out.printf(" %s%n", value.substring(0, i));
                    printWithWrap(value.substring(i + 1), width);
                    break;
                }
            }
        }
    }

    private static String nullToEmpty(Object object) {
        if (object == null) {
            return "";
        }
        return object.toString();
    }
}
