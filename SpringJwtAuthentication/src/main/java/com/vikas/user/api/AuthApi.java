package com.vikas.user.api;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.vikas.jwt.JwtTokenUtil;
import com.vikas.user.User;

@RestController
public class AuthApi {

	@Autowired
	AuthenticationManager authManager;
	
	@Autowired
	JwtTokenUtil jwtUtil;

	@PostMapping("/auth/login")
	public ResponseEntity<?> login(@RequestBody @Valid AuthRequest authRequest) {
		try {
			Authentication authentication = authManager.authenticate(
					new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
			User user = (User) authentication.getPrincipal();
			
			String accessToken = jwtUtil.generateAccessToken(user);
			AuthResponse authResponse = new AuthResponse(user.getEmail(), accessToken);
			
			return ResponseEntity.ok(authResponse);
		} catch (BadCredentialsException bce) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

}
