package ru.clevertec;

import ru.clevertec.broker.Broker;
import ru.clevertec.broker.BrokerService;
import ru.clevertec.model.Topic;
import ru.clevertec.model.thread.Consumer;
import ru.clevertec.model.thread.Producer;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class TopicManager {

    public static void main(String[] args) {
        BrokerService broker = new Broker();
        Topic topic = broker.createTopic("topic1", 2);

        List<String> messagesToPublish1 = List.of(
                "1-1",
                "2-1",
                "3-1",
                "4-1",
                "5-1",
                "6-1"
        );
        List<String> messagesToPublish2 = List.of(
                "1-2",
                "2-2",
                "3-2",
                "4-2",
                "5-2",
                "6-2"
        );

        Map<String, CountDownLatch> countDownLatchMap = Map.of(
                "Consumer1", new CountDownLatch(6),
                "Consumer2", new CountDownLatch(12),
                "Consumer3", new CountDownLatch(4));

        List<Consumer> consumers = List.of(
                new Consumer("Consumer1", topic, countDownLatchMap.get("Consumer1")),
                new Consumer("Consumer2", topic, countDownLatchMap.get("Consumer2")),
                new Consumer("Consumer3", topic, countDownLatchMap.get("Consumer3")));

        List<Producer> producers = List.of(
                new Producer("Producer1", topic, messagesToPublish1),
                new Producer("Producer2", topic, messagesToPublish2)
        );

        consumers.forEach(consumer -> new Thread(consumer).start());
        producers.forEach(producer -> new Thread(producer).start());

        countDownLatchMap.values().forEach(latch -> {
            try {
                latch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        System.out.println("Topic messages: " + broker.getTopic("topic1").getMessages());

        System.out.println("Consumer1 read: " + consumers.getFirst().getReadMessages());
        System.out.println("Consumer2 read: " + consumers.get(1).getReadMessages());
        System.out.println("Consumer3 read: " + consumers.get(2).getReadMessages());
    }
}
