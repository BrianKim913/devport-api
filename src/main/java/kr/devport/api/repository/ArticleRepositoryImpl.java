package kr.devport.api.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.devport.api.domain.entity.Article;
import kr.devport.api.domain.entity.QArticle;
import kr.devport.api.domain.enums.Category;
import kr.devport.api.domain.enums.ItemType;
import kr.devport.api.dto.request.ArticleSearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.util.StringUtils.hasText;

@Repository
@RequiredArgsConstructor
public class ArticleRepositoryImpl implements ArticleRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    private static final QArticle article = QArticle.article;

    @Override
    public Page<Article> searchWithCondition(ArticleSearchCondition condition, Pageable pageable) {
        List<Article> content = queryFactory
            .selectFrom(article)
            .where(
                categoryEq(condition.getCategory()),
                sourceEq(condition.getSource()),
                itemTypeEq(condition.getItemType()),
                keywordContains(condition.getKeyword()),
                scoreGoe(condition.getMinScore()),
                scoreLoe(condition.getMaxScore()),
                createdAtGoe(condition.getCreatedAfter()),
                createdAtLoe(condition.getCreatedBefore()),
                tagsContainsAny(condition.getTags())
            )
            .orderBy(article.score.desc(), article.createdAtSource.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory
            .select(article.count())
            .from(article)
            .where(
                categoryEq(condition.getCategory()),
                sourceEq(condition.getSource()),
                itemTypeEq(condition.getItemType()),
                keywordContains(condition.getKeyword()),
                scoreGoe(condition.getMinScore()),
                scoreLoe(condition.getMaxScore()),
                createdAtGoe(condition.getCreatedAfter()),
                createdAtLoe(condition.getCreatedBefore()),
                tagsContainsAny(condition.getTags())
            )
            .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    // ========== BooleanExpression Methods (Type-safe condition building) ==========

    private BooleanExpression categoryEq(Category category) {
        return category != null ? article.category.eq(category) : null;
    }

    private BooleanExpression sourceEq(String source) {
        return hasText(source) ? article.source.eq(source) : null;
    }

    private BooleanExpression itemTypeEq(ItemType itemType) {
        return itemType != null ? article.itemType.eq(itemType) : null;
    }

    private BooleanExpression keywordContains(String keyword) {
        if (!hasText(keyword)) {
            return null;
        }
        // Search in both Korean and English titles
        return article.summaryKoTitle.containsIgnoreCase(keyword)
            .or(article.titleEn.containsIgnoreCase(keyword));
    }

    private BooleanExpression scoreGoe(Integer minScore) {
        return minScore != null ? article.score.goe(minScore) : null;
    }

    private BooleanExpression scoreLoe(Integer maxScore) {
        return maxScore != null ? article.score.loe(maxScore) : null;
    }

    private BooleanExpression createdAtGoe(LocalDateTime from) {
        return from != null ? article.createdAtSource.goe(from) : null;
    }

    private BooleanExpression createdAtLoe(LocalDateTime to) {
        return to != null ? article.createdAtSource.loe(to) : null;
    }

    private BooleanExpression tagsContainsAny(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return null;
        }
        // Check if article has ANY of the specified tags
        // Note: ElementCollection requires subquery approach
        return article.tags.any().in(tags);
    }

    // ========== Autocomplete/Fulltext Search Methods ==========

    @Override
    public List<Article> searchAutocomplete(String query, int limit) {
        if (!hasText(query) || query.trim().length() < 2) {
            return List.of();
        }

        String searchTerm = query.trim();

        // Priority: 1 = title match, 2 = body-only match
        NumberExpression<Integer> priorityOrder = new CaseBuilder()
            .when(article.summaryKoTitle.containsIgnoreCase(searchTerm))
            .then(1)
            .otherwise(2);

        return queryFactory
            .selectFrom(article)
            .where(
                article.summaryKoTitle.containsIgnoreCase(searchTerm)
                    .or(article.summaryKoBody.containsIgnoreCase(searchTerm))
            )
            .orderBy(
                priorityOrder.asc(),           // Title matches first
                article.createdAtSource.desc() // Then by recency
            )
            .limit(limit)
            .fetch();
    }

    @Override
    public Page<Article> searchFulltext(String query, Pageable pageable) {
        if (!hasText(query) || query.trim().length() < 2) {
            return new PageImpl<>(List.of(), pageable, 0L);
        }

        String searchTerm = query.trim();

        // Priority: 1 = title match, 2 = body-only match
        NumberExpression<Integer> priorityOrder = new CaseBuilder()
            .when(article.summaryKoTitle.containsIgnoreCase(searchTerm))
            .then(1)
            .otherwise(2);

        BooleanExpression searchCondition = article.summaryKoTitle.containsIgnoreCase(searchTerm)
            .or(article.summaryKoBody.containsIgnoreCase(searchTerm));

        List<Article> content = queryFactory
            .selectFrom(article)
            .where(searchCondition)
            .orderBy(
                priorityOrder.asc(),           // Title matches first
                article.createdAtSource.desc() // Then by recency
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = countFulltextMatches(query);

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    @Override
    public Long countFulltextMatches(String query) {
        if (!hasText(query) || query.trim().length() < 2) {
            return 0L;
        }

        String searchTerm = query.trim();

        return queryFactory
            .select(article.count())
            .from(article)
            .where(
                article.summaryKoTitle.containsIgnoreCase(searchTerm)
                    .or(article.summaryKoBody.containsIgnoreCase(searchTerm))
            )
            .fetchOne();
    }
}
