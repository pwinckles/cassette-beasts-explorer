package com.pwinckles.cassette.scraper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pwinckles.cassette.common.model.DataBuilder;
import com.pwinckles.cassette.common.model.Move;
import com.pwinckles.cassette.common.model.Species;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Assembler {

    private static final Logger log = LoggerFactory.getLogger(Assembler.class);

    private final ObjectMapper objectMapper;

    public Assembler() {
        this.objectMapper = new ObjectMapper();
    }

    public void assemble(Path dir) throws IOException {
        var outputFile = dir.resolve(Constants.DATA_FILE);
        log.info("Assembling data into {}", outputFile);

        var data = DataBuilder.builder();

        try (var files = Files.list(dir.resolve(Constants.SPECIES_DIR))) {
            files.map(file -> readJson(file, Species.class)).forEach(data::addSpecies);
        }

        try (var files = Files.list(dir.resolve(Constants.MOVES_DIR))) {
            files.map(file -> readJson(file, Move.class)).forEach(data::addMoves);
        }

        objectMapper.writeValue(outputFile.toFile(), data.build());
    }

    private <T> T readJson(Path file, Class<T> clazz) {
        try {
            return objectMapper.readValue(file.toFile(), clazz);
        } catch (IOException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        }
    }
}
