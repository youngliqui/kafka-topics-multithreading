package ru.clevertec.model;

import lombok.Getter;
import ru.clevertec.util.RandomTimeoutGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Getter
public class Topic {
    private String name;
    private List<Message> messages;

    private final Semaphore semaphore;
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    public Topic(String name, int maxConsumers) {
        this.messages = new ArrayList<>();
        this.name = name;
        this.semaphore = new Semaphore(maxConsumers);
    }

    public void publishMessage(Message message) {
        lock.lock();
        try {
            messages.add(message);
            System.out.println(message + " was published");
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public Optional<Message> consumeMessage(int index) throws InterruptedException {
        semaphore.acquire();
        try {
            lock.lock();
            try {
                if (index >= messages.size()) {
                    System.out.println("Wait! index: " + index + ", thread: " + Thread.currentThread().getName());
                    condition.await(10, TimeUnit.SECONDS);
                    System.out.println("Go! index: " + index + ", thread: " + Thread.currentThread().getName());
                    return Optional.empty();
                }
                TimeUnit.MILLISECONDS.sleep(RandomTimeoutGenerator.generateRandomTimeout(500, 1500));
                return Optional.ofNullable(messages.get(index));
            } finally {
                lock.unlock();
            }
        } finally {
            semaphore.release();
        }
    }
}
