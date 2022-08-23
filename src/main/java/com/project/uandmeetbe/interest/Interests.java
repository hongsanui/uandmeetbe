package com.project.uandmeetbe.interest;

import com.project.uandmeetbe.member.model.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "T_INTEREST_EXERCISE")
public class Interests {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "INTEREST_EXERCISE_SEQUENCE_ID")
    private int interestExerciseSequenceId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_SEQUENCE_ID")
    private Member member;

    @Column(name = "EXERCISE")
    private String exercise;
}
