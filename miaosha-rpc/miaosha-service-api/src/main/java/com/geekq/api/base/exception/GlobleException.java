package com.geekq.api.base.exception;


import com.geekq.api.base.enums.ResultStatus;

public class GlobleException extends RuntimeException {

    private ResultStatus status;

    public GlobleException(ResultStatus status) {
        super();
        this.status = status;
    }

    public ResultStatus getStatus() {
        return status;
    }

    public void setStatus(ResultStatus status) {
        this.status = status;
    }
}
