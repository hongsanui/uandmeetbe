package com.project.uandmeetbe.room.dto;

import com.project.uandmeetbe.room.Room;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <h1>RoomOfList</h1>
 * <p>
 * 방 목록 카드 뷰를 위한 기본 내용 DTO
 * </p>
 */
@Getter
public class RoomOfList {

    private Long roomId;
    private String roomTitle;
    private int limitPeopleCount;
    private int participantCount;
    private List<String> tags;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startAppointmentDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endAppointmentDate;
    private String roomImagePath;
    private String exercise;



    public RoomOfList(){

    }

    @Builder(access = AccessLevel.PRIVATE)
    private RoomOfList(Room room, List<String> tags) {
        this.roomId = room.getId();
        this.roomTitle = room.getRoomTitle();
        this.limitPeopleCount = room.getLimitPeopleCount();
        this.participantCount = room.getParticipants().size();
        this.tags = tags;
        this.endAppointmentDate = room.getEndAppointmentDate();
        this.startAppointmentDate = room.getStartAppointmentDate();
        this.roomImagePath = room.getRoomImages().get(0).getImagePath();
        this.exercise = room.getExercise();
    }
    public static RoomOfList of(Room room, List<String> tags){

        return RoomOfList.builder()
                .room(room)
                .tags(tags)
                .build();
    }
}
