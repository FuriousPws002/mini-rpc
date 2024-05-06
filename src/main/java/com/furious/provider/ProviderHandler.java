package com.furious.provider;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

import com.furious.codec.Exchange;
import com.furious.codec.ExchangeTypeEnum;
import com.furious.codec.Request;
import com.furious.codec.Response;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;

/**
 * @author furious 2024/4/29
 */
@RequiredArgsConstructor
public class ProviderHandler extends SimpleChannelInboundHandler<Exchange<Request>> {

    private final ProviderServiceRegistry registry;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Exchange<Request> msg) throws Exception {
        System.err.println("收到客户端请求->" + msg);
        Request request = msg.getObject();
        Object object = registry.getObject(request.getClassName());

        Object[] args = request.getArgs();
        Object result;
        if (Objects.nonNull(args)) {
            Class[] parameterTypes = Arrays.stream(args).map(Object::getClass).toArray(Class[]::new);
            Method method = object.getClass().getMethod(request.getMethod(), parameterTypes);
            result = method.invoke(object, args);
        } else {
            Method method = object.getClass().getMethod(request.getMethod());
            result = method.invoke(object);
        }

        Exchange<Response> exchange = new Exchange<>(ExchangeTypeEnum.RESPONSE.getType());
        Response response = new Response();
        response.setReqNo(request.getReqNo());
        response.setResult(result);
        exchange.setObject(response);
        ctx.channel().writeAndFlush(exchange);
    }
}
