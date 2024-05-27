package com.rozhkov.callcenter.listener;

import com.rozhkov.callcenter.dto.UserRoomDto;

public interface UserChangeListener {
    void onUserAdded(UserRoomDto user);
}
