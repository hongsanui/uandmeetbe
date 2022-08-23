package com.project.uandmeetbe.jwt;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByRefreshToken(String refreshToken);
    boolean existsByKeyEmailAndMemberAgent(String userEmail, String memberAgent);
    void deleteByKeyEmailAndMemberAgent(String userEmail, String memberAgent);
}
