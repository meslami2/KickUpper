/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kickupper.ngram;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import kickupper.Configuration;

/**
 *
 * @author aleyase2-admin
 */
public class NgramUtils {

    

    public static List<String> ngrams(int n, String[] sentences) {
        List<String> ngrams = new ArrayList<String>();
        for (int i = 0; i < sentences.length; i++) {
            String trimmed = sentences[i].replaceAll("\\s+", " ").trim();
            trimmed = trimmed.replaceAll("[^A-Za-z0-9 ]", "");
            trimmed = trimmed.toLowerCase();
            String[] words = trimmed.split("\\s+");
            for (int j = 0; j < words.length - n + 1; j++) {
                final String ngram = concat(words, j, j + n);
                if (ngram != null) {
                    ngrams.add(ngram);
                }
            }
        }
        return ngrams;
    }
    
    
    public static List<NgramEntity> ngramsEntities(int n, String[] sentences) {
        List<NgramEntity> ngrams = new ArrayList<>();
        for (int i = 0; i < sentences.length; i++) {
            String trimmed = sentences[i].replaceAll("\\s+", " ").trim();
            trimmed = trimmed.replaceAll("[^A-Za-z0-9 ]", "");
            trimmed = trimmed.toLowerCase();
            String[] words = trimmed.split("\\s+");
            for (int j = 0; j < words.length - n + 1; j++) {
                final String ngram = concat(words, j, j + n);
                if (ngram != null) {
                	NgramEntity ne = new NgramEntity();
                	ne.content = ngram;
                	ne.sentenceIndex = i;
                	ne.startIndex = j;
                	ne.length = n;
                    ngrams.add(ne);
                }
            }
        }
        return ngrams;
    }

    public static String concat(String[] words, int start, int end) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < end; i++) {
            final String trimmed = words[i].replaceAll("\u00A0", "").trim();
            if (trimmed.length() == 0 || trimmed.equals("") || trimmed.length() > 32) {
                return null;
            }
            sb.append(i > start ? " " : "").append(trimmed);
        }
        return sb.toString();
    }

    public static List<String> getNgrams(String text, int start, int end) {
        List<String> ngrams = new ArrayList<>();
        String[] sentences = text.split("\\.");
        for (int i = start; i <= end; i++) {
            ngrams.addAll(ngrams(i, sentences));
        }
        return ngrams;
    }
    
    public static List<NgramEntity> getNgramEntities(String text, int start, int end) {
        List<NgramEntity> ngrams = new ArrayList<>();
        String[] sentences = text.split("\\.");
        for (int i = start; i <= end; i++) {
            ngrams.addAll(ngramsEntities(i, sentences));
        }
        return ngrams;
    }

    public static void main(String[] args) {
        String text = "This is in Javascript. I wasn't paying attention either";
        final List<String> ngrams = getNgrams(text, 3, 5);
        for (String str : ngrams) {
            System.out.println(str);
        }
        System.out.println("count=" + ngrams.size());
    }

    public static void extractNgrams(String inputFile, String outputFile) {
        System.out.println("Starting extract Ngrams from: " + inputFile + " to: " + outputFile);
        try {
            int countDocs = 0;
            int countNgrams = 0;
            FileWriter fw = new FileWriter(outputFile, false);
            BufferedWriter bw = new BufferedWriter(fw);
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            String line = null;
            while ((line = br.readLine()) != null) {
                countDocs++;
                String[] splits = line.split("\\s+", 3);
                if (splits.length < 3) {
                    System.out.println("less than 3 splits:" + line);
                    continue;
                }
                List<String> ngrams = getNgrams(splits[2].trim(), Configuration.MIN_NGRAMS, Configuration.MAX_NGRAMS);
                for (String ngram : ngrams) {
                    countNgrams++;
                    bw.write(splits[1] + " " + ngram + "\n");
                }
            }
            bw.close();
            br.close();
            System.out.println("extract ngrams done. Docs#:" + countDocs + " Ngrams#:" + countNgrams);
        } catch (IOException ex) {
            Logger.getLogger(NgramUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void aggregateNgrams(String inputFile, String outputFile) {
        System.out.println("Starting Ngrams aggregation from: " + inputFile + " to: " + outputFile);
        try {
            int countNgrams = 0;
            int pos_countAggregate = 0;
            int neg_countAggregate = 0;

            Map<String, Integer> pos_map = new HashMap<>();
            Map<String, Integer> neg_map = new HashMap<>();
            FileWriter fw = new FileWriter(outputFile, false);
            BufferedWriter bw = new BufferedWriter(fw);
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            String line = null;
            while ((line = br.readLine()) != null) {
                countNgrams++;
                String[] splits = line.split("\\s+", 2);
                String ngram = splits[1];
                if (splits[0].equals("1")) {
                    Integer freq = pos_map.get(ngram);
                    if (freq == null) {
                        pos_countAggregate++;
                        pos_map.put(ngram, 1);
                    } else {
                        pos_map.put(ngram, freq + 1);
                    }
                } else if (splits[0].equals("0")) {
                    Integer freq = neg_map.get(ngram);
                    if (freq == null) {
                        neg_countAggregate++;
                        neg_map.put(ngram, 1);
                    } else {
                        neg_map.put(ngram, freq + 1);
                    }
                } else {
                    System.out.println("Invalid Funded/Notfunded value:" + splits[0]);
                }
            }
            int idCount = 0;
            for (String ngram : pos_map.keySet()) {
                idCount++;
                Integer neg_freq = neg_map.get(ngram);
                if (neg_freq == null) {
                    neg_freq = 0;
                }
                bw.write(idCount + " " + pos_map.get(ngram) + " " + neg_freq + " " + ngram + "\n");
            }
            for (String ngram : neg_map.keySet()) {
                idCount++;
                Integer pos_freq = pos_map.get(ngram);
                if (pos_freq == null) {
                    bw.write(idCount + " " + 0 + " " + neg_map.get(ngram) + " " + ngram + "\n");
                }
            }
            System.out.println("extract ngrams done. Ngrams#:" + countNgrams + " Pos-Ngrams#:" + pos_countAggregate
                    + " Neg-Ngrams#:" + neg_countAggregate);
            bw.close();
            br.close();
        } catch (IOException ex) {
            Logger.getLogger(NgramUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


	public static void getBestWorstNgrams(String inputFile, String outputFile) {
		// TODO Auto-generated method stub
	}
}
