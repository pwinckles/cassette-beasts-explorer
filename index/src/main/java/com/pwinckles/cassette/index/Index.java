package com.pwinckles.cassette.index;

import com.pwinckles.cassette.common.model.Data;
import com.pwinckles.cassette.common.model.Move;
import com.pwinckles.cassette.common.model.MoveAccuracy;
import com.pwinckles.cassette.common.model.MoveCategory;
import com.pwinckles.cassette.common.model.Species;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordTokenizerFactory;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilterFactory;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.queryparser.flexible.standard.config.PointsConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;

public class Index implements AutoCloseable {

    public static final String DOC_TYPE_INDEX = "doc_type";
    public static final String DOC_TYPE_SPECIES = "species";
    public static final String DOC_TYPE_MOVE = "move";

    private static final Map<MoveCategory, String> CAT_MAP = Map.of(
            MoveCategory.MELEE, "melee attack",
            MoveCategory.RANGED, "ranged attack",
            MoveCategory.STATUS, "status effect",
            MoveCategory.MISC, "misc",
            MoveCategory.ACTIVE, "active",
            MoveCategory.PASSIVE, "passive",
            MoveCategory.AUTOMATED, "automated");

    private final Directory dir;
    private final Analyzer analyzer;

    public Index() throws IOException {
        dir = new ByteBuffersDirectory();
        var keywordAnalyzer = CustomAnalyzer.builder()
                .withTokenizer(KeywordTokenizerFactory.class)
                .addTokenFilter(LowerCaseFilterFactory.class)
                .addTokenFilter(ASCIIFoldingFilterFactory.class)
                .build();
        var standardAnalyzer = CustomAnalyzer.builder()
                .withTokenizer(StandardTokenizerFactory.class)
                .addTokenFilter(LowerCaseFilterFactory.class)
                .addTokenFilter(ASCIIFoldingFilterFactory.class)
                .build();
        analyzer = new PerFieldAnalyzerWrapper(
                standardAnalyzer,
                Map.ofEntries(
                        Map.entry(DOC_TYPE_INDEX, keywordAnalyzer),
                        Map.entry(SpeciesIndexNames.NAME, keywordAnalyzer),
                        Map.entry(SpeciesIndexNames.TYPE, keywordAnalyzer),
                        Map.entry(SpeciesIndexNames.REMASTER_FROM, keywordAnalyzer),
                        Map.entry(SpeciesIndexNames.REMASTER_TO, keywordAnalyzer),
                        Map.entry(MoveIndexNames.TYPE, keywordAnalyzer),
                        Map.entry(MoveIndexNames.AVOIDABLE, keywordAnalyzer),
                        Map.entry(MoveIndexNames.TARGET, keywordAnalyzer),
                        Map.entry(MoveIndexNames.COPYABLE, keywordAnalyzer),
                        Map.entry(MoveIndexNames.STATUS_EFFECT_KIND, keywordAnalyzer),
                        Map.entry(MoveIndexNames.SPECIES, keywordAnalyzer)));
    }

    public void index(Data data) throws IOException {
        var writerConfig = new IndexWriterConfig(analyzer);
        writerConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        try (var writer = new IndexWriter(dir, writerConfig)) {
            data.species().stream().map(this::toDocument).forEach(doc -> writeDoc(writer, doc));
            data.moves().stream().map(this::toDocument).forEach(doc -> writeDoc(writer, doc));
        }
    }

    public Searcher createSearcher() throws IOException {
        return new Searcher(dir, analyzer);
    }

    private Document toDocument(Species species) {
        var doc = new Document();
        doc.add(new TextField(DOC_TYPE_INDEX, DOC_TYPE_SPECIES, Field.Store.YES));
        doc.add(new IntField(SpeciesIndexNames.NUMBER, species.number(), Field.Store.NO));
        doc.add(new TextField(SpeciesIndexNames.NAME, species.name(), Field.Store.YES));
        doc.add(new TextField(SpeciesIndexNames.TYPE, species.type().name(), Field.Store.NO));
        if (species.remasterFrom() != null) {
            doc.add(new TextField(SpeciesIndexNames.REMASTER_FROM, species.remasterFrom(), Field.Store.NO));
        }
        species.remasterTo().forEach(to -> doc.add(new TextField(SpeciesIndexNames.REMASTER_TO, to, Field.Store.NO)));
        doc.add(new IntField(SpeciesIndexNames.HP, species.stats().hp(), Field.Store.NO));
        doc.add(new IntField(SpeciesIndexNames.M_ATTACK, species.stats().meleeAttack(), Field.Store.NO));
        doc.add(new IntField(SpeciesIndexNames.M_DEFENSE, species.stats().meleeDefense(), Field.Store.NO));
        doc.add(new IntField(SpeciesIndexNames.R_ATTACK, species.stats().rangedAttack(), Field.Store.NO));
        doc.add(new IntField(SpeciesIndexNames.R_DEFENSE, species.stats().rangedDefense(), Field.Store.NO));
        doc.add(new IntField(SpeciesIndexNames.SPEED, species.stats().speed(), Field.Store.NO));
        doc.add(new IntField(SpeciesIndexNames.ATTR_SUM, species.stats().attributeSum(), Field.Store.NO));
        doc.add(new IntField(SpeciesIndexNames.AP, species.stats().ap(), Field.Store.NO));
        doc.add(new IntField(SpeciesIndexNames.SLOTS, species.stats().moveSlots(), Field.Store.NO));
        species.moves()
                .compatible()
                .forEach(move -> doc.add(new TextField(SpeciesIndexNames.MOVE, move, Field.Store.NO)));
        return doc;
    }

