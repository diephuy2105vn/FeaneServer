package com.springboot.server.payload.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class MessageResponse {
    private EMessageResponse status;
    private String message ;
}
