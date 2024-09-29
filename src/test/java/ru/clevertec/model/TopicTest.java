package ru.clevertec.model;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class TopicTest {
    private Topic topic;

    @BeforeEach
    void setUp() {
        topic = new Topic("Topic1", 1);
    }

    @Test
    void shouldPublishMessage() {
        Message message = new Message("Test message");

        topic.publishMessage(message);

        List<Message> topicMessages = topic.getMessages();
        assertThat(topicMessages)
                .hasSize(1)
                .contains(message);
    }

    @Test
    void shouldConsumeMessage() throws InterruptedException {
        Message message = new Message("Test message");
        topic.getMessages().add(message);

        Optional<Message> actualMessage = topic.consumeMessage(0);

        assertThat(actualMessage)
                .isPresent()
                .get()
                .isEqualTo(message);
        assertThat(topic.getMessages()).hasSize(1);
    }

    @Test
    void testConcurrentConsumersConsumeMessages() throws InterruptedException {
        Message message1 = new Message("message1");
        Message message2 = new Message("message2");
        Message message3 = new Message("message3");

        topic.getMessages().addAll(List.of(message1, message2, message3));
        List<Optional<Message>> consumer1Messages = new ArrayList<>();
        List<Optional<Message>> consumer2Messages = new ArrayList<>();

        Thread consumer1 = new Thread(() -> {
            try {
                consumer1Messages.add(topic.consumeMessage(0));
                consumer1Messages.add(topic.consumeMessage(1));
                consumer1Messages.add(topic.consumeMessage(2));
            } catch (InterruptedException e) {
                fail(e.getMessage());
            }
        });
        Thread consumer2 = new Thread(() -> {
            try {
                consumer2Messages.add(topic.consumeMessage(0));
                consumer2Messages.add(topic.consumeMessage(1));
            } catch (InterruptedException e) {
                fail(e.getMessage());
            }
        });

        consumer1.start();
        consumer2.start();

        consumer1.join();
        consumer2.join();

        assertThat(topic.getMessages()).hasSize(3);
        assertThat(consumer1Messages)
                .hasSize(3)
                .contains(Optional.of(message1), Optional.of(message2), Optional.of(message3));
        assertThat(consumer2Messages)
                .hasSize(2)
                .contains(Optional.of(message1), Optional.of(message2));
    }
}