package project.server;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import project.WordChainInfo;

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

    public static void clearStringSet() {
        stringSet.clear();
    }

    private String getQueryURL() {
        final String word_id = word.toLowerCase();
        return "https://od-api.oxforddictionaries.com/api/v2/entries/en-us/" + word_id + "?fields=pronunciations&strictMatch=false";
    }

    private int sendQuery() {
        String queryURL = getQueryURL();

        Request request = new Request.Builder()
                .header("Accept", "application/json")
                .header("app_id", app_id)
                .header("app_key", app_key)
                .url(queryURL)
                .build();

        OkHttpClient client = new OkHttpClient();
        Response response;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            return WordChainInfo.RESULT_API_ERROR;
        }
        if (!response.isSuccessful()) {
            return WordChainInfo.RESULT_API_ERROR;
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
        APIResponse parsedResponse;
        try {
            parsedResponse = gson.fromJson(response.body().string(), APIResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
            return WordChainInfo.RESULT_API_ERROR;
        }
        if (isNoun(parsedResponse)) {
            stringSet.add(word);
            return WordChainInfo.RESULT_OK;
        } else {
            return WordChainInfo.RESULT_NOT_NOUN;
        }
    }

    private boolean isNoun(APIResponse response) {
        for (APIResponse.LexicalEntry entry : response.results.get(0).lexicalEntries) {
            if (entry.lexicalCategory.id.equalsIgnoreCase("noun")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Integer call() {
        if (word.equals("")) {
            return WordChainInfo.RESULT_TIMEOUT;
        }
        if (stringSet.contains(word)) {
            return WordChainInfo.RESULT_DUPLICATION;
        }
        return sendQuery();
    }
}
