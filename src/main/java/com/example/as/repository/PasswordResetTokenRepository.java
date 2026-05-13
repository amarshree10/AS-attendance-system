package com.example.as.repository;

import com.example.as.entity.PasswordResetToken;
import com.example.as.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    //トークン検索
    PasswordResetToken findByToken(String token);

    //既存のトークンを削除用
    @Transactional
    void deleteByUser(User user);
}
