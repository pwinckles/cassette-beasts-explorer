package com.pwinckles.cassette.common.model;

import io.soabase.recordbuilder.core.RecordBuilderFull;

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
