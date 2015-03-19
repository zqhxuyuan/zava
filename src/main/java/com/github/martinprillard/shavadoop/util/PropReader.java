package com.github.martinprillard.shavadoop.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 
 * @author martin prillard
 * 
 */
public class PropReader {

    //private final String URL_CONFIG_FILE = "src/main/resources/config.properties";
    private final String URL_CONFIG_FILE = "shavadoop.properties";
    public static final String FILE_DSA = "file_dsa";
    public static final String FILE_IP_ADRESS = "file_ip_adress";
    public static final String FILE_INPUT = "file_input";
    public static final String PORT_MASTER_DICTIONARY = "port_master_dictionary";
    public static final String PORT_TASK_TRACKER = "port_task_tracker";
    public static final String PORT_SHELL = "port_shell";
    public static final String WORKER_MAX = "worker_max";
    public static final String THREAD_MAX_BY_WORKER = "thread_max_by_worker";
    public static final String THREAD_QUEUE_MAX_BY_WORKER = "thread_queue_max_by_worker";
    public static final String THREAD_MAX_LIFETIME = "thread_max_lifetime";
    public static final String PATH_REPO = "path_repo";
    public static final String NETWORK_IP_REGEX = "network_ip_regex";
    public static final String MODE_DEBUG = "mode_debug";
    public static final String BLOC_SIZE_MIN = "bloc_size_min";
    public static final String TASK_TRACKER_FREQ = "task_tracker_freq";
    public static final String TASK_TRACKER_ANSWER_TIMEOUT = "task_tracker_answer_timeout";

    public String getPropValues(String key) {

        Properties prop = new Properties();

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(URL_CONFIG_FILE);
        try {
            prop.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return prop.getProperty(key);
    }

}
