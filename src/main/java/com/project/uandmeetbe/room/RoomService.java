package com.project.uandmeetbe.room;

import com.project.uandmeetbe.chat.ChatController;
import com.project.uandmeetbe.chat.code.ChatCode;
import com.project.uandmeetbe.chat.dto.MessageOfDelegate;
import com.project.uandmeetbe.chat.dto.MessageOfKickOut;
import com.project.uandmeetbe.chat.dto.MessageOfLeave;
import com.project.uandmeetbe.chat.dto.MessageOfParticipate;
import com.project.uandmeetbe.common.code.CommonCode;
import com.project.uandmeetbe.common.dto.Response;
import com.project.uandmeetbe.common.dto.WsResponse;
import com.project.uandmeetbe.common.util.ParsingEntityUtils;
import com.project.uandmeetbe.image.RoomImage;
import com.project.uandmeetbe.image.RoomImageService;
import com.project.uandmeetbe.image.dto.ImageProperties;
import com.project.uandmeetbe.participant.Participant;
import com.project.uandmeetbe.participant.ParticipantService;
import com.project.uandmeetbe.room.code.RoomCode;
import com.project.uandmeetbe.room.dto.*;
import com.project.uandmeetbe.room.exception.NotFoundRoomException;
import com.project.uandmeetbe.security.oauth2.tag.Tag;
import com.project.uandmeetbe.security.oauth2.tag.TagService;
import com.project.uandmeetbe.user.User;
import com.project.uandmeetbe.user.UserRepository;
import com.project.uandmeetbe.user.UserService;
import com.project.uandmeetbe.user.dto.UserOfParticipantInfo;
import com.project.uandmeetbe.user.exception.NotEnteredInformationException;
import com.project.uandmeetbe.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
/**
 * <h1>RoomService</h1>
 * <p>
 *     Room과 관련된 비즈니스 로직
 * </p>
 * <p>
 *     CRUD 및 참가와 퇴장 로직을 수행하는 클래스이다.
 * </p>
 *
 */

@RequiredArgsConstructor
@Transactional
@Service
public class RoomService {
    private final ParticipantService participantService;
    private final TagService tagService;
    private final ParsingEntityUtils parsingEntityUtils;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final RoomImageService roomImageService;
    private final UserService userService;
    private final ChatController chatController;
    private final ImageProperties imageProperties;


    //방 생성
    public Response createRoom(User user, RoomOfCreate roomOfCreate){

        //요청 유저의 엔티티 찾기
        User userEntity = findUserEntityById(user.getId());

        //추가 정보 입력했는지 확인
        if(userEntity.getNickname() == null){
            throw new NotEnteredInformationException();
        }

        //방 엔티티 만들기
        Room roomEntity = Room.of(roomOfCreate, userEntity);

        //방 저장
        Room room = roomRepository.save(roomEntity);

        //-- Tag --
        //List<String>을 List<Tag>로 변환
        List<Tag> tagList = parsingEntityUtils.parsingStringToTagEntity(roomOfCreate.getTags());

        //tag 저장
        tagService.saveTagFromRoomCreation(tagList, roomEntity);
        // -- Image --
        // image 로컬에 저장
        roomImageService.registerRoomImage(roomOfCreate.getRoomImages(), roomEntity);

        participantService.save(user, room);

        return Response.of(CommonCode.GOOD_REQUEST, RoomOfCreated.builder().createdRoomId(roomEntity.getId()).build());
    }

    //방 설명 페이지 조회
    public RoomOfInfo getRoomInfo(Long roomId){

        Room roomEntity = findRoomEntityById(roomId);

        //List<RoomImage> -> List<ImageOfRoomInfo>
        List<ImageOfRoomInfo> imageOfRoomInfos =
                parsingEntityUtils.parsingRoomImageToRoomInfoImage(roomEntity.getRoomImages());

        //List<Tag> -> List<String>
        List<String> tag =
                parsingEntityUtils.parsingTagEntityToString(roomEntity.getTags());

        //조회수 증가
        roomEntity.plusViewCount();

        return RoomOfInfo.of(roomEntity, imageOfRoomInfos, tag);

    }

