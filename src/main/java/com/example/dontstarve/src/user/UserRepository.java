package com.example.dontstarve.src.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import com.example.dontstarve.src.user.model.GetUserRes;

import java.util.Optional;

@Repository
@Component
public interface UserRepository extends JpaRepository<User, Integer>{

    // 회원조회 - 이메일 통해서 조회
    Optional<User> findByEmail(String eamil);

    // 회원정보조회 - 유저 식별자 통해서 조회
    @Query(value =
            "select new com.example.dontstarve.src.user.model.GetUserRes(email, name)" +
                    " from User where userId = :userId")
    GetUserRes getUser(@Param("userId") int userId);

}
