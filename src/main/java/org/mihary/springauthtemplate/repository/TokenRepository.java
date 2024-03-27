package org.mihary.springauthtemplate.repository;

import org.mihary.springauthtemplate.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
  Optional<Token> findByToken(String Token);
  void deleteByToken(String token);
}
