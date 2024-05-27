package com.rozhkov.callcenter.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@RequiredArgsConstructor
public class UserRoomDto {
    private UUID sessionId;
    private String username;
    private String password;
    private String room;
}
