package com.pwinckles.cassette.common.model;

import io.soabase.recordbuilder.core.RecordBuilderFull;
import java.util.Objects;

@RecordBuilderFull
public record CompatibleSpecies(String name, MoveSource source) {

    public CompatibleSpecies(String name, MoveSource source) {
        this.name = Objects.requireNonNull(name);
        this.source = Objects.requireNonNull(source);
    }
}
