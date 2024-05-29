package com.rozhkov.callcenter.service;

import com.rozhkov.callcenter.dto.UserRoomDto;
import com.rozhkov.callcenter.dto.UserSpecDto;
import com.rozhkov.callcenter.entity.User;
import com.rozhkov.callcenter.listener.UserChangeListener;
import com.rozhkov.callcenter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
public class LogicService {
    private final UserRepository userRepository;
    private final List<UserChangeListener> userChangeListeners;
    private final List<UserRoomDto> connectedUsersForAdminsQueue = new CopyOnWriteArrayList<>();
    private final List<UserSpecDto> connectedUsersForConsultantQueue = new CopyOnWriteArrayList<>();

    public ResponseEntity<?> addNewUser(UserRoomDto userRoomDto) {
        connectedUsersForAdminsQueue.add(userRoomDto);
        Optional<User> user = userRepository.findByUsername(userRoomDto.getUsername());
        if (user.isEmpty()) {
            Random random = new Random();
            userRoomDto.setUsername("user" + random.nextInt(1000));
        }
        for (UserChangeListener listener : userChangeListeners) {
            listener.onUserAdded(userRoomDto);
        }
        return ResponseEntity.ok(userRoomDto);
    }

    public ResponseEntity<?> removeUser(UUID uuid) {
        Optional<UserRoomDto> userRoomDto = connectedUsersForAdminsQueue
                .stream()
                .filter(e -> e.getSessionId().equals(uuid))
                .findFirst();
        userRoomDto.ifPresent(connectedUsersForAdminsQueue::remove);
        userRoomDto.ifPresent(u -> userChangeListeners
                .forEach(listener -> listener.
                        onUserAdded(u))
        );
        return ResponseEntity.ok(userRoomDto.orElse(new UserRoomDto()));
    }

    public ResponseEntity<?> getAdminsQueue() {
        return ResponseEntity.ok(connectedUsersForAdminsQueue);
    }

    public ResponseEntity<?> attachUserToSpecQueue(UserSpecDto userSpecDto) {
        UserRoomDto userRoomDto = new UserRoomDto(userSpecDto.getSessionId(),
                userSpecDto.getUsername(),
                userSpecDto.getRoom());
        if (connectedUsersForAdminsQueue.contains(userRoomDto)) {
            connectedUsersForConsultantQueue.add(userSpecDto);
            connectedUsersForAdminsQueue.remove(userRoomDto);
            for (UserChangeListener listener : userChangeListeners) {
                listener.onUserAdded(userRoomDto);
            }
        }
        return ResponseEntity.ok(userSpecDto);
    }


    public ResponseEntity<?> getUsersFromConsultantQueue() {
        return ResponseEntity.ok(connectedUsersForConsultantQueue);
    }
}
