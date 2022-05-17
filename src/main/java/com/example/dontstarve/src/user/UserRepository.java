package com.example.dontstarve.src.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Component
public interface UserRepository extends JpaRepository<User, Integer>{

    // 회원조회 - 이메일 통해서 조회
    Optional<User> findByEmail(String eamil);

}
