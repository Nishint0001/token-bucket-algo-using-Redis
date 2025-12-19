package com.example.token_bucket_algo_using.Redis.filter;

import com.example.token_bucket_algo_using.Redis.service.RateLimiterService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RateLimiterFilter extends OncePerRequestFilter {

    private final RateLimiterService rateLimiterService;

    public RateLimiterFilter(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String clientIp = request.getRemoteAddr();
        String key = "rate:ip:" + clientIp;
        boolean allowed=rateLimiterService.allowRequest(key,60,1);
        System.out.println("RateLimit key=" + key + " allowed=" + allowed);
        if(!allowed){
            response.setStatus(429);
            response.getWriter().write("Too many requests");
            return;
        }

        filterChain.doFilter(request,response);
    }
}
