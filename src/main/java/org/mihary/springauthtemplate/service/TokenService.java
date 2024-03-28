package org.mihary.springauthtemplate.service;

import lombok.AllArgsConstructor;
import org.mihary.springauthtemplate.model.Token;
import org.mihary.springauthtemplate.repository.TokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;
@Service
@AllArgsConstructor
@Transactional
public class TokenService {

  private final TokenRepository tokenRepository;

  public Token generateToken() {
    Token token = new Token();
    token.setToken(UUID.randomUUID().toString());
    token.setCreatedDate(Instant.now());

    return tokenRepository.save(token);
  }

  void validateRefreshToken(String token) {
    tokenRepository.findByToken(token)
      .orElseThrow(() -> new RuntimeException("invalid refresh token"));

  }

  public void deleteRefreshToken(String token) {
    tokenRepository.deleteByToken(token);
  }
}

