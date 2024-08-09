package com.exmple.queueservice;

import com.exmple.queueservice.config.handler.OrderQueueWebSocketHandler;
import com.exmple.queueservice.database.entity.QueueRequest;
import com.exmple.queueservice.database.repository.QueueRequestRepository;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class QueueService {

    private final BlockingQueue<QueueRequest> queue = new LinkedBlockingQueue<>();
    private Long currentTicket = 0L;

    private final QueueRequestRepository queueRequestRepository;

    public QueueService(QueueRequestRepository queueRequestRepository) {
        this.queueRequestRepository = queueRequestRepository;
    }

    public synchronized QueueRequest enterQueue() {
        currentTicket++;
        QueueRequest queueRequest = new QueueRequest();
        queueRequest.setUserId(UUID.randomUUID().toString());
        queueRequest.setTicketNumber(currentTicket);
        queueRequest.setProcessed(false);
        queueRequestRepository.save(queueRequest);
        queue.offer(queueRequest);
        return queueRequest;
    }

    public QueueRequest checkStatus(String userId) {
        return queueRequestRepository.findByUserId(userId);
    }

    @Scheduled(fixedDelay = 5000)
    public void processQueue() {
        try {
            QueueRequest currentRequest = queue.poll();
            if (currentRequest != null) {
                // 3초마다 처리 완료
                Thread.sleep(3000);

                currentRequest.setProcessed(true);
                queueRequestRepository.save(currentRequest);

                System.out.printf("Processing request for user: %s%n", currentRequest);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Scheduled(fixedDelay = 1000)
    public void processOrder() throws InterruptedException {
        QueueRequest currentRequest = queue.peek();
        if (currentRequest != null) {
            System.out.println("Current request: " + currentRequest);
            OrderQueueWebSocketHandler.broadcastQueueSize(currentRequest.getTicketNumber());
        }
    }

}
