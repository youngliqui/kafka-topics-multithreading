package ru.clevertec.model.Thread;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.model.Message;
import ru.clevertec.model.Topic;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsumerTest {
    private Consumer consumer;
    private CountDownLatch latch;
    @Spy
    private Topic topic = new Topic("test topic", 1);

    @BeforeEach
    void setUp() {
        latch = new CountDownLatch(2);
        consumer = new Consumer("Test consumer");
        consumer.setTopic(topic);
        consumer.setLatch(latch);
    }

    @Test
    void testConsumerReadMessages() throws InterruptedException {
        Message message1 = new Message("message1");
        Message message2 = new Message("message2");
        topic.getMessages().addAll(List.of(message1, message2));

        when(topic.consumeMessage(0)).thenReturn(Optional.of(message1));
        when(topic.consumeMessage(1)).thenReturn(Optional.of(message2));

        Thread consumerThread = new Thread(consumer);
        consumerThread.start();

        latch.await(15, TimeUnit.SECONDS);

        assertThat(consumer.getReadMessages())
                .hasSize(2)
                .contains(message1, message2);
        assertThat(consumer.getLastIndex()).isEqualTo(2);
    }

    @Test
    void testConsumerReadMessagesWithInterruption() throws InterruptedException {
        doThrow(new InterruptedException()).when(topic).consumeMessage(anyInt());

        Thread consumerThread = new Thread(consumer);
        consumerThread.start();

        latch.await(5, TimeUnit.SECONDS);

        assertThat(consumerThread.isInterrupted()).isTrue();
    }
}