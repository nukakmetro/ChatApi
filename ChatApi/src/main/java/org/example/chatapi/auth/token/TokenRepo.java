package org.example.chatapi.auth.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepo extends JpaRepository<Token, Long> {
    @Query(value = """
            select t
            from Token t
            where t.user.id in (
                select u.id
                from users u
                where u.id = :id
            )
            and (t.expired = false or t.revoked = false)\s
            """)
    List<Token> findAllValidTokenByUser(Long id);

    Optional<Token> findByToken(String token);
}
