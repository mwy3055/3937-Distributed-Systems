package project.server;

public class WordResult {

    private String word;
    private int rtnValue;
    private int scoreChange;
    private int lifeChange;

    public WordResult(String word, int rtnValue, int scoreChange, int lifeChange) {
        this.word = word;
        this.rtnValue = rtnValue;
        this.scoreChange = scoreChange;
        this.lifeChange = lifeChange;
    }

    public String getWord() {
        return word;
    }

    public int getRtnValue() {
        return rtnValue;
    }

    public int getScoreChange() {
        return scoreChange;
    }

    public int getLifeChange() {
        return lifeChange;
    }
}
