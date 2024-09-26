package ru.clevertec.broker;

import ru.clevertec.model.Topic;


public interface BrokerService {
    Topic createTopic(String name, int maxConsumersToRead);

    Topic getTopic(String topicName);
}
