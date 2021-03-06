package edu.ecnu.scsse.pizza.bussiness.server.controller;

import edu.ecnu.scsse.pizza.bussiness.server.model.request_response.order.OrderReceiveRequest;
import edu.ecnu.scsse.pizza.bussiness.server.model.request_response.order.OrderReceiveResponse;
import edu.ecnu.scsse.pizza.bussiness.server.service.OrderReceiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/orderReceive")
@CrossOrigin
public class OrderReceiveController {
    @Autowired
    private OrderReceiveService orderReceiveService;

    /**
     * 判断商家是否接单，并返回接单信息
     */
    @RequestMapping(value= "/getReceiveShopId",method = RequestMethod.POST)
    @ResponseBody
    public OrderReceiveResponse getReceiveShopId(@RequestBody OrderReceiveRequest orderReceiveRequest){
        return orderReceiveService.getReceiveShopId(orderReceiveRequest);
    }
}
