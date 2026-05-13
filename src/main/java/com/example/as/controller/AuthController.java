package com.example.as.controller;

import com.example.as.entity.User;
import com.example.as.repository.UserRepository;
import com.example.as.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordResetService resetService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    //パスワード忘れ画面(
    @GetMapping("/forgot-password")
    public String forgotForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String sendReset(@RequestParam String email, Model model) {

        User user = userRepo.findByEmail(email);
        //セキュリティ対策：存在ししなくっても同じリスポンス
        if (user != null) {
            String token = resetService.createResetToken(user);

            //メール送信
            resetService.sendResetMail(email, token);
        }
        model.addAttribute("message", "If the email exists, a reset link has been sent.");
        return "forgot-password";
    }

    //パスワード更新
    @GetMapping("/reset-password")
    public String resetForm(@RequestParam String token, Model model) {
        User user = resetService.validateToken(token);

        if (user == null) {
            model.addAttribute("error", "Invalid or expired.");
            return "forgot-password";
        }
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token,
                                @RequestParam String password,
                                Model model) {
        // パスワード形式
        String regex =
                "^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z0-9!@#$%^&*()_+=\\-{}\\[\\]:;'<>,.?/\\\\|`~]{8,}$";

        // パスワード形式エラー
        if (!password.matches(regex)) {

            model.addAttribute(
                    "resetError",
                    "Password must be at least 8 characters and include letters and numbers."
            );

            // tokenを戻す
            model.addAttribute("token", token);

            return "reset-password";
        }

        User user = resetService.validateToken(token);
        if (user == null) {
            model.addAttribute("resetError", "Invalid token");
            return "forgot-password";
        }
        resetService.updatePassword(user, password, passwordEncoder);
        // 使用済みトークン削除
        resetService.deleteToken(token);
        model.addAttribute("resetMessage", "Password changed successfully.");
        return "login";
    }

}
