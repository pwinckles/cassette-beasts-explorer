package com.pwinckles.cassette.common.model;

import io.avaje.jsonb.Json;
import io.soabase.recordbuilder.core.RecordBuilderFull;
import java.util.List;
import java.util.Map;

@Json
@RecordBuilderFull
public record BootlegMoves(List<String> common, Map<SpeciesType, List<String>> typeSpecific) {}
