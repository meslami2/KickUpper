/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kickupper;

/**
 *
 * @author aleyase2-admin
 */
public class Configuration {

	public static final int FREQ_FILTER = 4;
	
	public static final double CUTOFF_THRESHOLD = 0.25;

	public static final int KNN_THRESHOLD = 4;

	
	//for index creation
	public static final int MIN_NGRAMS = 4;
    public static final int MAX_NGRAMS = 4;
    
    //for query time
	public static final int MIN_NGRAM_Q = 4;
	public static final int MAX_NGRAM_Q = 4;
	
	//max number of search result
    public final static int defaultNumResults = 15;
    
    
    //for coloring
	public static double max_score = 1;
	public static double min_score = -1;
	
    public static final String baseDir = "C:\\Users\\Aale\\workspace\\KickUpper\\data\\";
    public static final String expr = "top40kEN-"+MIN_NGRAMS+"-"+MAX_NGRAMS;
    public static final String inputHTMLDir = baseDir + "large";
//	public static final String documentOuputFile = baseDir + expr+"-parsed.txt";
	public static final String documentOuputFile = baseDir + "docs_top40k.txt";
	public static final String generalizedDocFile = baseDir + "gen_docs_top40k.txt";
    public static final String ngramsOutputFile = baseDir + expr+"-ngrams.txt";
	public static final String ngramsAggregateFile = baseDir + expr + "-ngrams-aggr.txt";
	public static final String indexDir = baseDir + expr + "-index";
//	public static final String testDocsFile = documentOuputFile;
	public static final String testDocsFile = baseDir + "testDocs_tail5972.txt";
	public static final String generalizedtestDocsFile = baseDir + "gen_testDocs_tail5972.txt";
	public static final String evaluationResult = baseDir + expr + "-eval.txt";


//    public static final String baseDir = "/home/aleyase2/kickstarter/data/";
//    public static final String expr = "ng4f";
//    
//    public static final String ngramsOutputFile = baseDir + "ngrams_"+expr+".txt";
//    public static final String inputHTMLDir = baseDir + "vlarge";
//    public static final String ngramsAggregateFile = baseDir + "ngrams_aggregate_"+expr+".txt";
//    public static final String indexDir = baseDir + "index_"+expr;
//    public static final String documentOuputFile = baseDir + "docs_top40k.txt";
//    public static final String testDocsFile = baseDir + "testDocs_tail5972.txt";
//    public static final String evaluationResult = baseDir + "evals_"+expr+".txt";

}
