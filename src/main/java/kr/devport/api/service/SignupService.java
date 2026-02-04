package kr.devport.api.service;

import kr.devport.api.domain.entity.EmailVerificationToken;
import kr.devport.api.domain.entity.User;
import kr.devport.api.domain.enums.AuthProvider;
import kr.devport.api.domain.enums.UserRole;
import kr.devport.api.dto.request.SignupRequest;
import kr.devport.api.dto.response.AuthResponse;
import kr.devport.api.exception.DuplicateEmailException;
import kr.devport.api.exception.DuplicateUsernameException;
import kr.devport.api.repository.EmailVerificationTokenRepository;
import kr.devport.api.repository.UserRepository;
import kr.devport.api.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SignupService {

    private final UserRepository userRepository;
    private final EmailVerificationTokenRepository verificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public AuthResponse signup(SignupRequest request) {
        // Check username uniqueness
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateUsernameException("Username already exists: " + request.getUsername());
        }

        // Check email uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException("Email already exists: " + request.getEmail());
        }

        // Create user
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
            .username(request.getUsername())
            .password(passwordEncoder.encode(request.getPassword()))
            .email(request.getEmail())
            .name(request.getName())
            .authProvider(AuthProvider.local)
            .role(UserRole.USER)
            .emailVerified(false)
            .emailAddedAt(now)
            .createdAt(now)
            .updatedAt(now)
            .lastLoginAt(now)
            .build();

        user = userRepository.save(user);
        log.info("User created with username: {}", user.getUsername());

        // Generate email verification token
        String token = UUID.randomUUID().toString();
        EmailVerificationToken verificationToken = EmailVerificationToken.builder()
            .token(token)
            .user(user)
            .expiresAt(now.plusHours(24))
            .createdAt(now)
            .build();

        verificationTokenRepository.save(verificationToken);

        // Send verification email
        emailService.sendVerificationEmail(user, token);

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

    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }

    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }
}
