package com.rozhkov.callcenter.repository;

import com.rozhkov.callcenter.entity.Role;
import com.rozhkov.callcenter.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Query(value = "SELECT DISTINCT u.* FROM users u, users_roles_spec urs, roles r "
            + "WHERE u.id = urs.user_id"
            + " and urs.role_id = r.id"
            + " and r.role = 'ROLE_CONSULTANT'",
            nativeQuery = true)
    Optional<List<User>> findAllByRole();

    @Modifying
    @Transactional
    @Query(value = "insert into users_roles_spec " +
            "(user_id, role_id, spec_id) values " +
            "(:user_id, :role_id, :spec_id)",
            nativeQuery = true)
    void insertUserSpec(@Param("user_id") Long user_id,
                        @Param("role_id") Long role_id,
                        @Param("spec_id") Long spec_id);

    @Modifying
    @Transactional
    @Query(value = "delete from users_roles_spec where user_id = :user_id",
            nativeQuery = true)
    void deleteUserSpec(@Param("user_id") Long user_id);
}
