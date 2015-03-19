package com.github.shansun.guava.eventbus;

/**
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-4
 */
public class TestEvent {
    private final int	message;

    public int getMessage() {
        return message;
    }

    public TestEvent(int message) {
        super();
        this.message = message;
    }
}