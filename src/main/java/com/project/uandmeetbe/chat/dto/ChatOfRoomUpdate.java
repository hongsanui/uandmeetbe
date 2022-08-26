package com.project.uandmeetbe.chat.dto;

import com.project.uandmeetbe.room.dto.ImageOfRoomInfo;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ChatOfRoomUpdate {

    private String roomTitle;
    private String roomContent;
    private int limitPeopleCount;
    private List<String> tags;
    private List<ImageOfRoomInfo> roomImages;

}
