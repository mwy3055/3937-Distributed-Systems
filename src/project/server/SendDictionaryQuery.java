package project.server;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.Callable;

public class SendDictionaryQuery implements Callable<Integer> {
    private static final String app_id = "fe786f54";
    private static final String app_key = "4775983b0261c6e064bca920c4b64683";

    private static final HashSet<String> stringSet = new HashSet<>();

    private String word;

    public SendDictionaryQuery(String word) {
        this.word = word;
    }

    private static String queryURL(String word) {
        final String word_id = word.toLowerCase();
        return "https://od-api.oxforddictionaries.com/api/v2/entries/en-us/" + word_id + "?fields=pronunciations&strictMatch=false";
    }

    public int sendQuery() throws Exception {
        String queryURL = queryURL(word);

        Request request = new Request.Builder()
                .header("Accept", "application/json")
                .header("app_id", app_id)
                .header("app_key", app_key)
                .url(queryURL)
                .build();

        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Error code " + response);
        }
        /* Asynchronous request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Error code " + response);
                } else {
                    Gson gson = new Gson();
                    APIResponse parsedResponse = gson.fromJson(response.body().string(), APIResponse.class);

                    if (isNoun(parsedResponse)) {
                        System.out.println(input + " is a noun.");
                    } else {
                        System.out.println(input + " is not a noun.");
                    }
                    stringSet.add(input);
                }
            }
        });*/

        Gson gson = new Gson();
        APIResponse parsedResponse = gson.fromJson(response.body().string(), APIResponse.class);

        if (isNoun(parsedResponse)) {

            stringSet.add(word);
            return 1;
        } else {
            System.out.println(word + " is not a noun.");
            return 0;
        }
    }

    private static boolean isNoun(APIResponse response) {
        for (APIResponse.LexicalEntry entry : response.results.get(0).lexicalEntries) {
            if (entry.lexicalCategory.id.equalsIgnoreCase("noun")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Integer call() throws Exception {
        if (stringSet.contains(word)) {
            return -1;
        }
        return sendQuery();
    }
}
