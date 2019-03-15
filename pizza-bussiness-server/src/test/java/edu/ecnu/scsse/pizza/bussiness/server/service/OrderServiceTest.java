package edu.ecnu.scsse.pizza.bussiness.server.service;

import edu.ecnu.scsse.pizza.bussiness.server.TestApplication;
import edu.ecnu.scsse.pizza.bussiness.server.model.entity.Order;
import edu.ecnu.scsse.pizza.bussiness.server.model.entity.SaleStatus;
import edu.ecnu.scsse.pizza.bussiness.server.model.request_response.ResultType;
import edu.ecnu.scsse.pizza.bussiness.server.model.request_response.order.*;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.List;

public class OrderServiceTest extends TestApplication {
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceTest.class);
    @Autowired
    OrderService orderService;

    @Test
    public void testGetOrderList(){
        OrderManageResponse orderManageResponse = orderService.getOrderList();
        List<Order> orderList = orderManageResponse.getOrderList();
        logger.info("Total order number is {}",orderList.size());
        Assert.assertEquals(2,orderList.size());
    }

    @Test
    public void testGetOrderDetailByRightMenuId(){
        OrderDetailRequest request = new OrderDetailRequest(1);
        OrderDetailResponse orderDetailResponse = orderService.getOrderDetail(request);
        //OrderDetailResponse exceptedResponse = new OrderDetailResponse()
        Assert.assertEquals(1,Integer.parseInt(orderDetailResponse.getOrderId()));
        Assert.assertEquals(1,orderDetailResponse.getState().getDbValue());
        Assert.assertEquals("123",orderDetailResponse.getReceivePhone());
        Assert.assertEquals("cao miao",orderDetailResponse.getReceiveName());
        Assert.assertEquals("15800349392",orderDetailResponse.getBuyPhone());
        Assert.assertEquals(1,Integer.parseInt(orderDetailResponse.getShopId()));
        Assert.assertEquals("梁磊",orderDetailResponse.getShopName());
        Assert.assertEquals("",orderDetailResponse.getDriverId());
    }

    @Test
    public void testGetOrderDriverInfoByMenuId(){
        OrderDetailRequest request = new OrderDetailRequest(2);
        OrderDetailResponse orderDetailResponse = orderService.getOrderDetail(request);
        Assert.assertEquals(1,Integer.parseInt(orderDetailResponse.getDriverId()));
        Assert.assertEquals("13162308625",orderDetailResponse.getDriverPhone());
        Assert.assertEquals("chen",orderDetailResponse.getDriverName());
    }

    @Test
    public void testGetSaleStatusList() throws ParseException{
        SaleResponse response = orderService.getSaleStatusList("2019/03/01","2019/03/15");
        List<SaleStatus> saleList = response.getSaleStatusList();
        for(SaleStatus s:saleList)
            logger.info(s.toString());
        Assert.assertEquals(15,saleList.size());
        Assert.assertEquals("2019/03/01",saleList.get(0).getDate());
        Assert.assertEquals(1,saleList.get(5).getOrderNum());
        Assert.assertEquals(0,saleList.get(5).getCompleteNum());
        Assert.assertEquals(0,saleList.get(5).getCancelNum());
    }


    @Test
    public void testGetYesterdaySaleWithDate() throws ParseException{
        YesterdaySaleResponse response = orderService.getYesterdaySaleStatus("2019/03/14");
        Assert.assertEquals(ResultType.SUCCESS,response.getResultType());
        Assert.assertEquals(0,response.getOrderNum());
        Assert.assertEquals(0,response.getCancelNum());
        Assert.assertEquals(0,response.getCompleteNum());
    }



}
