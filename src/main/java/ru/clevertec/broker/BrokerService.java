package ru.clevertec.broker;

import ru.clevertec.model.Thread.Consumer;
import ru.clevertec.model.Thread.Producer;
import ru.clevertec.model.Topic;

import java.util.List;

public interface BrokerService {
    Topic createTopic(String name, int maxConsumersToRead);

    void startConsumer(Consumer consumer, String topicName, int messageCount);

    void startProducer(Producer producer, String topicName);

    void joinConsumers(List<Consumer> consumers) throws InterruptedException;

    Topic getTopic(String topicName);
}
