package com.qatester.hub.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class QaWebSocketHandler extends TextWebSocketHandler {

    private final CopyOnWriteArraySet<WebSocketSession> sessions = new CopyOnWriteArraySet<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        String sessionId = session.getId();
        System.out.println("Client connected: " + sessionId);

        // Send welcome message
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(
                Map.of("data", "Connected to server")
        )));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        System.out.println("Client disconnected: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();

        try {
            Map<String, Object> data = objectMapper.readValue(payload, Map.class);

            // Check token
            String token = (String) data.get("token");
            if (!"secret123".equals(token)) {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(
                        Map.of("error", "Unauthorized")
                )));
                return;
            }

            // Handle broadcast
            String type = (String) data.get("type");
            if ("broadcast".equals(type)) {
                String msg = (String) data.get("msg");
                Map<String, Object> broadcastMsg = Map.of(
                        "from", "server",
                        "msg", msg != null ? msg : ""
                );
                String jsonMsg = objectMapper.writeValueAsString(broadcastMsg);

                for (WebSocketSession s : sessions) {
                    if (s.isOpen()) {
                        s.sendMessage(new TextMessage(jsonMsg));
                    }
                }
            } else {
                // Echo message back
                Map<String, Object> response = Map.of("echo", data);
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
            }

        } catch (Exception e) {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(
                    Map.of("error", "Invalid JSON")
            )));
        }
    }
}
