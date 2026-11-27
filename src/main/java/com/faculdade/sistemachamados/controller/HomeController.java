package com.faculdade.sistemachamados.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.CookieValue;

import java.util.Base64;
import java.nio.charset.StandardCharsets;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index(
            @CookieValue(
                    name = "email_lembrado",
                    defaultValue = ""
            ) String emailCodificado,
            Model model) {

        String emailLembrado = "";

        if (!emailCodificado.isBlank()) {

            try {
                emailLembrado = new String(
                        Base64.getUrlDecoder()
                                .decode(emailCodificado),
                        StandardCharsets.UTF_8
                );

            } catch (IllegalArgumentException e) {
                emailLembrado = "";
            }
        }

        model.addAttribute("emailLembrado", emailLembrado);

        model.addAttribute(
                "lembrarMe",
                !emailLembrado.isBlank()
        );

        return "index";
    }
}