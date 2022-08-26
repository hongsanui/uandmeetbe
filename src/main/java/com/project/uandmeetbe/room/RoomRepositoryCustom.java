package com.project.uandmeetbe.room;


import com.project.uandmeetbe.room.dto.FieldsOfRoomList;
import com.project.uandmeetbe.room.dto.RoomOfList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
/**
 * <h1>RoomRepositoryCustom</h1>
 * <p>
 *     JPA Repository와 QueryDSL을 같이 쓰기위한(2 extends) Custom Repository
 * </p>
 */
public interface RoomRepositoryCustom {

    Page<RoomOfList> searchAll(FieldsOfRoomList fieldsOfRoomList, Pageable pageable);

}
