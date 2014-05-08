/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kickupper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import kickupper.ngram.NgramEntity;
import kickupper.ngram.NgramUtils;

/**
 * 
 * @author Aale
 */
public class Evaluation {

	static String[] colors = { "#FF0000", "#FF1100", "#FF2300", "#FF3400", "#FF4600", "#FF5700", "#FF6900", "#FF7B00", "#FF8C00",
			"#FF9E00", "#FFAF00", "#FFC100", "#FFD300", "#FFE400", "#FFF600", "#F7FF00", "#E5FF00", "#D4FF00", "#C2FF00", "#B0FF00",
			"#9FFF00", "#8DFF00", "#7CFF00", "#6AFF00", "#58FF00", "#47FF00", "#35FF00", "#24FF00", "#12FF00", "#00FF00" };

	public static String getColor(double score) {
		if (score > Configuration.max_score) {
			score = Configuration.max_score;
		}
		if (score < Configuration.min_score) {
			score = Configuration.min_score;
		}
		double norm = (score - Configuration.min_score) / (Configuration.max_score - Configuration.min_score);
		int index = (int) Math.floor(norm * colors.length);
		if (index > colors.length - 1) {
			index = colors.length - 1;
		}
		return colors[index];
	}

	public static double evaluate(String doc) {
		List<String> ngrams = NgramUtils.getNgrams(doc, Configuration.MIN_NGRAM_Q, Configuration.MAX_NGRAM_Q);
		double score = 0;
		double count = 0;
		double curScore = 0;
		double ignoreCount = 0;
		for (String ng : ngrams) {
			curScore = KNN.getScore(ng);
			if (Math.abs(curScore) > 0.000001) {
				score += curScore;
				count++;
			} else {
				ignoreCount++;
			}
		}
		System.out.println("ngrams#: " + ngrams.size() + " valid#: " + count + " score/count: " + (score / count) + " ignore#: "
				+ ignoreCount);
		return score / count;
	}

	public static String evaluateForGUI(String doc) {
		doc = doc.replaceAll("\u00A0", "");
		doc = doc.replaceAll("\\s+", " ");
		String result = "";
		List<ArrayList<String>> words = new ArrayList<ArrayList<String>>();
		List<ArrayList<Double>> scores = new ArrayList<ArrayList<Double>>();
		List<ArrayList<Integer>> counts = new ArrayList<ArrayList<Integer>>();

		String[] sentences = doc.split("\\.");
		for (int i = 0; i < sentences.length; i++) {
			words.add(i, new ArrayList<String>());
			scores.add(i, new ArrayList<Double>());
			counts.add(i, new ArrayList<Integer>());
			String[] w = sentences[i].split("\\s+");
			for (int j = 0; j < w.length; j++) {
				words.get(i).add(j, w[j]);
				scores.get(i).add(j, 0.0);
				counts.get(i).add(j, 0);
			}
		}
		List<NgramEntity> ngrams = NgramUtils.getNgramEntities(doc, Configuration.MIN_NGRAM_Q, Configuration.MAX_NGRAM_Q);
		double score = 0;
		for (NgramEntity ng : ngrams) {
			score = KNN.getScore(ng.content);
			for (int k = 0; k < ng.length; k++) {
				Double preScore = scores.get(ng.sentenceIndex).get(ng.startIndex + k);
				if (preScore != null) {
					scores.get(ng.sentenceIndex).set(ng.startIndex + k, preScore + score);
					Integer preCount = counts.get(ng.sentenceIndex).get(ng.startIndex + k);
					counts.get(ng.sentenceIndex).set(ng.startIndex + k, preCount + 1);
				} else {
					System.out.println("preScore is null for:" + ng);
				}
			}
		}
		String color = "";
		int max_line_length = 100;
		int cur_line_lenght = 0;
		result = "<div style=\"display:block;align:justify;\">";
		for (int i = 0; i < words.size(); i++) {
			for (int j = 0; j < words.get(i).size(); j++) {
				Double sc = scores.get(i).get(j) / counts.get(i).get(j);
				color = getColor(sc);
				String w = words.get(i).get(j);
				result += "<div style=\"display:inline;background-color:" + color + "\">" + w + " () " + scores.get(i).get(j) + "()"
						+ counts.get(i).get(j) + "&nbsp;</div>";
				cur_line_lenght += w.length() + 1;
				if (cur_line_lenght > max_line_length) {
					result += "<br>";
					cur_line_lenght = 0;
				}
				// result += words.get(i).get(j) + "<" + sc + ">" + " ";

			}
			result += "<div style=\"display:inline;background-color:" + color + "\">.&nbsp;</div>";
		}
		result += "</div>";
		System.out.println("ngrams#" + ngrams.size());
		// System.out.println("result=" + result);
		return result;
	}

