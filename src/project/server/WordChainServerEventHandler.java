package project.server;

import kr.ac.konkuk.ccslab.cm.entity.CMGroup;
import kr.ac.konkuk.ccslab.cm.entity.CMUser;
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.event.CMInterestEvent;
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent;
import kr.ac.konkuk.ccslab.cm.event.handler.CMAppEventHandler;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.stub.CMServerStub;
import project.WordChainInfo;
import project.event.GameStartEvent;
import project.event.RequestGameStartEvent;

public class WordChainServerEventHandler implements CMAppEventHandler {
    private WordChainServer m_server;
    private CMServerStub m_serverStub;

    public WordChainServerEventHandler(CMServerStub serverStub, WordChainServer server) {
        m_server = server;
        m_serverStub = serverStub;
    }

    @Override
    public void processEvent(CMEvent cme) {
        // TODO Auto-generated method stub
        System.out.println("Event arrived: " + cme.getType());
        switch (cme.getType()) {
            case CMInfo.CM_SESSION_EVENT:
                processSessionEvent(cme);
                break;
            case CMInfo.CM_INTEREST_EVENT:
                processInterestEvent(cme);
                break;
            case CMInfo.CM_DUMMY_EVENT:
                processDummyEvent(cme);
                break;
            case WordChainInfo.EVENT_REQUEST_GAME_START:
                processGameStartEvent(cme);
                break;
            default:
                return;
        }
    }

    private void processGameStartEvent(CMEvent cme) {
        RequestGameStartEvent event = (RequestGameStartEvent) cme;
        String sessionName = event.getSessionName();
        String groupName = event.getGroupName();
        CMGroup group = m_server.getGroup(sessionName, groupName);
        if (group.getGroupUsers().getMemberNum() >= 2) {
            m_server.startGame(sessionName, groupName);
        } else {
            printMessage(String.format("Reject game start of session [%s], group [%s].\n", sessionName, groupName));
            printMessage(String.format("Sender: %s\n", event.getSender()));
            GameStartEvent gameStartEvent = new GameStartEvent(0, sessionName, groupName, 0);
            System.out.println(m_serverStub.send(gameStartEvent, event.getSender()));
        }
    }

    private void processSessionEvent(CMEvent cme) {
        CMSessionEvent se = (CMSessionEvent) cme;
        switch (se.getID()) {
            case CMSessionEvent.LOGIN:
                printMessage("[" + se.getUserName() + "] requests login.\n");
                break;
            case CMSessionEvent.LOGOUT:
                printMessage("[" + se.getUserName() + "] logs out.\n");
                break;
            case CMSessionEvent.JOIN_SESSION:
                printMessage("[" + se.getUserName() + "] requests to join session(" + se.getSessionName() + ").\n");
                break;
            case CMSessionEvent.LEAVE_SESSION:
                printMessage("[" + se.getUserName() + "] leaves a session(" + se.getSessionName() + ").\n");
                CMGroup group = m_server.getGroup(se.getSessionName(), se.getCurrentGroupName());
                CMUser admin = group.getGroupAdmin();
                if (admin == null || (admin.getName().equals(se.getUserName()))) {
                    m_server.setGroupAdmin(se.getSessionName(), se.getCurrentGroupName());
                }
                break;
            default:
                return;
        }
    }

    private void processInterestEvent(CMEvent cme) {
        CMInterestEvent ie = (CMInterestEvent) cme;
        switch (ie.getID()) {
            case CMInterestEvent.USER_ENTER:
                printMessage("[" + ie.getUserName() + "] enters group(" + ie.getCurrentGroup() + ") in session("
                        + ie.getHandlerSession() + ").\n");
                CMGroup group = m_server.getGroup(ie.getHandlerSession(), ie.getCurrentGroup());
                if (group.getGroupUsers().getMemberNum() == 1) {
                    m_server.setGroupAdmin(ie.getHandlerSession(), ie.getCurrentGroup(), ie.getUserName());
                } else {
                    m_server.sendAdminEvent(ie.getUserName(), 0);
                }
                break;
            case CMInterestEvent.USER_LEAVE:
                printMessage("[" + ie.getUserName() + "] leaves group(" + ie.getHandlerGroup() + ") in session("
                        + ie.getHandlerSession() + ").\n");
                break;
            case CMInterestEvent.USER_TALK:
                printMessage("(" + ie.getHandlerSession() + ", " + ie.getHandlerGroup() + ")\n");
                printMessage("<" + ie.getUserName() + ">: " + ie.getTalk() + "\n");
                break;
            default:
                return;
        }
    }

    private void processDummyEvent(CMEvent cme) {
        CMDummyEvent due = (CMDummyEvent) cme;
        return;
    }

    private void printMessage(String strText) {
		/*
		m_outTextArea.append(strText);
		m_outTextArea.setCaretPosition(m_outTextArea.getDocument().getLength());
		*/
        m_server.printMessage(strText);
    }

}
