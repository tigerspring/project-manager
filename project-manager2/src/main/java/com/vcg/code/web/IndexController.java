package com.vcg.code.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.vcg.code.model.User;
import com.vcg.code.service.UserService;

@Controller
public class IndexController {

	@Autowired
	UserService uesrService;

	@RequestMapping(path = { "/", "/index" })
	public String index() {
		return "admin/index";
	}

	@RequestMapping("/login")
	public String login(HttpServletRequest request, User user) {
		return "admin/login";
	}

	@RequestMapping("/loginForm")
	public String loginForm(User user, HttpSession session) {
		if (user.getUsername() != null && user.getPassword() != null) {
			user = uesrService.login(user);
			if (user != null) {
				session.setAttribute("loginUser", user);
			}
		}

		return "redirect:index";
	}

	@RequestMapping("/loginOut")
	public void loginOut(HttpSession session) {
		session.setAttribute("loginUser", null);
	}
}
