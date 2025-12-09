package kr.devport.api.controller.admin;

import jakarta.validation.Valid;
import kr.devport.api.dto.request.admin.ArticleCreateRequest;
import kr.devport.api.dto.request.admin.ArticleUpdateRequest;
import kr.devport.api.dto.response.ArticleResponse;
import kr.devport.api.service.admin.ArticleAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/articles")
@RequiredArgsConstructor
public class ArticleAdminController {

    private final ArticleAdminService articleAdminService;

    @PostMapping
    public ResponseEntity<ArticleResponse> createArticle(@Valid @RequestBody ArticleCreateRequest request) {
        ArticleResponse response = articleAdminService.createArticle(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticleResponse> updateArticle(
        @PathVariable Long id,
        @Valid @RequestBody ArticleUpdateRequest request
    ) {
        ArticleResponse response = articleAdminService.updateArticle(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        articleAdminService.deleteArticle(id);
        return ResponseEntity.noContent().build();
    }
}
