package ru.clevertec.broker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.clevertec.exception.TopicNotFoundException;
import ru.clevertec.model.Thread.Consumer;
import ru.clevertec.model.Thread.Producer;
import ru.clevertec.model.Topic;

import java.util.List;
import java.util.concurrent.TimeUnit;

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
        broker.getTopics().add(topic);

        Topic actualTopic = broker.getTopic(topicName);

        assertThat(actualTopic).isNotNull().isEqualTo(topic);
    }

    @Test
    void shouldThrowExceptionWhenTopicNotFound() {
        String topicName = "dummy";

        assertThrows(TopicNotFoundException.class, () ->
                broker.getTopic(topicName));
    }

    @Test
    void testStartConsumer() throws InterruptedException {
        String topicName = "test topic";
        Topic topic = new Topic(topicName, 2);
        broker.getTopics().add(topic);
        Consumer consumer = new Consumer("test consumer");

        broker.startConsumer(consumer, topicName, 2);
        TimeUnit.SECONDS.sleep(2);

        assertThat(consumer.getTopic()).isNotNull();
        assertThat(consumer.getTopic().getName()).isEqualTo(topicName);
        assertThat(consumer.getLatch().getCount()).isEqualTo(2);
    }

    @Test
    void testStartProducer() throws InterruptedException {
        String topicName = "test topic";
        Topic topic = new Topic(topicName, 1);
        broker.getTopics().add(topic);
        Producer producer = new Producer("test producer", List.of("message1", "message2"));

        broker.startProducer(producer, topicName);
        TimeUnit.SECONDS.sleep(2);

        assertThat(producer.getTopic()).isNotNull();
        assertThat(producer.getTopic().getName()).isEqualTo(topicName);
    }

    @Test
    void testJoinConsumers() throws InterruptedException {
        String topicName = "test topic";
        broker.createTopic(topicName, 2);

        Producer producer = new Producer("producer1", List.of("message1", "message2"));
        Consumer consumer1 = new Consumer("consumer1");
        Consumer consumer2 = new Consumer("consumer2");

        broker.startProducer(producer, topicName);
        broker.startConsumer(consumer1, topicName, 2);
        broker.startConsumer(consumer2, topicName, 2);


        broker.joinConsumers(List.of(consumer1, consumer2));


        assertThat(consumer1.getLatch().getCount()).isEqualTo(0);
        assertThat(consumer2.getLatch().getCount()).isEqualTo(0);
    }
}