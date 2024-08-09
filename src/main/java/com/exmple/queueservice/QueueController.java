package com.exmple.queueservice;

import com.exmple.queueservice.database.entity.QueueRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/queue")
public class QueueController {

    private final QueueService queueService;

    public QueueController(QueueService queueService) {
        this.queueService = queueService;
    }

    @PostMapping("/enter")
    public QueueRequest enterQueue() {
        return queueService.enterQueue();
    }

    @GetMapping("/status")
    public QueueRequest checkStatus(@RequestParam String userId) {
        return queueService.checkStatus(userId);
    }

}
