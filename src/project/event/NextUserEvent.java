package project.event;

import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import project.WordChainInfo;

import java.nio.ByteBuffer;

public class NextUserEvent extends CMEvent {

    private String userName;

    public NextUserEvent() {
        this("");
    }

    public NextUserEvent(ByteBuffer msg) {
        this();
        this.unmarshall(msg);
    }

    public NextUserEvent(String userName) {
        this.m_nType = WordChainInfo.EVENT_NEXT_USER;
        this.userName = userName;
    }

    // byteNum should be increased when member variables are added
    protected int getByteNum() {
        int byteNum = super.getByteNum();
        byteNum += CMInfo.STRING_LEN_BYTES_LEN + this.userName.getBytes().length;
        return byteNum;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    protected void marshallBody() {
        this.putStringToByteBuffer(this.userName);
    }

    @Override
    protected void unmarshallBody(ByteBuffer msg) {
        this.userName = this.getStringFromByteBuffer(msg);
    }
}
