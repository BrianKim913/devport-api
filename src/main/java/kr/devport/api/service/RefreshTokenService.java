package kr.devport.api.service;

import kr.devport.api.domain.entity.RefreshToken;
import kr.devport.api.domain.entity.User;
import kr.devport.api.repository.RefreshTokenRepository;
import kr.devport.api.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Create and save a new refresh token for user
     */
    @Transactional
    public RefreshToken createRefreshToken(User user) {
        // Delete old refresh tokens for this user
        refreshTokenRepository.deleteByUser(user);

        String token = jwtTokenProvider.generateRefreshToken(user.getId());
        long expirationMs = jwtTokenProvider.getRefreshTokenExpirationMs();

        RefreshToken refreshToken = RefreshToken.builder()
            .user(user)
            .token(token)
            .expiresAt(LocalDateTime.now().plusSeconds(expirationMs / 1000))
            .createdAt(LocalDateTime.now())
            .build();

        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Find refresh token by token string
     */
    @Transactional(readOnly = true)
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    /**
     * Verify if refresh token is valid
     */
    @Transactional(readOnly = true)
    public boolean verifyRefreshToken(RefreshToken refreshToken) {
        if (refreshToken.isValid()) {
            return true;
        }

        // Delete expired or revoked token
        refreshTokenRepository.delete(refreshToken);
        return false;
    }

    /**
     * Revoke refresh token
     */
    @Transactional
    public void revokeRefreshToken(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(refreshToken -> {
            refreshToken.revoke();
            refreshTokenRepository.save(refreshToken);
        });
    }

    /**
     * Delete all refresh tokens for a user (logout)
     */
    @Transactional
    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }
}
