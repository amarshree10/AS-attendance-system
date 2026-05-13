package com.example.as.repository;

import com.example.as.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    // ログイン時に使用
    User findByUsername(String username);

    //メールで検索
    User findByEmail(String email);
    //重複チェック用
    boolean existsByEmail(String email);
}
