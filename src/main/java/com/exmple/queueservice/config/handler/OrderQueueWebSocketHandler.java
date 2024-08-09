package com.exmple.queueservice.config.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class OrderQueueWebSocketHandler extends TextWebSocketHandler {

    private static final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.printf("append session: %s\n", session.getId());
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        System.out.printf("remove session: %s, status: \n", session.getId(), status.getReason());
        sessions.remove(session);
    }

    public static void broadcastQueueSize(Long currentTicketNumber) {
        sessions.forEach(session -> {
            try {
                Queue queue = new Queue(currentTicketNumber);
                session.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(queue)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}

class Queue {

    private final Long ticketNumber;

    public Queue(Long ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public Long getTicketNumber() {
        return ticketNumber;
    }
}
