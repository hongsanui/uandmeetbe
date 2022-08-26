package com.project.uandmeetbe.security.oauth2.tag;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {

    void deleteAllByRoomId(Long roomId);
}
