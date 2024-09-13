package ru.clevertec.model.Thread;

import lombok.Getter;
import lombok.Setter;
import ru.clevertec.model.Message;
import ru.clevertec.model.Topic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Getter
public class Consumer implements Runnable {
    private String name;
    @Setter
    private Topic topic;
    @Setter
    private CountDownLatch latch;
    private List<Message> readMessages;
    private int lastIndex;

    public Consumer(String name) {
        this.name = name;
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
