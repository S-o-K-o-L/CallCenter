package com.rozhkov.callcenter.repository;

import com.rozhkov.callcenter.entity.Role;
import com.rozhkov.callcenter.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Query(value = "SELECT u.* FROM users u, users_roles_spec urs, roles r "
            + "WHERE u.id = urs.user_id"
            + " and urs.role_id = r.id"
            + " and r.role = 'ROLE_CONSULTANT'",
            nativeQuery = true)
    Optional<List<User>> findAllByRole();
}
