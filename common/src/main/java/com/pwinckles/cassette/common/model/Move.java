package com.pwinckles.cassette.common.model;

import io.avaje.jsonb.Json;
import io.soabase.recordbuilder.core.RecordBuilderFull;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Json
@RecordBuilderFull
public record Move(
        String name,
        String description,
        Set<MoveCategory> categories,
        MoveType type,
        Integer power,
        MoveHits numHits,
        MoveAccuracy accuracy,
        int cost,
        MoveTarget target,
        boolean copyable,
        int priority,
        List<StatusEffect> statusEffects,
        List<CompatibleSpecies> compatibleSpecies,
        String url) {

    public Move(
            String name,
            String description,
            Set<MoveCategory> categories,
            MoveType type,
            Integer power,
            MoveHits numHits,
            MoveAccuracy accuracy,
            int cost,
            MoveTarget target,
            boolean copyable,
            int priority,
            List<StatusEffect> statusEffects,
            List<CompatibleSpecies> compatibleSpecies,
            String url) {
        this.name = Objects.requireNonNull(name);
        this.description = Objects.requireNonNull(description);
        this.categories = Objects.requireNonNull(categories);
        this.type = Objects.requireNonNull(type);
        this.power = power;
        this.numHits = numHits;
        this.accuracy = accuracy;
        this.cost = cost;
        this.target = Objects.requireNonNull(target);
        this.copyable = copyable;
        this.priority = priority;
        this.statusEffects = Objects.requireNonNull(statusEffects);
        this.compatibleSpecies = Objects.requireNonNull(compatibleSpecies);
        this.url = url;
    }
}
