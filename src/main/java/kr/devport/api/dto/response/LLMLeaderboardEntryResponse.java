package kr.devport.api.dto.response;

import kr.devport.api.domain.entity.LLMModel;
import kr.devport.api.domain.enums.BenchmarkType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Leaderboard entry for specific benchmark
 * Used for /api/llm/leaderboard/{benchmarkType} endpoint
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LLMLeaderboardEntryResponse {

    private Long id;
    private String modelId;
    private String modelName;
    private String provider;
    private String license;

    // The score for the specific benchmark being queried
    private BigDecimal score;
    private Integer rank;

    // Additional useful metrics
    private BigDecimal priceBlended;
    private Long contextWindow;

    public static LLMLeaderboardEntryResponse fromEntity(
        LLMModel model,
        BenchmarkType benchmarkType,
        BigDecimal score,
        Integer rank
    ) {
        return LLMLeaderboardEntryResponse.builder()
            .id(model.getId())
            .modelId(model.getModelId())
            .modelName(model.getModelName())
            .provider(model.getProvider())
            .license(model.getLicense())
            .score(score)
            .rank(rank)
            .priceBlended(model.getPriceBlended())
            .contextWindow(model.getContextWindow())
            .build();
    }
}
