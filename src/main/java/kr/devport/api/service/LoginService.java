package kr.devport.api.service;

import kr.devport.api.domain.entity.User;
import kr.devport.api.domain.enums.AuthProvider;
import kr.devport.api.dto.request.LoginRequest;
import kr.devport.api.dto.response.AuthResponse;
import kr.devport.api.exception.InvalidCredentialsException;
import kr.devport.api.exception.OAuth2AccountException;
import kr.devport.api.repository.UserRepository;
import kr.devport.api.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public AuthResponse login(LoginRequest request) {
        // Find user by username
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

        // Verify authProvider is LOCAL
        if (user.getAuthProvider() != AuthProvider.local) {
            throw new OAuth2AccountException(
                "This account uses " + user.getAuthProvider().name() + " login. Please use OAuth2 to sign in."
            );
        }

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        // Update last login
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("User logged in: {}", user.getUsername());

        // Generate JWT tokens
        String accessToken = jwtTokenProvider.createAccessToken(user.getId());
        String refreshToken = refreshTokenService.createRefreshToken(user).getToken();

        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .expiresIn(jwtTokenProvider.getAccessTokenExpirationMs())
            .build();
    }
}
