package kr.devport.api.domain.port.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "Port information response")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortResponse {

    @Schema(description = "Port external ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private String id;

    @Schema(description = "Port slug (URL identifier)", example = "llm")
    private String slug;

    @Schema(description = "Port number", example = "11434")
    private Integer portNumber;

    @Schema(description = "Port name", example = "LLMs")
    private String name;

    @Schema(description = "Port description", example = "Large Language Model tools and frameworks")
    private String description;

    @Schema(description = "Port accent color (hex)", example = "#a855f7")
    private String accentColor;

    @Schema(description = "Number of projects in this port", example = "12")
    private Integer projectCount;

    @Schema(description = "Recent releases count (last 30 days)", example = "5")
    private Integer recentReleaseCount;
}
