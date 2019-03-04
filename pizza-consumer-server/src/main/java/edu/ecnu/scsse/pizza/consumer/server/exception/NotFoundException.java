package edu.ecnu.scsse.pizza.consumer.server.exception;

import static edu.ecnu.scsse.pizza.consumer.server.exception.ExceptionType.BUSSINESS;

public class NotFoundException extends ConsumerServerException {

    public NotFoundException(String message) {
        super(BUSSINESS, "未找到相应数据！", message, null);
    }

}
