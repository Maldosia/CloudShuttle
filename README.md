# CloudShuttle 云梭

CloudShuttle 是一个基于 Netty 的高性能、声明式自定义 TCP 协议框架，专为工业、物联网、金融等场景下的协议快速集成与开发而设计。它支持灵活的协议结构声明、自动消息编解码、易用的 API 和完善的扩展机制，助力开发者高效构建可靠的 TCP 通信系统。

---

## 特性亮点

- **声明式协议定义**：通过链式 DSL 或模板，快速声明自定义 TCP 协议结构（起始标志、功能码、长度、帧体、帧尾等）。
- **自动消息编解码**：无需手写字节解析，支持多消息类型自动注册与分发。
- **帧头灵活扩展**：支持自定义帧头字段（如版本、时间戳、流水号等），便于业务扩展。
- **高性能与易用性兼备**：基于 Netty，API 简洁，易于集成到任意 Java 项目。
- **完善的生命周期管理**：支持 TCP 服务端/客户端的启动、关闭、事件处理。
- **开箱即用的测试用例**：集成 JUnit 测试，保障协议和消息收发的正确性。

---

## 快速开始

### 1. 引入依赖

Maven:
```xml
<dependency>
  <groupId>com.maldosia</groupId>
  <artifactId>cloudshuttle</artifactId>
  <version>1.0.0</version>
</dependency>
```

### 2. 声明协议结构
```java
ProtocolDefinition def = ProtocolDslBuilder.standard()
    .addField("VERSION", FieldType.BYTE, 1)
    .endFlag(0x16)
    .description("带版本号的标准协议")
    .build();
Protocol protocol = new Protocol(def);
```

### 3. 定义消息类型
```java
@MessageType(code = {0x01})
public class LoginRequest implements Message {
    private String username;
    private FrameHeader header;
    // ...getter/setter...
    @Override public void setFrameHeader(FrameHeader header) { this.header = header; }
    @Override public FrameHeader getFrameHeader() { return header; }
    @Override public void serialize(ByteBuf buf) { buf.writeBytes(username.getBytes()); }
    @Override public void deserialize(ByteBuf buf) {
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        this.username = new String(bytes);
    }
}
```

### 4. 注册消息类型
```java
MessageAutoRegistrar.registerAll(protocol, "com.example.demo.message");
```

### 5. 启动服务端/客户端
```java
TcpServer server = new TcpServer(9000, protocol);
server.addHandler(new SimpleChannelInboundHandler<Message>() {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) {
        // 处理消息
    }
});
server.startup();

TcpClient client = new TcpClient("127.0.0.1", 9000, protocol);
client.startup();
```

---

## 目录结构

- `core/` 用户常用API（协议、消息、帧头、服务端/客户端等）
- `core/codec/` 编解码器（内部实现）
- `core/field/` 字段定义（内部实现）
- `core/message/` 消息注解、工厂、自动注册工具（内部实现）

---

## 贡献指南

我们欢迎任何形式的贡献，包括但不限于：
- 提交 Issue 或 Bug 报告
- 提交 Pull Request
- 优化文档、完善测试

请先阅读 [CONTRIBUTING.md](./CONTRIBUTING.md) 了解详细流程。

---

## License

CloudShuttle 遵循 Apache License 2.0 开源协议，详见 [LICENSE](./LICENSE)。

---

## 联系与支持

- 提交 [GitHub Issue](https://github.com/maldosia/cloudshuttle/issues)
- 邮箱：maldosiawl@gmail.com

---

## 致谢

感谢所有贡献者和开源社区的支持！