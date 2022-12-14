package com.project.uandmeetbe.chat;

import com.project.uandmeetbe.chat.dto.ChatOfHistory;
import com.project.uandmeetbe.chat.dto.ChatOfPubRoom;
import com.project.uandmeetbe.chat.dto.ClientMessage;
import com.project.uandmeetbe.participant.ParticipantRepository;
import com.project.uandmeetbe.participant.exception.NotParticipateRoomException;
import com.project.uandmeetbe.room.Room;
import com.project.uandmeetbe.room.RoomRepository;
import com.project.uandmeetbe.room.exception.NotFoundRoomException;
import com.project.uandmeetbe.user.User;
import com.project.uandmeetbe.user.UserRepository;
import com.project.uandmeetbe.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <h1>ChatService</h1>
 * <p>
 *     채팅과 관련된 요청에 대한 컨트롤러
 * </p>
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final ParticipantRepository participantRepository;
    private final ChatRepository chatRepository;

    /**
     * 클라이언트가 보낸 메세지를 DB에 저장하는 메소드
     * @param message : 클라이언트가 보낸 메세지 DTO
     * @param roomId : 클라이언트가 보낸 Destination room
     * @return : 발행할 메세지 DTO
     */
    public ChatOfPubRoom saveChat(ClientMessage message, Long roomId, Long userId){

//        User userEntity = userRepository.findById(message.getUserId())
//                .orElseThrow(() -> new UserNotFoundException());
        User userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException());

        Room roomEntity = roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundRoomException());

        ChatMessage chatMessage = chatRepository.save(ChatMessage.of(message.getMessage(), userEntity, roomEntity));

        return ChatOfPubRoom.builder()
                .userId(userEntity.getId())
                .userProfileImagePath(userEntity.getUserProfileImage())
                .nickname(userEntity.getNickname())
                .message(chatMessage.getMessage())
                .sendAt(chatMessage.getSendAt())
                .build();

    }

    /**
     * 클라이언트가 실제 해당 방에 참가했는지 확인하는 메소드
     * @param userId : 채팅을 발행하는 클라이언트 id(pk)
     * @param roomId : 발행하려는 방 id(pk)
     */
    public void checkValidate(Long userId, Long roomId){
        //유저 존재 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("해당하는 유저를 찾을 수 없습니다."));
        //방 존재 확인
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundRoomException("해당하는 방을 찾을 수 없습니다."));
        //참여 여부 확인
        //참여 했을 경우
        if(participantRepository.existsByUserAndRoom(user, room)){
            return;
        }

        //참여하지 않았을 경우
        throw new NotParticipateRoomException("Auth");
    }

    /**
     * 과거 채팅을 조회하는 메소드
     * @param pageable : 채팅 내용 페이징 객체
     * @param roomId : 조회하려는 방 id(pk)
     * @return : 조회된 채팅 페이징
     */
    public Page<ChatOfHistory> getChatHistory(Pageable pageable, Long roomId){
        Pageable page = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.ASC,"sendAt").ascending());


        Room roomEntity = roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundRoomException("해당 방을 찾을 수 없습니다."));
        Page<ChatMessage> chatEntityList = chatRepository.findByRoomOrderBySendAtDesc(roomEntity, page);
        Page<ChatOfHistory> pageList = chatEntityList.map(
                chat -> ChatOfHistory.builder()
                        .nickname(chat.getUser().getNickname())
                        .userProfileImagePath(chat.getUser().getUserProfileImage())
                        .userId(chat.getUser().getId())
                        .sendAt(chat.getSendAt())
                        .message(chat.getMessage())
                        .build()
        );

        return pageList;
    }

    public ChatOfPubRoom getMessage(Long userId, ClientMessage message){

        User userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException());

        return ChatOfPubRoom.builder()
                .userId(userEntity.getId())
                .userProfileImagePath(userEntity.getUserProfileImage())
                .nickname(userEntity.getNickname())
                .message(message.getMessage())
                .build();
    }

}
