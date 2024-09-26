package ru.clevertec.broker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.clevertec.exception.TopicNotFoundException;
import ru.clevertec.model.Topic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BrokerTest {
    private Broker broker;

    @BeforeEach
    void setUp() {
        broker = new Broker();
    }

    @Test
    void shouldCreateTopic() {
        String topicName = "new topic";
        int maxConsumers = 3;

        Topic actualTopic = broker.createTopic(topicName, maxConsumers);

        assertThat(actualTopic).isNotNull();
        assertThat(actualTopic.getName()).isEqualTo(topicName);
        assertThat(actualTopic.getSemaphore().availablePermits()).isEqualTo(maxConsumers);
    }

    @Test
    void shouldGetTopic() {
        String topicName = "test topic";
        Topic topic = new Topic(topicName, 2);
        broker.getTopics().put(topicName, topic);

        Topic actualTopic = broker.getTopic(topicName);

        assertThat(actualTopic).isNotNull().isEqualTo(topic);
    }

    @Test
    void shouldThrowExceptionWhenTopicNotFound() {
        String topicName = "dummy";

        assertThrows(TopicNotFoundException.class, () ->
                broker.getTopic(topicName));
    }
}