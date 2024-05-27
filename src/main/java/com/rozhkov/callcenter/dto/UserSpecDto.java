package com.rozhkov.callcenter.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@RequiredArgsConstructor
public class UserSpecDto {
    private UUID sessionId;
    private String username;
    private String room;
    private String spec;
}
