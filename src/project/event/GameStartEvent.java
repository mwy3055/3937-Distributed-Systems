package project.event;

import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import project.WordChainInfo;

import java.nio.ByteBuffer;

public class GameStartEvent extends CMEvent {

    private String sessionName;
    private String groupName;
    private int order;

    public GameStartEvent() {
        this("", "", 0);
    }

    public GameStartEvent(String sessionName, String groupName, int order) {
        this.m_nType = WordChainInfo.EVENT_GAME_START;
        this.sessionName = sessionName;
        this.groupName = groupName;
        this.order = order;
    }

    public GameStartEvent(ByteBuffer msg) {
        this();
        this.unmarshall(msg);
    }

    @Override
    public CMEvent unmarshall(ByteBuffer msg) {
        msg.clear();
        this.unmarshallHeader(msg);
        this.unmarshallBody(msg);
        return this;
    }

    @Override
    protected int getByteNum() {
        int byteNum = super.getByteNum();
        byteNum += CMInfo.STRING_LEN_BYTES_LEN + this.sessionName.getBytes().length;
        byteNum += CMInfo.STRING_LEN_BYTES_LEN + this.groupName.getBytes().length;
        byteNum += 4;
        return byteNum;
    }

    @Override
    protected void marshallBody() {
        this.putStringToByteBuffer(sessionName);
        this.putStringToByteBuffer(groupName);
        this.putInt2BytesToByteBuffer(order);
    }

    @Override
    protected void unmarshallBody(ByteBuffer msg) {
        this.sessionName = getStringFromByteBuffer(msg);
        this.groupName = getStringFromByteBuffer(msg);
        this.order = getInt2BytesFromByteBuffer(msg);
    }

    public String getSessionName() {
        return sessionName;
    }

    public String getGroupName() {
        return groupName;
    }

    public int getOrder() {
        return order;
    }
}
