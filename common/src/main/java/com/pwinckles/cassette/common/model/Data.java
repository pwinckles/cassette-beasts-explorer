package com.pwinckles.cassette.common.model;

import io.avaje.jsonb.Json;
import io.soabase.recordbuilder.core.RecordBuilderFull;

import java.util.List;
import java.util.Objects;

@Json
@RecordBuilderFull
public record Data(List<Species> species, List<Move> moves) {
    public Data(List<Species> species, List<Move> moves) {
        this.species = Objects.requireNonNull(species);
        this.moves = Objects.requireNonNull(moves);
    }
}
