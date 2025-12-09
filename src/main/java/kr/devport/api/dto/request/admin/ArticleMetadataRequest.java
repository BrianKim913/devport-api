package kr.devport.api.dto.request.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleMetadataRequest {
    private Integer stars;
    private Integer comments;
    private Integer upvotes;
    private String readTime;  // e.g., "5분", "12분"
    private String language;
}
