package kr.devport.api.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Represents an LLM model creator/provider (e.g., OpenAI, Anthropic, Alibaba).
 * This entity stores provider information from the Artificial Analysis API.
 */
@Entity
@Table(name = "model_creators")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModelCreator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * External ID from Artificial Analysis API (UUID format).
     * Example: "d874d370-74d3-4fa0-ba00-5272f92f946b"
     */
    @Column(unique = true, length = 100, name = "external_id")
    private String externalId;

    /**
     * URL-friendly slug identifier.
     * Example: "alibaba", "openai", "anthropic"
     */
    @NotBlank
    @Column(nullable = false, unique = true, length = 100)
    private String slug;

    /**
     * Display name of the model creator.
     * Example: "Alibaba", "OpenAI", "Anthropic"
     */
    @NotBlank
    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;

    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
