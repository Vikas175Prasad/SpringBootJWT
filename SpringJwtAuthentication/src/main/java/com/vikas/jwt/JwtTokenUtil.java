package com.vikas.jwt;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.vikas.user.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtTokenUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenUtil.class);

	public static final long EXPIRY = 24 * 60 * 60 * 1000; // 24hrs

	@Value("${app.jwt.secret}")
	private String secretKey;

	public String generateAccessToken(User user) {
		return Jwts.builder().setSubject(user.getId() + "," + user.getEmail()).setIssuer("Vikas")
				.setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis() + EXPIRY))
				.signWith(SignatureAlgorithm.HS512, secretKey).compact();
	}

	public boolean validateAccessToken(String accessToken) {
		try {
			Jwts.parser().setSigningKey(secretKey).parseClaimsJws(accessToken);
			return true;
		} catch (ExpiredJwtException eje) {
			LOGGER.error("JWT expired" + eje);
		} catch (IllegalArgumentException iae) {
			LOGGER.error("Token is null, empty or has only whitespaces" + iae);
		} catch (MalformedJwtException mje) {
			LOGGER.error("JWT is invalid" + mje);
		} catch (UnsupportedJwtException uje) {
			LOGGER.error("JWT is not supported" + uje);
		} catch (SignatureException se) {
			LOGGER.error("Signature validation failed" + se);
		}
		return false;
	}

	public String getSubject(String token) {
		return parseClaims(token).getSubject();
	}

	public Claims parseClaims(String token) {
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
	}
}
