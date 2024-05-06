package com.furious.codec;

import java.util.Objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author furious 2024/4/29
 */
@Getter
@RequiredArgsConstructor
public enum ExchangeTypeEnum {

    REQUEST(Integer.valueOf(1).byteValue(), Request.class),
    RESPONSE(Integer.valueOf(2).byteValue(), Response.class),
    ;

    private final Byte type;
    private final Class<?> clazz;

    public static Class<?> clazz(Byte type) {
        for (ExchangeTypeEnum e : ExchangeTypeEnum.values()) {
            if (Objects.equals(e.getType(), type)) {
                return e.getClazz();
            }
        }
        throw new IllegalArgumentException(type + " type is not supported");
    }

}
