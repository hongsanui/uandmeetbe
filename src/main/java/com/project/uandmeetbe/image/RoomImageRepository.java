package com.project.uandmeetbe.image;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomImageRepository extends JpaRepository<RoomImage , Long> {
    void deleteAllByRoomId(Long roomId);
}
