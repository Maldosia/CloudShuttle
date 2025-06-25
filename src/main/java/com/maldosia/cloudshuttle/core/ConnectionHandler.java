/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.maldosia.cloudshuttle.core;

import com.maldosia.cloudshuttle.core.options.NetworkOptions;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

@ChannelHandler.Sharable
public class ConnectionHandler extends ChannelDuplexHandler {

    private static final Logger log = LoggerFactory.getLogger(ConnectionHandler.class);

    private final TcpClient tcpClient;

    public ConnectionHandler(TcpClient tcpClient) {
        this.tcpClient = tcpClient;
    }

    /**
     * @see ChannelDuplexHandler#connect(ChannelHandlerContext, SocketAddress, SocketAddress, ChannelPromise)
     */
    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress,
                        SocketAddress localAddress, ChannelPromise promise) throws Exception {
//        log.info("connect");
        super.connect(ctx, remoteAddress, localAddress, promise);
    }

    /**
     * @see ChannelDuplexHandler#disconnect(ChannelHandlerContext, ChannelPromise)
     */
    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
//        log.info("disconnect");
        super.disconnect(ctx, promise);
    }

    /**
     * @see ChannelDuplexHandler#close(ChannelHandlerContext, ChannelPromise)
     */
    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
//        log.info("close");
        super.close(ctx, promise);
    }

    /**
     * @see io.netty.channel.ChannelInboundHandlerAdapter#channelRegistered(ChannelHandlerContext)
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
//        log.info("channelRegistered");
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
//        log.info("channelUnregistered");
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        log.info("channelActive");
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("Disconnect from {}", this.tcpClient.getUrl().getRemoteUrl());
        super.channelInactive(ctx);

        if (this.tcpClient.option(NetworkOptions.RECONNECT_SWITCH)) {
            Integer reconnectIntervals = this.tcpClient.option(NetworkOptions.RECONNECT_INTERVALS);
            ctx.channel().eventLoop().schedule(tcpClient::connect, reconnectIntervals, TimeUnit.SECONDS);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object event) throws Exception {
//        log.info("userEventTriggered");
        super.userEventTriggered(ctx, event);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        log.warn("exceptionCaught");
        ctx.channel().close();
    }
}
