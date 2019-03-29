package edu.ecnu.scsse.pizza.bussiness.server.service;

import edu.ecnu.scsse.pizza.bussiness.server.exception.BusinessServerException;
import edu.ecnu.scsse.pizza.bussiness.server.exception.ExceptionType;
import edu.ecnu.scsse.pizza.bussiness.server.exception.NotFoundException;
import edu.ecnu.scsse.pizza.bussiness.server.model.entity.Driver;
import edu.ecnu.scsse.pizza.bussiness.server.model.enums.OperateObject;
import edu.ecnu.scsse.pizza.bussiness.server.model.enums.OperateResult;
import edu.ecnu.scsse.pizza.bussiness.server.model.enums.OperateType;
import edu.ecnu.scsse.pizza.bussiness.server.model.request_response.driver.DriverDetailRequest;
import edu.ecnu.scsse.pizza.bussiness.server.model.request_response.driver.DriverDetailResponse;
import edu.ecnu.scsse.pizza.bussiness.server.model.request_response.driver.DriverManageResponse;
import edu.ecnu.scsse.pizza.bussiness.server.utils.CopyUtils;
import edu.ecnu.scsse.pizza.data.domain.DriverEntity;
import edu.ecnu.scsse.pizza.data.domain.PizzaShopEntity;
import edu.ecnu.scsse.pizza.data.repository.DriverJpaRepository;
import edu.ecnu.scsse.pizza.data.repository.PizzaShopJpaRepository;
import org.aspectj.weaver.ast.Not;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DriverService {
    private static final Logger log = LoggerFactory.getLogger(ShopService.class);

    @Autowired
    private DriverJpaRepository driverJpaRepository;

    @Autowired
    private PizzaShopJpaRepository shopJpaRepository;

    @Autowired
    private OperateLoggerService operateLoggerService;

    public List<Driver> getDriverList(){
//        DriverManageResponse driverManageResponse;
        List<Driver> driverList = new ArrayList<>();
        List<DriverEntity> driverEntityList = driverJpaRepository.findAll();
        if(driverEntityList.size()!=0){
//            driverManageResponse = new DriverManageResponse();
            driverList = driverEntityList.stream().map(this::convert).collect(Collectors.toList());
//            driverManageResponse.setDriverList(driverList);
        }
        else{
            NotFoundException e = new NotFoundException("Driver list is not found.");
//            driverManageResponse = new DriverManageResponse(e);
            log.warn("Fail to find the driver list.", e);
        }

        return driverList;
    }

    public DriverDetailResponse editDriverDetail(DriverDetailRequest request) throws BusinessServerException{
        DriverDetailResponse driverDetailResponse;
        int driverId = request.getDriverId();
        String operateType = OperateType.UPDATE.getExpression();
        String operateObj = OperateObject.DRIVER.getExpression() + String.valueOf(driverId);
        try {
            Optional<DriverEntity> optional = driverJpaRepository.findById(driverId);
            if (optional.isPresent()) {
                DriverEntity entity = optional.get();
                entity.setName(request.getName());
                entity.setPhone(request.getPhone());
                entity.setShopId(request.getShopId());
                driverJpaRepository.saveAndFlush(entity);
                driverDetailResponse = new DriverDetailResponse(driverId);
                operateLoggerService.addOperateLogger(operateType, operateObj, OperateResult.SUCCESS.getExpression());
            } else {
                String message = String.format("Driver %s is not found.",driverId);
                NotFoundException e = new NotFoundException(message);
                driverDetailResponse = new DriverDetailResponse(e);
                log.warn(message, e);
                operateLoggerService.addOperateLogger(operateType, operateObj, OperateResult.FAILURE.getExpression() + " :"+message);
            }
        }catch (Exception e){
            log.error("Fail to update driver.",e);
            throw new BusinessServerException(ExceptionType.REPOSITORY, "Fail to update driver.", e);
        }

        return driverDetailResponse;
    }

    public DriverDetailResponse addNewDriver(DriverDetailRequest request) throws BusinessServerException{
        DriverEntity driverEntity;
        DriverDetailResponse response;
        String type = OperateType.INSERT.getExpression();//操作类型
        String object = OperateObject.DRIVER.getExpression();//操作对象
        try {
            driverEntity = new DriverEntity();
            driverEntity.setName(request.getName());
            driverEntity.setPhone(request.getPhone());
            driverEntity.setShopId(request.getShopId());
            driverJpaRepository.saveAndFlush(driverEntity);
            response = new DriverDetailResponse(driverEntity.getId());
            operateLoggerService.addOperateLogger(type, object, OperateResult.SUCCESS.getExpression());
        }catch (Exception e){
            String message = "Fail to insert driver.";
            log.error(message,e);
            throw new BusinessServerException(ExceptionType.REPOSITORY, message, e);
        }
        return response;
    }

    private Driver convert(DriverEntity driverEntity){
        Driver driver = new Driver();
        CopyUtils.copyProperties(driverEntity,driver);
        Optional<PizzaShopEntity> optional = shopJpaRepository.findPizzaShopEntityById(driverEntity.getShopId());
        if(optional.isPresent()){
            PizzaShopEntity entity = optional.get();
            String shopName = entity.getName();
            driver.setShopName(shopName);
        }
        return driver;
    }
}