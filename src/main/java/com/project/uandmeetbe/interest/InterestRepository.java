package com.project.uandmeetbe.interest;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * <h1>InterestRepository</h1>
 * <p>
 *     관심 종목 엔티티 CRUD 리포지토리
 * </p>
 */
public interface InterestRepository extends JpaRepository<Interest, Long> {
}
