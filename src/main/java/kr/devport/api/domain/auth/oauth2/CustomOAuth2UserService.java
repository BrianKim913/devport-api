package kr.devport.api.domain.auth.oauth2;

import kr.devport.api.domain.auth.entity.User;
import kr.devport.api.domain.auth.enums.AuthProvider;
import kr.devport.api.domain.auth.enums.UserRole;
import kr.devport.api.domain.auth.repository.UserRepository;
import kr.devport.api.domain.common.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // GitHub의 경우 기본 응답에 이메일이 없으면 별도 API로 조회한다.
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        log.info("Loading OAuth2 user for provider: {}", registrationId);

        if ("github".equals(registrationId)) {
            String currentEmail = oAuth2User.getAttribute("email");
            log.info("GitHub user email from attributes: {}", currentEmail);

            if (currentEmail == null) {
                log.info("Email is null, fetching from GitHub API");
                String email = fetchGitHubEmail(userRequest.getAccessToken().getTokenValue());
                log.info("Fetched email from GitHub API: {}", email);

                if (email != null) {
                    Map<String, Object> modifiedAttributes = new java.util.HashMap<>(oAuth2User.getAttributes());
                    modifiedAttributes.put("email", email);
                    oAuth2User = new org.springframework.security.oauth2.core.user.DefaultOAuth2User(
                        oAuth2User.getAuthorities(),
                        modifiedAttributes,
                        "id"
                    );
                }
            }
        }

        return processOAuth2User(userRequest, oAuth2User);
    }

    private String fetchGitHubEmail(String accessToken) {
        try {
            log.info("Fetching GitHub email from API");
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                "https://api.github.com/user/emails",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );

            List<Map<String, Object>> emails = response.getBody();
            log.info("GitHub API response - emails count: {}", emails != null ? emails.size() : 0);

            if (emails != null && !emails.isEmpty()) {
                for (Map<String, Object> emailData : emails) {
                    if (Boolean.TRUE.equals(emailData.get("primary"))) {
                        String primaryEmail = (String) emailData.get("email");
                        log.info("Found primary email: {}", primaryEmail);
                        return primaryEmail;
                    }
                }
                String firstEmail = (String) emails.get(0).get("email");
                log.info("No primary email found, using first email: {}", firstEmail);
                return firstEmail;
            }
            log.warn("No emails returned from GitHub API");
        } catch (Exception e) {
            log.error("Failed to fetch GitHub email", e);
        }
        return null;
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
            registrationId,
            oAuth2User.getAttributes()
        );

        log.info("Processing OAuth2 user - Provider: {}, Email: {}, ID: {}",
            registrationId, oAuth2UserInfo.getEmail(), oAuth2UserInfo.getId());

        if (oAuth2UserInfo.getEmail() == null || oAuth2UserInfo.getEmail().isEmpty()) {
            log.error("Email not found from OAuth2 provider: {}", registrationId);
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        AuthProvider authProvider = AuthProvider.valueOf(registrationId);
        Optional<User> userOptional = userRepository.findByAuthProviderAndProviderId(
            authProvider,
            oAuth2UserInfo.getId()
        );

        User user;
        if (userOptional.isPresent()) {
            log.info("Existing user found for provider: {}", authProvider);
            user = updateExistingUser(userOptional.get(), oAuth2UserInfo);
        } else {
            // 같은 이메일로 이미 등록된 계정이 있는지 확인
            Optional<User> existingEmailUser = userRepository.findByEmail(oAuth2UserInfo.getEmail());
            if (existingEmailUser.isPresent()) {
                User existing = existingEmailUser.get();
                log.error("Cannot register {} account - email {} already exists with provider: {}",
                    authProvider, oAuth2UserInfo.getEmail(), existing.getAuthProvider());
                throw new OAuth2AuthenticationException("같은 정보의 계정이 이미 존재합니다");
            }
            log.info("Registering new user with provider: {}", authProvider);
            user = registerNewUser(authProvider, oAuth2UserInfo);
        }

        return CustomUserDetails.create(user, oAuth2User.getAttributes());
    }

    private User registerNewUser(AuthProvider authProvider, OAuth2UserInfo oAuth2UserInfo) {
        User user = User.builder()
            .email(oAuth2UserInfo.getEmail())
            .name(oAuth2UserInfo.getName())
            .profileImageUrl(oAuth2UserInfo.getImageUrl())
            .authProvider(authProvider)
            .providerId(oAuth2UserInfo.getId())
            .role(UserRole.USER)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .lastLoginAt(LocalDateTime.now())
            .build();

        return userRepository.save(user);
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        existingUser.setName(oAuth2UserInfo.getName());
        existingUser.setProfileImageUrl(oAuth2UserInfo.getImageUrl());
        existingUser.setUpdatedAt(LocalDateTime.now());
        existingUser.setLastLoginAt(LocalDateTime.now());

        return userRepository.save(existingUser);
    }
}
