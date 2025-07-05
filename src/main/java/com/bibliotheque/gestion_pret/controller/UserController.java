package com.bibliotheque.gestion_pret.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {

    @GetMapping("/dashboard/{id}")
    public String userDashboard(@PathVariable Long id, Model model) {
        // Ici, vous pouvez utiliser l'ID pour récupérer plus d'infos sur l'adhérent
        // et les passer à la vue.
        model.addAttribute("userId", id);
        return "user/user-dashboard"; // Nom du fichier HTML (user-dashboard.html)
    }
}