package com.example.demo.utils;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

public class JwtAuthenticationTokenFilter {
	
	private static final String AUTH_HEADER = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	protected void doFilterInternal(HttpServletRequest request,
						HttpServletResponse response, FilterChain chain)
	                    throws ServletException, IOException {
		
		String token = request.getHeader(AUTH_HEADER);
		
		if (token != null && token.startsWith(BEARER_PREFIX)) {
			token = token.substring(7);
		}		
		
		String username = jwtTokenUtil.getUsernameFromToken(token);
		
		if (username != null &&
			SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails =
					this.userDetailsService.loadUserByUsername(username);
			
			if (jwtTokenUtil.tokenValido(token)) {
					UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,null, userDetails.getAuthorities());			
										
					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					
					SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}
		
		chain.doFilter(request, response);
	}

}
