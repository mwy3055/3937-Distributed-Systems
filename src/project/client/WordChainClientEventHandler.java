package project.client;

import kr.ac.konkuk.ccslab.cm.entity.CMUser;
import kr.ac.konkuk.ccslab.cm.event.CMDataEvent;
import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.event.handler.CMAppEventHandler;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.info.CMInteractionInfo;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;
import project.WordChainHelper;
import project.WordChainInfo;
import project.event.*;

import java.util.concurrent.TimeUnit;

public class WordChainClientEventHandler implements CMAppEventHandler {

    private WordChainClient m_client;
    private CMClientStub m_clientStub;

    public WordChainClientEventHandler(CMClientStub clientStub, WordChainClient client) {
        m_client = client;
        m_clientStub = clientStub;
    }

    @Override
    public void processEvent(CMEvent cme) {
        switch (cme.getType()) {
            case CMInfo.CM_DATA_EVENT:
                processDataEvent(cme);
                break;
            case WordChainInfo.EVENT_NEXT_USER:
                processNextUserEvent(cme);
                break;
            case WordChainInfo.EVENT_RESULT_WORD:
                processReplyWordEvent(cme);
                break;
            case WordChainInfo.EVENT_NOTIFY_ADMIN:
                processNotifyAdminEvent(cme);
                break;
            case WordChainInfo.EVENT_GAME_START:
                processGameStartEvent(cme);
                break;
            case WordChainInfo.EVENT_GAME_FINISH:
                processGameFinishEvent(cme);
                break;
            default:
                return;
        }
    }

    private void processNotifyAdminEvent(CMEvent cme) {
        NotifyAdminEvent event = (NotifyAdminEvent) cme;
        CMInteractionInfo info = m_clientStub.getCMInfo().getInteractionInfo();
        CMUser myself = info.getMyself();
        if (event.isAdmin() == 1) {
            myself.setAdmin(true);
            System.out.println(String.format("You are the admin of session [%s], group [%s].",
                    myself.getCurrentSession(), myself.getCurrentGroup()));
            if (!m_client.isGamePlaying()) {
                m_client.setWaitingGameStart(true);
                m_client.startRequestThread();
            }
        } else {
            myself.setAdmin(false);
            if (!m_client.isGamePlaying()) {
                m_client.interruptRequestThread();
                m_client.startWaitingThread();
            }
        }
    }

    private void processGameStartEvent(CMEvent cme) {
        GameStartEvent event = (GameStartEvent) cme;
        if (event.getStart() == 1) {
            m_client.playGame();
        } else {
            printMessage("Server Response: Can't start the game. Wait more players to come.");
        }
    }

    private void processNextUserEvent(CMEvent cme) {
        NextUserEvent event = (NextUserEvent) cme;
        CMUser myself = m_clientStub.getMyself();
        if (event.getUserName().equals(myself.getName())) {
            String input = null;
            System.out.println("Your turn! Type the word.");
            System.out.print(String.format("Previous word: [%s] -> ", event.getPreviousWord()));
            WordChainHelper.lines.clear();
            try {
                input = WordChainHelper.lines.poll(WordChainInfo.WORD_TIME_LIMIT_SEC, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (input == null || input.equals("")) {
                System.out.println("Timeout!");
                input = "";
            } else {
                System.out.println(String.format("Your input: %s", input));
            }
            WordSendingEvent sendEvent = new WordSendingEvent(input, myself.getCurrentSession(), myself.getCurrentGroup());
            sendEvent.setSender(m_clientStub.getMyself().getName());
            m_clientStub.send(sendEvent, m_clientStub.getDefaultServerName());
        } else {
            System.out.println(String.format("User [%s] is typing the word...", event.getUserName()));
        }
    }

    private void processReplyWordEvent(CMEvent cme) {
        WordResultEvent resultEvent = (WordResultEvent) cme;
        CMUser myself = m_clientStub.getMyself();
        String userName = resultEvent.getUserName();
        String word = resultEvent.getWord();
        int resultCode = resultEvent.getResultCode();
        int scoreChange = resultEvent.getScoreChange();

        if (!userName.equals(myself.getName())) {
            printMessage(String.format("User [%s] typed [%s], score change : %d", userName, word, scoreChange));
            printMessage(String.format("Result: %s\n", WordChainHelper.getWordResultString(resultCode)));
        } else if (resultCode == WordChainInfo.RESULT_OK) {
            printMessage(String.format("[Server] %s is valid. You got %d scores!\n", word, scoreChange));
        } else if (resultCode == WordChainInfo.RESULT_NOT_NOUN) {
            printMessage(String.format("[Server] %s is not a noun. Your life will decrease by 1.\n", word));
        } else if (resultCode == WordChainInfo.RESULT_DUPLICATION) {
            printMessage(String.format("[Server] Someone already said %s. Your life will decrease by 1.\n", word));
        } else if (resultCode == WordChainInfo.RESULT_TIMEOUT) {
            printMessage(String.format("[Server] Timeout! Your life will decrease by 1.\n"));
        } else {
            printMessage("[Server] Unknown error. This turn will be passed.\n");
        }
    }

    private void processGameFinishEvent(CMEvent cme) {
        GameFinishEvent finishEvent = (GameFinishEvent) cme;
        printMessage(finishEvent.getResult());
        printMessage("Game finished.\n");
        m_client.setGamePlaying(false);
        WordChainHelper.stopGettingInput();

        // TODO: After game finish: what to do?
        m_client.terminateClient();
    }

    private void processDataEvent(CMEvent cme) {
        CMDataEvent de = (CMDataEvent) cme;
        switch (de.getID()) {
            case CMDataEvent.NEW_USER:
                printMessage("[" + de.getUserName() + "] enters group(" + de.getHandlerGroup() + ") in session("
                        + de.getHandlerSession() + ").\n");
                break;
            case CMDataEvent.REMOVE_USER:
                printMessage("[" + de.getUserName() + "] leaves group(" + de.getHandlerGroup() + ") in session("
                        + de.getHandlerSession() + ").\n");
                break;
            default:
                return;
        }
    }

    private void printMessage(String strText) {
        System.out.println(strText);
    }

}
