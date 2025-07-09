package com.maldosia.cloudshuttle.example;

import com.maldosia.cloudshuttle.core.*;
import com.maldosia.cloudshuttle.core.message.MessageType;
import com.maldosia.cloudshuttle.core.message.MessageAutoRegistrar;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CloudShuttle 快速上手示例
 * 展示如何声明协议、定义消息、注册消息类型、启动服务端/客户端并收发消息。
 */
public class QuickStartExample {
    private static final Logger log = LoggerFactory.getLogger(QuickStartExample.class);

    public static void main(String[] args) throws Exception {
        // 1. 声明协议结构
        ProtocolDefinition def = ProtocolDslBuilder.standard()
                .addField("VERSION", FieldType.CUSTOM, 1)
                .endFlag(0x16)
                .description("带版本号的标准协议")
                .build();
        Protocol protocol = new Protocol(def);

        // 2. 自动注册消息类型（扫描当前包）
        MessageAutoRegistrar.registerAll(protocol, QuickStartExample.class.getPackage().getName());

        // 3. 启动服务端
        TcpServer server = new TcpServer(9000, protocol);
        server.addHandler(new SimpleChannelInboundHandler<Message>() {
            @Override
            protected void channelRead0(ChannelHandlerContext ctx, Message msg) {
                if (msg instanceof LoginRequest) {
                    LoginRequest req = (LoginRequest) msg;
                    log.info("服务端收到登录请求: {}", req.getUsername());
                    LoginResponse resp = new LoginResponse();
                    resp.setSuccess(true);
                    resp.setMessage("登录成功");
                    ctx.writeAndFlush(resp);
                }
            }
        });
        server.startup();

        // 4. 启动客户端
        TcpClient client = new TcpClient("127.0.0.1", 9000, protocol);
        client.addHandler(new SimpleChannelInboundHandler<Message>() {
            @Override
            protected void channelRead0(ChannelHandlerContext ctx, Message msg) {
                if (msg instanceof LoginResponse) {
                    LoginResponse resp = (LoginResponse) msg;
                    log.info("客户端收到登录响应: {}", resp.getMessage());
                }
            }
        });
        client.startup();

        // 5. 发送登录请求
        LoginRequest login = new LoginRequest();
        login.setUsername("admin");
        client.channel.writeAndFlush(login);

        // 6. 优雅关闭
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            client.shutdown();
            server.shutdown();
        }));
    }

    /**
     * 登录请求消息
     */
    @MessageType(code = {0x01})
    public static class LoginRequest implements Message {
        private String username;
        private FrameHeader header;
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        @Override public void setFrameHeader(FrameHeader header) { this.header = header; }
        @Override public FrameHeader getFrameHeader() { return header; }
        @Override
        public void serialize(ByteBuf buf) {
            buf.writeBytes(username.getBytes());
        }
        @Override
        public void deserialize(ByteBuf buf) {
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            this.username = new String(bytes);
        }
    }

    /**
     * 登录响应消息
     */
    @MessageType(code = {0x02})
    public static class LoginResponse implements Message {
        private boolean success;
        private String message;
        private FrameHeader header;
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        @Override public void setFrameHeader(FrameHeader header) { this.header = header; }
        @Override public FrameHeader getFrameHeader() { return header; }
        @Override
        public void serialize(ByteBuf buf) {
            buf.writeBoolean(success);
            buf.writeBytes(message.getBytes());
        }
        @Override
        public void deserialize(ByteBuf buf) {
            this.success = buf.readBoolean();
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            this.message = new String(bytes);
        }
    }
} 