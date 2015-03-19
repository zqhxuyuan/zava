package com.github.martinprillard.shavadoop.util;

/**
 * 
 * @author martin prillard
 * 
 */
public class Constant {

    // shavadoop
    public final static String APP_VERSION = "v1.0";
    public final static String APP_EXTENSION = ".jar";
    public final static String APP_JAR = "shavadoop" + "_" + APP_VERSION + APP_EXTENSION;
    public final static String USERNAME = System.getProperty("user.name");

    // path directory
    public static String PATH_JAR_MASTER_TODECODE = Constant.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    public static String PATH_JAR_MASTER;
    public static String PATH_REPO = new PropReader().getPropValues(PropReader.PATH_REPO);
    public final static String PATH_REPO_RES = PATH_REPO + "/temp/";
    public final static String PATH_JAR = PATH_REPO + "/" + APP_JAR;
    public final static String PATH_NETWORK_IP_DEFAULT_FILE = PATH_REPO + "/ip_adress";
    public static String PATH_NETWORK_IP_FILE = PATH_NETWORK_IP_DEFAULT_FILE;
    public final static String PATH_DSA_DEFAULT_FILE = "/.ssh/id_dsa";
    public final static String PATH_ROOT = "^/+$";
    // path files
    public final static String PATH_F_SPLITING = PATH_REPO_RES + "S";
    public final static String PATH_F_MAPPING = PATH_REPO_RES + "UM";
    public final static String F_MAPPING_BY_WORKER = "W";
    public final static String PATH_F_SHUFFLING_DICTIONARY = PATH_REPO_RES + "DSM";
    public final static String PATH_F_REDUCING = PATH_REPO_RES + "RM";
    public final static String PATH_F_FINAL_RESULT = PATH_REPO_RES + "output";

    // mode
    public final static boolean MODE_DEBUG = Boolean.parseBoolean(new PropReader().getPropValues(PropReader.MODE_DEBUG));
    public final static String APP_DEBUG_TITLE = "------------------------";
    public final static String APP_DEBUG_BLOC = "**************************************************";

    // separator
    public final static String SEP_NAME_FILE = "_";
    public final static String SEP_WORD = " ";
    public final static String SEP_CONTAINS_FILE = ", ";
    public final static String SEP_SCP_FILES = ",";

    // socket
    public final static String SEP_SOCKET_MESSAGE = ";";
    public final static String MESSAGE_TASKTRACKER_REQUEST = "ARE_YOU_ALIVE";
    public final static String ANSWER_TASKTRACKER_REQUEST_OK = "OK";
    public final static String ANSWER_TASKTRACKER_REQUEST_TASK_FINISHED = "TASK_FINISHED";
    // job
    private final static int BYTE_SIZE = 1000000;
    public final static int THREAD_MAX_LIFETIME = Integer.parseInt(new PropReader().getPropValues(PropReader.THREAD_MAX_LIFETIME));
    public final static int THREAD_MAX_SPLIT_MAPPING = 4;
    public final static int BLOC_SIZE_MIN = Integer.parseInt(new PropReader().getPropValues(PropReader.BLOC_SIZE_MIN)) * BYTE_SIZE;
    public final static int TASK_TRACKER_FREQ = Integer.parseInt(new PropReader().getPropValues(PropReader.TASK_TRACKER_FREQ));
    public final static int TASK_TRACKER_ANSWER_TIMEOUT = Integer.parseInt(new PropReader().getPropValues(PropReader.TASK_TRACKER_ANSWER_TIMEOUT));

}
