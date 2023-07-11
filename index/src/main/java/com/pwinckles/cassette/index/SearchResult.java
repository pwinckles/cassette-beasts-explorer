package com.pwinckles.cassette.index;

public sealed interface SearchResult {

    record SpeciesResult(String name) implements SearchResult {}

    record MoveResult(String name) implements SearchResult {}

}
