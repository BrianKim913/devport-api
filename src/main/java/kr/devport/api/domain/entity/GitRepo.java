package kr.devport.api.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.devport.api.domain.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * GitRepo entity for storing GitHub trending repositories
 * Separated from Article entity to handle repository-specific metadata
 */
@Entity
@Table(name = "git_repos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GitRepo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Repository details
    @Column(nullable = false, length = 500, name = "full_name")
    private String fullName;  // e.g., "facebook/react"

    @Column(nullable = false, length = 1000, unique = true)
    private String url;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Repository metadata
    @Column(length = 100)
    private String language;  // Programming language

    @Column
    private Integer stars;

    @Column
    private Integer forks;

    @Column(name = "stars_this_week")
    private Integer starsThisWeek;

    // Korean summary (LLM generated)
    @Column(length = 500, name = "summary_ko_title")
    private String summaryKoTitle;

    @Column(columnDefinition = "TEXT", name = "summary_ko_body")
    private String summaryKoBody;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private Category category;  // Technology category

    // Trending metrics
    @Column(nullable = false)
    @Builder.Default
    private Integer score = 0;

    // Timestamps
    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;

    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;
}
