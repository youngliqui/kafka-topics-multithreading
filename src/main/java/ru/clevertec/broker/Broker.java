package ru.clevertec.broker;

import lombok.Getter;
import ru.clevertec.exception.TopicNotFoundException;
import ru.clevertec.model.Thread.Consumer;
import ru.clevertec.model.Thread.Producer;
import ru.clevertec.model.Topic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Getter
public class Broker implements BrokerService {
    private final List<Topic> topics;

    public Broker() {
        this.topics = new ArrayList<>();
    }

    public Topic createTopic(String name, int maxConsumersToRead) {
        Topic topic = new Topic(name, maxConsumersToRead);
        topics.add(topic);
        return topic;
    }

    public void startConsumer(Consumer consumer, String topicName, int messageCount) {
        Topic topic = getTopic(topicName);
        CountDownLatch latch = new CountDownLatch(messageCount);
        consumer.setTopic(topic);
        consumer.setLatch(latch);
        new Thread(consumer).start();
    }

    public void startProducer(Producer producer, String topicName) {
        Topic topic = getTopic(topicName);
        producer.setTopic(topic);
        new Thread(producer).start();
    }

    public void joinConsumers(List<Consumer> consumers) throws InterruptedException {
        for (Consumer consumer : consumers) {
            if (consumer.getLatch() != null) {
                consumer.getLatch().await(20, TimeUnit.SECONDS);
            }
        }
    }

    public Topic getTopic(String topicName) {
        return topics.stream()
                .filter(topic -> topic.getName().equals(topicName))
                .findFirst()
                .orElseThrow(() -> new TopicNotFoundException("topic with name: " + topicName + " was not found"));
    }
}
