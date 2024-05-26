package com.rozhkov.callcenter.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UserRoomDto {
    private String username;
    private String password;
    private String room;
}