    //방 수정
    @Transactional
    public Response modifyRoomInfo(RoomOfUpdate roomOfUpdate, Long roomId){
        Room roomEntity = findRoomEntityById(roomId);

        //방 인원
        if(roomEntity.getParticipants().size() > roomOfUpdate.getLimitPeopleCount()){
            return Response.of(RoomCode.NOT_MODIFY_PARTICIPANT_COUNT, null);
        }

        //-- Tag --
        // Tag Service 에서 모든 DB값 삭제 후, 넘어온 값들로 새롭게 매핑
        List<Tag> tagList = parsingEntityUtils.parsingStringToTagEntity(roomOfUpdate.getTags());
        tagService.modifyTagFromRoomUpdate(tagList, roomEntity);

        //-- Image --
        // Image Service 에서 모든 DB값 삭제 후, 넘어온 값들로 새롭게 매핑 및 저장
        roomImageService.updateRoomImage(roomOfUpdate.getRoomImages(), roomEntity);

        //-- update Room Entity --
        roomEntity.updateRoom(roomOfUpdate);


        //WS 메세지 보내기
        sendMessage(roomEntity.getId(), getWsRoomDetailInfo(roomEntity));

        //Http Content 생성
        return Response.of(CommonCode.GOOD_REQUEST, null);
    }

    //방 필터링 조회
    public Page<RoomOfList> roomFields(FieldsOfRoomList fieldsOfRoomList, Pageable pageable){
        Page<RoomOfList> list = roomRepository.searchAll(fieldsOfRoomList, pageable);

        return list;
    }

    //방 참가
    @Transactional
    public Response participateRoom(User currentUser, Long roomId){

        //엔티티 찾기
        UserAndRoomOfService userAndRoomEntity =
                findEntityById(currentUser.getId(), roomId);

        Room roomEntity = userAndRoomEntity.getRoom();

        User userEntity = userAndRoomEntity.getUser();


        //인원이 가득찬 경우
        if(roomEntity.getParticipants().size() >= roomEntity.getLimitPeopleCount()){
            return Response.of(RoomCode.FULL_ROOM, null);
        }
        //시간이 지난 방인 경우
        if(roomEntity.getEndAppointmentDate().isBefore(LocalDateTime.now())){
            return Response.of(RoomCode.TIME_OUT_ROOM, null);
        }

        // 참가 저장
        participantService.save(currentUser, roomEntity);

        //WS 메세지 생성
        WsResponse wsResponse = WsResponse.of(ChatCode.SYSTEM_USER_ENTER,
                MessageOfParticipate.builder()
                        .id(userEntity.getId())
                        .mannerPoint(userEntity.getMannerPoint())
                        .userNickname(userEntity.getNickname())
                        .gender(userEntity.getGender())
                        .build());

        //참가 WS 메세지 보내기
        sendMessage(roomEntity.getId(), wsResponse);
        //방 변화 WS 메세지 보내기

        Room updatedRoomEntity = findRoomEntityById(roomEntity.getId());
        
        sendMessage(roomEntity.getId(), getWsRoomDetailInfo(updatedRoomEntity));

        // 정상적으로 참여가 가능한 경우
        // HTTP 응답 메세지 생성
        return Response.of(RoomCode.SUCCESS_PARTICIPATE_ROOM, null);
    }

    public List<UserOfParticipantInfo> getParticipantsInfo(List<Participant> participantList){
        List<UserOfParticipantInfo> userOfParticipantInfoList = new ArrayList<>();

        for (Participant participant : participantList){
            userOfParticipantInfoList.add(userService.getParticipantInfo(participant.getUser().getId(), participant));
        }
        return userOfParticipantInfoList;
    }

    public boolean getAttendance(Long userId, Long roomId){
        UserAndRoomOfService userRoomEntities = findEntityById(userId, roomId);
        User userEntity = userRoomEntities.getUser();
        Room roomEntity = userRoomEntities.getRoom();
        return participantService.checkAttendance(userEntity, roomEntity);
    }

