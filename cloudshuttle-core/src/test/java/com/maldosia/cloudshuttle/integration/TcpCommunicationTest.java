package com.maldosia.cloudshuttle.integration;

import com.maldosia.cloudshuttle.core.*;
import com.maldosia.cloudshuttle.core.message.MessageType;
import com.maldosia.cloudshuttle.core.message.MessageAutoRegistrar;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TCP 通信集成测试
 * 测试服务端和客户端的通信功能
 */
@TestInstance(Lifecycle.PER_CLASS)
public class TcpCommunicationTest {
    
    private TcpServer server;
    private TcpClient client;
    private static final int PORT = 19012;
    private static final String HOST = "127.0.0.1";
    
    private CountDownLatch messageReceivedLatch;
    private TestMessage receivedMessage;

    @BeforeAll
    public void setUp() {
        // 创建测试协议
        ProtocolDefinition def = ProtocolDslBuilder.standard()
                .addField("VERSION", FieldType.CUSTOM, 1)
                .endFlag(0x16)
                .description("集成测试协议")
                .build();
        Protocol protocol = new Protocol(def);
        
        // 注册消息类型
        MessageAutoRegistrar.registerAll(protocol, this.getClass().getPackage().getName());
        
        // 启动服务端
        server = new TcpServer(PORT, protocol);
        server.addHandler(new SimpleChannelInboundHandler<Message>() {
            @Override
            protected void channelRead0(ChannelHandlerContext ctx, Message msg) {
                if (msg instanceof TestMessage) {
                    receivedMessage = (TestMessage) msg;
                    messageReceivedLatch.countDown();
                    
                    // 发送响应
                    TestResponse response = new TestResponse();
                    response.setSuccess(true);
                    response.setMessage("服务器已收到消息: " + receivedMessage.getContent());
                    ctx.writeAndFlush(response);
                }
            }
        });
        server.startup();
        
        // 启动客户端
        client = new TcpClient(HOST, PORT, protocol);
        client.startup();
    }

    @BeforeEach
    public void beforeEach() {
        messageReceivedLatch = new CountDownLatch(1);
        receivedMessage = null;
    }

    @AfterAll
    public void tearDown() {
        if (client != null) {
            client.shutdown();
        }
        if (server != null) {
            server.shutdown();
        }
    }

    @Test
    public void testBasicMessageExchange() throws Exception {
        // 客户端发送消息
        TestMessage message = new TestMessage();
        message.setContent("Hello Server");
        client.channel.writeAndFlush(message);
        
        // 等待服务端接收消息
        boolean received = messageReceivedLatch.await(5, TimeUnit.SECONDS);
        assertTrue(received, "服务端应该在5秒内收到消息");
        
        // 验证接收到的消息
        assertNotNull(receivedMessage);
        assertEquals("Hello Server", receivedMessage.getContent());
    }

    @Test
    public void testMultipleMessageExchange() throws Exception {
        // 发送多个消息
        for (int i = 0; i < 3; i++) {
            CountDownLatch latch = new CountDownLatch(1);
            TestMessage message = new TestMessage();
            message.setContent("Message " + i);
            
            // 临时设置接收器
            final TestMessage[] tempReceived = new TestMessage[1];
            server.addHandler(new SimpleChannelInboundHandler<Message>() {
                @Override
                protected void channelRead0(ChannelHandlerContext ctx, Message msg) {
                    if (msg instanceof TestMessage) {
                        tempReceived[0] = (TestMessage) msg;
                        latch.countDown();
                    }
                }
            });
            
            client.channel.writeAndFlush(message);
            boolean received = latch.await(2, TimeUnit.SECONDS);
            assertTrue(received, "消息 " + i + " 应该在2秒内被接收");
            assertEquals("Message " + i, tempReceived[0].getContent());
        }
    }

    @Test
    public void testLargeMessageExchange() throws Exception {
        // 发送大消息
        StringBuilder largeContent = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            largeContent.append("Large message content ");
        }
        
        TestMessage message = new TestMessage();
        message.setContent(largeContent.toString());
        client.channel.writeAndFlush(message);
        
        // 等待接收
        boolean received = messageReceivedLatch.await(10, TimeUnit.SECONDS);
        assertTrue(received, "大消息应该在10秒内被接收");
        
        // 验证内容
        assertNotNull(receivedMessage);
        assertEquals(largeContent.toString(), receivedMessage.getContent());
    }

    @Test
    public void testConcurrentMessageExchange() throws Exception {
        // 并发发送多个消息
        int messageCount = 10;
        CountDownLatch[] latches = new CountDownLatch[messageCount];
        TestMessage[] messages = new TestMessage[messageCount];
        
        for (int i = 0; i < messageCount; i++) {
            latches[i] = new CountDownLatch(1);
            messages[i] = new TestMessage();
            messages[i].setContent("Concurrent Message " + i);
        }
        
        // 并发发送
        for (int i = 0; i < messageCount; i++) {
            final int index = i;
            new Thread(() -> {
                client.channel.writeAndFlush(messages[index]);
            }).start();
        }
        
        // 等待所有消息被接收
        for (CountDownLatch latch : latches) {
            boolean received = latch.await(5, TimeUnit.SECONDS);
            assertTrue(received, "所有并发消息都应该在5秒内被接收");
        }
    }

    @Test
    public void testConnectionStability() throws Exception {
        // 测试连接稳定性
        for (int i = 0; i < 50; i++) {
            TestMessage message = new TestMessage();
            message.setContent("Stability Test " + i);
            client.channel.writeAndFlush(message);
            
            // 短暂等待
            Thread.sleep(100);
        }
        
        // 验证连接仍然活跃
        assertTrue(client.channel.isActive(), "连接应该保持活跃状态");
    }

    // 测试用的消息类
    @MessageType(code = {0x01})
    public static class TestMessage implements Message {
        private String content;
        private FrameHeader header;

        public TestMessage() {}

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }

        @Override
        public void setFrameHeader(FrameHeader header) { this.header = header; }
        @Override
        public FrameHeader getFrameHeader() { return header; }
        @Override
        public void serialize(ByteBuf buf) {
            if (content != null) {
                buf.writeBytes(content.getBytes());
            }
        }
        @Override
        public void deserialize(ByteBuf buf) {
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            this.content = new String(bytes);
        }
    }

    @MessageType(code = {0x02})
    public static class TestResponse implements Message {
        private boolean success;
        private String message;
        private FrameHeader header;

        public TestResponse() {}

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        @Override
        public void setFrameHeader(FrameHeader header) { this.header = header; }
        @Override
        public FrameHeader getFrameHeader() { return header; }
        @Override
        public void serialize(ByteBuf buf) {
            buf.writeBoolean(success);
            if (message != null) {
                buf.writeBytes(message.getBytes());
            }
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