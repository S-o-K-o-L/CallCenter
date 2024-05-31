package com.rozhkov.callcenter.listener;

import com.rozhkov.callcenter.dto.UserRoomDto;

public interface UserAdminListener {
    void onUserAdded(UserRoomDto user);
    void onUserDelete(UserRoomDto user);
}
