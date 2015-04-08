package com.zqh.play.traffic;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LampController
{
    // 刚开始让由南向北的灯变绿;
    private Lamp currentLamp;
    /* 每隔10秒将当前绿灯变为红灯，并让下一个方向的灯变绿 */
    public LampController()
    {
        currentLamp = Lamp.S2N;
        currentLamp.light();
        ScheduledExecutorService timer = Executors.newScheduledThreadPool(1);
        timer.scheduleAtFixedRate(new Runnable()
        {
            public void run()
            {
                currentLamp = currentLamp.blackOut();
            }
        }, 10, 10, TimeUnit.SECONDS);
    }
}