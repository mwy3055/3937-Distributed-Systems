package project.event;

import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;

import java.nio.ByteBuffer;

public class WordResultEvent extends CMEvent {



    private int resultCode;
    private String word;
    private int scoreChange;
    private int lifeChange;

    public WordResultEvent() {
        this(0, "", 0, 0);
    }

    public WordResultEvent(int code, String word, int scoreChange, int lifeChange) {
        this.resultCode = code;
        this.word = word;
        this.scoreChange = scoreChange;
        this.lifeChange = lifeChange;
    }

    public WordResultEvent(ByteBuffer msg) {
        this();
        this.unmarshall(msg);
    }

    public WordResultEvent unmarshall(ByteBuffer msg) {
        msg.clear();
        this.unmarshallHeader(msg);
        this.unmarshallBody(msg);
        return this;
    }

    @Override
    protected int getByteNum() {
        int byteNum = super.getByteNum();
        byteNum += 4 * 3;
        byteNum += CMInfo.STRING_LEN_BYTES_LEN + this.word.getBytes().length;
        return byteNum;
    }

    @Override
    protected void unmarshallBody(ByteBuffer msg) {
        this.resultCode = getInt2BytesFromByteBuffer(msg);
        this.word = getStringFromByteBuffer(msg);
        this.scoreChange = getInt2BytesFromByteBuffer(msg);
        this.lifeChange = getInt2BytesFromByteBuffer(msg);
    }

    @Override
    protected void marshallBody() {
        this.putInt2BytesToByteBuffer(this.resultCode);
        this.putStringToByteBuffer(this.word);
        this.putInt2BytesToByteBuffer(this.scoreChange);
        this.putInt2BytesToByteBuffer(this.lifeChange);
    }

    public static int getResultCorrect() {
        return RESULT_CORRECT;
    }

    public static void setResultCorrect(int resultCorrect) {
        RESULT_CORRECT = resultCorrect;
    }

    public static int getResultNotnoun() {
        return RESULT_NOTNOUN;
    }

    public static void setResultNotnoun(int resultNotnoun) {
        RESULT_NOTNOUN = resultNotnoun;
    }

    public static int getResultDuplication() {
        return RESULT_DUPLICATION;
    }

    public static void setResultDuplication(int resultDuplication) {
        RESULT_DUPLICATION = resultDuplication;
    }

    public static int getResultTimeout() {
        return RESULT_TIMEOUT;
    }

    public static void setResultTimeout(int resultTimeout) {
        RESULT_TIMEOUT = resultTimeout;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getScoreChange() {
        return scoreChange;
    }

    public void setScoreChange(int scoreChange) {
        this.scoreChange = scoreChange;
    }

    public int getLifeChange() {
        return lifeChange;
    }

    public void setLifeChange(int lifeChange) {
        this.lifeChange = lifeChange;
    }
}
