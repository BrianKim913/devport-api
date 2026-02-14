package kr.devport.api.domain.port.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Project overview (README summary) response")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectOverviewResponse {

    @Schema(description = "Korean summary (2-3 sentences)", example = "Ollama는 로컬에서 LLM을 쉽게 실행할 수 있는 도구입니다.")
    private String summary;

    @Schema(description = "Key highlights (6-8 bullets)", example = "[\"간편한 설치\", \"다양한 모델 지원\"]")
    private List<String> highlights;

    @Schema(description = "Quickstart commands", example = "curl https://ollama.ai/install.sh | sh")
    private String quickstart;

    @Schema(description = "Useful links (JSON)", example = "[{\"label\":\"GitHub\",\"url\":\"...\"}]")
    private String links;

    @Schema(description = "Source URL (README)", example = "https://github.com/ollama/ollama/blob/main/README.md")
    private String sourceUrl;

    @Schema(description = "Fetched timestamp", example = "2026-02-01T10:00:00")
    private LocalDateTime fetchedAt;

    @Schema(description = "Summarized timestamp", example = "2026-02-01T10:05:00")
    private LocalDateTime summarizedAt;
}
