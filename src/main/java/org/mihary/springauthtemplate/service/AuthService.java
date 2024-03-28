package org.mihary.springauthtemplate.service;

import org.mihary.springauthtemplate.dto.AuthenticationResponse;
import org.mihary.springauthtemplate.dto.LoginRequest;
import org.mihary.springauthtemplate.dto.RefreshTokenRequest;
import org.mihary.springauthtemplate.dto.RegisterRequest;
import org.mihary.springauthtemplate.model.User;
import org.mihary.springauthtemplate.repository.UserRepository;
import org.mihary.springauthtemplate.security.JwtProvider;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final PasswordEncoder passwordEncoder;

  private final UserRepository userRepository;

  private final AuthenticationManager authenticationManager;

  private final JwtProvider jwtProvider;

  private final TokenService tokenService;

  @Value("${admin.email}")
  private String adminEmail;

  @Transactional
  public void signup(RegisterRequest registerRequest){
    User user = new User();
    user.setUsername(registerRequest.getUsername());
    user.setEmail(registerRequest.getEmail());
    user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
    user.setCreated(Instant.now());
    user.setEnabled(true);
    if (registerRequest.getEmail().equals(adminEmail)) {
      user.setRole("ADMIN");
    } else {
      user.setRole("USER");
    }
    userRepository.save(user);

  }

  public AuthenticationResponse login(LoginRequest loginRequest) {
    User user = userRepository.findByUsername(loginRequest.getUsername())
      .orElseThrow();
    Authentication authenticate = authenticationManager
      .authenticate(new UsernamePasswordAuthenticationToken(
        loginRequest.getUsername(), loginRequest.getPassword()
      ));
    SecurityContextHolder.getContext().setAuthentication(authenticate);
    String token = jwtProvider.generateToken(authenticate);
    return AuthenticationResponse.builder()
      .authenticationToken(token)
      .token(tokenService.generateToken().getToken())
      .expiresAt(Instant.now()
        .plusMillis(jwtProvider.getJwtExpirationInMillis()))
      .username(loginRequest.getUsername())
      .role(user.getRole())
      .build();
  }

  @Transactional(readOnly = true)
  public User getCurrentUser() {
    Jwt principal = (Jwt) SecurityContextHolder.
      getContext().getAuthentication().getPrincipal();
    return userRepository.findByUsername(principal.getSubject())
      .orElseThrow(() -> new UsernameNotFoundException("User name not found - " + principal.getSubject()));
  }

  public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
    tokenService.validateRefreshToken(refreshTokenRequest.getToken());
    String token = jwtProvider.generateTokenWithUserName(refreshTokenRequest.getUsername());
    return AuthenticationResponse.builder()
      .authenticationToken(token)
      .token(refreshTokenRequest.getToken())
      .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
      .username(refreshTokenRequest.getUsername())
      .build();
  }

}