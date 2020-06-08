package project;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class WordChainHelper {

    public static BlockingQueue<String> lines = new LinkedBlockingQueue<>();
    private static boolean isGettingInput = false;
    private static Thread inputThread;

    public static void startGettingInput() {
        if (isGettingInput) {
            return;
        }
        isGettingInput = true;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        if (inputThread == null) {
            inputThread = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        lines.add(br.readLine());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                }
            });
        }
        inputThread.setDaemon(true);
        inputThread.start();
    }

    public static void stopGettingInput() {
        inputThread.interrupt();
        lines.clear();
    }

    public static String getWordResultString(int resultCode) {
        switch (resultCode) {
            case WordChainInfo.RESULT_OK:
                return "OK";
            case WordChainInfo.RESULT_NOT_NOUN:
                return "NOT NOUN";
            case WordChainInfo.RESULT_DUPLICATION:
                return "DUPLICATION";
            case WordChainInfo.RESULT_TIMEOUT:
                return "TIMEOUT";
            case WordChainInfo.RESULT_API_ERROR:
                return "API ERROR";
            default:
                return "UNKNOWN ERROR";
        }
    }

}
