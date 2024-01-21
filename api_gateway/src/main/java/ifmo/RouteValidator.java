package ifmo;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;

@Service
public class RouteValidator {
    public static final List<String> openEndpoints = List.of(
            "/api/v1/auth/register",
            "/api/v1/auth/authenticate",
            "/api/v1/auth/verify"
    );

    public Predicate<ServerHttpRequest> isSecured =
            serverHttpRequest -> openEndpoints.stream()
                    .noneMatch(uri -> serverHttpRequest.getURI().getPath().contains(uri));
}

