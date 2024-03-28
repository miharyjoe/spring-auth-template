package org.mihary.springauthtemplate.controller;


import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.mihary.springauthtemplate.dto.AuthenticationResponse;
import org.mihary.springauthtemplate.dto.LoginRequest;
import org.mihary.springauthtemplate.dto.RefreshTokenRequest;
import org.mihary.springauthtemplate.dto.RegisterRequest;
import org.mihary.springauthtemplate.service.AuthService;
import org.mihary.springauthtemplate.service.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
  private final AuthService authService;
  private final TokenService tokenService;

  @PostMapping("/signup")
  public ResponseEntity<String> signup(@RequestBody RegisterRequest registerRequest){
    authService.signup(registerRequest);
    return new ResponseEntity<>("User Registration Successful", HttpStatus.OK);
  }

  @PostMapping("/login")
  public AuthenticationResponse login (@RequestBody LoginRequest loginRequest){
    return authService.login(loginRequest);
  }

  @PostMapping("/refresh/token")
  public AuthenticationResponse refreshToken (
    @Valid @RequestBody RefreshTokenRequest refreshTokenRequest){
    return authService.refreshToken(refreshTokenRequest);
  }

  @PostMapping("/logout")
  public ResponseEntity<String> logout(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
    tokenService.deleteRefreshToken(refreshTokenRequest.getToken());
    return ResponseEntity.status(OK).body("Refresh Token Deleted Successfully!!");
  }
}