    public RoomsOfMyRoom getMyRoom(User currentUser){
        User userEntity = findUserEntityById(currentUser.getId());

        List<Room> hostingRoomEntities = userEntity.getHostingRooms();

        List<Participant> participateEntities = userEntity.getParticipateRooms();

        List<Room> participatingRoomEntities = new ArrayList<>();
        for (Participant participant : participateEntities){
            participatingRoomEntities.add(participant.getRoom());
        }

        List<Room> filteredParticipatingRoom = participatingRoomEntities;

        List<Room> imminentRoomEntities = filteredParticipatingRoom.stream().filter(room->{
            if (LocalDateTime.now().isAfter(room.getStartAppointmentDate())) {
                return false;
            }
            return true;
        }).collect(Collectors.toList());

        Collections.sort(imminentRoomEntities, new SortByDate());

        //시간 정렬
        return RoomsOfMyRoom.builder()
                .hostingRooms(parsingEntityUtils.parsingRoomToRoomOfList(hostingRoomEntities))
                .participateRooms(parsingEntityUtils.parsingRoomToRoomOfList(participatingRoomEntities))
                .imminentRooms(parsingEntityUtils.parsingRoomToRoomOfList(imminentRoomEntities))
                .build();

    }

    //방장 위임
    public Response delegate(User currentUser, Long roomId, Long targetUserId){

        UserAndRoomOfService userAndRoomEntity = findEntityById(currentUser.getId(), roomId);

        // 해당 방 찾기
        Room roomEntity = userAndRoomEntity.getRoom();

        // 요청 유저 찾기.
        User requestUser = userAndRoomEntity.getUser();


        // 위임 받는 유저 찾기.
        User delegatedUser = findUserEntityById(targetUserId);

        // 요청 유저가 방장인지 확인
        if(requestUser.getId() != roomEntity.getHost().getId()){
            // 해당하는 사람이 방장이 아님.
        }

        // 요청자 및 위임받는 유저 방에 참여했는지 확인
        if(!getAttendance(requestUser.getId(), roomId) && !getAttendance(delegatedUser.getId(), roomId)){
            //둘 중 1명이 참여하지 않았을 경우
            return Response.of(CommonCode.BAD_REQUEST, null);
        }

        // 방장 위임
        roomEntity.updateHost(delegatedUser);


        //WS 메세지 생성
        WsResponse wsResponse = WsResponse.of(ChatCode.SYSTEM_USER_DELEGATED,
                MessageOfDelegate.builder()
                        .beforeHostId(requestUser.getId())
                        .beforeHostNickname(requestUser.getNickname())
                        .afterHostId(delegatedUser.getId())
                        .afterHostNickname(delegatedUser.getNickname())
                        .build());

        //위임 WS 메세지 보내기
        sendMessage(roomEntity.getId(), wsResponse);

        //방 변화 WS 메세지 보내기
        sendMessage(roomEntity.getId(), getWsRoomDetailInfo(roomEntity));



        // HTTP 응답 메세지 생성
        Response response = Response.of(RoomCode.DELEGATE,MessageOfDelegate.builder()
                .beforeHostId(requestUser.getId())
                .beforeHostNickname(requestUser.getNickname())
                .afterHostId(delegatedUser.getId())
                .afterHostNickname(delegatedUser.getNickname()).build());

        return response;

    }

