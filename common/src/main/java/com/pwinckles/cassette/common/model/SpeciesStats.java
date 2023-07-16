package com.pwinckles.cassette.common.model;

import io.soabase.recordbuilder.core.RecordBuilderFull;

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
