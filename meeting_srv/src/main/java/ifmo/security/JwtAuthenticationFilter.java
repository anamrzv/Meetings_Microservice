package ifmo.security;

import ifmo.exceptions.CustomBadRequestException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private static final int START_OF_JWT_TOKEN = 7;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userLogin;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        jwt = authHeader.substring(START_OF_JWT_TOKEN);
        try {
            userLogin = jwtService.extractUserLogin(jwt);
        } catch (CustomBadRequestException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        if (userLogin != null && jwtService.isTokenValid(jwt)) {
            request.setAttribute("Username", userLogin);
            filterChain.doFilter(request, response);
        } else response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
