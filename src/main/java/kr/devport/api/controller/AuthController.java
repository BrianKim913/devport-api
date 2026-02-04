package kr.devport.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.devport.api.dto.request.ForgotPasswordRequest;
import kr.devport.api.dto.request.LoginRequest;
import kr.devport.api.dto.request.RefreshTokenRequest;
import kr.devport.api.dto.request.ResetPasswordRequest;
import kr.devport.api.dto.request.SignupRequest;
import kr.devport.api.dto.response.AuthResponse;
import kr.devport.api.dto.response.TokenResponse;
import kr.devport.api.dto.response.UserResponse;
import kr.devport.api.security.CustomUserDetails;
import kr.devport.api.service.AuthService;
import kr.devport.api.service.EmailVerificationService;
import kr.devport.api.service.LoginService;
import kr.devport.api.service.PasswordResetService;
import kr.devport.api.service.SignupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@Tag(name = "Authentication", description = "Authentication and user management endpoints")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AuthController {

    private final AuthService authService;
    private final SignupService signupService;
    private final LoginService loginService;
    private final EmailVerificationService emailVerificationService;
    private final PasswordResetService passwordResetService;

    @Operation(
        summary = "Get current user",
        description = "Retrieve information about the currently authenticated user. Requires a valid JWT token."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved user information",
            content = @Content(schema = @Schema(implementation = UserResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid or missing JWT token",
            content = @Content
        )
    })
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UserResponse user = authService.getCurrentUser(userDetails.getId());
        return ResponseEntity.ok(user);
    }

    @Operation(
        summary = "Refresh access token",
        description = "Get a new access token using a valid refresh token. Does not require authentication."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully refreshed access token",
            content = @Content(schema = @Schema(implementation = TokenResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid or expired refresh token",
            content = @Content
        )
    })
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshAccessToken(
        @Valid @RequestBody RefreshTokenRequest request
    ) {
        TokenResponse tokenResponse = authService.refreshAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(tokenResponse);
    }

    @Operation(
        summary = "Logout",
        description = "Revoke all refresh tokens for the current user. Requires a valid JWT token."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully logged out"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid or missing JWT token",
            content = @Content
        )
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        authService.logout(userDetails.getId());
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Sign up with username and password",
        description = "Create a new LOCAL account with username, password, and email. Sends verification email."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully created account",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Username or email already exists",
            content = @Content
        )
    })
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest request) {
        AuthResponse response = signupService.signup(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Login with username and password",
        description = "Authenticate with username and password for LOCAL accounts"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully authenticated",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid credentials",
            content = @Content
        )
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = loginService.login(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Verify email address",
        description = "Verify email address with token from email link"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Email verified successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid or expired token", content = @Content),
        @ApiResponse(responseCode = "404", description = "Token not found", content = @Content)
    })
    @PostMapping("/verify-email")
    public ResponseEntity<Map<String, String>> verifyEmail(@RequestParam String token) {
        emailVerificationService.verifyEmail(token);
        return ResponseEntity.ok(Map.of("message", "Email verified successfully"));
    }

    @Operation(
        summary = "Resend verification email",
        description = "Resend email verification link to the current user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Verification email sent"),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @PostMapping("/resend-verification")
    public ResponseEntity<Map<String, String>> resendVerification(
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        emailVerificationService.resendVerificationEmail(userDetails.getId());
        return ResponseEntity.ok(Map.of("message", "Verification email sent"));
    }

    @Operation(
        summary = "Request password reset",
        description = "Request password reset link via email (LOCAL accounts only)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password reset email sent"),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.createResetToken(request.getEmail());
        return ResponseEntity.ok(Map.of("message", "Password reset email sent"));
    }

    @Operation(
        summary = "Reset password",
        description = "Reset password with token from email link"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password reset successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid or expired token", content = @Content),
        @ApiResponse(responseCode = "404", description = "Token not found", content = @Content)
    })
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
    }

    @Operation(
        summary = "Check username availability",
        description = "Check if a username is available for registration"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Username availability checked")
    })
    @GetMapping("/check-username")
    public ResponseEntity<Map<String, Boolean>> checkUsername(@RequestParam String username) {
        boolean available = signupService.isUsernameAvailable(username);
        return ResponseEntity.ok(Map.of("available", available));
    }

    @Operation(
        summary = "Check email availability",
        description = "Check if an email is available for registration"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Email availability checked")
    })
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam String email) {
        boolean available = signupService.isEmailAvailable(email);
        return ResponseEntity.ok(Map.of("available", available));
    }
}
