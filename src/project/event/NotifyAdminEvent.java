package project.event;

import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import project.WordChainInfo;

import java.nio.ByteBuffer;

public class NotifyAdminEvent extends CMEvent {

    private int isAdmin;

    public NotifyAdminEvent() {
        this(0);
    }

    public NotifyAdminEvent(int isAdmin) {
        this.m_nType = WordChainInfo.EVENT_NOTIFY_ADMIN;
        this.isAdmin = isAdmin;
    }

    public NotifyAdminEvent(ByteBuffer msg) {
        this();
        this.unmarshall(msg);
    }

    @Override
    protected int getByteNum() {
        int byteNum = super.getByteNum();
        byteNum += Integer.BYTES;
        return byteNum;
    }

    @Override
    protected void marshallBody() {
        putInt2BytesToByteBuffer(isAdmin);
    }

    @Override
    protected void unmarshallBody(ByteBuffer msg) {
        isAdmin = getInt2BytesFromByteBuffer(msg);
    }

    public int isAdmin() {
        return isAdmin;
    }
}
