/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kickupper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import kickupper.index.Indexer;
import kickupper.ngram.NgramUtils;

/**
 * 
 * @author aleyase2-admin
 */
public class OfflineJobs {

	/**
	 * @param args
	 *            the command line arguments
	 */
	static Map<String, Integer> fundedEntities = new HashMap<>();
	static Map<String, Integer> unFundedEntities = new HashMap<>();

	// static final String outputDir = "C:\\Data\\kickstarter\\all.csv";
	static NER ed = new NER();
	static int fundedCount = 0;
	static int unfundedCount = 0;

	public static void main(String[] args) {
		// 1- parse html files and put description in format [id descrption]
		// each line
		// read(Configuration.inputHTMLDir, Configuration.documentOuputFile);
		// 1.5 - extract entities
//		extractEntities(Configuration.testDocsFile, Configuration.generalizedtestDocsFile);
		// 2- extract ngrams from the previous file
//		NgramUtils.extractNgrams(Configuration.generalizedDocFile, Configuration.ngramsOutputFile);
		// 3- aggregate ngrams
		NgramUtils.aggregateNgrams(Configuration.ngramsOutputFile, Configuration.ngramsAggregateFile);
		NgramUtils.getBestWorstNgrams(Configuration.ngramsAggregateFile, Configuration.baseDir + "bestWorst.txt");
		
		// 4-build search engine based on ngrams
		
//		Indexer.index(Configuration.ngramsAggregateFile, Configuration.indexDir);
	}

	public static void read(String dir, String outputFile) {
		FileWriter fw = null;
		try {
			System.out.println("Read all files in directory: " + dir);
			List<KSDocument> docs = new ArrayList<>();
			File folder = new File(dir);
			File[] listOfFiles = folder.listFiles();
			fw = new FileWriter(outputFile, false);
			BufferedWriter bw = new BufferedWriter(fw);
			int count = 0;
			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile()) {
					count++;
					String filepath = listOfFiles[i].getAbsolutePath();
					String filename = listOfFiles[i].getName();
					KSDocument ksdoc = KSDocument.parse(filepath, filename);
					
					// docs.add(ksdoc);
					bw.write(ksdoc.getId() + " " + ksdoc.getFunded() + " " + ksdoc.getDescripton() + "\n");
				} else if (listOfFiles[i].isDirectory()) {
					System.out.println("Directory " + listOfFiles[i].getName());
				}
			}
			bw.close();
			System.out.println("Read " + count + " files from " + dir);
		} catch (IOException ex) {
			Logger.getLogger(OfflineJobs.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			try {
				fw.close();
			} catch (IOException ex) {
				Logger.getLogger(OfflineJobs.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
	
	public static void extractEntities(String inputFile, String outputFile) {
		try {
			
			int countDocs = 0;
			FileWriter fw = new FileWriter(outputFile, false);
			BufferedWriter bw = new BufferedWriter(fw);
			BufferedReader br = new BufferedReader(new FileReader(inputFile));
			String line = null;
			while ((line = br.readLine()) != null) {
				countDocs++;
				System.out.println("doc# "+countDocs);
				String[] splits = line.split("\\s+", 3);
				if (splits.length < 3) {
					System.out.println("less than 3 splits:" + line);
					continue;
				}
				String text = splits[2].trim();
//				System.out.println("original: "+ text);
				String result = ed.detectEntities(text);
				result = result.replaceAll("<ORGANIZATION>((.|\n)*?)</ORGANIZATION>", "ORGCODE");
				result = result.replaceAll("<DATE>((.|\n)*?)</DATE>", "DATECODE");
				result = result.replaceAll("<LOCATION>((.|\n)*?)</LOCATION>", "LOCCODE");
				result = result.replaceAll("<PERSON>((.|\n)*?)</PERSON>", "PERSONCODE");
				result = result.replaceAll("<TIME>((.|\n)*?)</TIME>", "TIMECODE");
				result = result.replaceAll("<MONEY>((.|\n)*?)</MONEY>", "MONEYCODE");
//				System.out.println("gen: "+ result);
				bw.write(splits[0] + " " + splits[1] + " " + result + "\n");
			}
			bw.close();
			br.close();
			System.out.println("entity extraction done. Docs#:" + countDocs);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
