package com.lumberjack.pollapp.controller;

import com.lumberjack.pollapp.model.AttributeBindingModel;
import com.lumberjack.pollapp.model.Option;
import com.lumberjack.pollapp.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@Slf4j
public class MainController {

    @Autowired
    private RedisService redisService;

    @RequestMapping("/")
    public String pollPage(Model theModel, HttpServletRequest request) {
        List<Option> allOptions = redisService.getAllOptions();
        theModel.addAttribute("allOptions", allOptions);
        theModel.addAttribute("option", "");
        AttributeBindingModel attributeBindingModel = redisService.checkIfVoted(request.getRemoteUser());
        if (attributeBindingModel.isUserVoted()) {
            theModel.addAttribute("userVoted", true);
        } else {
            theModel.addAttribute("userVoted", false);
        }
        theModel.addAttribute("allPolls", attributeBindingModel.getAllPolls());

        return "poll-page";
    }

    @PostMapping("/poll")
    public String vote(@ModelAttribute("option") String optionSelected,
                       HttpServletRequest request,
                       BindingResult result, HttpServletResponse response) {
        if (!request.isUserInRole("USER")) {
            return "redirect:/login";

        }

        redisService.calculatePercentage(optionSelected, request.getRemoteUser());

        return "redirect:/";
    }

    @RequestMapping("/login")
    public String loginPage() {
        return "login-page";
    }


    @RequestMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        return "login-page";
    }


}
