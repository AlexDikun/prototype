package ru.dikun.prototype.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @GetMapping("/admin")
    public ResponseEntity<String> adminSpeech() {
        System.out.println("Проверка ролевых полномочий");

        return new ResponseEntity<>("Администратор у руля", HttpStatus.OK);
    }

}
