package com.rozhkov.callcenter.repository;

import com.rozhkov.callcenter.entity.Role;
import com.rozhkov.callcenter.entity.Spec;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface SpecRepository extends JpaRepository<Spec, Long> {
    Optional<Spec> findBySpec(String spec);
}
