package com.furious.consumer;

import com.furious.codec.Exchange;
import com.furious.codec.Response;
import com.furious.protocol.ExchangeMetadata;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;

/**
 * @author furious 2024/4/29
 */
@RequiredArgsConstructor
public class ConsumerHandler extends SimpleChannelInboundHandler<Exchange<Response>> {

    private final ConsumerServiceDiscovery discovery;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Exchange<Response> msg) throws Exception {
        System.err.println("读到服务端响应->" + msg);
        ExchangeMetadata metadata = discovery.getPending().get(ctx.channel());
        metadata.setResponse(msg.getObject());
        metadata.complete();
    }
}
