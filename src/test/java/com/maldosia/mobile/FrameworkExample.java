package com.maldosia.mobile;

import com.maldosia.cloudshuttle.core.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 示例使用代码
 */
public class FrameworkExample {
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(FrameworkExample.class);

        // 1. 创建协议定义
        ProtocolDefinition protocolDef = ProtocolDefinition.builder()
                .addStartFlag((byte) 0xAA, (byte) 0x55, (byte) 0x99, (byte) 0x66) // 起始标志
                .addHeaderField(4, "checksum", FieldType.HEADER) // 校验和字段
                .addHeaderField(4, "version", FieldType.HEADER)  // 版本字段
                .addFunctionCodeField(4)                         // 功能码字段
                .addLengthField(4)                               // 长度字段
                .addHeaderField(12, "reserved", FieldType.HEADER) // 预留字段
                .addBodyField()                                  // 报文体
//                .addEndFlag((byte) 0x66, (byte) 0x99, (byte) 0x55, (byte) 0xAA) // 结束标志
                .build();

        // 2. 创建协议处理器
        Protocol protocol = new Protocol(protocolDef);

        // 3. 注册消息类型
        protocol.registerMessage(LoginCommand.class, LoginCommand::new);

        // 4. 创建TCP服务器
        TcpServer server = new TcpServer(8080, protocol);
        server.addHandler(new SimpleChannelInboundHandler<Message>() {
            @Override
            protected void channelRead0(ChannelHandlerContext ctx, Message msg) {
                if (msg instanceof LoginCommand) {
                    LoginCommand cmd = (LoginCommand) msg;
                    System.out.println("收到登录请求, 用户名: " + cmd.getUsername());
                    System.out.println("协议版本: " + cmd.getVersion());

                    // 处理登录逻辑...
                    // 发送响应...
                }
            }

            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                logger.error(cause.getMessage(), cause);
//                System.err.println("处理消息时出错: " + cause.getMessage());
                ctx.close();
            }
        });
        server.startup();

        // 5. 创建TCP客户端
        TcpClient client = new TcpClient("localhost", 8080, protocol);
        client.startup();

        // 6. 创建并发送登录消息
        LoginCommand login = new LoginCommand();
        login.setUsername("admin");
        login.setPassword("secret");

        // 设置帧头
        FrameHeader header = new FrameHeader();
        header.addField("version", Bytes.fromInt(1, 4)); // 设置版本号为1
        login.setFrameHeader(header);

        // 发送消息
        client.channel.writeAndFlush(login);

        // 7. 关闭资源
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            client.shutdown();
            server.shutdown();
        }));
    }
}
