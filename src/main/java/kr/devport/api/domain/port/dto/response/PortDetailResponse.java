package kr.devport.api.domain.port.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Schema(description = "Port detail with projects and hot releases")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortDetailResponse {

    @Schema(description = "Port information")
    private PortResponse port;

    @Schema(description = "Projects in this port")
    private List<ProjectSummaryResponse> projects;

    @Schema(description = "Hot releases (high-impact events)")
    private List<HotReleaseResponse> hotReleases;
}
