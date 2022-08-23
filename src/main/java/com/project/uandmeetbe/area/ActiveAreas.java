package com.project.uandmeetbe.area;

import com.project.uandmeetbe.member.model.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;

import javax.persistence.*;

@Table(name = "T-AREA")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ActiveAreas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AREA_SEQUENCE_ID")
    private String areaSequenceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_SEQUENCE_ID")
    private Member member;

    @Column(name = "AREA")
    private String area;
}
