package edu.ecnu.scsse.pizza.bussiness.server.controller;

import edu.ecnu.scsse.pizza.bussiness.server.model.LoginRequest;
import edu.ecnu.scsse.pizza.bussiness.server.model.LoginResponse;
import edu.ecnu.scsse.pizza.bussiness.server.service.AdminService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    @ResponseBody
    public LoginResponse adminLogin(@RequestBody LoginRequest loginRequest){
        return adminService.adminLogin(loginRequest);
    }

}
