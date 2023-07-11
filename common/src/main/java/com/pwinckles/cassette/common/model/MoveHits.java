package com.pwinckles.cassette.common.model;

import io.avaje.jsonb.Json;
import io.soabase.recordbuilder.core.RecordBuilderFull;

@Json
@RecordBuilderFull
public record MoveHits(int min, int max) {

    public MoveHits(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("Min (" + min + ") cannot be greater than max (" + max + ").");
        }

        this.min = min;
        this.max = max;
    }
}
