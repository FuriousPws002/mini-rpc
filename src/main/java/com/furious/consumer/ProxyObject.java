package com.furious.consumer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.UUID;

import com.furious.codec.Exchange;
import com.furious.codec.ExchangeTypeEnum;
import com.furious.codec.Request;
import com.furious.codec.Response;
import com.furious.protocol.ExchangeMetadata;

import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;

/**
 * @author furious 2024/4/29
 */
@RequiredArgsConstructor
public class ProxyObject implements InvocationHandler {

    private final ConsumerServiceDiscovery discovery;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> clazz = method.getDeclaringClass();
        if (Objects.equals(clazz, Object.class)) {
            return method.invoke(this, args);
        }
        Exchange<Request> exchange = new Exchange<>(ExchangeTypeEnum.REQUEST.getType());
        Request request = new Request();
        request.setReqNo(UUID.randomUUID().toString());
        request.setClassName(clazz.getName());
        request.setMethod(method.getName());
        request.setArgs(args);
        exchange.setObject(request);
        ExchangeMetadata metadata = null;
        try {
            metadata = discovery.getMetadata(request);
            Channel channel = metadata.getFuture().channel();
            channel.writeAndFlush(exchange);
            metadata.pending();
            Response response = metadata.getResponse();
            return response.getResult();
        } finally {
            discovery.release(metadata);
        }

    }


}
