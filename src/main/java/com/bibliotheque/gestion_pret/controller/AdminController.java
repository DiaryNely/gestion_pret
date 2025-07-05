package com.bibliotheque.gestion_pret.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin") // Toutes les URL de ce contrôleur commenceront par /admin
public class AdminController {

    @GetMapping("/dashboard")
    public String adminDashboard() {
        return "admin/admin-dashboard"; // Nom du fichier HTML (admin-dashboard.html)
    }
}