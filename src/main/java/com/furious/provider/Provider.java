package com.furious.provider;

import com.furious.codec.RequestCodec;
import com.furious.codec.ResponseCodec;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author furious 2024/4/29
 */
public class Provider {

    private final EventLoopGroup boss = new NioEventLoopGroup();
    private final EventLoopGroup worker = new NioEventLoopGroup();

    public void start() {
        new Thread(this::run).start();
    }

    public void close() {
        boss.shutdownGracefully();
        worker.shutdownGracefully();
    }

    private void run() {
        ProviderServiceRegistry registry = ProviderServiceRegistry.INSTANCE;
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ResponseCodec());
                        ch.pipeline().addLast(new RequestCodec());
                        ch.pipeline().addLast(new ProviderHandler(registry));
                    }
                }).bind(8888);
    }
}
