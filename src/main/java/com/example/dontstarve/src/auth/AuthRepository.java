package com.example.dontstarve.src.auth;

import com.example.dontstarve.src.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<User, Integer> {
    // 회원조회 - 아이디 통해서 조회
    Optional<User> findById(String id);

}
