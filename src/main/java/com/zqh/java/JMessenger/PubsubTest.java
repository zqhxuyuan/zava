package com.zqh.java.JMessenger;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PubsubTest
{
    private static final String TOPIC_FOOD = "tf";

    private static final String TOPIC_ELECTRONICS = "te";

    private static final String TOPIC_COMMERCE = "tc";

    private static Pubsub pubsub = Pubsub.getInstance();

    public static class FoodSubscriber implements Pubsub.Listener
    {

        public void subscribe()
        {
            pubsub.addListener(TOPIC_FOOD, this);
        }

        @Override
        public void onEventReceived(String type, Object object)
        {
            if (TOPIC_FOOD.equals(type))
            {
                System.out.println("FS-TOPIC_FOOD-" + (String) object);
            }
        }

    }

    public static class ElectronicsSubscriber implements Pubsub.Listener
    {
        public void subscribe()
        {
            pubsub.addListener(TOPIC_ELECTRONICS, this);
        }

        @Override
        public void onEventReceived(String type, Object object)
        {
            if (TOPIC_ELECTRONICS.equals(type))
            {
                System.out.println("ES-TOPIC_ELECTRONICS-" + (String) object);
            }
        }
    }

    public static class GeneralSubscriber implements Pubsub.Listener
    {
        public void subscribe()
        {
            pubsub.addListener(TOPIC_ELECTRONICS, this);
            pubsub.addListener(TOPIC_COMMERCE, this);
            pubsub.addListener(TOPIC_FOOD, this);
        }

        @Override
        public void onEventReceived(String type, Object object)
        {
            if (TOPIC_COMMERCE.equals(type))
            {
                System.out.println("GS-TOPIC_COMMERCE-" + (String) object);
            }
            else if (TOPIC_ELECTRONICS.equals(type))
            {
                System.out.println("GS-TOPIC_ELECTRONICS-" + (String) object);
            }
            else if (TOPIC_FOOD.equals(type))
            {
                System.out.println("GS-TOPIC_FOOD-" + (String) object);
            }
        }
    }

    public static void main(String[] args)
    {
        FoodSubscriber fs = new FoodSubscriber();
        ElectronicsSubscriber es = new ElectronicsSubscriber();
        GeneralSubscriber gs = new GeneralSubscriber();
        fs.subscribe();
        es.subscribe();
        gs.subscribe();
        int numThreads = 10;
        ExecutorService ex = Executors.newFixedThreadPool(numThreads);
        for (int i = 0; i < numThreads; i++)
        {
            ex.execute(new Runnable()
            {
                @Override
                public void run()
                {
                    Random r = new Random();
                    for (int j = 0; j < 10; j++)
                    {
                        int val = r.nextInt(4);
                        String topic = getRandomTopic(val);
                        pubsub.publish(topic, topic + "-" + j);
                    }
                }
            });
        }
        ex.shutdown();
    }

    private static String getRandomTopic(int i)
    {
        if (i == 0)
            return TOPIC_COMMERCE;
        else if (i == 1)
            return TOPIC_ELECTRONICS;
        else
            return TOPIC_FOOD;
    }
}