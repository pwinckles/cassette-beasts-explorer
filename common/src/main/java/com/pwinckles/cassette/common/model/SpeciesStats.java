package com.pwinckles.cassette.common.model;

import io.avaje.jsonb.Json;
import io.soabase.recordbuilder.core.RecordBuilderFull;

@Json
@RecordBuilderFull
public record SpeciesStats(
        int hp,
        int meleeAttack,
        int meleeDefense,
        int rangedAttack,
        int rangedDefense,
        int speed,
        int attributeSum,
        int ap,
        int moveSlots) {}
