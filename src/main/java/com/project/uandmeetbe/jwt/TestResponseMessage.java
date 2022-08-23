package com.project.uandmeetbe.jwt;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TestResponseMessage {

    private String signUpCheckValue;

    public TestResponseMessage(String sign){
        this.signUpCheckValue = sign;
    }
}
