package com.maldosia.mobile;

import com.maldosia.cloudshuttle.core.TcpClient;
import com.maldosia.cloudshuttle.core.TcpServer;
import com.maldosia.cloudshuttle.core.field.FieldType;
import com.maldosia.cloudshuttle.core.message.*;
import com.maldosia.cloudshuttle.core.protocol.Protocol;
import com.maldosia.cloudshuttle.core.protocol.ProtocolDefinition;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.junit.jupiter.api.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TcpDualMessageIntegrationTest {
    private TcpServer server;
    private TcpClient client;
    private static final int PORT = 19011;
    private static final String HOST = "127.0.0.1";
    private static final byte[] CODE_A = new byte[]{(byte)0xA1};
    private static final byte[] CODE_B = new byte[]{(byte)0xB2};

    private CountDownLatch latch;
    private final String[] receivedContent = new String[1];
    private final int[] receivedNumber = new int[1];

    // MessageA
    @MessageType(code = {(byte)0xA1})
    public static class MessageA extends AutoMessage {
        @FieldDef(order = 1, length = 0)
        private String content;
        public MessageA() {}
        public MessageA(String content) { this.content = content; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        @Override
        public void deserialize(ByteBuf body) {
            int len = body.readableBytes();
            byte[] bytes = new byte[len];
            body.readBytes(bytes);
            this.content = new String(bytes);
        }
        @Override
        public void serialize(ByteBuf buf) {
            buf.writeBytes(content.getBytes());
        }
    }
    // MessageB
    @MessageType(code = {(byte)0xB2})
    public static class MessageB extends AutoMessage {
        @FieldDef(order = 1, length = 4)
        private int number;
        public MessageB() {}
        public MessageB(int number) { this.number = number; }
        public int getNumber() { return number; }
        public void setNumber(int number) { this.number = number; }
        @Override
        public void deserialize(ByteBuf body) {
            this.number = body.readInt();
        }
        @Override
        public void serialize(ByteBuf buf) {
            buf.writeInt(number);
        }
    }

    private Protocol buildProtocol() {
        ProtocolDefinition def = ProtocolDefinition.builder()
            .addFixedField("START", FieldType.START_FLAG, new byte[]{0x68})
            .addField("CODE", FieldType.FUNCTION_CODE, 1)
            .addField("LEN", FieldType.LENGTH, 2)
            .addField("BODY", FieldType.BODY, 0)
            .addFixedField("END", FieldType.END_FLAG, new byte[]{0x16})
            .description("双消息协议")
            .protocolType("TCP")
            .build();
        Protocol protocol = new Protocol(def);
        // 自动注册消息类型
        MessageAutoRegistrar.registerAll(protocol, this.getClass().getPackage().getName());
        return protocol;
    }

    @BeforeEach
    public void beforeEach() {
        latch = new CountDownLatch(1);
        receivedContent[0] = null;
        receivedNumber[0] = 0;
    }

    @BeforeAll
    public void setUp() {
        Protocol protocol = buildProtocol();
        server = new TcpServer(PORT, protocol);
        server.addHandler(new SimpleChannelInboundHandler<Message>() {
            @Override
            protected void channelRead0(ChannelHandlerContext ctx, Message msg) {
                if (msg instanceof MessageA) {
                    receivedContent[0] = ((MessageA) msg).getContent();
                    latch.countDown();
                    ctx.writeAndFlush(new MessageB(12345));
                } else if (msg instanceof MessageB) {
                    receivedNumber[0] = ((MessageB) msg).getNumber();
                    latch.countDown();
                }
            }
        });
        server.startup();
        client = new TcpClient(HOST, PORT, protocol);
        client.addHandler(new SimpleChannelInboundHandler<Message>() {
            @Override
            protected void channelRead0(ChannelHandlerContext ctx, Message msg) {
                if (msg instanceof MessageB) {
                    int number = ((MessageB) msg).getNumber();
                    System.out.println("number: " + number);
                    receivedNumber[0] = number;
                    latch.countDown();
                }
            }
        });
        client.startup();
    }

    @AfterAll
    public void tearDown() {
        if (client != null) client.shutdown();
        if (server != null) server.shutdown();
    }

    @Test
    public void testDualMessageExchange() throws Exception {
        // client 先发A
        client.channel.writeAndFlush(new MessageA("hello-server"));
        // 等待收到B
        boolean ok = latch.await(3, TimeUnit.SECONDS);
        assertTrue(ok, "未收到MessageB响应");
        assertEquals(12345, receivedNumber[0], "MessageB内容不正确");
    }

    @Test
    public void testServerReceiveClientB() throws Exception {
        // client 发A
        client.channel.writeAndFlush(new MessageA("client-to-server"));
        boolean ok = latch.await(3, TimeUnit.SECONDS);
        assertTrue(ok, "未收到MessageA");
        assertEquals("client-to-server", receivedContent[0], "MessageA内容不正确");
    }
} 