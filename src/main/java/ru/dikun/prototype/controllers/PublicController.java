package ru.dikun.prototype.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PublicController {
    
    @GetMapping("/public")
    public ResponseEntity<String> helloWorld () {
        System.out.println("Проверка ролевых полномочий");

        return new ResponseEntity<>("Привет мир!", HttpStatus.OK);
    }
    
}
