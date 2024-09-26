package ru.clevertec.model.thread;

import lombok.Getter;
import ru.clevertec.model.Message;
import ru.clevertec.model.Topic;
import ru.clevertec.util.RandomTimeoutGenerator;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Getter
public class Producer implements Runnable {
    private final String name;
    private final Topic topic;
    private final List<String> messages;

    public Producer(String name, Topic topic, List<String> messages) {
        this.name = name;
        this.topic = topic;
        this.messages = messages;
    }

    @Override
    public void run() {
        for (String content : messages) {
            Message message = new Message(content);
            topic.publishMessage(message);
            try {
                TimeUnit.MILLISECONDS.sleep(RandomTimeoutGenerator.generateRandomTimeout(1000, 3000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
