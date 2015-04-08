package com.zqh.play.traffic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by zqhxuyuan on 15-4-7.
 *
 * 每个Road对象代表一条路线，总共有12条路线，即系统中总共要产生12个Road实例对象。 每条路线上随机增加新的车辆，增加到一个集合中保存。
 * 每条路线每隔一秒都会检查控制本路线的灯是否为绿，是则将本路线保存车的集合中的第一辆车移除，即表示车穿过了路口。
 */
public class Road
{
    List<String> vechicles = new ArrayList<String>();
    private String name = null;

    public Road(String name)
    {
        this.name = name;
        // 模拟车辆不断随机上路的过程
        ExecutorService pool = Executors.newSingleThreadExecutor();
        pool.execute(new Runnable()
        {
            public void run()
            {
                for (int i = 1; i < 1000; i++)
                {
                    try
                    {
                        // 每1到10秒内有辆车上路
                        Thread.sleep((new Random().nextInt(10) + 1) * 1000);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    vechicles.add(Road.this.name + "-" + i);
                }
            }
        });
        //每隔一秒检查对应的灯是否为绿，是则放行一辆车
        ScheduledExecutorService timer = Executors.newScheduledThreadPool(1);
        timer.scheduleAtFixedRate(new Runnable()
        {
            public void run()
            {
                if (vechicles.size() > 0)
                {
                    boolean lighted = Lamp.valueOf(Road.this.name).isLighted();
                    if (lighted)
                    {
                        System.out.println(vechicles.remove(0) + " is traversing! ");
                    }
                }
            }
        }, 1, 1, TimeUnit.SECONDS);
    }
}