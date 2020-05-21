package project;

/**
 *  Constants, other shared values of WordChain project.
 */
public class WordChainInfo {

    /* results of SendDictionaryQuery */
    public static final int RESULT_OK = 400;
    public static final int RESULT_NOTNOUN = 401;
    public static final int RESULT_DUPLICATION = 402;
    public static final int RESULT_TIMEOUT = 403;

    public static final int EVENT_SEND_WORD = 990;
    public static final int EVENT_NEXTUSER = 991;
    public static final int EVENT_RESULT_WORD = 992;
    public static final int EVENT_START_GAME = 993;
    public static final int EVENT_FINISH_GAME = 994;
    public static final int EVENT_TIME_OVER = 995;
}
