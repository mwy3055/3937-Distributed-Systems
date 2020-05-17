package project.event;

import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import project.WordChainInfo;

import java.nio.ByteBuffer;

/* Event for player's result */
/* TODO: make event for other player's result: */
public class WordResultEvent extends CMEvent {

    private int resultCode;
    private String word;
    private int scoreChange;

    public WordResultEvent() {
        this(0, "", 0);
    }

    public WordResultEvent(int code, String word, int scoreChange) {
        this.m_nType = WordChainInfo.EVENT_RESULT_WORD;
        this.resultCode = code;
        this.word = word;
        this.scoreChange = scoreChange;
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
        byteNum += 4 * 2;
        byteNum += CMInfo.STRING_LEN_BYTES_LEN + this.word.getBytes().length;
        return byteNum;
    }

    @Override
    protected void unmarshallBody(ByteBuffer msg) {
        this.resultCode = getInt2BytesFromByteBuffer(msg);
        this.word = getStringFromByteBuffer(msg);
        this.scoreChange = getInt2BytesFromByteBuffer(msg);
    }

    @Override
    protected void marshallBody() {
        this.putInt2BytesToByteBuffer(this.resultCode);
        this.putStringToByteBuffer(this.word);
        this.putInt2BytesToByteBuffer(this.scoreChange);
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
}
