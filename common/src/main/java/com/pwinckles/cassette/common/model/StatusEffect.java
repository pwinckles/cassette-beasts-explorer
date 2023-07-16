package com.pwinckles.cassette.common.model;

import io.soabase.recordbuilder.core.RecordBuilderFull;
import java.util.Objects;

@RecordBuilderFull
public record StatusEffect(String name, Kind kind) {

    public StatusEffect(String name, Kind kind) {
        this.name = Objects.requireNonNull(name);
        this.kind = Objects.requireNonNull(kind);
    }

    public enum Kind {
        BUFF,
        DEBUFF,
        TRANSMUTATION,
        MISC;

        public static Kind fromString(String value) {
            var upper = value.toUpperCase();
            for (var entry : values()) {
                if (entry.name().equals(upper)) {
                    return entry;
                }
            }
            throw new IllegalArgumentException("Unknown kind: " + value);
        }
    }
}
