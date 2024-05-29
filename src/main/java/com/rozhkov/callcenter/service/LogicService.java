package com.rozhkov.callcenter.service;

import com.rozhkov.callcenter.dto.UserRoomDto;
import com.rozhkov.callcenter.dto.UserSpecDto;
import com.rozhkov.callcenter.entity.Role;
import com.rozhkov.callcenter.entity.Spec;
import com.rozhkov.callcenter.entity.User;
import com.rozhkov.callcenter.listener.UserChangeListener;
import com.rozhkov.callcenter.repository.SpecRepository;
import com.rozhkov.callcenter.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
public class LogicService {
    private final UserRepository userRepository;
    private final SpecRepository specRepository;
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

@Transactional
    public ResponseEntity<?> updateSpec(UserSpecDto userSpecDto) throws InterruptedException {
        List<Spec> specs = specRepository.findAll();
        User user = userRepository.findByUsername(userSpecDto.getUsername()).get();

        Collection<Role> roles = user.getRoles();
        Set<Role> roleSet = new HashSet<>(roles);
        specs.removeIf(s -> !userSpecDto.getSpecs().contains(s.getSpec()));

        if (userSpecDto.getSpecs().isEmpty()) {
            specs.add(specRepository.findBySpec("NO_SPEC").get());
        }


        userRepository.deleteUserSpec(user.getId());
        userRepository.flush();



        for (Role role : roleSet) {
            for (Spec spec : specs) {
                userRepository.insertUserSpec(user.getId(),
                                role.getId(),
                                spec.getId());
            }
        }

        userRepository.flush();
        return ResponseEntity.ok(user);
    }
}
