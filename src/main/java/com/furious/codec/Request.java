package com.furious.codec;

import java.io.Serializable;

import lombok.Data;

/**
 * @author furious 2024/4/29
 */
@Data
public class Request implements Serializable {

    private static final long serialVersionUID = 3520684312804587953L;

    private String reqNo;
    private String className;
    private String method;
    private Object[] args;
}
