package com.rozhkov.callcenter.listener;

import com.rozhkov.callcenter.dto.UserRoomDto;
import com.rozhkov.callcenter.dto.UserSpecDto;

public interface UserConsultantListener {
    void onUserConsultantChange(UserSpecDto user);
    void onConsultantSpecChange(UserSpecDto user);
}
