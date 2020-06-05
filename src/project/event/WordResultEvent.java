package project.event;

import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import project.WordChainInfo;

import java.nio.ByteBuffer;

/* Event for player's result */
public class WordResultEvent extends CMEvent {

    private String userName;
    private int resultCode;
    private String word;
    private int scoreChange;
    private int lifeChange;

    public WordResultEvent() {
        this("", 0, "", 0, 0);
    }

    public WordResultEvent(String userName, int code, String word, int scoreChange, int lifeChange) {
        this.m_nType = WordChainInfo.EVENT_RESULT_WORD;
        this.userName = userName;
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
        byteNum += Integer.BYTES * 3;
        byteNum += CMInfo.STRING_LEN_BYTES_LEN + this.userName.getBytes().length;
        byteNum += CMInfo.STRING_LEN_BYTES_LEN + this.word.getBytes().length;
        return byteNum;
    }

    @Override
    protected void unmarshallBody(ByteBuffer msg) {
        this.userName = getStringFromByteBuffer(msg);
        this.resultCode = getInt2BytesFromByteBuffer(msg);
        this.word = getStringFromByteBuffer(msg);
        this.scoreChange = getInt2BytesFromByteBuffer(msg);
        this.lifeChange = getInt2BytesFromByteBuffer(msg);
    }

    @Override
    protected void marshallBody() {
        this.putStringToByteBuffer(this.userName);
        this.putInt2BytesToByteBuffer(this.resultCode);
        this.putStringToByteBuffer(this.word);
        this.putInt2BytesToByteBuffer(this.scoreChange);
        this.putInt2BytesToByteBuffer(this.lifeChange);
    }

    public String getUserName() {
        return userName;
    }

    public int getResultCode() {
        return resultCode;
    }

    public String getWord() {
        return word;
    }

    public int getScoreChange() {
        return scoreChange;
    }

    public int getLifeChange() {
        return lifeChange;
    }

}
