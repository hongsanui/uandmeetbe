package com.project.uandmeetbe.session;

import com.project.uandmeetbe.participant.Participant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
/**
 * <h1>SocketSession</h1>
 * <p>
 *     Socket의 Session 엔티티
 * </p>
 *
 */
@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class SocketSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Participant participant;

    @Column(name = "SOCKET_SESSION_ID")
    private String socketSessionId;

}
