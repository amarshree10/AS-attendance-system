package com.example.as.controller;

import com.example.as.dto.RegisterForm;
import com.example.as.entity.User;
import com.example.as.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/*新規ユーザー登録*/
@Controller
public class RegisterController {
    @Autowired
    private UserRepository repo;

    @Autowired
    private BCryptPasswordEncoder encoder;

    //画面表示
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("form", new RegisterForm());
        return "register";
    }

    //登録処理
    @Transactional
    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("form") RegisterForm form,
                           BindingResult result,
                           Model model) {

        //重複チェック
        if (repo.findByUsername(form.getUsername()) != null) {
            result.rejectValue("username", null, "User already exists.");
        }

        //メール重複チェック
        if(repo.existsByEmail(form.getEmail())){
            result.rejectValue("email", null,"Email already registered");
        }

        // パスワード一致チェック
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            result.rejectValue("confirmPassword", null, "Passwords do not match.");
        }

        if (result.hasErrors()) {
            return "register";
        }

        //保存
        User user = new User();
        user.setUsername(form.getUsername());

        //パスワードをハッシュ化
        user.setPassword(encoder.encode(form.getPassword()));
        user.setFirstname(form.getFirstname());
        user.setLastname(form.getLastname());
        user.setEmail(form.getEmail());
        user.setPhone(form.getPhone());
        user.setBirthday(form.getBirthday());

        repo.save(user);

        return "redirect:/login";
    }
}