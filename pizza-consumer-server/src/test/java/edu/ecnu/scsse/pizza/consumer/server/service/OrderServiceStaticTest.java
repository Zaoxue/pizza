package edu.ecnu.scsse.pizza.consumer.server.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.google.gson.Gson;
import edu.ecnu.scsse.pizza.consumer.server.FakeFactory;
import edu.ecnu.scsse.pizza.consumer.server.exception.ConsumerServerException;
import edu.ecnu.scsse.pizza.consumer.server.exception.IllegalArgumentException;
import edu.ecnu.scsse.pizza.consumer.server.exception.PayFailureException;
import edu.ecnu.scsse.pizza.consumer.server.exception.ServiceException;
import edu.ecnu.scsse.pizza.consumer.server.model.PayType;
import edu.ecnu.scsse.pizza.consumer.server.model.entity.Order;
import edu.ecnu.scsse.pizza.consumer.server.utils.HttpUtils;
import edu.ecnu.scsse.pizza.data.domain.OrderEntity;
import edu.ecnu.scsse.pizza.data.enums.OrderStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;

import static edu.ecnu.scsse.pizza.consumer.server.utils.ThrowableCaptor.thrownBy;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;

@RunWith(PowerMockRunner.class)
@PrepareForTest({OrderService.class, HttpUtils.class})
@PowerMockIgnore({"javax.management.*","com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*"})
public class OrderServiceStaticTest {

    private static final Gson GSON = new Gson();

    private OrderService orderService;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(OrderService.class);
        orderService = new OrderService();
    }

    @Test
    public void testPayRequestWithNullOrderId() {
        IllegalArgumentException e = (IllegalArgumentException) thrownBy(() -> orderService.payRequest(null, 20.12, PayType.MOBILE));
        assertEquals(e.getMessage(), "orderUuid can't be null.");
    }

    @Test
    public void testPayRequestWithZeroPrice() {
        IllegalArgumentException e = (IllegalArgumentException) thrownBy(() -> orderService.payRequest("AAA", 0, PayType.MOBILE));
        assertEquals(e.getMessage(), "totalPrice must be positive.");
    }

    @Test
    public void testPayRequestWithNegativePrice() {
        IllegalArgumentException e = (IllegalArgumentException) thrownBy(() -> orderService.payRequest("AAA", -10, PayType.MOBILE));
        assertEquals(e.getMessage(), "totalPrice must be positive.");
    }

    @Test
    public void testPayRequest() throws Exception {
        String returnString = "body";
        DefaultAlipayClient client = PowerMockito.mock(DefaultAlipayClient.class);
        AlipayTradeWapPayResponse payResponse = new AlipayTradeWapPayResponse();
        payResponse.setBody(returnString);
        Mockito.when(client.pageExecute(any())).thenReturn(payResponse);
        PowerMockito.whenNew(DefaultAlipayClient.class).withAnyArguments().thenReturn(client);

        double price = 20.12;
        String orderUuid = "AAA";

        String result = orderService.payRequest(orderUuid, price, PayType.MOBILE);
        assertEquals(returnString, result);
    }

    @Test
    public void testPayRequestFailure() throws Exception {
        String errorMsg = "Error Msg.";
        DefaultAlipayClient client = PowerMockito.mock(DefaultAlipayClient.class);
        AlipayTradeWapPayResponse payResponse = new AlipayTradeWapPayResponse();
        payResponse.setSubCode("1122");
        payResponse.setMsg(errorMsg);
        Mockito.when(client.pageExecute(any())).thenReturn(payResponse);
        PowerMockito.whenNew(DefaultAlipayClient.class).withAnyArguments().thenReturn(client);

        double price = 20.12;
        String orderUuid = "AAA";

        PayFailureException e = (PayFailureException) thrownBy(() -> orderService.payRequest(orderUuid, price, PayType.MOBILE));
        assertEquals(e.getMessage(), payResponse.getMsg());
    }

    @Test
    public void testPayRequestWithAlipayApiException() throws Exception {
        DefaultAlipayClient client = PowerMockito.mock(DefaultAlipayClient.class);
        AlipayApiException exception = new AlipayApiException();
        Mockito.when(client.pageExecute(any())).thenThrow(exception);
        PowerMockito.whenNew(DefaultAlipayClient.class).withAnyArguments().thenReturn(client);

        double price = 20.12;
        String orderUuid = "AAA";

        PayFailureException e = (PayFailureException) thrownBy(() -> orderService.payRequest(orderUuid, price, PayType.MOBILE));
        assertEquals(e.getCause(), exception);
    }

    @Test
    public void testSendOrderWithNullOrderUuid() {
        IllegalArgumentException e = (IllegalArgumentException) thrownBy(() ->
                orderService.sendOrder(null, 1));
        assertEquals(e.getMessage(), "orderUuid can't be null.");
    }

    @Test
    public void testSendOrderWithZeroUserAddressId() {
        IllegalArgumentException e = (IllegalArgumentException) thrownBy(() ->
                orderService.sendOrder("AAA", 0));
        assertEquals(e.getMessage(), "userAddressId must be positive.");
    }

    @Test
    public void testSendOrderWithNegativeUserAddressId() {
        IllegalArgumentException e = (IllegalArgumentException) thrownBy(() ->
                orderService.sendOrder("AAA", -1));
        assertEquals(e.getMessage(), "userAddressId must be positive.");
    }

    @Test
    public void testSendOrder() throws IOException, ConsumerServerException {
        String orderUuid = "AAA";
        Integer userAddressId = 1;
        PowerMockito.mockStatic(HttpUtils.class);
        OrderEntity entity = FakeFactory.fakeOrderEntity(1, orderUuid, 1, 1, OrderStatus.WAIT_DELIVERY).get();
        PowerMockito.when(HttpUtils.commitOrder(orderUuid, userAddressId)).thenReturn(entity);
        Order order = orderService.sendOrder(orderUuid, userAddressId);
        assertEquals(order.getStatus(), OrderStatus.WAIT_DELIVERY);
        assertEquals(order.getId(), orderUuid);
    }

    @Test
    public void testSendOrderWithIOException() throws IOException, ConsumerServerException {
        String orderUuid = "AAA";
        Integer userAddressId = 1;
        PowerMockito.mockStatic(HttpUtils.class);
        PowerMockito.when(HttpUtils.commitOrder(orderUuid, userAddressId)).thenThrow(IOException.class);
        ServiceException exception = (ServiceException) thrownBy(() -> orderService.sendOrder(orderUuid, userAddressId));
        assertEquals(exception.getMessage(), "I/O Exception while sending http request to Business Server.");
    }

    @Test
    public void testSetters(){
        OrderService.Phones phones=new OrderService.Phones();
        phones.setServicePhone("");
        phones.setDeliverymanPhone("");
        phones.setShopPhone("");
    }
}
