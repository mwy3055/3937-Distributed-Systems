package project.server;

import java.util.ArrayList;
import java.util.List;

public class APIResponse {
    public String id;
    public Metadata metadata;

    public ArrayList<Result> results = new ArrayList<>();
    public String word;

    class Metadata {
        public String operation;
        public String provider;
        public String schema;
    }

    class Result {
        public String id;
        public String language;
        public List<LexicalEntry> lexicalEntries = new ArrayList<>();
    }

    class LexicalEntry {
        public String language;
        public LexicalCategory lexicalCategory;
    }

    class LexicalCategory {
        public String id;
        public String text;
    }
}
