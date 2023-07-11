package com.pwinckles.cassette.common.model;

import io.avaje.jsonb.Json;
import io.soabase.recordbuilder.core.RecordBuilderFull;
import java.util.List;

@Json
@RecordBuilderFull
public record SpeciesMoves(List<String> initial, List<String> learned, List<String> compatible) {}
