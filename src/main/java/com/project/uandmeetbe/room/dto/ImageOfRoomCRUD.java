package com.project.uandmeetbe.room.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * <h1>ImageOfRoomCRUD</h1>
 * <p>
 *     방 생성, 수정 관련 방 이미지 DTO
 * </p>
 */
@Getter
@Builder
public class ImageOfRoomCRUD {

    private int order;
    private String roomImageExtension;
    private String imageSource;

}
