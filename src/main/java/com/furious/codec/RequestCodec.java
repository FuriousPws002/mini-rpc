package com.furious.codec;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

/**
 * @author furious 2024/4/29
 */
public class RequestCodec extends MessageToMessageCodec<ByteBuf, Exchange<Request>> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Exchange<Request> msg, List<Object> out) throws Exception {
        ByteBuf buf = Unpooled.buffer();
        Serializer.INSTANCE.serialize(msg, buf);
        out.add(buf);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        Exchange<Request> exchange = Serializer.INSTANCE.deserialize(msg);
        out.add(exchange);
    }
}
