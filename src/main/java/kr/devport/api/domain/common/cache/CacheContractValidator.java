package kr.devport.api.domain.common.cache;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Validates that all required cache names from CacheNames are initialized in the CacheManager.
 * Fails application startup if cache name drift is detected, preventing runtime issues.
 */
@Component
public class CacheContractValidator {
    
    private static final Logger log = LoggerFactory.getLogger(CacheContractValidator.class);
    
    private final CacheManager cacheManager;
    
    public CacheContractValidator(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
    
    @PostConstruct
    public void validateCacheContract() {
        Set<String> requiredCaches = getRequiredCacheNames();
        Collection<String> initializedCaches = cacheManager.getCacheNames();
        
        Set<String> missingCaches = requiredCaches.stream()
            .filter(cacheName -> !initializedCaches.contains(cacheName))
            .collect(Collectors.toSet());
        
        if (!missingCaches.isEmpty()) {
            String errorMessage = String.format(
                "Cache contract violation: Required caches are not initialized in CacheManager: %s. " +
                "Initialized caches: %s. Please ensure all CacheNames constants have corresponding TTL configuration.",
                missingCaches, initializedCaches
            );
            log.error(errorMessage);
            throw new IllegalStateException(errorMessage);
        }
        
        log.info("Cache contract validation passed. All {} required caches are initialized: {}", 
            requiredCaches.size(), requiredCaches);
    }
    
    private Set<String> getRequiredCacheNames() {
        // Extract all cache names from CacheNames constants via reflection
        Set<String> cacheNames = new HashSet<>();
        try {
            for (var field : CacheNames.class.getDeclaredFields()) {
                if (field.getType() == String.class && java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                    cacheNames.add((String) field.get(null));
                }
            }
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to extract cache names from CacheNames class", e);
        }
        return cacheNames;
    }
}
