package com.furious.codec;

import java.util.Objects;

import com.alibaba.fastjson.JSONObject;

import io.netty.buffer.ByteBuf;

/**
 * @author furious 2024/4/29
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class Serializer {

    public static Serializer INSTANCE = new Serializer();

    public void serialize(Exchange obj, ByteBuf buf) {
        buf.writeInt(obj.getMagicNumber());
        buf.writeByte(obj.getType());
        if (Objects.nonNull(obj.getObject())) {
            byte[] bytes = JSONObject.toJSONBytes(obj.getObject());
            buf.writeInt(bytes.length);
            buf.writeBytes(bytes);
        } else {
            buf.writeInt(0);
        }
    }

    public Exchange deserialize(ByteBuf buf) {
        buf.skipBytes(4);
        byte type = buf.readByte();
        Exchange exchange = new Exchange(type);
        int length = buf.readInt();
        if (length > 0) {
            byte[] bytes = new byte[length];
            buf.readBytes(bytes);
            Object object = JSONObject.parseObject(bytes, ExchangeTypeEnum.clazz(type));
            exchange.setObject(object);
        }

        exchange.setLength(length);
        return exchange;
    }

}
