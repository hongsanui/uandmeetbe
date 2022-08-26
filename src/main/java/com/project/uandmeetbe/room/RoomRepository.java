package com.project.uandmeetbe.room;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * <h1>RoomRepository</h1>
 * <p>
 *     운동 방 DAO
 *   <ul>
 *          <li> {@link JpaRepository<Room,Long>} : Spring Data Jpa</li>
 *          <li> {@link RoomRepositoryCustom} : queryDsl implement Object </li>
 *   </ul>
 * </p>
 *
 */
public interface RoomRepository extends JpaRepository<Room, Long>, RoomRepositoryCustom {

    /**
     * <h2>getAvailableRoomCount</h2>
     * @return Long : 시간이 지나지 않고, 인원이 꽉차지 않은 방 개수
     */
    @Query("select count(*) from Room a WHERE DATE(Now()) < DATE(a.startAppointmentDate) AND a.limitPeopleCount > (select count(*) from Participant where room = a.id)")
    Long getAvailableRoomCount();

}
