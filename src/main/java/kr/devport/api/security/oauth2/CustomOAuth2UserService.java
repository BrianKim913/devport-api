package kr.devport.api.security.oauth2;

import kr.devport.api.domain.entity.User;
import kr.devport.api.domain.enums.AuthProvider;
import kr.devport.api.domain.enums.UserRole;
import kr.devport.api.repository.UserRepository;
import kr.devport.api.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        return processOAuth2User(userRequest, oAuth2User);
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
            registrationId,
            oAuth2User.getAttributes()
        );

        if (oAuth2UserInfo.getEmail() == null || oAuth2UserInfo.getEmail().isEmpty()) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        AuthProvider authProvider = AuthProvider.valueOf(registrationId);
        Optional<User> userOptional = userRepository.findByAuthProviderAndProviderId(
            authProvider,
            oAuth2UserInfo.getId()
        );

        User user;
        if (userOptional.isPresent()) {
            user = updateExistingUser(userOptional.get(), oAuth2UserInfo);
        } else {
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
