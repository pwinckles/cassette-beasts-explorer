package com.pwinckles.cassette.common.model;

import io.soabase.recordbuilder.core.RecordBuilderFull;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RecordBuilderFull
public record BootlegMoves(List<String> common, Map<SpeciesType, List<String>> typeSpecific) {

    public BootlegMoves(List<String> common, Map<SpeciesType, List<String>> typeSpecific) {
        this.common = Objects.requireNonNull(common);
        this.typeSpecific = Objects.requireNonNull(typeSpecific);
    }
}
