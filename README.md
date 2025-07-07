# CloudShuttle 云梭

CloudShuttle是一个基于Netty框架的高性能、可扩展的网络通信框架，专为构建自定义协议的TCP服务器和客户端而设计。它提供了灵活的协议定义机制、高效的消息编解码功能以及简洁易用的API，帮助开发者快速构建可靠的网络通信系统。

## 功能特点

- **灵活的协议定义**：通过简洁的API定义自定义协议，支持起始标志、结束标志、头部字段、功能码、长度字段和报文体等元素。
- **高性能通信**：基于Netty框架，充分利用其异步非阻塞特性，提供卓越的吞吐量和低延迟。
- **可扩展的消息处理**：支持注册多种消息类型，并通过简单的接口处理不同类型的消息。
- **完整的生命周期管理**：提供服务器和客户端的启动、关闭等完整生命周期管理功能。
- **丰富的日志支持**：集成SLF4J日志门面，方便进行系统监控和问题排查。

## 技术架构

CloudShuttle基于Java语言开发，主要依赖以下技术栈：

- **Netty**：高性能网络编程框架
- **SLF4J**：日志门面
- **Logback**：日志实现

## 安装使用

### 引入依赖

Maven用户可以在`pom.xml`中添加以下依赖：
<dependency>
<groupId>io.github.yourusername</groupId>
<artifactId>cloudshuttle</artifactId>
<version>1.0.0</version>
</dependency>
### 快速开始

下面是一个简单的示例，展示如何使用CloudShuttle构建一个简单的登录系统：

```java
public class QuickStartExample {
    public static void main(String[] args) throws Exception {
        // 定义协议
        ProtocolDefinition protocolDef = ProtocolDefinition.builder()
            .addStartFlag((byte) 0xAA, (byte) 0x55, (byte) 0x99, (byte) 0x66)
            .addHeaderField(4, "checksum", FieldType.HEADER)
            .addHeaderField(4, "version", FieldType.HEADER)
            .addFunctionCodeField(4)
            .addLengthField(4)
            .addHeaderField(12, "reserved", FieldType.HEADER)
            .addBodyField()
            .build();

        // 创建协议处理器
        Protocol protocol = new Protocol(protocolDef);
        
        // 注册消息类型
        protocol.registerMessage(LoginCommand.class, LoginCommand::new);
        protocol.registerMessage(LoginResponse.class, LoginResponse::new);

        // 启动服务器
        TcpServer server = new TcpServer(8080, protocol);
        server.addHandler(new SimpleChannelInboundHandler<Message>() {
            @Override
            protected void channelRead0(ChannelHandlerContext ctx, Message msg) {
                if (msg instanceof LoginCommand) {
                    LoginCommand cmd = (LoginCommand) msg;
                    System.out.println("收到登录请求 - 用户名: " + cmd.getUsername());
                    
                    // 处理登录逻辑...
                    
                    // 返回响应
                    LoginResponse response = new LoginResponse();
                    response.setSuccess(true);
                    response.setMessage("登录成功");
                    ctx.writeAndFlush(response);
                }
            }
        });
        server.startup();

        // 启动客户端
        TcpClient client = new TcpClient("localhost", 8080, protocol);
        client.addHandler(new SimpleChannelInboundHandler<Message>() {
            @Override
            protected void channelRead0(ChannelHandlerContext ctx, Message msg) {
                if (msg instanceof LoginResponse) {
                    LoginResponse response = (LoginResponse) msg;
                    System.out.println("登录结果: " + (response.isSuccess() ? "成功" : "失败"));
                    System.out.println("消息: " + response.getMessage());
                }
            }
        });
        client.startup();

        // 发送登录请求
        LoginCommand loginCmd = new LoginCommand();
        loginCmd.setUsername("admin");
        loginCmd.setPassword("password");
        client.channel.writeAndFlush(loginCmd);

        // 优雅关闭
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            client.shutdown();
            server.shutdown();
        }));
    }
}
```

## 文档

完整的文档可以在[Wiki页面](https://github.com/yourusername/cloudshuttle/wiki)找到，包括：

- [协议定义指南](https://github.com/yourusername/cloudshuttle/wiki/Protocol-Definition)
- [消息处理机制](https://github.com/yourusername/cloudshuttle/wiki/Message-Handling)
- [高级配置选项](https://github.com/yourusername/cloudshuttle/wiki/Advanced-Configuration)
- [性能调优建议](https://github.com/yourusername/cloudshuttle/wiki/Performance-Tuning)
- [API参考文档](https://github.com/yourusername/cloudshuttle/wiki/API-Reference)

## 贡献

我们欢迎任何形式的贡献，无论是提交问题、提出建议还是提交代码。

1. 请先在[Issue Tracker](https://github.com/yourusername/cloudshuttle/issues)中搜索是否已有相关问题或建议
2. 提交问题时，请提供详细的重现步骤和环境信息
3. 提交代码时，请遵循项目的代码风格和提交规范
4. 所有贡献都需要通过Pull Request提交，并经过审核

## 许可证

CloudShuttle采用Apache License 2.0许可证，详情见[LICENSE](https://github.com/yourusername/cloudshuttle/blob/master/LICENSE)文件。

## 联系我们

如果您有任何问题或建议，可以通过以下方式联系我们：

- 提交[GitHub Issue](https://github.com/yourusername/cloudshuttle/issues)