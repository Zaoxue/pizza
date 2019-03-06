package edu.ecnu.scsse.pizza.bussiness.server.model;

import edu.ecnu.scsse.pizza.bussiness.server.exception.BusinessServerException;

public abstract class BaseResponse{

    private ResultType resultType = ResultType.SUCCESS;

    private String errorMsg;

    private Throwable cause;

    public BaseResponse() {}

    public BaseResponse(BusinessServerException e) {
        this.setResultType(ResultType.FAILURE);
        this.setErrorMsg(e.getHintMessage());
        this.setCause(e);
    }

    public ResultType getResultType() {
        return resultType;
    }

    public void setResultType(ResultType resultType) {
        this.resultType = resultType;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Throwable getCause() {
        return cause;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }
}
