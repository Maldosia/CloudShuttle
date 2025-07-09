package com.maldosia.cloudshuttle.message;

import com.maldosia.cloudshuttle.core.*;
import com.maldosia.cloudshuttle.core.message.MessageType;
import com.maldosia.cloudshuttle.core.message.MessageAutoRegistrar;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * MessageAutoRegistrar 单元测试
 * 测试消息自动注册功能
 */
public class MessageAutoRegistrarTest {

    private Protocol protocol;

    @BeforeEach
    public void setUp() {
        // 创建测试协议
        ProtocolDefinition def = ProtocolDslBuilder.standard()
                .addField("VERSION", FieldType.CUSTOM, 1)
                .endFlag(0x16)
                .description("测试协议")
                .build();
        protocol = new Protocol(def);
    }

    @Test
    public void testRegisterAll() {
        // 测试自动注册所有消息类型
        MessageAutoRegistrar.registerAll(protocol, this.getClass().getPackage().getName());
        
        // 验证消息类型是否被正确注册
        assertNotNull(protocol.getFactory(new byte[]{0x01}));
        assertNotNull(protocol.getFactory(new byte[]{0x02}));
    }

    @Test
    public void testRegisterMessageWithAnnotation() {
        // 测试注册带注解的消息类型
        MessageAutoRegistrar.registerAll(protocol, this.getClass().getPackage().getName());
        
        // 验证带注解的消息类型是否被正确注册
        assertNotNull(protocol.getFactory(new byte[]{0x01}));
        assertNotNull(protocol.getFactory(new byte[]{0x02}));
    }

    @Test
    public void testRegisterMessageWithoutAnnotation() {
        // 测试注册不带注解的消息类型（应该被忽略）
        MessageAutoRegistrar.registerAll(protocol, this.getClass().getPackage().getName());
        
        // 验证不带注解的消息类型不会被注册
        assertNull(protocol.getFactory(new byte[]{(byte)0x99})); // 假设的非注解消息功能码
    }

    @Test
    public void testRegisterMultipleMessages() {
        // 测试注册多个消息类型
        MessageAutoRegistrar.registerAll(protocol, this.getClass().getPackage().getName());
        
        // 验证多个消息类型是否被正确注册
        assertNotNull(protocol.getFactory(new byte[]{0x01}));
        assertNotNull(protocol.getFactory(new byte[]{0x02}));
        assertNotNull(protocol.getFactory(new byte[]{0x03}));
    }

    @Test
    public void testRegisterAllWithNullPackage() {
        // 测试使用空包名注册
        assertThrows(RuntimeException.class, () -> {
            MessageAutoRegistrar.registerAll(protocol, null);
        });
    }

    @Test
    public void testRegisterAllWithEmptyPackage() {
        // 测试使用空包名注册
        assertThrows(RuntimeException.class, () -> {
            MessageAutoRegistrar.registerAll(protocol, "");
        });
    }

    @Test
    public void testRegisterMessageWithMultipleCodes() {
        // 测试注册带多个功能码的消息类型
        MessageAutoRegistrar.registerAll(protocol, this.getClass().getPackage().getName());
        
        // 验证多个功能码的消息类型是否被正确注册
        assertNotNull(protocol.getFactory(new byte[]{0x03}));
        assertNotNull(protocol.getFactory(new byte[]{0x04}));
        assertNotNull(protocol.getFactory(new byte[]{0x05}));
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
    public static class AnnotatedTestMessage implements Message {
        private int value;
        private FrameHeader header;

        public AnnotatedTestMessage() {}

        public int getValue() { return value; }
        public void setValue(int value) { this.value = value; }

        @Override
        public void setFrameHeader(FrameHeader header) { this.header = header; }
        @Override
        public FrameHeader getFrameHeader() { return header; }
        @Override
        public void serialize(ByteBuf buf) {
            buf.writeInt(value);
        }
        @Override
        public void deserialize(ByteBuf buf) {
            this.value = buf.readInt();
        }
    }

    public static class NonAnnotatedTestMessage implements Message {
        private String data;
        private FrameHeader header;

        public NonAnnotatedTestMessage() {}

        public String getData() { return data; }
        public void setData(String data) { this.data = data; }

        @Override
        public void setFrameHeader(FrameHeader header) { this.header = header; }
        @Override
        public FrameHeader getFrameHeader() { return header; }
        @Override
        public void serialize(ByteBuf buf) {
            if (data != null) {
                buf.writeBytes(data.getBytes());
            }
        }
        @Override
        public void deserialize(ByteBuf buf) {
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            this.data = new String(bytes);
        }
    }

    @MessageType(code = {0x03, 0x04, 0x05})
    public static class MultiCodeTestMessage implements Message {
        private byte[] data;
        private FrameHeader header;

        public MultiCodeTestMessage() {}

        public byte[] getData() { return data; }
        public void setData(byte[] data) { this.data = data; }

        @Override
        public void setFrameHeader(FrameHeader header) { this.header = header; }
        @Override
        public FrameHeader getFrameHeader() { return header; }
        @Override
        public void serialize(ByteBuf buf) {
            if (data != null) {
                buf.writeBytes(data);
            }
        }
        @Override
        public void deserialize(ByteBuf buf) {
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            this.data = bytes;
        }
    }
} 