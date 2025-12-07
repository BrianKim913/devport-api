package kr.devport.api.dto.response;

import kr.devport.api.domain.entity.LLMBenchmark;
import kr.devport.api.domain.enums.BenchmarkType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Benchmark metadata for frontend display
 * Used for /api/llm/benchmark-categories endpoint
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LLMBenchmarkResponse {

    private BenchmarkType benchmarkType;
    private String displayName;
    private String categoryGroup;  // "Agentic", "Reasoning", "Coding", "Specialized", "Composite"
    private String description;  // Short description
    private String explanation;  // Detailed explanation for tooltips
    private Integer sortOrder;

    public static LLMBenchmarkResponse fromEntity(LLMBenchmark benchmark) {
        return LLMBenchmarkResponse.builder()
            .benchmarkType(benchmark.getBenchmarkType())
            .displayName(benchmark.getDisplayName())
            .categoryGroup(benchmark.getCategoryGroup())
            .description(benchmark.getDescription())
            .explanation(benchmark.getExplanation())
            .sortOrder(benchmark.getSortOrder())
            .build();
    }
}
