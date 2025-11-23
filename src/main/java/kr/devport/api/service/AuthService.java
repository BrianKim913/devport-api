package kr.devport.api.service;

import kr.devport.api.domain.entity.User;
import kr.devport.api.dto.response.UserResponse;
import kr.devport.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;

    /**
     * Get current user information
     */
    public UserResponse getCurrentUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        return convertToUserResponse(user);
    }

    /**
     * Convert User entity to UserResponse DTO
     */
    private UserResponse convertToUserResponse(User user) {
        return UserResponse.builder()
            .id(user.getId())
            .email(user.getEmail())
            .name(user.getName())
            .profileImageUrl(user.getProfileImageUrl())
            .authProvider(user.getAuthProvider())
            .role(user.getRole())
            .createdAt(user.getCreatedAt())
            .lastLoginAt(user.getLastLoginAt())
            .build();
    }
}
