package com.example.inventoryAuth.Filter;


import com.example.inventoryAuth.Utility.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.example.inventoryAuth.Service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    @Autowired
    public JwtAuthFilter(JwtUtil jwtUtil, CustomUserDetailsService customUserDetailsService){
        this.jwtUtil=jwtUtil;
        this.customUserDetailsService=customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();

        if (path.startsWith("/api/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        String username=null;
        String token=null;

        try {
            if(authHeader!=null && authHeader.startsWith("Bearer ")){
                token = authHeader.substring(7);
                username = jwtUtil.extractUsername(token);
            }

            if(username!=null && SecurityContextHolder.getContext().getAuthentication()==null){
                UserDetails user = customUserDetailsService.loadUserByUsername(username);

                if(jwtUtil.isTokenValid(username,user,token)){
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities());

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Stale, malformed, or otherwise unusable token (e.g. it names a user
            // that no longer exists after a DB reset). Fall through as anonymous
            // rather than letting the exception propagate and turn every request -
            // including public endpoints - into a blanket 403 via the default
            // AuthenticationEntryPoint.
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request,response);

    }
}
