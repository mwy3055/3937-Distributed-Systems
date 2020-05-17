package project.event;

import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import project.WordChainInfo;

import java.nio.ByteBuffer;

public class WordSendingEvent extends CMEvent {

    private String word;

    public WordSendingEvent() {
        this.m_nType = WordChainInfo.EVENT_SEND_WORD;
        this.word = "";
    }

    public WordSendingEvent(ByteBuffer msg) {
        this();
        this.unmarshall(msg);
    }

    public void setWord(String word) {
        if (word != null) {
            this.word = word;
        }
    }

    protected int getByteNum() {
        int byteNum = super.getByteNum();
        byteNum += CMInfo.STRING_LEN_BYTES_LEN + this.word.getBytes().length;
        return byteNum;
    }

    public String getWord() {
        return this.word;
    }

    @Override
    protected void marshallBody() {
        this.putStringToByteBuffer(this.word);
    }

    @Override
    protected void unmarshallBody(ByteBuffer msg) {
        this.word = this.getStringFromByteBuffer(msg);
    }
}
