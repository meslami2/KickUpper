package kickupper.search;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import kickupper.index.SpecialAnalyzer;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Searcher {

    private IndexSearcher indexSearcher;
    private SpecialAnalyzer analyzer;
    private static SimpleHTMLFormatter formatter;
    private static final int numFragments = 4;
    private static final String defaultField = "content";

    /**
     * Sets up the Lucene index Searcher with the specified index.
     *
     * @param indexPath The path to the desired Lucene index.
     */
    public Searcher(String indexPath) {
        try {
            final File indexFile = new File(indexPath);
            System.out.println("load index from:" + indexFile.getAbsolutePath());
            IndexReader reader = DirectoryReader.open(FSDirectory.open(indexFile));
            indexSearcher = new IndexSearcher(reader);
            indexSearcher.setSimilarity(new BM25Similarity());
            analyzer = new SpecialAnalyzer();
            formatter = new SimpleHTMLFormatter("<strong>", "</strong>");
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * The main search function.
     *
     * @param searchQuery Set this object's attributes as needed.
     * @return
     */
    public SearchResult search(SearchQuery searchQuery) {
        BooleanQuery combinedQuery = new BooleanQuery();
        for (String field : searchQuery.fields()) {
            try {
                QueryParser parser = new QueryParser(Version.LUCENE_46, field, analyzer);
                Query textQuery = parser.parse(searchQuery.queryText());
                combinedQuery.add(textQuery, BooleanClause.Occur.MUST);
            } catch (ParseException ex) {
                Logger.getLogger(Searcher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return runSearch(combinedQuery, searchQuery);
    }

    /**
     * The simplest search function. Searches the abstract field and returns a
     * the default number of results.
     *
     * @param queryText The text to search
     * @return the SearchResult
     */
    public SearchResult search(String queryText) {
        return search(new SearchQuery(queryText, defaultField));
//        return search(new SearchQuery(queryText, Arrays.asList("content", "title", "url")));
    }

    /**
     * Performs the actual Lucene search.
     *
     * @param luceneQuery
     * @param numResults
     * @return the SearchResult
     */
    private SearchResult runSearch(Query luceneQuery, SearchQuery searchQuery) {
        try {
            TopDocs docs = indexSearcher.search(luceneQuery, searchQuery.fromDoc() + searchQuery.numResults());
            ScoreDoc[] hits = docs.scoreDocs;
            String field = searchQuery.fields().get(0);

            SearchResult searchResult = new SearchResult(searchQuery, docs.totalHits);
            for (ScoreDoc hit : hits) {
                Document doc = indexSearcher.doc(hit.doc);
                ResultDoc rdoc = new ResultDoc(hit.doc);

                String highlighted = null;
                try {
                    Highlighter highlighter = new Highlighter(formatter, new QueryScorer(luceneQuery));
                    rdoc.score(hit.score);
                    String content = doc.getField("content").stringValue();
                    rdoc.content(content);
                    String contents = doc.getField(field).stringValue();
                    rdoc.content(contents);
                    int freqNeg = Integer.parseInt(doc.getField("freqN").stringValue());
                    rdoc.freqNeg(freqNeg);
                    int freqPos = Integer.parseInt(doc.getField("freqP").stringValue());
                    rdoc.freqPos(freqPos);
                    String[] snippets = highlighter.getBestFragments(analyzer, field, contents, numFragments);
                    highlighted = createOneSnippet(snippets);
                } catch (InvalidTokenOffsetsException exception) {
                    exception.printStackTrace();
                    highlighted = "(no snippets yet)";
                }

                searchResult.addResult(rdoc);
                searchResult.setSnippet(rdoc, highlighted);
            }

            searchResult.trimResults(searchQuery.fromDoc());
            return searchResult;
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return new SearchResult(searchQuery);
    }

    /**
     * Create one string of all the extracted snippets from the highlighter
     *
     * @param snippets
     * @return
     */
    private String createOneSnippet(String[] snippets) {
        String result = " ... ";
        for (String s : snippets) {
            result += s + " ... ";
        }
        return result;
    }
}
