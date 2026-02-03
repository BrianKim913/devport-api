package kr.devport.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "Autocomplete suggestions wrapper with total matches count")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleAutocompleteListResponse {

    @Schema(description = "Top autocomplete suggestions")
    private List<ArticleAutocompleteResponse> suggestions;

    @Schema(description = "Total matching articles for 'Show all X results' UI", example = "42")
    private Long totalMatches;
}