    // 강퇴
    public Response kickOut(User currentUser, Long roomId, Long targetUserId){

        UserAndRoomOfService userAndRoomEntity = findEntityById(currentUser.getId(), roomId);

        // 해당 방 찾기
        Room roomEntity = userAndRoomEntity.getRoom();

        // 요청 유저 찾기.
        User requestUser = userAndRoomEntity.getUser();

        // 위임 받는 유저 찾기.
        User kickedOutUser = findUserEntityById(targetUserId);

        // 요청 유저가 방장인지 확인
        if(requestUser.getId() != roomEntity.getHost().getId()){
            // 해당하는 사람이 방장이 아님.
            return Response.of(RoomCode.NO_PERMISSION, null);
        }

        // 요청자 및 위임받는 유저 방에 참여했는지 확인
        if(!getAttendance(requestUser.getId(), roomId) && !getAttendance(kickedOutUser.getId(), roomId)){
            //둘 중 1명이 참여하지 않았을 경우
            return Response.of(CommonCode.BAD_REQUEST, null);
        }

        // 강퇴
        participantService.kickUser(kickedOutUser.getId(), roomEntity.getId());

        //WS 메세지 생성
        WsResponse wsResponse = WsResponse.of(ChatCode.SYSTEM_USER_KICKED_OUT,
                MessageOfKickOut.builder()
                        .id(kickedOutUser.getId())
                        .userNickname(kickedOutUser.getNickname())
                        .build());

        //강퇴 WS 메세지 보내기
        sendMessage(roomEntity.getId(), wsResponse);

        //방 변화 WS 메세지 보내기
        sendMessage(roomEntity.getId(), getWsRoomDetailInfo(roomEntity));

        // HTTP 응답 메세지 생성
        Response response = Response.of(
                RoomCode.KICKED_OUT, MessageOfKickOut.builder()
                .id(kickedOutUser.getId())
                .userNickname(kickedOutUser.getNickname())
                .build());
        return response;
    }
    //방 나가기
    public Response out(User currentUser, Long roomId){

        // user, room 식별
        UserAndRoomOfService userAndRoomEntity = findEntityById(currentUser.getId(), roomId);
        User userEntity = userAndRoomEntity.getUser();
        Room roomEntity = userAndRoomEntity.getRoom();

        // status code
        ChatCode chatCode = ChatCode.SYSTEM_USER_OUT;

        // 요청자가 방에 참가했는지 확인
        if(!getAttendance(userEntity.getId(), roomEntity.getId())){
            // 해당 유저가 방에 참가하지 않았을 경우
            return Response.of(RoomCode.NOT_PARTICIPATE_ROOM, null);
        }

        // 요청 유저가 방장인 경우
        if(userEntity.getId() == roomEntity.getHost().getId()){
            //방장일 경우
            chatCode = ChatCode.SYSTEM_HOST_OUT;


            //participant 관계 테이블 삭제
            participantService.out(userEntity, roomEntity);

            //방 인원이 0명인 경우 방삭제
            if(roomEntity.getParticipants().size() <= 0){
                //방 이미지 모두 삭제
                roomImageService.deleteLocalImage(roomEntity);

                //나가기 처리(DB 삭제)
                roomRepository.deleteById(roomEntity.getId());

                Response response = Response.of(RoomCode.USER_OUT_FROM_ROOM, null);

                return response;
            }


            //참가자 중 방장 권한 위임할 랜덤 유저 선택하기.
            Random random = new Random();

            int randomNumber = random.nextInt(roomEntity.getParticipants().size());

            Long targetUserId = roomEntity.getParticipants().get(randomNumber).getUser().getId();

            //선택된 유저 엔티티 조회
            User targetUserEntity = findUserEntityById(targetUserId);

            //방장 위임
            roomEntity.updateHost(targetUserEntity);

            MessageOfLeave messageBody = MessageOfLeave.builder()
                    .id(targetUserEntity.getId())
                    .mannerPoint(targetUserEntity.getMannerPoint())
                    .userNickname(targetUserEntity.getNickname())
                    .gender(targetUserEntity.getGender())
                    .build();

            //WS 메세지 생성
            WsResponse wsResponse = WsResponse.of(chatCode,
                    messageBody);

            //나가기 WS 메세지 보내기
            sendMessage(roomEntity.getId(), wsResponse);

            //방 변화 WS 메세지 보내기시
            Room updatedRoomEntity = findRoomEntityById(roomEntity.getId());
            sendMessage(roomEntity.getId(), getWsRoomDetailInfo(updatedRoomEntity));

            // HTTP 응답 메세지 생성
            Response response = Response.of(
                    RoomCode.USER_OUT_FROM_ROOM, null);
            // 소켓 통신, 정보 발행

            return response;

        }

        //방장이 아닌 경우
        //나가기 처리(DB 삭제)
        participantService.out(userEntity, roomEntity);

        //메세지 body 생성
        MessageOfLeave messageBody = MessageOfLeave.builder()
                .id(userEntity.getId())
                .mannerPoint(userEntity.getMannerPoint())
                .userNickname(userEntity.getNickname())
                .gender(userEntity.getGender())
                .build();
        //WS 메세지 생성
        WsResponse wsResponse = WsResponse.of(ChatCode.SYSTEM_USER_OUT,
                messageBody);

        //WS 메세지 보내기
        sendMessage(roomEntity.getId(), wsResponse);

        //방 업데이트
        Room updatedRoomEntity = findRoomEntityById(roomEntity.getId());

        sendMessage(roomEntity.getId(), getWsRoomDetailInfo(updatedRoomEntity));

        // HTTP 응답 메세지 생성
        Response response = Response.of(
                RoomCode.USER_OUT_FROM_ROOM, messageBody);
        // 소켓 통신, 정보 발행

        return response;
    }

