package project.event;

import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import project.WordChainInfo;

import java.nio.ByteBuffer;

public class WordSendingEvent extends CMEvent {

    private String word;
    private String sessionName;
    private String groupName;

    public WordSendingEvent() {
        this("", "", "");
    }

    public WordSendingEvent(String word, String sessionName, String groupName) {
        this.m_nType = WordChainInfo.EVENT_SEND_WORD;
        this.m_nID = WordChainInfo.EVENT_SEND_WORD;
        this.word = word;
        this.sessionName = sessionName;
        this.groupName = groupName;
    }

    public WordSendingEvent(ByteBuffer msg) {
        this();
        this.unmarshall(msg);
    }

    protected int getByteNum() {
        int byteNum = super.getByteNum();
        byteNum += CMInfo.STRING_LEN_BYTES_LEN + this.word.getBytes().length;
        byteNum += CMInfo.STRING_LEN_BYTES_LEN + this.sessionName.getBytes().length;
        byteNum += CMInfo.STRING_LEN_BYTES_LEN + this.groupName.getBytes().length;
        return byteNum;
    }

    @Override
    protected void marshallBody() {
        this.putStringToByteBuffer(this.word);
        this.putStringToByteBuffer(this.sessionName);
        this.putStringToByteBuffer(this.groupName);
    }

    @Override
    protected void unmarshallBody(ByteBuffer msg) {
        this.word = this.getStringFromByteBuffer(msg);
        this.sessionName = this.getStringFromByteBuffer(msg);
        this.groupName = this.getStringFromByteBuffer(msg);
    }

    public String getWord() {
        return this.word;
    }

    public String getSessionName() {
        return sessionName;
    }

    public String getGroupName() {
        return groupName;
    }
}
