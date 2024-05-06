package com.furious.codec;

import java.io.Serializable;

import lombok.Data;

/**
 * 传输对象
 * Request
 * Response
 *
 * 数据格式如下
 * 4byte + 1byte + 4byte + data
 * 魔数 + 数据类型 + 数据长度 + 数据
 *
 * @author furious 2024/4/28
 */
@Data
public class Exchange<T> implements Serializable {

    private static final long serialVersionUID = 9055901924096213060L;

    private Integer magicNumber = 0xABCDEF;
    private final Byte type;
    private Integer length;
    private T object;

    public Exchange(Byte type) {
        this.type = type;
    }
}
