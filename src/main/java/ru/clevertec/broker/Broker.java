package ru.clevertec.broker;

import lombok.Getter;
import ru.clevertec.exception.TopicNotFoundException;
import ru.clevertec.model.Topic;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
public class Broker implements BrokerService {
    private final Map<String, Topic> topics;

    public Broker() {
        this.topics = new HashMap<>();
    }

    public Topic createTopic(String name, int maxConsumersToRead) {
        Topic topic = new Topic(name, maxConsumersToRead);
        topics.put(name, topic);
        return topic;
    }

    public Topic getTopic(String topicName) {
        return Optional.ofNullable(topics.get(topicName))
                .orElseThrow(() ->
                        new TopicNotFoundException("topic with name: " + topicName + " was not found"));
    }
}
