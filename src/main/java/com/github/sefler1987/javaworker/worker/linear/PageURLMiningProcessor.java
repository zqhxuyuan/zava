package com.github.sefler1987.javaworker.worker.linear;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.sefler1987.javaworker.worker.TaskProcessor;
import com.github.sefler1987.javaworker.worker.WorkerTask;

/**
 * Given a specified URL, the processor will try to mine all of the URLs out from the page. The URLs
 * are guaranteed to be unique.
 *
 * @author xuanyin.zy E-mail:xuanyin.zy@taobao.com
 * @since Sep 15, 2012 4:19:15 PM
 */
public class PageURLMiningProcessor implements TaskProcessor {
    private static final String URL_PATTERN = "http(s)?://[\\w\\.\\/]*(\\.htm|\\.do|\\.html|\\.xhtm|\\.xhtml)";

    private static final int MAX_PAGE_SIZE = 1024 * 1024 * 10;

    private static final int BUFFER_SIZE = 128 * 1024;

    @Override
    public void process(WorkerTask<?> task) {
        if (!(task instanceof PageURLMiningTask))
            throw new IllegalArgumentException("Excepted PageURLMiningTask but was: " + task.getClass().getSimpleName());

        PageURLMiningTask urlMiningTask = (PageURLMiningTask) task;

        try {
            //先访问任务提供的目标URL
            URL url = new URL(urlMiningTask.getTargetURL());

            URLConnection urlConnection = url.openConnection();
            urlConnection.setConnectTimeout((int) TimeUnit.SECONDS.toMillis(2));
            urlConnection.setReadTimeout((int) TimeUnit.SECONDS.toMillis(2));

            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream), BUFFER_SIZE);

            //目标URL的页面内容
            StringBuilder pageContent = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                pageContent.append(line);

                if (line.length() > MAX_PAGE_SIZE || pageContent.length() > MAX_PAGE_SIZE) {
                    break;
                }
            }

            //这个目标页面上有没有链接
            Matcher matcher = Pattern.compile(URL_PATTERN).matcher(pageContent);
            while (matcher.find()) {
                //添加到这个任务需要挖掘的URL集合中
                urlMiningTask.addMinedURL(matcher.group());
            }

            //这个目标页面访问完毕,任务结束. 那么那些需要挖掘的页面呢?
            urlMiningTask.setDone(true);
        } catch (Exception e) {
            //System.err.println("Error while fetching specified URL: " + urlMiningTask.getTargetURL() + "\nException" + e.toString());
        } finally {
            synchronized (urlMiningTask) {
                urlMiningTask.notifyAll();
            }
        }
    }
}
