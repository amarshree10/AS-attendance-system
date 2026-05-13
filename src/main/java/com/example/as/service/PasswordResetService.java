package com.example.as.service;

import com.example.as.entity.PasswordResetToken;
import com.example.as.entity.User;
import com.example.as.repository.PasswordResetTokenRepository;
import com.example.as.repository.UserRepository;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private PasswordResetTokenRepository tokenRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private JavaMailSender mailSender;

    // application.properties から取得
    @Value("${spring.mail.username}")
    private String fromMail;


    @Transactional
    public String createResetToken(User user) {
        //既存のトークンを削除
        tokenRepo.deleteByUser(user);

        //UUIDでランダムトークン生成
        String token = UUID.randomUUID().toString();
        PasswordResetToken t = new PasswordResetToken();
        t.setToken(token);
        t.setUser(user);
        t.setExpiryDate(LocalDateTime.now().plusMinutes(30));

        tokenRepo.save(t);
        return token;
    }

    //メール送信
    public void sendResetMail(String email, String token) {
        try {
            String link = "http://localhost:8080/reset-password?token=" + token;
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(new InternetAddress(fromMail,"AS System"));
            helper.setTo(email);
            helper.setSubject("Password Reset");
            helper.setText("Click the link below:\n\n" + link + "\n\nThis link expires in 30 minutes.");
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //トークン検証
    public User validateToken(String token) {
        PasswordResetToken t = tokenRepo.findByToken(token);
        if (t == null || t.getExpiryDate().isBefore(LocalDateTime.now())) {
            return null;
        }
        return t.getUser();
    }

    //パスワード更新
    public void updatePassword(User user, String newPassword, PasswordEncoder encoder) {

        //ハッシュ化して保存
        user.setPassword(encoder.encode(newPassword));
        userRepo.save(user);
    }

    public void deleteToken(String token) {
        PasswordResetToken t = tokenRepo.findByToken(token);
        if (t != null) {
            tokenRepo.delete(t);
        }
    }
}
