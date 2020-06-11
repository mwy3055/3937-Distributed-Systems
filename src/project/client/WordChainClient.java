package project.client;

import kr.ac.konkuk.ccslab.cm.entity.CMUser;
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;
import project.WordChainHelper;
import project.WordChainInfo;
import project.event.GameStartEvent;
import project.event.RequestGameStartEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static java.lang.Thread.interrupted;

public class WordChainClient {
    private CMClientStub m_clientStub;
    private WordChainClientEventHandler m_eventHandler;

    private boolean gamePlaying;
    private boolean isWaitingGameStart;

    // for admin user to request the server to start the game
    private Thread requestGameStartThread = new Thread(() -> {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("If you want to start the game, enter \"start\" to the console.");
        System.out.println("You can only start the game when there are more than 2 users in the group.");
        while (!interrupted() && isWaitingGameStart) {
            String input = "";
            while (!input.equalsIgnoreCase("start")) {
                try {
                    input = br.readLine();
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
                if (input.equalsIgnoreCase("start")) {
                    break;
                }
                System.out.println("Enter \"start\" to start the game.");
            }
            if (sendGameStartEvent()) {
                break;
            }
        }
        playGame();
    });

    // for non-admin user to wait the game start
    private Thread waitGameStartThread = new Thread(() -> {
        System.out.println("Wait the admin to start the game.");
    });

    public WordChainClient() {
        m_clientStub = new CMClientStub();
        m_eventHandler = new WordChainClientEventHandler(m_clientStub, this);

        isWaitingGameStart = false;
        gamePlaying = false;
    }

    public CMClientStub getClientStub() {
        return m_clientStub;
    }

    public WordChainClientEventHandler getClientEventHandler() {
        return m_eventHandler;
    }

    ///////////////////////////////////////////////////////////////
    // TODO: Implement from here


    public void connectServer() {
        boolean bRet = m_clientStub.startCM();
        if (!bRet) {
            System.err.println("CM initialization error!");
            return;
        }

        // get current server info from the server configuration file
        String strCurServerAddress = null;
        int nCurServerPort = -1;
        String strNewServerAddress = "";
        String strNewServerPort = "";

        strCurServerAddress = m_clientStub.getServerAddress();
        nCurServerPort = m_clientStub.getServerPort();

        // ask the user if he/she would like to change the server info
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("========== start WordChainClient ==========");
        System.out.println("Default server address: " + strCurServerAddress);
        System.out.println("Default server port: " + nCurServerPort);

        try {
            String temp = "";
            while (!temp.equalsIgnoreCase("Y") && !temp.equalsIgnoreCase("N")) {
                System.out.print(String.format("Do you want to connect to default server?(Y/N): "));
                temp = br.readLine().trim();
            }
            if (temp.equalsIgnoreCase("N")) {
                System.out.print("Enter new server address: ");
                strNewServerAddress = br.readLine().trim();
                System.out.print("Enter new server port: ");
                strNewServerPort = br.readLine().trim();
            }
            if (!strNewServerAddress.isEmpty() && !strNewServerAddress.equals(strCurServerAddress))
                m_clientStub.setServerAddress(strNewServerAddress);
            if (!strNewServerPort.isEmpty() && Integer.parseInt(strNewServerPort) != nCurServerPort)
                m_clientStub.setServerPort(Integer.parseInt(strNewServerPort));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void login() {
        String userName = null;

        // TODO: If you want to use user database, you should get password input
        System.out.println("====== Login to server.");
        System.out.print("user name: ");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            userName = br.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Sent the login request. Please wait...");

        CMSessionEvent se = m_clientStub.syncLoginCM(userName, "");
        System.out.println(se.getReturnCode());
        if (se != null && se.getReturnCode() != 0) {
            System.out.println(String.format("Successfully logged in to session %s!", se.getSessionName()));
        } else {
            System.err.println("Failed the login request!");
            System.exit(1);
        }
        System.out.println("======");
    }

    /* For admin user */
    public boolean sendGameStartEvent() {
        CMUser myself = m_clientStub.getMyself();
        if (!myself.isAdmin()) {
            System.err.println("You are not a admin. Wait the admin to start the game.\n");
            return false;
        }
        if (gamePlaying) {
            System.err.println("Currently game is playing now.");
            return false;
        }
        System.out.println("Send game start request to server.");
        RequestGameStartEvent event = new RequestGameStartEvent(myself.getCurrentSession(), myself.getCurrentGroup());
        event.setSender(myself.getName());
        GameStartEvent startEvent = (GameStartEvent) m_clientStub.sendrecv(event, m_clientStub.getDefaultServerName(),
                WordChainInfo.EVENT_GAME_START, WordChainInfo.EVENT_GAME_START, 1000);
        return (startEvent != null) && (startEvent.getStart() == 1);
    }

    public void playGame() {
        // TODO: follow the server's instruction(event) until game is over
        interruptRequestThread();
        interruptWaitingThread();
        synchronized (this) {
            isWaitingGameStart = false;
        }
        gamePlaying = true;

        CMUser myself = m_clientStub.getMyself();
        String sessionName = myself.getCurrentSession();
        String groupName = myself.getCurrentGroup();
        System.out.println("=================================================");
        System.out.println(String.format("Game starts at session [%s], group [%s].", sessionName, groupName));
        System.err.println("WARNING: Do not enter your inputs blindly.");
        System.err.println("WARNING: It will violate the whole game process.");
        System.out.println("=================================================");
        WordChainHelper.startGettingInput();
    }

    public void setWaitingGameStart(boolean start) {
        this.isWaitingGameStart = start;
    }

    public boolean isGamePlaying() {
        return gamePlaying;
    }

    public void setGamePlaying(boolean playing) {
        this.gamePlaying = playing;
    }

    public void startRequestThread() {
        if (!requestGameStartThread.isAlive()) {
            requestGameStartThread.start();
        }
    }

    public void startWaitingThread() {
        if (!waitGameStartThread.isAlive()) {
            waitGameStartThread.start();
        }
    }

    public void interruptRequestThread() {
        requestGameStartThread.interrupt();
    }

    public void interruptWaitingThread() {
        waitGameStartThread.interrupt();
    }

    public void terminateClient() {
        System.out.println("Terminate WordChain Client.");
        m_clientStub.terminateCM();
        // TODO: InterruptedException why?
        System.exit(0);
    }

    ///////////////////////////////////////////////////////////////
    // TODO: Implement end.

    public static void main(String[] args) {
        WordChainClient client = new WordChainClient();
        CMClientStub cmStub = client.getClientStub();
        cmStub.setAppEventHandler(client.getClientEventHandler());
        client.connectServer();
        client.login();
    }
}
