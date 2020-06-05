package project.event;

import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import project.WordChainInfo;

import java.nio.ByteBuffer;

public class GameFinishEvent extends CMEvent {
    // TODO: all result texts will be contained in this one variable
    private String result;

    public GameFinishEvent() {
        this("");
    }

    public GameFinishEvent(String result) {
        this.m_nType = WordChainInfo.EVENT_GAME_FINISH;
        this.result = result;
    }

    public GameFinishEvent(ByteBuffer msg) {
        this();
        this.unmarshall(msg);
    }

    @Override
    protected int getByteNum() {
        int byteNum = super.getByteNum();
        byteNum += CMInfo.STRING_LEN_BYTES_LEN + result.getBytes().length;
        return byteNum;
    }

    protected void unmarshallBody(ByteBuffer msg) {
        this.result = getStringFromByteBuffer(msg);
    }


    protected void marshallBody() {
        putStringToByteBuffer(result);
    }

    public String getResult() {
        return result;
    }
}
