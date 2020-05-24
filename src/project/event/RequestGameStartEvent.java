package project.event;

import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import project.WordChainInfo;

import java.nio.ByteBuffer;

public class RequestGameStartEvent extends CMEvent {

    private String sessionName;
    private String groupName;

    public RequestGameStartEvent() {
        this("", "");
    }

    public RequestGameStartEvent(String sessionName, String groupName) {
        this.m_nType = WordChainInfo.EVENT_GAME_START;
        this.sessionName = sessionName;
        this.groupName = groupName;
    }

    public RequestGameStartEvent(ByteBuffer msg) {
        this();
        this.unmarshall(msg);
    }

    @Override
    protected int getByteNum() {
        int byteNum = super.getByteNum();
        byteNum += CMInfo.STRING_LEN_BYTES_LEN + this.sessionName.getBytes().length;
        byteNum += CMInfo.STRING_LEN_BYTES_LEN + this.groupName.getBytes().length;
        return byteNum;
    }

    @Override
    protected void marshallBody() {
        this.putStringToByteBuffer(sessionName);
        this.putStringToByteBuffer(groupName);
    }

    @Override
    protected void unmarshallBody(ByteBuffer msg) {
        this.sessionName = this.getStringFromByteBuffer(msg);
        this.groupName = this.getStringFromByteBuffer(msg);
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
