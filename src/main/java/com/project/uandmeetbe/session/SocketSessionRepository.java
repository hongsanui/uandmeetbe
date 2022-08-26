package com.project.uandmeetbe.session;

import com.project.uandmeetbe.participant.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
/**
 * <h1>SocketSessionRepository</h1>
 * <p>소켓 세션 엔티티 CRUD 레포지토리</p>
 *
 */
public interface SocketSessionRepository extends JpaRepository<SocketSession, Long> {

    SocketSession findBySocketSessionId(String socketSessionId);
    void deleteBySocketSessionIdAndParticipant(String socketSessionId, Participant participant);

}
