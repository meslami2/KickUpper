/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kickupper;

import java.util.ArrayList;
import kickupper.search.ResultDoc;
import kickupper.search.SearchResult;
import kickupper.search.Searcher;

/**
 * 
 * @author aleyase2-admin
 */
public class KNN {

	static Searcher searcher = new Searcher(Configuration.indexDir);

	public static double getScore(String query) {
		query = query.replaceAll("[^A-Za-z0-9 ]", "");
		 query = "\"" + query + "\"";
		// System.out.println("query=" + query);
		SearchResult result = searcher.search(query);
		ArrayList<ResultDoc> results = result.getDocs();
		if (results.size() == 0) {
//			System.out.println("no result");
			return 0;
		}
		double score = 0;
		int rank = 1;
		// for (ResultDoc rdoc : results) {
		// int diff = rdoc.freqPos() - rdoc.freqNeg();
		// score += (diff / Math.log(rank + 1));
		// rank++;
		// }
		// return score / results.size();
		int neg_vote = 0;
		int pos_vote = 0;
		int threshold = Configuration.KNN_THRESHOLD;
		for (ResultDoc rdoc : results) {
			if (rdoc.freqPos() - rdoc.freqNeg() > threshold) {
				pos_vote++;
			} else if (rdoc.freqNeg() - rdoc.freqPos() > threshold) {
				neg_vote++;
			}
			rank++;
		}
//		System.out.println("posV: "+ pos_vote + " negV: "+ neg_vote);
		if (pos_vote - neg_vote > 0){ //before was 3
//			System.out.println("p "+(pos_vote - neg_vote));
			return (pos_vote - neg_vote)/(pos_vote + neg_vote);
		}
		else if (pos_vote - neg_vote < 0){
//			System.out.println("n "+(pos_vote - neg_vote));
			return (pos_vote - neg_vote)/(pos_vote + neg_vote);
		}
		else
			return 0;
	}

	public static void main(String[] args) {
		// System.out.println(getScore("be able to continue the"));
		System.out.println(getScore("We are so excited to"));
	}

}
