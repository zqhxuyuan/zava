package com.github.shansun.guava.eventbus;

import com.google.common.eventbus.Subscribe;

/**
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-4
 */
public class MultiListener {
    private Integer	lastInteger;
    private Long	lastLong;

    @Subscribe
    public void listenInteger(Integer event) {
        System.out.println("Received integer event... [" + event + "]");
        lastInteger = event;
    }

    @Subscribe
    public void listenLong(Long event) {
        System.out.println("Received long event... [" + event + "]");
        lastLong = event;
    }

    public Integer getLastInteger() {
        return lastInteger;
    }

    public Long getLastLong() {
        return lastLong;
    }
}