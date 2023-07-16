package com.pwinckles.cassette.common.model;

import io.soabase.recordbuilder.core.RecordBuilderFull;
import java.util.List;

@RecordBuilderFull
public record SpeciesMoves(List<String> initial, List<String> learned, List<String> compatible, BootlegMoves bootleg) {}
