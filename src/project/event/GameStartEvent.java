package project.event;

import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import project.WordChainInfo;

import java.nio.ByteBuffer;

public class GameStartEvent extends CMEvent {

    private int start;
    private String sessionName;
    private String groupName;
    private int order;

    public GameStartEvent() {
        this(0, "", "", 0);
    }

    public GameStartEvent(int start, String sessionName, String groupName, int order) {
        this.m_nType = WordChainInfo.EVENT_GAME_START;
        this.m_nID = WordChainInfo.EVENT_GAME_START;
        this.start = start;
        this.sessionName = sessionName;
        this.groupName = groupName;
        this.order = order;
    }

    public GameStartEvent(ByteBuffer msg) {
        this();
        this.unmarshall(msg);
    }

    @Override
    protected int getByteNum() {
        int byteNum = super.getByteNum();
        byteNum += CMInfo.STRING_LEN_BYTES_LEN + this.sessionName.getBytes().length;
        byteNum += CMInfo.STRING_LEN_BYTES_LEN + this.groupName.getBytes().length;
        byteNum += Integer.BYTES * 2;
        return byteNum;
    }

    @Override
    protected void marshallBody() {
        this.putInt2BytesToByteBuffer(this.start);
        this.putStringToByteBuffer(this.sessionName);
        this.putStringToByteBuffer(this.groupName);
        this.putInt2BytesToByteBuffer(this.order);
    }

    @Override
    protected void unmarshallBody(ByteBuffer msg) {
        this.start = this.getInt2BytesFromByteBuffer(msg);
        this.sessionName = this.getStringFromByteBuffer(msg);
        this.groupName = this.getStringFromByteBuffer(msg);
        this.order = this.getInt2BytesFromByteBuffer(msg);
    }

    public int getStart() {
        return start;
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
