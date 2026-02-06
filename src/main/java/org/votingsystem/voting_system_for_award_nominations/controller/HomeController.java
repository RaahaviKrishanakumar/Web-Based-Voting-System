package org.votingsystem.voting_system_for_award_nominations.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.votingsystem.voting_system_for_award_nominations.modelentity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("user", new User());
        return "index";
    }


    @GetMapping("/about2")
    public String about(Model model) {
        model.addAttribute("aboutText",
                "The RhythmX  Music Awards, the world’s largest fan-voted music awards show...");
        return "about";
    }

   // @GetMapping("/userdashboard")
   // public String userDashboard(Model model) {
    //    List<Map<String, String>> newsList = new ArrayList<>();
   //     Map<String, String> n1 = Map.of("title", "News Title 1", "description", "Some description", "image", "news1.jpg");
    //    newsList.add(n1);
   //     model.addAttribute("newsList", newsList);
    //    return "userdashboard";
  //  }

   // @GetMapping("/admin/dashboard")
   // public String adminDashboard(Model model) {
     //   model.addAttribute("totalUsers", 120);
    //    model.addAttribute("totalCategories", 6);
     //   model.addAttribute("totalVotes", 2300);
    //    model.addAttribute("activeEvent", "Music Awards 2025");

     //   return "admindashboard";
   // }
}
