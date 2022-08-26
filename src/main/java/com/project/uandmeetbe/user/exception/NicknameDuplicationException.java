package com.project.uandmeetbe.user.exception;

/**
 * <h1>NicknameDuplicationException</h1>
 * <p>
 *     닉네임이 중복되었을 경우 발생하는 예외
 * </p>
 *
 */
public class NicknameDuplicationException extends RuntimeException{
    public NicknameDuplicationException() {
        super();
    }

    public NicknameDuplicationException(String message) {
        super(message);
    }
}
