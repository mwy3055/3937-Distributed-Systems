package project.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ServerMain {
    public static void main(String[] args) throws Exception {
        ExecutorService service = Executors.newSingleThreadExecutor();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            String query = br.readLine().strip();
            if (query.equals("Q")) {
                break;
            }
            /* query: word which will be judged whether it is a noun */
            SendDictionaryQuery queryObject = new SendDictionaryQuery(query);
            Future<Integer> result = service.submit(queryObject);

            int rtnValue = result.get();
            switch (rtnValue) {
                case -2:
                    System.out.println("Dictionary API connection error!");
                    break;
                case -1:
                    System.out.println(query + " already exists.");
                    break;
                case 0:
                    System.out.println(query + " is not a noun.");
                    break;
                case 1:
                    System.out.println(query + " is a noun.");
                    break;
            }
        }
        System.exit(0);
    }
}
