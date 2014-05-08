/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kickupper;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;

/**
 * 
 * @author aleyase2-admin
 */
public class NER {

	private static CRFClassifier classifier3;
	private static CRFClassifier classifier7;
	private static CRFClassifier classifier4;

	static {
		classifier3 = CRFClassifier.getClassifierNoExceptions("edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz");
		classifier7 = CRFClassifier.getClassifierNoExceptions("edu/stanford/nlp/models/ner/english.muc.7class.distsim.crf.ser.gz");
		classifier4 = CRFClassifier.getClassifierNoExceptions("edu/stanford/nlp/models/ner/english.conll.4class.distsim.crf.ser.gz");
	}

	public String detectEntities(String text) {

		try {
			text = text.replaceAll("\\p{Cc}", " ");
			text = text.replaceAll("[^A-Za-z0-9 :;!\\?\\.,\'\"]", " ");
			return myEntityRecognition(classifier7, text);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String myEntityRecognition(AbstractSequenceClassifier<?> classifier, String inText) {
		String htmlString = classifier.classifyToString(inText, "inlineXML", true);
		return htmlString;
	}
}
