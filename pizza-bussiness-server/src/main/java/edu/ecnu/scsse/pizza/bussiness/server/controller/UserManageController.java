package edu.ecnu.scsse.pizza.bussiness.server.controller;


import edu.ecnu.scsse.pizza.bussiness.server.model.request_response.user.UserManageResponse;
import edu.ecnu.scsse.pizza.bussiness.server.service.UserManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/user")
public class UserManageController {
    @Autowired
    private UserManageService userManageService;

    /**
     * 查看用户信息列表
     */
    @RequestMapping(value = "/getUserList",method = RequestMethod.GET)
    @ResponseBody
    public UserManageResponse getUserList(){
        return userManageService.getUserList();
    }
}
