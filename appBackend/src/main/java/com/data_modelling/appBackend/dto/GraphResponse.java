package com.data_modelling.appBackend.dto;

import lombok.*;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GraphResponse {
    private List<GraphNode> nodes;
    private List<GraphEdge> edges;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GraphNode {
        private String id;
        private String label;
        private String type;
        private long count;
        private Map<String, Object> data;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GraphEdge {
        private String id;
        private String source;
        private String target;
        private String label;
        private String relationship;
    }
}
