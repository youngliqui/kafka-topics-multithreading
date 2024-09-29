package ru.clevertec.model.thread;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.model.Message;
import ru.clevertec.model.Topic;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ProducerTest {
    private Producer producer;
    @Spy
    private final Topic topic = new Topic("test topic", 1);

    @BeforeEach
    void setUp() {
        List<String> messages = List.of("message1", "message2");
        producer = new Producer("test producer", topic, messages);
    }

    @Test
    void testProducerWriteMessages() throws InterruptedException {
        Thread producerThread = new Thread(producer);
        producerThread.start();
        producerThread.join();

        Mockito.verify(topic, Mockito.times(2)).publishMessage(any(Message.class));
        Assertions.assertThat(topic.getMessages())
                .hasSize(2)
                .contains(new Message("message1"), new Message("message2"));
    }
}