	public static void evaluateDocs(String inputFile, String outputFile) {
		System.out.println("Evaluate docs in : " + inputFile + " outputs: " + outputFile);
		try {

			int countDocs = 0;
			int countNgrams = 0;
			FileWriter fw = new FileWriter(outputFile, false);
			BufferedWriter bw = new BufferedWriter(fw);
			BufferedReader br = new BufferedReader(new FileReader(inputFile));
			String line = null;
			while ((line = br.readLine()) != null) {

				countDocs++;
				// if (countDocs < 500) {
				// continue;
				// }

				System.out.println("Doc:" + countDocs);
				// if (countDocs == 1000) {
				// break;
				// }
				String[] splits = line.split("\\s+", 3);
				String id = splits[0];
				String state = splits[1];
				String content = splits[2];
				System.out.println("state:" + state);
				double score = evaluate(content);
				System.out.println();
				bw.write(id + " " + state + " " + score + "\n");
			}
			bw.close();
			br.close();
			System.out.println("extract ngrams done. Docs#:" + countDocs + " Ngrams#:" + countNgrams);
		} catch (IOException ex) {
			Logger.getLogger(Evaluation.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public static void calcAccuracy(String evalFile, Double cutoff) {
		Double cf = Configuration.CUTOFF_THRESHOLD;
		if (cutoff != null) {
			cf = cutoff;
		}
		try {

			int countDocs = 0;
			double pre_surat = 0.0;
			double pre_makhraj = 0.0;
			double recall_makhraj = 0.0;
			BufferedReader br = new BufferedReader(new FileReader(evalFile));
			String line = null;
			while ((line = br.readLine()) != null) {
				countDocs++;
				String[] splits = line.split("\\s+");
				// String id = splits[0];
				int state = Integer.parseInt(splits[1]);
				double score = Double.parseDouble(splits[2]);
				int norm_score = score >= cf ? 1 : 0;
				if (norm_score == 1 && state == 1) {
					pre_surat++;
				}
				if (norm_score == 1) {
					pre_makhraj++;
				}
				if (state == 1) {
					recall_makhraj++;
				}

			}
			br.close();
			double precision = pre_surat / pre_makhraj;
			double recall = pre_surat / recall_makhraj;
			double fmeasure = 2 * precision * recall / (precision + recall);
//			System.out.println("Docs#:" + countDocs + " \tPre:" + precision + " \tRec: " + recall + " \tF-measure: " + fmeasure + "  \tCutoff:" + cf);
			System.out.println( countDocs + "\t" + precision + "\t" + recall + " \t" + fmeasure + "  \t" + cf);
			
		} catch (IOException ex) {
			Logger.getLogger(Evaluation.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	public static void main(String[] args) {
		// String str_pos =
		// "For the last few years my art has been developing around a concept I call “Moments of Insight”.";
		// String str_neg =
		// "and print on demand comic book company, August Comics. August Comics' 1st comic book will be a  4-issue, black and white, 20 pages per issue, Filipino-centric martial arts action comic";
		// System.out.println("pos=" + evaluate(str_pos));
		// System.out.println("neg=" + evaluate(str_neg));
		// evaluateDocs(Configuration.generalizedtestDocsFile,
		// Configuration.evaluationResult);
		String evalFile = Configuration.baseDir + "top40k-3-3-eval-first500.txt";
		for (double d = -0.9; d < 0.9; d += 0.05) {
			calcAccuracy(evalFile, d);
		}
		// String doc =
		// "for years. I am so excited this project is happening! Recording is pretty much done, next step is production. I need a bit more in funds to push this thing over the edge and launch my original";
		// doc = "this is a problem that you like";
		// evaluateForGUI(doc);
	}
}
