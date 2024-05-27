package com.rozhkov.callcenter.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class UserRoomDto {

    private UUID sessionId;

    private String username;

    private String room;

    public UserRoomDto(UUID sessionId, String username, String room) {
        this.sessionId = sessionId;
        this.username = username;
        this.room = room;
    }
}
