package com.bibliotheque.gestion_pret.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bibliotheque.gestion_pret.model.Pret;
import com.bibliotheque.gestion_pret.service.AdminService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        // On récupère toutes les listes et statistiques d'un coup
        model.addAttribute("stats", adminService.getDashboardStatistics());
        model.addAttribute("livres", adminService.getAllLivres());
        model.addAttribute("adherents", adminService.getAllAdherents());

        return "admin/admin-dashboard";
    }

    @GetMapping("/prets")
    public String gestionPrets(Model model) {
        List<Pret> prets = adminService.getAllPrets();
        model.addAttribute("prets", prets);
        return "admin/admin-prets";
    }
}
