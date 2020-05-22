package project.event;

import kr.ac.konkuk.ccslab.cm.event.CMEvent;

import java.nio.ByteBuffer;

public class GameFinishEvent extends CMEvent {

    protected void unmarshallBody(ByteBuffer msg){};

    protected void marshallBody(){};
}
