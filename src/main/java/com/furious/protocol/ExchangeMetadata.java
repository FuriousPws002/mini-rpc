package com.furious.protocol;

import com.furious.codec.Request;
import com.furious.codec.Response;

import io.netty.channel.ChannelFuture;
import lombok.Data;
import lombok.SneakyThrows;

/**
 * @author furious 2024/4/29
 */
@Data
public class ExchangeMetadata {

    private final Request request;
    private volatile Response response;
    private ChannelFuture future;

    @SneakyThrows
    public void pending() {
        synchronized (request) {
            request.wait();
        }
    }

    public void complete() {
        synchronized (request) {
            request.notify();
        }
    }

}
