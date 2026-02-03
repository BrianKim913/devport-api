package kr.devport.api.repository;

import kr.devport.api.domain.entity.Article;
import kr.devport.api.dto.request.ArticleSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ArticleRepositoryCustom {

    /**
     * QueryDSL을 사용한 동적 검색 (페이지네이션)
     * - 타입 안전한 쿼리 빌딩
     * - 8개 선택적 필터 조건 지원
     * - 키워드 검색 (한글/영문 제목)
     */
    Page<Article> searchWithCondition(ArticleSearchCondition condition, Pageable pageable);

    /**
     * 자동완성 검색 (제목 우선, 본문 검색)
     * - 제목 매칭 우선 정렬
     * - 최신순 2차 정렬
     * - 최소 2자 이상 쿼리 필요
     */
    List<Article> searchAutocomplete(String query, int limit);

    /**
     * 전체 텍스트 검색 (페이지네이션)
     * - 한글 제목/본문 검색
     * - 제목 매칭 우선 정렬
     * - 최신순 2차 정렬
     */
    Page<Article> searchFulltext(String query, Pageable pageable);

    /**
     * 전체 텍스트 검색 결과 수 카운트
     * - "전체 X개 결과 보기" UI용
     */
    Long countFulltextMatches(String query);
}
