package kickupper.search;

public class ResultDoc {

    private int _id;
    private int _freq_negative = 0;
    private int _freq_positive = 0;

    private float _score = 0;
    private String _content = "[no content]";

    public ResultDoc(int id) {
        _id = id;
    }

    public int id() {
        return _id;
    }

    public ResultDoc freqNeg(int freqNeg) {
        _freq_negative = freqNeg;
        return this;
    }

    public int freqNeg() {
        return _freq_negative;
    }

    public ResultDoc freqPos(int freqPos) {
        _freq_positive = freqPos;
        return this;
    }

    public int freqPos() {
        return _freq_positive;
    }

    public String content() {
        return _content;
    }

    public ResultDoc content(String nContent) {
        _content = nContent;
        return this;
    }

    public ResultDoc score(float score) {
        _score = score;
        return this;
    }

    public float score() {
        return _score;
    }

}
