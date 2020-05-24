package project.event;

import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import project.WordChainInfo;

import java.nio.ByteBuffer;

public class NotifyAdminEvent extends CMEvent {

    public NotifyAdminEvent() {
        this.m_nType= WordChainInfo.EVENT_NOTIFY_ADMIN;
    }

    public NotifyAdminEvent(ByteBuffer msg) {
        this();
        this.unmarshall(msg);
    }

    @Override
    protected void marshallBody() {

    }

    @Override
    protected void unmarshallBody(ByteBuffer msg) {

    }
}
