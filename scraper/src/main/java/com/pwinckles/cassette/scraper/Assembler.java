package com.pwinckles.cassette.scraper;

import com.pwinckles.cassette.common.model.Data;
import com.pwinckles.cassette.common.model.DataBuilder;
import com.pwinckles.cassette.common.model.Move;
import com.pwinckles.cassette.common.model.Species;
import io.avaje.jsonb.JsonType;
import io.avaje.jsonb.Jsonb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Assembler {

    private static final Logger log = LoggerFactory.getLogger(Assembler.class);

    private JsonType<Data> dataJsonType;
    private final JsonType<Species> speciesJsonType;
    private final JsonType<Move> moveJsonType;

    public Assembler() {
        var jsonb = Jsonb.builder().build();
        this.dataJsonType = jsonb.type(Data.class);
        this.speciesJsonType = jsonb.type(Species.class);
        this.moveJsonType = jsonb.type(Move.class);
    }

    public void assemble(Path dir) throws IOException {
        var outputFile = dir.resolve(Constants.DATA_FILE);
        log.info("Assembling data into {}", outputFile);

        var data = DataBuilder.builder();

        try (var files = Files.list(dir.resolve(Constants.SPECIES_DIR))) {
            files.map(file -> readJson(file, speciesJsonType))
                    .forEach(data::addSpecies);
        }

        try (var files = Files.list(dir.resolve(Constants.MOVES_DIR))) {
            files.map(file -> readJson(file, moveJsonType))
                    .forEach(data::addMoves);
        }

        try (var writer = Files.newBufferedWriter(outputFile)) {
            dataJsonType.toJson(data.build(), writer);
        }
    }

    private <T> T readJson(Path file, JsonType<T> jsonType) {
        try (var reader = Files.newBufferedReader(file)) {
            return jsonType.fromJson(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        }
    }

}
