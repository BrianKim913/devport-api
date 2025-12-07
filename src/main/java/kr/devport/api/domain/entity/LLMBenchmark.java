package kr.devport.api.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.devport.api.domain.enums.BenchmarkType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Stores metadata about each LLM benchmark for frontend display.
 * This is reference data that doesn't change frequently.
 */
@Entity
@Table(name = "llm_benchmarks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LLMBenchmark {

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "benchmark_type", length = 100)
    private BenchmarkType benchmarkType;  // e.g., "TERMINAL_BENCH_HARD"

    @NotBlank
    @Column(nullable = false, length = 200, name = "display_name")
    private String displayName;  // e.g., "Terminal & Coding (Agentic)"

    @NotBlank
    @Column(nullable = false, length = 50, name = "category_group")
    private String categoryGroup;  // "Agentic", "Reasoning", "Coding", "Specialized", "Composite"

    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;  // Short description (1-2 sentences)

    @Column(columnDefinition = "TEXT")
    private String explanation;  // Detailed explanation for tooltips

    @Column(name = "sort_order")
    private Integer sortOrder;  // Display order in UI

    @NotNull
    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
