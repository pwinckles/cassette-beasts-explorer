package com.pwinckles.cassette.cli;

public sealed interface Input {

    record Empty() implements Input {}

    record Help() implements Input {}

    record Exit() implements Input {}

    record Query(String query) implements Input {}

    static Input parse(String input) {
        var index = input.indexOf(' ');
        var firstWord =
                input.substring(0, index == -1 ? input.length() : index).trim().toLowerCase();

        return switch (firstWord) {
            case "" -> new Empty();
            case "help" -> new Help();
            case "exit", "quit" -> new Exit();
            default -> new Query(input);
        };
    }
}