    //시간 정렬을 위한 스태틱클래스
    static class SortByDate implements Comparator<Room> {

        @Override
        public int compare(Room o1, Room o2) {

            return o1.getStartAppointmentDate().compareTo(o2.getStartAppointmentDate());
        }
    }
    // 유저 엔티티 찾기
    public User findUserEntityById(Long targetUserId){

        return  userRepository.findById(targetUserId)
                .orElseThrow(() -> new UserNotFoundException("해당 유저를 찾을 수 없습니다."));
    }
    // 룸 엔티티 찾기
    public Room findRoomEntityById(Long roomId){

        return roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundRoomException("해당 방을 찾을 수 없습니다."));
    }
    // 유저 + 룸 엔티티 찾기
    public UserAndRoomOfService findEntityById(Long userId, Long roomId){
        User userEntity = findUserEntityById(userId);
        Room roomEntity = findRoomEntityById(roomId);

        return UserAndRoomOfService.builder()
                .room(roomEntity)
                .user(userEntity)
                .build();
    }

    // 참여할 수 있는 방 개수 조회
    public Response<?> getRoomCount(){

        Long count = roomRepository.getAvailableRoomCount();

        return Response.of(CommonCode.GOOD_REQUEST, CountOfAvailableRoom.builder().count(count).build());

    }
    //운동 대기방 조회
    public Response<?> getRoomDetailInfo(Long roomId, User user){

        UserAndRoomOfService userAndRoomOfService = findEntityById(user.getId(), roomId);

        return Response.of(CommonCode.GOOD_REQUEST,
                RoomOfParticipate.builder()
                        .roomOfInfo(getRoomInfo(roomId))
                        .participants(getParticipantsInfo(userAndRoomOfService.getRoom().getParticipants()))
                        .build()
                );

    }

    public WsResponse getWsRoomDetailInfo(Room roomEntity){
        return WsResponse.of(ChatCode.ROOM_UPDATED,
                RoomOfParticipate.builder()
                        .roomOfInfo(getRoomInfo(roomEntity.getId()))
                        .participants(getParticipantsInfo(roomEntity.getParticipants()))
                        .build()
        );
    }

    // 메세지 발행
    public void sendMessage(Long roomId, WsResponse response){

        chatController.sendServerMessage(roomId, response);
    }

    public Response getRoomImageSources(Long roomId, User user){

        UserAndRoomOfService userAndRoomOfService = findEntityById(user.getId(), roomId);
        Room roomEntity = userAndRoomOfService.getRoom();
        User userEntity = userAndRoomOfService.getUser();

        if(userEntity.getId() != roomEntity.getHost().getId()){
            return Response.of(RoomCode.NO_PERMISSION, null);
        }
        List<RoomImage> roomImageList = roomEntity.getRoomImages();
        if(roomImageList.size() == 1 && imageProperties.checkDefaultImage(roomImageList.get(0).getImagePath())){
            return Response.of(RoomCode.DEFAULT_ROOM_IMAGE, null);
        }
        List<ImageSourcesOfRoom> roomImageSourceList = new ArrayList<>();
        for(RoomImage roomImage : roomImageList){
            roomImageSourceList.add(roomImageService.getRoomImageSources(roomImage));
        }

        return Response.of(CommonCode.GOOD_REQUEST, roomImageSourceList);

    }




}