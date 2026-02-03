package kr.devport.api.repository;

import kr.devport.api.domain.entity.Article;
import kr.devport.api.dto.request.ArticleSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArticleRepositoryCustom {

    /**
     * QueryDSL을 사용한 동적 검색 (페이지네이션)
     * - 타입 안전한 쿼리 빌딩
     * - 8개 선택적 필터 조건 지원
     * - 키워드 검색 (한글/영문 제목)
     */
    Page<Article> searchWithCondition(ArticleSearchCondition condition, Pageable pageable);
}
