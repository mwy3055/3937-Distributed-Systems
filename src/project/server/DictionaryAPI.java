package project.server;

import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.HashSet;

public class DictionaryAPI {
    private static final String app_id = "fe786f54";
    private static final String app_key = "4775983b0261c6e064bca920c4b64683";

    private static final HashSet<String> stringSet = new HashSet<>();

    private static String queryURL(String word) {
        final String word_id = word.toLowerCase();
        return "https://od-api.oxforddictionaries.com/api/v2/entries/en-us/" + word_id + "?fields=pronunciations&strictMatch=false";
    }

    public static void sendQuery(String input) throws Exception {
        if (stringSet.contains(input)) {
            System.out.println(input + " already exists!");
            return;
        }
        String queryURL = queryURL(input);

        Request request = new Request.Builder()
                .header("Accept", "application/json")
                .header("app_id", app_id)
                .header("app_key", app_key)
                .url(queryURL)
                .build();

        OkHttpClient client = new OkHttpClient();
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
        });
    }

    private static boolean isNoun(APIResponse response) {
        for (APIResponse.LexicalEntry entry : response.results.get(0).lexicalEntries) {
            if (entry.lexicalCategory.id.equalsIgnoreCase("noun")) {
                return true;
            }
        }
        return false;
    }

}
