package ru.clevertec;

import ru.clevertec.broker.Broker;
import ru.clevertec.broker.BrokerService;
import ru.clevertec.model.Thread.Consumer;
import ru.clevertec.model.Thread.Producer;
import ru.clevertec.model.Topic;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        BrokerService broker = new Broker();
        Topic topic = broker.createTopic("MyTopic", 2);

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

        Consumer consumer1 = new Consumer("Consumer1");
        Consumer consumer2 = new Consumer("Consumer2");
        Consumer consumer3 = new Consumer("Consumer3");
        broker.startConsumer(consumer1, topic.getName(), 6);
        broker.startConsumer(consumer2, topic.getName(), 13);
        broker.startConsumer(consumer3, topic.getName(), 4);

        TimeUnit.SECONDS.sleep(1);

        Producer producer1 = new Producer("Producer1", messagesToPublish1);
        Producer producer2 = new Producer("Producer2", messagesToPublish2);
        broker.startProducer(producer1, topic.getName());
        broker.startProducer(producer2, topic.getName());

        broker.joinConsumers(List.of(consumer1, consumer2));

        System.out.println("Topic messages: " + broker.getTopic("MyTopic").getMessages());

        System.out.println(consumer1.getName() + " read: " + consumer1.getReadMessages());
        System.out.println(consumer2.getName() + " read: " + consumer2.getReadMessages());
        System.out.println(consumer3.getName() + " read: " + consumer3.getReadMessages());
    }
}