    private Document toDocument(Move move) {
        var doc = new Document();
        doc.add(new TextField(DOC_TYPE_INDEX, DOC_TYPE_MOVE, Field.Store.YES));
        doc.add(new TextField(MoveIndexNames.NAME, move.name(), Field.Store.YES));
        doc.add(new TextField(MoveIndexNames.DESC, move.description(), Field.Store.NO));
        doc.add(new TextField(MoveIndexNames.TYPE, move.type().name(), Field.Store.NO));
        move.categories().forEach(cat -> doc.add(new TextField(MoveIndexNames.CAT, CAT_MAP.get(cat), Field.Store.NO)));
        if (move.power() != null) {
            doc.add(new IntField(MoveIndexNames.POWER, move.power(), Field.Store.NO));
        }
        if (move.numHits() != null) {
            doc.add(new IntField(MoveIndexNames.MIN_HITS, move.numHits().min(), Field.Store.NO));
            doc.add(new IntField(MoveIndexNames.MAX_HITS, move.numHits().max(), Field.Store.NO));
        }
        if (move.accuracy() instanceof MoveAccuracy.Unavoidable) {
            doc.add(new TextField(MoveIndexNames.AVOIDABLE, "false", Field.Store.NO));
        } else if (move.accuracy() instanceof MoveAccuracy.Avoidable avoidable) {
            doc.add(new TextField(MoveIndexNames.AVOIDABLE, "true", Field.Store.NO));
            doc.add(new IntField(MoveIndexNames.ACCURACY, avoidable.percentToHit(), Field.Store.NO));
        }
        doc.add(new IntField(MoveIndexNames.COST, move.cost(), Field.Store.NO));
        if (move.target() != null) {
            doc.add(new TextField(MoveIndexNames.TARGET, move.target().name().replace("_", " "), Field.Store.NO));
        }
        doc.add(new TextField(MoveIndexNames.COPYABLE, Boolean.toString(move.copyable()), Field.Store.NO));
        doc.add(new IntField(MoveIndexNames.PRIORITY, move.priority(), Field.Store.NO));
        move.statusEffects().forEach(effect -> {
            doc.add(new TextField(MoveIndexNames.STATUS_EFFECT, effect.name(), Field.Store.NO));
            doc.add(new TextField(
                    MoveIndexNames.STATUS_EFFECT_KIND, effect.kind().name(), Field.Store.NO));
        });
        move.compatibleSpecies()
                .forEach(species -> doc.add(new TextField(MoveIndexNames.SPECIES, species.name(), Field.Store.NO)));
        return doc;
    }

    private void writeDoc(IndexWriter writer, Document doc) {
        try {
            writer.addDocument(doc);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void close() throws Exception {
        dir.close();
    }

    public static class Searcher implements AutoCloseable {
        private final DirectoryReader reader;
        private final StandardQueryParser parser;
        private final IndexSearcher searcher;

        private Searcher(Directory dir, Analyzer analyzer) throws IOException {
            reader = DirectoryReader.open(dir);
            searcher = new IndexSearcher(reader);
            parser = new StandardQueryParser(analyzer);
            parser.setAllowLeadingWildcard(true);
            var intConfig = new PointsConfig(NumberFormat.getIntegerInstance(), Integer.class);
            parser.setPointsConfigMap(Map.ofEntries(
                    Map.entry(SpeciesIndexNames.NUMBER, intConfig),
                    Map.entry(SpeciesIndexNames.HP, intConfig),
                    Map.entry(SpeciesIndexNames.M_ATTACK, intConfig),
                    Map.entry(SpeciesIndexNames.M_DEFENSE, intConfig),
                    Map.entry(SpeciesIndexNames.R_ATTACK, intConfig),
                    Map.entry(SpeciesIndexNames.R_DEFENSE, intConfig),
                    Map.entry(SpeciesIndexNames.SPEED, intConfig),
                    Map.entry(SpeciesIndexNames.ATTR_SUM, intConfig),
                    Map.entry(SpeciesIndexNames.AP, intConfig),
                    Map.entry(SpeciesIndexNames.SLOTS, intConfig),
                    Map.entry(MoveIndexNames.POWER, intConfig),
                    Map.entry(MoveIndexNames.MIN_HITS, intConfig),
                    Map.entry(MoveIndexNames.MAX_HITS, intConfig),
                    Map.entry(MoveIndexNames.ACCURACY, intConfig),
                    Map.entry(MoveIndexNames.PRIORITY, intConfig),
                    Map.entry(MoveIndexNames.COST, intConfig)));
        }

        public List<SearchResult> search(String query) throws IOException, QueryNodeException {
            var parsedQuery = parser.parse(query, "");
            var docResults = searcher.search(parsedQuery, Integer.MAX_VALUE).scoreDocs;
            var storedFields = searcher.storedFields();

            var results = new ArrayList<SearchResult>();

            for (var docResult : docResults) {
                var doc = storedFields.document(docResult.doc);
                var result =
                        switch (doc.get(DOC_TYPE_INDEX)) {
                            case DOC_TYPE_SPECIES -> new SearchResult.SpeciesResult(doc.get(SpeciesIndexNames.NAME));
                            case DOC_TYPE_MOVE -> new SearchResult.MoveResult(doc.get(MoveIndexNames.NAME));
                            default -> throw new IllegalStateException("Unknown doc type: " + doc.get(DOC_TYPE_INDEX));
                        };
                results.add(result);
            }

            return results;
        }

        @Override
        public void close() throws Exception {
            reader.close();
        }
    }
}
