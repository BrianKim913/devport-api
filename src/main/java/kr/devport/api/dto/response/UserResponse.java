package kr.devport.api.dto.response;

import kr.devport.api.domain.enums.AuthProvider;
import kr.devport.api.domain.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String email;
    private String name;
    private String profileImageUrl;
    private AuthProvider authProvider;
    private UserRole role;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
}
