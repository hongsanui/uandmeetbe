package com.project.uandmeetbe.participant;

import com.project.uandmeetbe.room.Room;
import com.project.uandmeetbe.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    boolean existsByUserAndRoom(User user, Room room);

    Optional<Participant> findByUserAndRoom(User user, Room room);
    //Participant findBySocketSessionId(String sessionId);
}
