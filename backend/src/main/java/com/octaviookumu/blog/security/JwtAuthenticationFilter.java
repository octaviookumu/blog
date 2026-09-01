package com.octaviookumu.blog.security;

import com.octaviookumu.blog.services.AuthenticationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationService authenticationService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            // Extract the token to pass in to our authenticationService
            String token = extractToken(request);
            if (token != null) {
                UserDetails userDetails = authenticationService.validateToken(token);

                // if the credentials are valid
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                // set this on our security context
                SecurityContextHolder.getContext().setAuthentication(authentication);
                // makes the authentication object available for the rest of the request, essentially setting the authenticated user

                // can be handy later on: setting the user id, rather than having to look up the user by the email address each time, we use the user id
                if (userDetails instanceof BlogUserDetails) { // use this sparingly
                    request.setAttribute("userId", ((BlogUserDetails) userDetails).getId());
                }

                // at this point exceptions will bubble up, say if there's in an invalid
                // Since we are in a filter, we simply choose to not authenticate a user rather than return an exception each time
                // then we can rely on the application to do what it needs to do when there is no authenticated user
            }
        } catch (Exception e) {
            // Do not throw exceptions, just don't authenticate the user
            // Good practice to put a log line
            log.warn("Received invalid auth token");
        }

        filterChain.doFilter(request, response);

    }

    private String extractToken(HttpServletRequest http) {
        String bearerToken = http.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
