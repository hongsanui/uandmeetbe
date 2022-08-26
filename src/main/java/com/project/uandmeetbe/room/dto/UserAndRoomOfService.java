
package com.project.uandmeetbe.room.dto;


import com.project.uandmeetbe.room.Room;
import com.project.uandmeetbe.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserAndRoomOfService {
    private User user;
    private Room room;

}
