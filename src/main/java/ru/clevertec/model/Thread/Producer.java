package ru.clevertec.model.Thread;

import lombok.Getter;
import lombok.Setter;
import ru.clevertec.model.Message;
import ru.clevertec.model.Topic;
import ru.clevertec.util.RandomTimeoutGenerator;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Getter
public class Producer implements Runnable {
    private String name;
    @Setter
    private Topic topic;
    private List<String> messages;

    public Producer(String name, List<String> messages) {
        this.name = name;
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
