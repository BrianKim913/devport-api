package kr.devport.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.devport.api.domain.enums.Category;
import kr.devport.api.domain.enums.ItemType;
import kr.devport.api.dto.request.ArticleSearchCondition;
import kr.devport.api.dto.response.ArticleDetailResponse;
import kr.devport.api.dto.response.ArticlePageResponse;
import kr.devport.api.dto.response.ArticleResponse;
import kr.devport.api.dto.response.TrendingTickerResponse;
import kr.devport.api.security.CustomUserDetails;
import kr.devport.api.service.ArticleService;
import kr.devport.api.service.MyPageService;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Articles", description = "Article and content management endpoints (public)")
@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final MyPageService myPageService;

    @Operation(
        summary = "Get articles with pagination",
        description = "Retrieve articles with optional category filtering and pagination support"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved articles",
            content = @Content(schema = @Schema(implementation = ArticlePageResponse.class))
        )
    })
    @GetMapping
    public ResponseEntity<ArticlePageResponse> getArticles(
        @Parameter(description = "Category filter: ALL, AI_LLM, DEVOPS_SRE, BACKEND, INFRA_CLOUD, OTHER")
        @RequestParam(required = false) Category category,
        @Parameter(description = "Page number (0-indexed)")
        @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Number of items per page")
        @RequestParam(defaultValue = "9") int size
    ) {
        ArticlePageResponse response = articleService.getArticles(category, page, size);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get trending ticker articles",
        description = "Retrieve trending articles for the ticker display"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved trending ticker articles"
        )
    })
    @GetMapping("/trending-ticker")
    public ResponseEntity<List<TrendingTickerResponse>> getTrendingTicker(
        @Parameter(description = "Number of articles for ticker display")
        @RequestParam(defaultValue = "20") int limit
    ) {
        List<TrendingTickerResponse> response = articleService.getTrendingTicker(limit);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Search articles with multiple filters (QueryDSL)",
        description = "Advanced search with 8 optional filters: category, source, itemType, keyword, score range, date range, and tags. Uses QueryDSL for type-safe dynamic query building."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved articles matching search criteria",
            content = @Content(schema = @Schema(implementation = ArticlePageResponse.class))
        )
    })
    @GetMapping("/search")
    public ResponseEntity<ArticlePageResponse> searchArticles(
        @Parameter(description = "Category filter")
        @RequestParam(required = false) Category category,

        @Parameter(description = "Source filter (e.g., 'hackernews', 'reddit', 'medium')")
        @RequestParam(required = false) String source,

        @Parameter(description = "Item type filter (BLOG, DISCUSSION, REPO)")
        @RequestParam(required = false) ItemType itemType,

        @Parameter(description = "Keyword search in Korean and English titles")
        @RequestParam(required = false) String keyword,

        @Parameter(description = "Minimum score")
        @RequestParam(required = false) Integer minScore,

        @Parameter(description = "Maximum score")
        @RequestParam(required = false) Integer maxScore,

        @Parameter(description = "Created after (ISO datetime)")
        @RequestParam(required = false) LocalDateTime createdAfter,

        @Parameter(description = "Created before (ISO datetime)")
        @RequestParam(required = false) LocalDateTime createdBefore,

        @Parameter(description = "Tags filter (matches ANY of the tags)")
        @RequestParam(required = false) List<String> tags,

        @Parameter(description = "Page number (0-indexed)")
        @RequestParam(defaultValue = "0") int page,

        @Parameter(description = "Number of items per page")
        @RequestParam(defaultValue = "9") int size
    ) {
        ArticleSearchCondition condition = ArticleSearchCondition.builder()
            .category(category)
            .source(source)
            .itemType(itemType)
            .keyword(keyword)
            .minScore(minScore)
            .maxScore(maxScore)
            .createdAfter(createdAfter)
            .createdBefore(createdBefore)
            .tags(tags)
            .build();

        ArticlePageResponse response = articleService.searchArticles(condition, page, size);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get article detail by UUID",
        description = "Retrieve detailed information about a specific article including full Korean summary (Markdown)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved article detail",
            content = @Content(schema = @Schema(implementation = ArticleDetailResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Article not found"
        )
    })
    @GetMapping("/{externalId}")
    public ResponseEntity<ArticleDetailResponse> getArticleByExternalId(
        @Parameter(description = "Article external UUID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
        @PathVariable String externalId
    ) {
        ArticleDetailResponse response = articleService.getArticleByExternalId(externalId);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Track article view",
        description = "Track article view and auto-record read history for authenticated users"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "View tracked successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Article not found"
        )
    })
    @PostMapping("/{articleId}/view")
    public ResponseEntity<Void> trackView(
        @Parameter(description = "Article external UUID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
        @PathVariable String articleId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails != null) {
            myPageService.trackArticleView(userDetails.getId(), articleId);
        }
        return ResponseEntity.ok().build();
    }
}
