package project;

/**
 * Constants, other shared values of WordChain project.
 */
public class WordChainInfo {

    /* results of SendDictionaryQuery */
    public static final int RESULT_OK = 400;
    public static final int RESULT_NOT_NOUN = 401;
    public static final int RESULT_DUPLICATION = 402;
    public static final int RESULT_TIMEOUT = 403;
    public static final int RESULT_API_ERROR = 404;

    public static final int EVENT_SEND_WORD = 900;
    public static final int EVENT_NEXT_USER = 901;
    public static final int EVENT_RESULT_WORD = 902;
    public static final int EVENT_REQUEST_GAME_START = 903;
    public static final int EVENT_NOTIFY_ADMIN = 904;
    public static final int EVENT_GAME_START = 905;
    public static final int EVENT_GAME_FINISH = 906;

    public static final int WORD_TIME_LIMIT_MILIS = 10000;
    public static final int WORD_TIME_LIMIT_SEC = 10;

}
