package com.knu.subway.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knu.subway.entity.dto.SubwayDTO;
import com.knu.subway.webSocket.WebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class JsonConverter {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(WebSocketHandler.class);

    public List<String> convertToJsonList(List<SubwayDTO> data) {
        return data.stream()
                .map(dto -> {
                    try {
                        return objectMapper.writeValueAsString(dto);
                    } catch (Exception e) {
                        log.error("Error converting SubwayDTO to JSON: {}", dto, e);
                        return "{}"; // Return empty JSON object on error
                    }
                })
                .collect(Collectors.toList());
    }

    public String joinJsonStrings(List<String> jsonData) {
        return String.join("\n", jsonData);
    }
}
