package kr.devport.api.security.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.devport.api.domain.entity.RefreshToken;
import kr.devport.api.domain.entity.User;
import kr.devport.api.repository.UserRepository;
import kr.devport.api.security.CustomUserDetails;
import kr.devport.api.security.JwtTokenProvider;
import kr.devport.api.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;

    @Value("${app.oauth2.redirect-uri}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // Generate access token (1 hour)
        String accessToken = jwtTokenProvider.generateAccessToken(userDetails.getId());

        // Generate and save refresh token (30 days)
        User user = userRepository.findById(userDetails.getId())
            .orElseThrow(() -> new RuntimeException("User not found"));
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return UriComponentsBuilder.fromUriString(redirectUri)
            .queryParam("accessToken", accessToken)
            .queryParam("refreshToken", refreshToken.getToken())
            .build()
            .toUriString();
    }
}
