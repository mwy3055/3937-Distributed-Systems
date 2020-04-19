package project.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ServerMain {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            String query = br.readLine().strip();
            if (query.equals("Q")) {
                break;
            }
            /* query: word which will be judged whether it is a noun */
            DictionaryAPI.sendQuery(query);
        }
        System.exit(0);
    }
}
