package kr.devport.api.dto.response;

import kr.devport.api.domain.entity.ModelCreator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DTO for ModelCreator information in API responses.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModelCreatorResponse {

    private Long id;
    private String externalId;  // UUID from API
    private String slug;        // e.g., "openai", "anthropic", "alibaba"
    private String name;        // e.g., "OpenAI", "Anthropic", "Alibaba"

    /**
     * Convert ModelCreator entity to DTO.
     */
    public static ModelCreatorResponse from(ModelCreator modelCreator) {
        if (modelCreator == null) {
            return null;
        }

        return ModelCreatorResponse.builder()
                .id(modelCreator.getId())
                .externalId(modelCreator.getExternalId())
                .slug(modelCreator.getSlug())
                .name(modelCreator.getName())
                .build();
    }
}
