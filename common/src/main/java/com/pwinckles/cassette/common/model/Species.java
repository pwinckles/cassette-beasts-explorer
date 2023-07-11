package com.pwinckles.cassette.common.model;

import io.avaje.jsonb.Json;
import io.soabase.recordbuilder.core.RecordBuilderFull;

import java.util.List;
import java.util.Objects;

@Json
@RecordBuilderFull
public record Species(String name,
                      int number,
                      SpeciesType type,
                      String remasterFrom,
                      List<String> remasterTo,
                      SpeciesStats stats,
                      SpeciesMoves moves,
                      String url) {

    public Species(String name,
                   int number,
                   SpeciesType type,
                   String remasterFrom,
                   List<String> remasterTo,
                   SpeciesStats stats,
                   SpeciesMoves moves,
                   String url) {
        this.name = Objects.requireNonNull(name);
        this.number = number;
        this.type = type;
        this.remasterFrom = remasterFrom;
        this.remasterTo = Objects.requireNonNull(remasterTo);
        this.stats = Objects.requireNonNull(stats);
        this.moves = Objects.requireNonNull(moves);
        this.url = url;
    }
}
