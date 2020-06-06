package project.server;

public class WordResult {

    private String word;
    private int rtnValue;
    private int lifeChange;

    public WordResult(String word, int rtnValue, int lifeChange) {
        this.word = word;
        this.rtnValue = rtnValue;
        this.lifeChange = lifeChange;
    }

    public String getWord() {
        return word;
    }

    public int getRtnValue() {
        return rtnValue;
    }

    public int getLifeChange() {
        return lifeChange;
    }
}
