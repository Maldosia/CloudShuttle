package com.maldosia.cloudshuttle.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * FrameHeader 单元测试
 * 测试帧头的各种功能
 */
public class FrameHeaderTest {

    private FrameHeader header;
    private ProtocolDefinition definition;

    @BeforeEach
    public void setUp() {
        // 创建测试协议定义
        definition = ProtocolDslBuilder.standard()
                .addField("VERSION", FieldType.CUSTOM, 1)
                .addField("TIMESTAMP", FieldType.CUSTOM, 4)
                .endFlag(0x16)
                .description("测试协议")
                .build();
        header = new FrameHeader();
    }

    @Test
    public void testFrameHeaderCreation() {
        // 测试帧头创建
        assertNotNull(header);
    }

    @Test
    public void testSetAndGetField() {
        // 测试设置和获取字段值
        header.addField("VERSION", new byte[]{1});
        header.addField("TIMESTAMP", new byte[]{0, 0, 0, 100});
        
        byte[] version = header.getField("VERSION");
        byte[] timestamp = header.getField("TIMESTAMP");
        
        assertNotNull(version);
        assertNotNull(timestamp);
        assertEquals(1, version.length);
        assertEquals(4, timestamp.length);
        assertEquals(1, version[0]);
        assertEquals(100, timestamp[3]);
    }

    @Test
    public void testSetFieldWithNull() {
        // 测试设置空值
        header.addField("VERSION", null);
        byte[] version = header.getField("VERSION");
        assertNull(version);
    }

    @Test
    public void testGetNonExistentField() {
        // 测试获取不存在的字段
        byte[] value = header.getField("NON_EXISTENT");
        assertNull(value);
    }

    @Test
    public void testSetNonExistentField() {
        // 测试设置不存在的字段（应该被接受）
        header.addField("NON_EXISTENT", new byte[]{1, 2, 3});
        byte[] value = header.getField("NON_EXISTENT");
        assertNotNull(value);
        assertEquals(3, value.length);
    }

    @Test
    public void testFieldOverwrite() {
        // 测试字段值覆盖
        header.addField("VERSION", new byte[]{1});
        header.addField("VERSION", new byte[]{2});
        
        byte[] version = header.getField("VERSION");
        assertNotNull(version);
        assertEquals(1, version.length);
        assertEquals(2, version[0]);
    }

    @Test
    public void testGetAllFields() {
        // 测试获取所有字段
        header.addField("VERSION", new byte[]{1});
        header.addField("TIMESTAMP", new byte[]{0, 0, 0, 100});
        
        // 验证所有自定义字段都可以获取
        assertNotNull(header.getField("VERSION"));
        assertNotNull(header.getField("TIMESTAMP"));
    }

    @Test
    public void testFrameHeaderWithDifferentFieldTypes() {
        // 测试不同字段类型的帧头
        FrameHeader multiTypeHeader = new FrameHeader();
        
        // 设置不同长度的字段
        multiTypeHeader.addField("BYTE_FIELD", new byte[]{1});
        multiTypeHeader.addField("SHORT_FIELD", new byte[]{1, 2});
        multiTypeHeader.addField("INT_FIELD", new byte[]{1, 2, 3, 4});
        multiTypeHeader.addField("LONG_FIELD", new byte[]{1, 2, 3, 4, 5, 6, 7, 8});
        
        // 验证字段值
        assertEquals(1, multiTypeHeader.getField("BYTE_FIELD").length);
        assertEquals(2, multiTypeHeader.getField("SHORT_FIELD").length);
        assertEquals(4, multiTypeHeader.getField("INT_FIELD").length);
        assertEquals(8, multiTypeHeader.getField("LONG_FIELD").length);
    }

    @Test
    public void testFrameHeaderEquality() {
        // 测试帧头相等性
        FrameHeader header1 = new FrameHeader();
        FrameHeader header2 = new FrameHeader();
        
        // 设置相同的字段值
        header1.addField("VERSION", new byte[]{1});
        header2.addField("VERSION", new byte[]{1});
        
        // 验证字段值相等
        assertArrayEquals(header1.getField("VERSION"), header2.getField("VERSION"));
    }

    @Test
    public void testFrameHeaderWithEmptyDefinition() {
        // 测试空定义的帧头
        FrameHeader emptyHeader = new FrameHeader();
        assertNotNull(emptyHeader);
        
        // 尝试设置字段（应该被接受）
        emptyHeader.addField("VERSION", new byte[]{1});
        assertNotNull(emptyHeader.getField("VERSION"));
    }

    @Test
    public void testFrameHeaderFieldValidation() {
        // 测试字段验证
        // 设置正确长度的字段
        header.addField("VERSION", new byte[]{1}); // 长度1
        assertNotNull(header.getField("VERSION"));
        
        // 设置不同长度的字段（应该被接受）
        header.addField("VERSION", new byte[]{1, 2}); // 长度2
        assertNotNull(header.getField("VERSION"));
        assertEquals(2, header.getField("VERSION").length);
    }

    @Test
    public void testFrameHeaderWithLargeData() {
        // 测试大数据字段
        byte[] largeData = new byte[1024];
        for (int i = 0; i < largeData.length; i++) {
            largeData[i] = (byte) (i % 256);
        }
        
        header.addField("TIMESTAMP", largeData);
        byte[] retrieved = header.getField("TIMESTAMP");
        
        assertNotNull(retrieved);
        assertEquals(largeData.length, retrieved.length);
        assertArrayEquals(largeData, retrieved);
    }

    @Test
    public void testFrameHeaderClearFields() {
        // 测试清除字段（通过设置null）
        header.addField("VERSION", new byte[]{1});
        header.addField("TIMESTAMP", new byte[]{1, 2, 3, 4});
        
        // 清除字段
        header.addField("VERSION", null);
        header.addField("TIMESTAMP", null);
        
        assertNull(header.getField("VERSION"));
        assertNull(header.getField("TIMESTAMP"));
    }

    @Test
    public void testFrameHeaderWithSpecialCharacters() {
        // 测试特殊字符字段名
        FrameHeader specialHeader = new FrameHeader();
        specialHeader.addField("FIELD_WITH_UNDERSCORE", new byte[]{1});
        specialHeader.addField("FieldWithCamelCase", new byte[]{2});
        
        assertNotNull(specialHeader.getField("FIELD_WITH_UNDERSCORE"));
        assertNotNull(specialHeader.getField("FieldWithCamelCase"));
        assertEquals(1, specialHeader.getField("FIELD_WITH_UNDERSCORE")[0]);
        assertEquals(2, specialHeader.getField("FieldWithCamelCase")[0]);
    }
} 