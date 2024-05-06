package com.furious.codec;

import java.io.Serializable;

import lombok.Data;

/**
 * @author furious 2024/4/29
 */
@Data
public class Response implements Serializable {

    private static final long serialVersionUID = -6771226233203249638L;

    private String reqNo;
    private Integer status;
    private String msg;
    private Object result;
}
