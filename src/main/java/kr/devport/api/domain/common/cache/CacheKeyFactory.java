package kr.devport.api.domain.common.cache;

import kr.devport.api.domain.llm.enums.BenchmarkType;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Canonical cache key factory for scoped cached reads.
 * 
 * Provides deterministic key builders that normalize equivalent query parameter sets
 * to identical cache keys, preventing cross-query collisions and key drift.
 * 
 * Key canonicalization rules:
 * - Trim strings and normalize null/blank to "all"
 * - Lowercase case-insensitive filters
 * - Sort multi-value dimensions where order is not meaningful
 * - Include pagination dimensions deterministically
 * 
 * Auth segmentation policy:
 * Public caches remain unsegmented by user context (no auth-based key dimensions).
 * This is intentional - these endpoints serve public data with no user-specific filtering.
 */
public final class CacheKeyFactory {
    
    private CacheKeyFactory() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
    
    // ========== Article Domain Keys ==========
    
    /**
     * Key for article list endpoint (public, unsegmented by user).
     * 
     * Dimensions: category (optional), page, size
     * Example: "all_0_20" or "AI_0_20"
     */
    public static String articleListKey(kr.devport.api.domain.article.enums.Category category, int page, int size) {
        String categoryKey = category != null ? category.name() : "all";
        return String.format("%s_%d_%d", categoryKey, page, size);
    }
    
    /**
     * Key for trending ticker endpoint (public, unsegmented by user).
     * 
     * Dimensions: limit
     * Example: "10"
     */
    public static String trendingTickerKey(int limit) {
        return String.valueOf(limit);
    }
    
    // ========== Git Repository Domain Keys ==========
    
    /**
     * Key for git repository list endpoint (public, unsegmented by user).
     * 
     * Dimensions: category (optional), page, size
     * Example: "all_0_20" or "WEB_0_20"
     */
    public static String gitRepoListKey(kr.devport.api.domain.gitrepo.enums.Category category, int page, int size) {
        String categoryKey = category != null ? category.name() : "all";
        return String.format("%s_%d_%d", categoryKey, page, size);
    }
    
    /**
     * Key for trending git repositories endpoint (public, unsegmented by user).
     * 
     * Dimensions: page, size
     * Example: "0_20"
     */
    public static String trendingGitReposKey(int page, int size) {
        return String.format("%d_%d", page, size);
    }
    
    /**
     * Key for git repositories by language endpoint (public, unsegmented by user).
     * 
     * Dimensions: language (normalized), limit
     * Example: "java_10"
     */
    public static String gitReposByLanguageKey(String language, int limit) {
        String normalizedLanguage = normalizeString(language);
        return String.format("%s_%d", normalizedLanguage, limit);
    }
    
    // ========== LLM Domain Keys ==========
    
    /**
     * Key for LLM leaderboard endpoint (public, unsegmented by user).
     * 
     * Dimensions: benchmarkType (required), provider (optional), creatorSlug (optional),
     * license (optional), maxPrice (optional), minContextWindow (optional)
     * 
     * All optional filters are normalized to "all" when null/blank.
     * Numeric filters (maxPrice, minContextWindow) are included when present to prevent
     * collisions across distinct filter values.
     * 
     * Example: "GPQA_DIAMOND_openai_all_all_null_null"
     */
    public static String llmLeaderboardKey(
        BenchmarkType benchmarkType,
        String provider,
        String creatorSlug,
        String license,
        BigDecimal maxPrice,
        Long minContextWindow
    ) {
        String benchmarkKey = benchmarkType.name();
        String providerKey = normalizeString(provider);
        String creatorKey = normalizeString(creatorSlug);
        String licenseKey = normalizeString(license);
        String priceKey = maxPrice != null ? maxPrice.toPlainString() : "null";
        String contextKey = minContextWindow != null ? minContextWindow.toString() : "null";
        
        return String.format("%s_%s_%s_%s_%s_%s",
            benchmarkKey, providerKey, creatorKey, licenseKey, priceKey, contextKey);
    }
    
    /**
     * Key for all benchmarks endpoint (public, unsegmented by user).
     * 
     * No dimensions - single cache entry for all benchmarks.
     * Example: "all"
     */
    public static String allBenchmarksKey() {
        return "all";
    }
    
    // ========== Normalization Utilities ==========
    
    /**
     * Normalize a string filter to prevent key drift from equivalent values.
     * 
     * - Trim whitespace
     * - Convert null/blank to "all"
     * - Lowercase for case-insensitive matching
     */
    private static String normalizeString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "all";
        }
        return value.trim().toLowerCase();
    }
    
    /**
     * Normalize a list of strings to prevent key drift from order variations.
     * 
     * - Trim and lowercase each element
     * - Remove null/blank entries
     * - Sort alphabetically
     * - Join with comma separator
     */
    private static String normalizeList(String... values) {
        if (values == null || values.length == 0) {
            return "all";
        }
        
        String normalized = Arrays.stream(values)
            .filter(v -> v != null && !v.trim().isEmpty())
            .map(String::trim)
            .map(String::toLowerCase)
            .sorted()
            .collect(Collectors.joining(","));
        
        return normalized.isEmpty() ? "all" : normalized;
    }
}
