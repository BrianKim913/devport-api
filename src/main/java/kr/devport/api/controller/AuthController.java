package kr.devport.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.devport.api.dto.request.RefreshTokenRequest;
import kr.devport.api.dto.response.TokenResponse;
import kr.devport.api.dto.response.UserResponse;
import kr.devport.api.security.CustomUserDetails;
import kr.devport.api.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Tag(name = "Authentication", description = "Authentication and user management endpoints")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AuthController {

    private final AuthService authService;

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
}
