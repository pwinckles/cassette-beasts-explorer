package com.pwinckles.cassette.index;

import com.pwinckles.cassette.common.model.SpeciesType;

public sealed interface SearchResult {

    record SpeciesResult(String name, boolean bootleg, SpeciesType type) implements SearchResult {}

    record MoveResult(String name) implements SearchResult {}
}
