package com.rozhkov.callcenter.service;

import com.rozhkov.callcenter.dto.UserRoomDto;
import com.rozhkov.callcenter.dto.UserSpecDto;
import com.rozhkov.callcenter.entity.Role;
import com.rozhkov.callcenter.entity.Spec;
import com.rozhkov.callcenter.entity.User;
import com.rozhkov.callcenter.listener.UserAdminListener;
import com.rozhkov.callcenter.listener.UserConsultantListener;
import com.rozhkov.callcenter.repository.SpecRepository;
import com.rozhkov.callcenter.repository.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
public class LogicService {
    private final UserRepository userRepository;
    private final SpecRepository specRepository;
    private List<UserAdminListener> userAdminListeners;
    private List<UserConsultantListener> userConsultantListeners;
    private final List<UserRoomDto> connectedUsersForAdminsQueue = new CopyOnWriteArrayList<>();
    private final List<UserSpecDto> connectedUsersForConsultantQueue = new CopyOnWriteArrayList<>();

    private final EntityManager entityManager;
    @Autowired
    public void setUserAdminListeners(List<UserAdminListener> userAdminListeners) {
        this.userAdminListeners = userAdminListeners;
    }

    @Autowired
    public void setUserConsultantListeners(List<UserConsultantListener> userConsultantListeners) {
        this.userConsultantListeners = userConsultantListeners;
    }

    public ResponseEntity<?> addNewUser(UserRoomDto userRoomDto) {
        connectedUsersForAdminsQueue.add(userRoomDto);
        Optional<User> user = userRepository.findByUsername(userRoomDto.getUsername());
        if (user.isEmpty()) {
            Random random = new Random();
            userRoomDto.setUsername("user" + random.nextInt(1000));
        }
        for (UserAdminListener listener : userAdminListeners) {
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
        userRoomDto.ifPresent(u -> userAdminListeners
                .forEach(listener -> {
                    listener.onUserAdded(u);
                    listener.onUserDelete(u);
                })
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
            for (UserAdminListener listener : userAdminListeners) {
                listener.onUserAdded(userRoomDto);
            }
            for (UserConsultantListener listener : userConsultantListeners) {
                listener.onUserConsultantChange(userSpecDto);
            }
        }
        return ResponseEntity.ok(userSpecDto);
    }

    public ResponseEntity<?> getUsersFromConsultantQueue() {
        return ResponseEntity.ok(connectedUsersForConsultantQueue);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ResponseEntity<?> updateSpec(UserSpecDto userSpecDto) {
        List<Spec> specs = specRepository.findAll();
        User user = userRepository.findByUsername(userSpecDto.getUsername()).get();

        specs.removeIf(s -> !userSpecDto.getSpecs().contains(s.getSpec()));

        if (userSpecDto.getSpecs().isEmpty()) {
            specs.add(specRepository.findBySpec("NO_SPEC").get());
        }

        userRepository.deleteUserSpec(user.getId());
        userRepository.flush();

        specs.forEach(spec -> userRepository.insertUserSpec(user.getId(),
                spec.getId()));

        userRepository.flush();
        Session session = entityManager.unwrap(Session.class);
        session.clear();

        return ResponseEntity.ok(user);
    }

    public ResponseEntity<?> updateSpecListener(UserSpecDto userSpecDto) {
        ResponseEntity<?> responseEntity = updateSpec(userSpecDto);
        for (UserConsultantListener listener : userConsultantListeners) {
            listener.onConsultantSpecChange(userSpecDto);
        }
        return responseEntity;
    }


    public ResponseEntity<?> delUserFromConsultantQueue(UserSpecDto userSpecDto) {
        Optional<UserSpecDto> userSpecDto1 = connectedUsersForConsultantQueue
                .stream()
                .filter(e -> e.getSessionId().equals(userSpecDto.getSessionId()))
                .findFirst();
        userSpecDto1.ifPresent(connectedUsersForConsultantQueue::remove);
        UserRoomDto userRoomDto = new UserRoomDto();
        for (UserConsultantListener listener : userConsultantListeners) {
            listener.onUserConsultantChange(userSpecDto1.get());
        }
        if (userSpecDto1.isPresent()) {
            userRoomDto.setSessionId(userSpecDto1.get().getSessionId());
            userRoomDto.setRoom(userSpecDto1.get().getRoom());
            userRoomDto.setUsername(userSpecDto1.get().getUsername());
            connectedUsersForAdminsQueue.add(userRoomDto);
            for (UserAdminListener listener : userAdminListeners) {
                listener.onUserAdded(userRoomDto);
            }
        }
        return ResponseEntity.ok(userRoomDto);
    }
}
