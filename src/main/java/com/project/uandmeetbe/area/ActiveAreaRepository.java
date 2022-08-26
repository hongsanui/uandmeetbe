package com.project.uandmeetbe.area;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * <h1>ActiveAreaRepository</h1>
 * <p>
 *     관심 지역 엔티티 CRUD 리포지토리
 * </p>
 */
public interface ActiveAreaRepository extends JpaRepository<ActiveArea, Long> {
}
