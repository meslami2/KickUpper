package kickupper.ngram;

public class NgramEntity {
	public String content;
	public int sentenceIndex;
	public int startIndex;
	public int length;
	@Override
	public String toString() {
		return "NgramEntity [content=" + content + ", sentenceIndex=" + sentenceIndex + ", startIndex=" + startIndex + ", length=" + length
				+ "]";
	}
	
	
}
