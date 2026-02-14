package kr.devport.api.domain.port.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "project_overviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectOverview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false, unique = true)
    private Project project;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String summary;

    @ElementCollection
    @CollectionTable(name = "project_overview_highlights", joinColumns = @JoinColumn(name = "overview_id"))
    @Column(name = "highlight", columnDefinition = "TEXT")
    @Builder.Default
    private List<String> highlights = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String quickstart;

    @Column(columnDefinition = "TEXT")
    private String links;

    @Column(name = "source_url", length = 500)
    private String sourceUrl;

    @Column(name = "raw_hash", length = 64)
    private String rawHash;

    @Column(name = "fetched_at")
    private LocalDateTime fetchedAt;

    @Column(name = "summarized_at")
    private LocalDateTime summarizedAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
