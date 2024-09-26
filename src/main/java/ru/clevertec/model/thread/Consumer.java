package ru.clevertec.model.thread;

import lombok.Getter;
import ru.clevertec.model.Message;
import ru.clevertec.model.Topic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Getter
public class Consumer implements Runnable {
    private final String name;
    private final Topic topic;
    private final CountDownLatch latch;
    private final List<Message> readMessages;
    private int lastIndex;

    public Consumer(String name, Topic topic, CountDownLatch latch) {
        this.name = name;
        this.topic = topic;
        this.latch = latch;
        readMessages = new ArrayList<>();
    }

    @Override
    public void run() {
        try {
            while (latch.getCount() > 0) {
                topic.consumeMessage(lastIndex)
                        .ifPresent(message -> {
                            System.out.println(name + " read message: " + message);
                            readMessages.add(message);
                            lastIndex++;
                            latch.countDown();
                        });
            }
            System.out.println(name + " finished reading messages");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
