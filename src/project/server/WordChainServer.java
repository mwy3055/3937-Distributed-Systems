package project.server;

import kr.ac.konkuk.ccslab.cm.entity.CMGroup;
import kr.ac.konkuk.ccslab.cm.entity.CMMember;
import kr.ac.konkuk.ccslab.cm.entity.CMSession;
import kr.ac.konkuk.ccslab.cm.entity.CMUser;
import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.info.CMInteractionInfo;
import kr.ac.konkuk.ccslab.cm.manager.CMCommManager;
import kr.ac.konkuk.ccslab.cm.manager.CMConfigurator;
import kr.ac.konkuk.ccslab.cm.sns.CMSNSUserAccessSimulator;
import kr.ac.konkuk.ccslab.cm.stub.CMServerStub;
import project.WordChainHelper;
import project.WordChainInfo;
import project.event.*;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class WordChainServer extends JFrame {

    private static final long serialVersionUID = 1L;

    private JTextPane m_outTextPane;
    private JButton m_startStopButton;
    private CMServerStub m_serverStub;
    private WordChainServerEventHandler m_eventHandler;
    private CMSNSUserAccessSimulator m_uaSim;

    private ExecutorService executor = Executors.newCachedThreadPool();
    private boolean isGamePlaying = false;

    WordChainServer() {
        ButtonActionListener cmActionListener = new ButtonActionListener();
        setTitle("WordChainServer");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        m_outTextPane = new JTextPane();
        m_outTextPane.setEditable(false);

        StyledDocument doc = m_outTextPane.getStyledDocument();
        addStylesToDocument(doc);

        add(m_outTextPane, BorderLayout.CENTER);
        JScrollPane scroll = new JScrollPane(m_outTextPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        add(scroll);

        JPanel topButtonPanel = new JPanel();
        topButtonPanel.setLayout(new FlowLayout());
        add(topButtonPanel, BorderLayout.NORTH);

        m_startStopButton = new JButton("Start Server CM");
        m_startStopButton.addActionListener(cmActionListener);
        m_startStopButton.setEnabled(false);
        // add(startStopButton, BorderLayout.NORTH);
        topButtonPanel.add(m_startStopButton);

        setVisible(true);

        // create CM stub object and set the event handler
        m_serverStub = new CMServerStub();
        m_eventHandler = new WordChainServerEventHandler(m_serverStub, this);
        m_uaSim = new CMSNSUserAccessSimulator();

        // start cm
        startCM();
    }

    private void addStylesToDocument(StyledDocument doc) {
        Style defStyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);

        Style regularStyle = doc.addStyle("regular", defStyle);
        StyleConstants.setFontFamily(regularStyle, "SansSerif");

        Style boldStyle = doc.addStyle("bold", defStyle);
        StyleConstants.setBold(boldStyle, true);
    }

    public CMServerStub getServerStub() {
        return m_serverStub;
    }

    public WordChainServerEventHandler getServerEventHandler() {
        return m_eventHandler;
    }

    public void updateTitle() {
        CMUser myself = m_serverStub.getMyself();
        if (CMConfigurator.isDServer(m_serverStub.getCMInfo())) {
            setTitle("WordChainServer [\"" + myself.getName() + "\"]");
        } else {
            if (myself.getState() < CMInfo.CM_LOGIN) {
                setTitle("WordChain Additional Server [\"?\"]");
            } else {
                setTitle("WordChain Additional Server [\"" + myself.getName() + "\"]");
            }
        }
    }

    public void startCM() {
        boolean bRet = false;
        String strSavedServerAddress = null;
        String strCurServerAddress = null;
        int nSavedServerPort = -1;

        strSavedServerAddress = m_serverStub.getServerAddress();
        strCurServerAddress = CMCommManager.getLocalIP();
        nSavedServerPort = m_serverStub.getServerPort();

        JTextField serverAddressTextField = new JTextField(strCurServerAddress);
        JTextField serverPortTextField = new JTextField(String.valueOf(nSavedServerPort));
        Object msg[] = {"Server Address: ", serverAddressTextField, "Server Port: ", serverPortTextField};
        int option = JOptionPane.showConfirmDialog(null, msg, "Server Information", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String strNewServerAddress = serverAddressTextField.getText();
            int nNewServerPort = Integer.parseInt(serverPortTextField.getText());
            if (!strNewServerAddress.equals(strSavedServerAddress) || nNewServerPort != nSavedServerPort)
                m_serverStub.setServerInfo(strNewServerAddress, nNewServerPort);
        }

        bRet = m_serverStub.startCM();
        if (!bRet) {
            printStyledMessage("CM initialization error!\n", "bold");
        } else {
            printStyledMessage("WordChainServer starts.\n", "bold");
            m_startStopButton.setEnabled(true);
            m_startStopButton.setText("Stop WordChainServer");
            updateTitle();
        }
    }

    public void printMessage(String strText) {
        StyledDocument doc = m_outTextPane.getStyledDocument();
        try {
            doc.insertString(doc.getLength(), strText, null);
            m_outTextPane.setCaretPosition(m_outTextPane.getDocument().getLength());

        } catch (BadLocationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return;
    }

    public void printStyledMessage(String strText, String strStyleName) {
        StyledDocument doc = m_outTextPane.getStyledDocument();
        try {
            doc.insertString(doc.getLength(), strText, doc.getStyle(strStyleName));
            m_outTextPane.setCaretPosition(m_outTextPane.getDocument().getLength());

        } catch (BadLocationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return;
    }

    public CMSession getSession(String sessionName) {
        CMInteractionInfo interInfo = m_serverStub.getCMInfo().getInteractionInfo();
        return interInfo.findSession(sessionName);
    }

    public CMGroup getGroup(String sessionName, String groupName) {
        CMInteractionInfo interInfo = m_serverStub.getCMInfo().getInteractionInfo();
        CMSession session = getSession(sessionName);
        if (session == null) {
            System.out.println(String.format("Failed to get session [%s]!\n", sessionName));
            return null;
        }
        return session.findGroup(groupName);
    }

    public CMUser getUser(CMGroup group, String userName) {
        return group.getGroupUsers().findMember(userName);
    }

    /* Set the specified user to the admin of the group */
    public void setGroupAdmin(String sessionName, String groupName, String userName) {
        CMGroup group = getGroup(sessionName, groupName);
        CMUser user = getUser(group, userName);
        if (user == null) {
            printMessage(String.format("setGroupAdmin(): user [%s] doesn't exist at session [%s], group [%s].\n",
                    userName, sessionName, groupName));
            return;
        }
        group.setGroupAdmin(user);
        sendAdminEvent(user, 1);
        printMessage(String.format("User %s is now the admin of the session %s, group %s.\n", userName, sessionName,
                groupName));
    }

    /* Set the any user to the admin of the group */
    public void setGroupAdmin(String sessionName, String groupName) {
        CMGroup group = getGroup(sessionName, groupName);
        CMMember member = group.getGroupUsers();
        if (member.isEmpty()) {
            group.setGroupAdmin(null);
            return;
        }
        CMUser user = member.getUser(0);
        group.setGroupAdmin(user);
        sendAdminEvent(user, 1);
        printMessage(String.format("User %s is now the admin of the session %s, group %s.\n", user.getName(),
                sessionName, groupName));
    }

    public void sendAdminEvent(CMUser user, int isAdmin) {
        NotifyAdminEvent adminEvent = new NotifyAdminEvent(isAdmin);
        m_serverStub.send(adminEvent, user.getName());
    }

    public void sendAdminEvent(String userName, int isAdmin) {
        NotifyAdminEvent adminEvent = new NotifyAdminEvent(isAdmin);
        m_serverStub.send(adminEvent, userName);
    }

    public void startGame(String sessionName, String groupName) {
        GameControl control = new GameControl(sessionName, groupName);
        control.start();
    }

    private WordResult processWordEvent(WordSendingEvent wordEvent, long timeDiff) {
        String word = wordEvent.getWord();
        if (!word.equals("")) {
            printMessage(String.format("User [%s] from group [%s], session [%s] sent word [%s].\n", wordEvent.getSender(),
                    wordEvent.getGroupName(), wordEvent.getSessionName(), word));
        }
        SendDictionaryQuery query = new SendDictionaryQuery(word);
        Future<Integer> result = executor.submit(query);

        int rtnValue = -2;
        int scoreChange = 0, lifeChange = 0;
        try {
            rtnValue = result.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        switch (rtnValue) {
            case WordChainInfo.RESULT_API_ERROR:
                printMessage("Dictionary API connection error!\n");
                break;
            case WordChainInfo.RESULT_DUPLICATION:
                printMessage(String.format("%s already exists.\n", word));
                lifeChange = -1;
                break;
            case WordChainInfo.RESULT_NOT_NOUN:
                printMessage(String.format("%s is not a noun.\n", word));
                lifeChange = -1;
                break;
            case WordChainInfo.RESULT_OK:
                printMessage(String.format("%s is a noun.\n", word));
                scoreChange = calculateScore(timeDiff);
                break;
            case WordChainInfo.RESULT_TIMEOUT:
                printMessage(String.format("User [%s]: timeout\n", wordEvent.getSender()));
                lifeChange = -1;
                break;
            default:
                printMessage("ELSE\n");
                break;
        }
        return new WordResult(wordEvent.getWord(), rtnValue, scoreChange, lifeChange);
    }

    private int calculateScore(long timeDiff) {
        int score = (int) (50 + (WordChainInfo.WORD_TIME_LIMIT_MILIS - timeDiff) / 100);
        return score;
    }

    public void sendQueryResult(String sessionName, String groupName, String userName, String word, int resultCode,
                                int scoreChange, int lifeChange) {
        WordResultEvent resultEvent = new WordResultEvent(userName, resultCode, word, scoreChange, lifeChange);
        m_serverStub.cast(resultEvent, sessionName, groupName);
    }

    public boolean isGamePlaying() {
        return isGamePlaying;
    }

    public class ButtonActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton) e.getSource();
            if (button.getText().equals("Start WordChainServer")) {
                boolean bRet = m_serverStub.startCM();
                if (!bRet) {
                    printStyledMessage("CM initialization error!\n", "bold");
                } else {
                    printStyledMessage("WordChainServer starts.\n", "bold");
                    button.setText("Stop WordChainServer");
                }
                setTitle("WordChainServer (\"SERVER\")");
            } else if (button.getText().equals("Stop WordChainServer")) {
                m_serverStub.terminateCM();
                printMessage("WordChainServer terminates.\n");
                button.setText("Start WordChainServer");
            }
        }
    }

    public class GameControl extends Thread {
        private final String groupName;
        private final String sessionName;
        private String previousWord;

        private final CMSession currentSession;
        private final CMGroup currentGroup;

        private int turnLeft;

        public GameControl(String sessionName, String groupName) {
            this.sessionName = sessionName;
            this.groupName = groupName;
            this.previousWord = "";

            currentSession = getSession(sessionName);
            currentGroup = getGroup(sessionName, groupName);
            turnLeft = 30;
            // turnLeft = 4; // for debug
        }

        // main function
        public void run() {
            isGamePlaying = true;
            currentSession.startGame();
            currentGroup.init();
            WordChainHelper.init();

            CMMember member = currentGroup.getGroupUsers();
            int order = 1;
            for (CMUser user : member.getAllMembers()) {
                GameStartEvent event = new GameStartEvent(1, sessionName, groupName, order);
                m_serverStub.send(event, user.getName());
                order++;
            }

            previousWord = getRandomAlphabet();
            printMessage(String.format("Game start at Session [%s], group [%s]!\n", sessionName, groupName));
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // main loop
            while (turnLeft > 0 && currentGroup.canGameProceed()) {
                printMessage(String.format("Current turn of session [%s], group [%s]: %d\n", sessionName, groupName, turnLeft));
                CMUser nextUser = currentGroup.getNextUser();
                if (nextUser == null) {
                    printMessage(String.format("Insufficient user number to continue the game.\n"));
                    break;
                }
                printMessage(String.format("Next user of session [%s], group [%s] is [%s].\n", sessionName, groupName,
                        nextUser.getName()));

                NextUserEvent event = new NextUserEvent(nextUser.getName(), previousWord);
                long t1 = System.currentTimeMillis();
                CMEvent[] receivedEvents = m_serverStub.castrecv(event, sessionName, groupName,
                        WordChainInfo.EVENT_SEND_WORD, WordChainInfo.EVENT_SEND_WORD, 1, WordChainInfo.WORD_TIME_LIMIT_MILIS);
                long t2 = System.currentTimeMillis();

                if (receivedEvents == null || receivedEvents.length == 0) {
                    printMessage(String.format("Reply of user [%s]: TIMEOUT\n", nextUser.getName()));
                    sendQueryResult(sessionName, groupName, nextUser.getName(), "TIMEOUT", WordChainInfo.RESULT_TIMEOUT,
                            0, -1);
                } else {
                    WordSendingEvent receivedEvent = (WordSendingEvent) receivedEvents[0];
                    WordResult result = processWordEvent(receivedEvent, t2 - t1);
                    if (result.getRtnValue() == WordChainInfo.RESULT_OK) {
                        previousWord = result.getWord();
                        nextUser.addScore(result.getScoreChange());
                    } else {
                        nextUser.decreaseLife();
                    }
                    sendQueryResult(receivedEvent.getSessionName(), receivedEvent.getGroupName(),
                            receivedEvent.getSender(), result.getWord(), result.getRtnValue(), result.getScoreChange(),
                            result.getLifeChange());
                }
                turnLeft--;
                currentGroup.refreshQueue();
            }

            printMessage(String.format("Session [%s], group [%s]: game finished.\n", currentSession.getSessionName(),
                    currentGroup.getGroupName()));
            GameFinishEvent finishEvent = new GameFinishEvent(getResultString());
            m_serverStub.cast(finishEvent, sessionName, groupName);

            isGamePlaying = false;
            currentSession.finishGame();
            currentGroup.finishGame();
        }

        private String getResultString() {
            StringBuilder resultBuilder = new StringBuilder();
            resultBuilder.append("Result\n");
            for (CMUser user : currentGroup.getGroupUsers().getAllMembers()) {
                resultBuilder.append(String.format("Username: [%s], score: [%s]\n", user.getName(), user.getScore()));
            }
            return resultBuilder.toString();
        }

        private String getRandomAlphabet() {
            int random = new Random().nextInt('z' - 'a');
            return String.format("%c", 'a' + random);
        }
    }

    public static void main(String[] args) {
        WordChainServer server = new WordChainServer();
        CMServerStub cmStub = server.getServerStub();

        cmStub.setAppEventHandler(server.getServerEventHandler());
    }
}
