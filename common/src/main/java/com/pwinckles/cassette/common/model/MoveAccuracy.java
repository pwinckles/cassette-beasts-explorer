package com.pwinckles.cassette.common.model;

import io.avaje.jsonb.Json;

@Json
@Json.SubType(type = MoveAccuracy.Unavoidable.class, name="UNAVOIDABLE")
@Json.SubType(type = MoveAccuracy.Avoidable.class, name="AVOIDABLE")
public interface MoveAccuracy {

    record Unavoidable() implements MoveAccuracy {}

    record Avoidable(int percentToHit) implements MoveAccuracy {
        public Avoidable(int percentToHit) {
            if (percentToHit < 0) {
                throw new IllegalArgumentException("percentToHit must be greater than 0, but was " + percentToHit);
            } else if (percentToHit > 100) {
                throw new IllegalArgumentException("percentToHit must be less than or equal to 100, but was " + percentToHit);
            }
            this.percentToHit = percentToHit;
        }
    }

}
