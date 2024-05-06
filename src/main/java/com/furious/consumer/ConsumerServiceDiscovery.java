package com.furious.consumer;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.furious.codec.Request;
import com.furious.codec.RequestCodec;
import com.furious.codec.ResponseCodec;
import com.furious.protocol.ExchangeMetadata;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * @author furious 2024/4/29
 */
@Data
@RequiredArgsConstructor
public class ConsumerServiceDiscovery {

    private final String host;
    private final Integer port;
    private final Bootstrap bootstrap;

    private final EventLoopGroup group = new NioEventLoopGroup();
    private final Map<Class<?>, Object> map = new HashMap<>();
    private final Map<Channel, ChannelFuture> channels = new HashMap<>();
    private final Map<Channel, ExchangeMetadata> pending = new HashMap<>();

    public ConsumerServiceDiscovery(String host, Integer port) {
        this.host = host;
        this.port = port;
        this.bootstrap = getBootstrap();
        for (int i = 0; i < 200; i++) {
            ChannelFuture future = getFuture();
            channels.put(future.channel(), future);
        }
    }

    public synchronized ExchangeMetadata getMetadata(Request request) {
        Set<Channel> keys = pending.keySet();
        for (Channel ch : this.channels.keySet()) {
            if (!keys.contains(ch)) {
                ExchangeMetadata metadata = new ExchangeMetadata(request);
                metadata.setFuture(channels.get(ch));
                pending.put(ch, metadata);
                return metadata;
            }
        }
        throw new IllegalArgumentException("No channel is available");
    }

    public synchronized void release(ExchangeMetadata metadata) {
        if (Objects.isNull(metadata) || Objects.isNull(metadata.getFuture())) {
            return;
        }
        pending.remove(metadata.getFuture().channel());
    }

    public <T> T get(Class<T> clazz) {
        Object object = map.get(clazz);
        if (Objects.nonNull(object)) {
            return (T) object;
        }

        object = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new ProxyObject(this));
        map.put(clazz, object);
        return (T) object;
    }

    @SneakyThrows
    private ChannelFuture getFuture() {
        return bootstrap.connect(host, port).sync();
    }

    private Bootstrap getBootstrap() {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                             @Override
                             protected void initChannel(SocketChannel ch) throws Exception {
                                 ch.pipeline().addLast(new ResponseCodec());
                                 ch.pipeline().addLast(new RequestCodec());
                                 ch.pipeline().addLast(new ConsumerHandler(ConsumerServiceDiscovery.this));
                             }
                         }

                );
        return bootstrap;
    }

    public void close() {
        for (Channel channel : channels.keySet()) {
            channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
        group.shutdownGracefully();
    }

}
