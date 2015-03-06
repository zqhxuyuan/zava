package com.zqh.fun.pp;

/**
 * Created by hadoop on 15-2-12.
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyRobot {

    private List<String> hrefs   = Collections.synchronizedList(new ArrayList<String>());
    private List<String> visited = Collections.synchronizedList(new ArrayList<String>());
    private List<String> images  = Collections.synchronizedList(new ArrayList<String>());

    public MyRobot(String href) {
        hrefs.add(href);
    }

    public void run() throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        pool.execute(new DealUrl(hrefs, visited, images));
        Thread.sleep(8000);
        pool.execute(new DealUrl(hrefs, visited, images));
        pool.shutdown();
    }

    public static void main(String[] args) throws InterruptedException {
        MyRobot robot = new MyRobot("http://500ququ.com/");
        robot.run();
    }

}