package com.project.uandmeetbe.common.controller;

import com.project.uandmeetbe.common.code.CommonCode;
import com.project.uandmeetbe.common.dto.FieldValidationError;
import com.project.uandmeetbe.common.dto.Response;
import com.project.uandmeetbe.mannerpoint.code.MannerPointCode;
import com.project.uandmeetbe.mannerpoint.exception.NotDownPointingMannerPointException;
import com.project.uandmeetbe.mannerpoint.exception.NotUpPointingMannerPointException;
import com.project.uandmeetbe.room.code.RoomCode;
import com.project.uandmeetbe.room.exception.NotFoundRoomException;
import com.project.uandmeetbe.user.code.UserCode;
import com.project.uandmeetbe.user.exception.NicknameDuplicationException;
import com.project.uandmeetbe.user.exception.NotEnteredInformationException;
import com.project.uandmeetbe.user.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <h1>ExceptionControllerAdvice</h1>
 * <p>
 *    컨트롤러에서 발생한 예외 상황을 모두 처리하는 전역 컨트롤러
 * </p>
 */
@Slf4j
@RestControllerAdvice
public class ExceptionControllerAdvice {

    /**
     * 입력값 검증 실패 시, 검증 실패 안내 및 실패한 필드, 메시지를 응답으로 담아 보낸다.
     * @param ex 검증 실패 오류
     * @return response - 검증 실패 필드, 메시지를 포함하는 응답
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response<List<FieldValidationError>>> handleValidationError(BindException ex) {
        List<FieldValidationError> errors = ex.getFieldErrors().stream()
                .map(error -> FieldValidationError.of(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        return ResponseEntity.badRequest().body(Response.of(CommonCode.VALIDATION_FAIL, errors));
    }

    // non User found exception
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Response> handleUserNotFountException() {
        return ResponseEntity.badRequest().body(Response.of(UserCode.USER_NOT_FOUNT, null));
    }

    // Nickname Duplication exception
    @ExceptionHandler(NicknameDuplicationException.class)
    public ResponseEntity<Response> handleNicknameDuplicationException() {

        return ResponseEntity.ok().body(Response.of(UserCode.DUPLICATED_NICKNAME, null));
    }

    // Not Entered Additional Information exception
    @ExceptionHandler(NotEnteredInformationException.class)
    public ResponseEntity<Response> handleNotEnteredInformationException() {

        return ResponseEntity.ok().body(Response.of(UserCode.NOT_ENTERED_INFORMATION, null));
    }

    @ExceptionHandler(NotFoundRoomException.class)
    public ResponseEntity<Response> handleNotFoundRoomException(){
        return ResponseEntity.ok().body(Response.of(RoomCode.NOT_FOUND_ROOM, null));
    }

    @ExceptionHandler(NotUpPointingMannerPointException.class)
    public ResponseEntity<Response> handleNotUpPointingMannerPointException(){

        return ResponseEntity.ok().body(Response.of(MannerPointCode.FAIL_MANNER_POINT_UP, null));
    }

    @ExceptionHandler(NotDownPointingMannerPointException.class)
    public ResponseEntity<Response> handleNotDownPointingMannerPointException(){
        return ResponseEntity.ok().body(Response.of(MannerPointCode.FAIL_MANNER_POINT_DOWN, null));
    }

//    @ExceptionHandler({IllegalArgumentException.class, NullPointerException.class})
//    public ResponseEntity<Response> handleIllegalArgumentException(){
//        return ResponseEntity.badRequest().body(Response.of(CommonCode.ILLEGAL_REQUEST, null));
//    }


}
