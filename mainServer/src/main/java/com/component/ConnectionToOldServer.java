package com.component;

import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Lazy(false)
@Component
public class ConnectionToOldServer {

    private final WebClient webClient;

    public ConnectionToOldServer() {
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:8082")
                .build();
    }

    public List<Map<String, Object>> getClients() {
        return webClient.post()
                .uri("/clients")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .block();
    }

    public List<Map<String, Object>> getNotes(String agency, String dateFrom, String dateTo, String clientGuid) {
        Map<String, Object> notesFrom = Map.of(
                "agency", agency,
                "dateFrom", dateFrom,
                "dateTo", dateTo,
                "clientGuid", clientGuid);
        return webClient.post()
                .uri("/notes")
                .bodyValue(notesFrom)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .block();
    }
}