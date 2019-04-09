package edu.ecnu.scsse.pizza.bussiness.server.service;

import edu.ecnu.scsse.pizza.bussiness.server.exception.NotFoundException;
import edu.ecnu.scsse.pizza.bussiness.server.model.entity.Menu;
import edu.ecnu.scsse.pizza.bussiness.server.model.entity.Order;
import edu.ecnu.scsse.pizza.bussiness.server.model.entity.PendingOrder;
import edu.ecnu.scsse.pizza.bussiness.server.model.entity.SaleStatus;
import edu.ecnu.scsse.pizza.bussiness.server.model.request_response.ResultType;
import edu.ecnu.scsse.pizza.bussiness.server.model.request_response.SimpleResponse;
import edu.ecnu.scsse.pizza.bussiness.server.model.request_response.menu.MenuDetailResponse;
import edu.ecnu.scsse.pizza.bussiness.server.utils.CastEntity;
import edu.ecnu.scsse.pizza.bussiness.server.utils.CopyUtils;
import edu.ecnu.scsse.pizza.data.bean.MenuBean;
import edu.ecnu.scsse.pizza.data.bean.OrderBean;
import edu.ecnu.scsse.pizza.data.domain.*;
import edu.ecnu.scsse.pizza.data.enums.OrderStatus;
import edu.ecnu.scsse.pizza.bussiness.server.model.request_response.order.*;
import edu.ecnu.scsse.pizza.data.enums.PizzaStatus;
import edu.ecnu.scsse.pizza.data.enums.PizzaTag;
import edu.ecnu.scsse.pizza.data.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Constructor;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(SessionService.class);

    @Autowired
    private OrderJpaRepository orderJpaRepository;

    @Autowired
    private UserAddressJpaRepository userAddressJpaRepository;

    @Autowired
    private OrderMenuJpaRepository  orderMenuJpaRepository;

    public List<OrderManageResponse> getOrderList() throws Exception {
        Date start = new Date();
        List<OrderManageResponse> orderList = new ArrayList<>();
        List<Object[]> objects = orderJpaRepository.getOrderBeans();
        List<OrderBean> orderBeans = CastEntity.castEntityToOrderBean(objects, OrderBean.class);
        if(orderBeans.size()!=0){
            orderList = orderBeans.stream().map(this::convertFromBeanToResponse).collect(Collectors.toList());
        }
        else{
            log.warn("Fail to find the order list.");
        }
        Date end = new Date();
        double time = (end.getTime()-start.getTime())/1000.0;
        log.info("Query time:"+String.valueOf(time)+"s");
        return orderList;
    }

    public OrderManageResponse getOrderDetail(int orderId) throws Exception{
        OrderManageResponse orderManageResponse = new OrderManageResponse();
        List<Object[]> object = orderJpaRepository.getOrderBeanById(orderId);
        if(object!=null){
            OrderBean orderBean = (CastEntity.castEntityToOrderBean(object,OrderBean.class)).get(0);
            orderManageResponse = convertFromBeanToResponseWithMenuList(orderBean);
        }
        else{
            NotFoundException e = new NotFoundException("Order detail is not found.");
            log.warn("Fail to find the order detail.", e);
        }
        return orderManageResponse;
    }

    public YesterdaySaleResponse getYesterdaySaleStatus(String yesterday) throws ParseException{
        List<SaleStatus> saleStatusList = getSaleStatusList(yesterday,yesterday);
        return new YesterdaySaleResponse(saleStatusList.get(0));
    }

    public List<SaleStatus> getSaleStatusList(String startDate,String endDate) throws ParseException {
        List<SaleStatus> saleStatusList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        long end = sdf.parse(endDate).getTime();
        long start = sdf.parse(startDate).getTime();
        int days = (int)((end-start)/(1000 * 60 * 60 * 24))+1;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sdf.parse(startDate));
        for(int i=0;i<days;i++){
            String date = sdf.format(calendar.getTime());
            SaleStatus saleStatus = getDaySaleStatus(date);
            //if(saleStatus.getOrderNum()!=0)
            saleStatusList.add(saleStatus);
            calendar.add(Calendar.DATE,+1);
        }
        return saleStatusList;
    }

    private SaleStatus getDaySaleStatus(String date){
        List<OrderEntity> orderEntityList = orderJpaRepository.findOrderByCommitTime(date);
        int orderNum = orderEntityList.size();
        int completeNum = 0;
        int cancelNum = 0;
        double totalAmount = 0;
        for(OrderEntity orderEntity : orderEntityList){
            switch (OrderStatus.fromDbValue(orderEntity.getState())){
                case RECEIVED:
                    completeNum++;
                    break;
                case CANCELED:
                    cancelNum++;
                    break;
                default:
                    break;
            }
            if(orderEntity.getTotalPrice()!=null)
                totalAmount += orderEntity.getTotalPrice();
        }
        return new SaleStatus(date,orderNum, completeNum, cancelNum, totalAmount);
    }

    public List<PendingOrder> getPendingRequestList(){
        List<PendingOrder> pendingList = new ArrayList<>();
        List<PendingOrder> resultList = new ArrayList<>();
        List<OrderEntity> orderEntityList = orderJpaRepository.findPendingList();
        if(orderEntityList.size()!=0){
            pendingList = orderEntityList.stream().map(this::convertToPendingOrder).collect(Collectors.toList());
            for(PendingOrder pendingOrder:pendingList){
                if(pendingOrder.getPeriod()<=10)
                    resultList.add(pendingOrder);
            }
        }
        else{
            log.warn("Fail to find pending list.");
        }
        return resultList;
    }

    public SimpleResponse changeOrderStatus(int orderId, OrderStatus status){
        SimpleResponse response = new SimpleResponse();
        String msg;
        Optional<OrderEntity> optional = orderJpaRepository.findById(orderId);
        if(optional.isPresent()){
            OrderEntity orderEntity = optional.get();
            if(OrderStatus.fromDbValue(orderEntity.getState())==OrderStatus.CANCEL_CHECKING){
                orderEntity.setState(status.getDbValue());
                orderJpaRepository.saveAndFlush(orderEntity);
                response.setResultType(ResultType.SUCCESS);
                msg = String.format("Success:change order %d status to %s",orderId, status);
                response.setSuccessMsg(msg);
                log.info(msg);
            }
            else{
                response.setResultType(ResultType.FAILURE);
                msg = "Wrong order status.";
                response.setErrorMsg(msg);
                log.warn(msg);
            }
        }
        else{
            msg = String.format("Order %d is not found.",orderId);
            NotFoundException e = new NotFoundException(msg);
            response = new SimpleResponse(e);
            log.warn(msg);
        }
        return response;
    }

    public List<PendingOrder> getCancelOrderList(){
        List<PendingOrder> cancelList = new ArrayList<>();
        List<OrderEntity> orderEntityList = orderJpaRepository.findCancelOrderList();
        if(orderEntityList.size()!=0){
            cancelList = orderEntityList.stream().map(this::convertToPendingOrder).collect(Collectors.toList());
        }
        else{
            log.warn("Fail to find cancel list.");
        }
        return cancelList;
    }


    private OrderManageResponse convertFromBeanToResponse(OrderBean orderBean){
        OrderManageResponse response = new OrderManageResponse();
        CopyUtils.copyProperties(orderBean,response);
        response.setOrderId(String.valueOf(orderBean.getId()));
        response.setShopId(String.valueOf(orderBean.getShopId()));
        response.setDriverId(String.valueOf(orderBean.getDriverId()));
        response.setState(OrderStatus.fromDbValue(orderBean.getState()).getExpression());
        String commitTimePattern = "yyyy/MM/dd hh:MM:ss";
        DateFormat df = new SimpleDateFormat(commitTimePattern);
        if(orderBean.getCommitTime()!=null)
            response.setCommitTime(df.format(orderBean.getCommitTime()));
        if(orderBean.getStartDeliverTime()!=null)
            response.setStartDeliverTime(df.format(orderBean.getStartDeliverTime()));
        if(orderBean.getArriveTime()!=null)
            response.setArriveTime(df.format(orderBean.getArriveTime()));
        return response;
    }

    private OrderManageResponse convertFromBeanToResponseWithMenuList(OrderBean orderBean) throws Exception{
        OrderManageResponse response = convertFromBeanToResponse(orderBean);
        //设置订购的披萨信息
        List<MenuDetailResponse> menuList = new ArrayList<>();
        List<Object[]> objects = orderMenuJpaRepository.findMenuListByOrderId(orderBean.getId());
        List<MenuBean> menuBeans = CastEntity.castEntityToMenuBean(objects,MenuBean.class);
        menuList = menuBeans.stream().map(this::convertFromMenuBeanToMenuResponse).collect(Collectors.toList());
        response.setMenuList(menuList);
        return response;
    }

    private MenuDetailResponse convertFromMenuBeanToMenuResponse(MenuBean menuBean){
        MenuDetailResponse menu = new MenuDetailResponse();
        CopyUtils.copyProperties(menuBean,menu);
        menu.setId(String.valueOf(menuBean.getMenuId()));
        menu.setIngredients(new ArrayList<>());
        menu.setState(PizzaStatus.fromDbValue(menuBean.getState()).getExpression());
        menu.setTagName(PizzaTag.fromDbValue(menuBean.getTag()).getExpression());
        return menu;
    }

    private PendingOrder convertToPendingOrder(OrderEntity orderEntity){
        PendingOrder pendingOrder = new PendingOrder();
        CopyUtils.copyProperties(orderEntity,pendingOrder);
        pendingOrder.setState(OrderStatus.fromDbValue(orderEntity.getState()).getExpression());
        pendingOrder.setOrderId(String.valueOf(orderEntity.getId()));

        //设置收货人信息
        Optional<UserAddressEntity> userAddressEntityOptional = userAddressJpaRepository.findByUserIdAndAddressId(orderEntity.getUserId(),orderEntity.getAddressId());
        if(userAddressEntityOptional.isPresent()){
            UserAddressEntity userAddressEntity = userAddressEntityOptional.get();
            pendingOrder.setReceiveName(userAddressEntity.getName());
            pendingOrder.setReceivePhone(userAddressEntity.getPhone());
        }

        //下单时间的格式转换
        String commitTimePattern = "yyyy/MM/dd hh:MM:ss";
        DateFormat df = new SimpleDateFormat(commitTimePattern);
        if(orderEntity.getCommitTime()!=null) {
            pendingOrder.setCommitTime(df.format(orderEntity.getCommitTime()));
            //已支付时长
            long date = orderEntity.getCommitTime().getTime();
            long cur = (new Date()).getTime();
            double period = (cur-date)/1000/60;
            pendingOrder.setPeriod(period);
            String p;
            if(period>10)
                p = ">10min";
            else
                p = (int)period+"min";
            pendingOrder.setPaidPeriod(p);
        }
        return pendingOrder;
    }
}
