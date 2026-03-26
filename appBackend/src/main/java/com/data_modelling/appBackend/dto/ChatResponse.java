package com.data_modelling.appBackend.dto;

import lombok.*;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatResponse {
    private String question;
    private String sql;
    private String answer;
    private List<Map<String, Object>> results;
    private String error;
}
