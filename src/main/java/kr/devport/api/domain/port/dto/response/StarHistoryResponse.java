package kr.devport.api.domain.port.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Schema(description = "Star history data point")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StarHistoryResponse {

    @Schema(description = "Date", example = "2026-01-01")
    private LocalDate date;

    @Schema(description = "Star count on this date", example = "44500")
    private Integer stars;
}
