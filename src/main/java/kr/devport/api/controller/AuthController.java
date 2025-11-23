package kr.devport.api.controller;

import kr.devport.api.dto.response.UserResponse;
import kr.devport.api.security.CustomUserDetails;
import kr.devport.api.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * GET /api/auth/me
     * Get current authenticated user information
     *
     * @param userDetails Current authenticated user
     * @return Current user information
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UserResponse user = authService.getCurrentUser(userDetails.getId());
        return ResponseEntity.ok(user);
    }
}
