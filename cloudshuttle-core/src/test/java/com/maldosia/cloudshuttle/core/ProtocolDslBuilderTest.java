package com.maldosia.cloudshuttle.core;

import com.maldosia.cloudshuttle.core.field.FieldDefinition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ProtocolDslBuilder 单元测试
 * 测试 DSL 构建器的各种功能和边界条件
 */
public class ProtocolDslBuilderTest {

    @Test
    public void testStandardProtocolBuilder() {
        // 测试标准协议构建
        ProtocolDefinition def = ProtocolDslBuilder.standard()
                .endFlag(0x16)
                .description("标准协议")
                .build();

        assertNotNull(def);
        assertEquals("标准协议", def.getDescription());
        assertEquals("TCP", def.getProtocolType());
        assertTrue(def.isStandard());
        // 标准协议包含起始标志、功能码、长度、帧体、结束标志
        assertEquals(5, def.getFields().size());
    }

    @Test
    public void testProtocolWithCustomFields() {
        // 测试添加自定义字段
        ProtocolDefinition def = ProtocolDslBuilder.standard()
                .addField("VERSION", FieldType.CUSTOM, 1)
                .addField("TIMESTAMP", FieldType.CUSTOM, 4)
                .addField("SEQUENCE", FieldType.CUSTOM, 2)
                .endFlag(0x16)
                .description("带自定义字段的协议")
                .build();

        assertNotNull(def);
        assertEquals(8, def.getFields().size()); // 5个标准字段 + 3个自定义字段
        
        // 验证标准字段
        assertEquals(FieldType.START_FLAG, def.getFields().get(0).getType());
        assertEquals(FieldType.FUNCTION_CODE, def.getFields().get(1).getType());
        assertEquals(FieldType.LENGTH, def.getFields().get(2).getType());
        assertEquals(FieldType.BODY, def.getFields().get(3).getType());
        assertEquals(FieldType.END_FLAG, def.getFields().get(4).getType());
        
        // 验证自定义字段
        assertEquals("VERSION", def.getFields().get(5).getName());
        assertEquals(FieldType.CUSTOM, def.getFields().get(5).getType());
        assertEquals(1, def.getFields().get(5).getLength());
        
        assertEquals("TIMESTAMP", def.getFields().get(6).getName());
        assertEquals(FieldType.CUSTOM, def.getFields().get(6).getType());
        assertEquals(4, def.getFields().get(6).getLength());
        
        assertEquals("SEQUENCE", def.getFields().get(7).getName());
        assertEquals(FieldType.CUSTOM, def.getFields().get(7).getType());
        assertEquals(2, def.getFields().get(7).getLength());
    }

    @Test
    public void testProtocolWithCustomFieldType() {
        // 测试自定义字段类型
        ProtocolDefinition def = ProtocolDslBuilder.standard()
                .addField("CUSTOM", FieldType.CUSTOM, 8)
                .endFlag(0x16)
                .description("自定义字段类型协议")
                .build();

        assertNotNull(def);
        assertEquals(6, def.getFields().size()); // 5个标准字段 + 1个自定义字段
        assertEquals(FieldType.CUSTOM, def.getFields().get(5).getType());
        assertEquals(8, def.getFields().get(5).getLength());
    }

    @Test
    public void testProtocolWithProtocolType() {
        // 测试协议类型设置
        ProtocolDefinition def = ProtocolDslBuilder.standard()
                .endFlag(0x16)
                .description("TCP协议")
                .protocolType("TCP")
                .build();

        assertNotNull(def);
        assertEquals("TCP", def.getProtocolType());
    }

    @Test
    public void testMultipleFieldTypes() {
        // 测试多种字段类型
        ProtocolDefinition def = ProtocolDslBuilder.standard()
                .addField("CUSTOM_FIELD1", FieldType.CUSTOM, 1)
                .addField("CUSTOM_FIELD2", FieldType.CUSTOM, 2)
                .addField("CUSTOM_FIELD3", FieldType.CUSTOM, 4)
                .addField("CUSTOM_FIELD4", FieldType.CUSTOM, 8)
                .addField("CUSTOM_FIELD5", FieldType.CUSTOM, 10)
                .endFlag(0x16)
                .description("多字段类型协议")
                .build();

        assertNotNull(def);
        assertEquals(10, def.getFields().size()); // 5个标准字段 + 5个自定义字段
        
        // 验证标准字段
        assertEquals(FieldType.START_FLAG, def.getFields().get(0).getType());
        assertEquals(FieldType.FUNCTION_CODE, def.getFields().get(1).getType());
        assertEquals(FieldType.LENGTH, def.getFields().get(2).getType());
        assertEquals(FieldType.BODY, def.getFields().get(3).getType());
        assertEquals(FieldType.END_FLAG, def.getFields().get(4).getType());
        
        // 验证自定义字段类型
        assertEquals(FieldType.CUSTOM, def.getFields().get(5).getType());
        assertEquals(FieldType.CUSTOM, def.getFields().get(6).getType());
        assertEquals(FieldType.CUSTOM, def.getFields().get(7).getType());
        assertEquals(FieldType.CUSTOM, def.getFields().get(8).getType());
        assertEquals(FieldType.CUSTOM, def.getFields().get(9).getType());
    }

    @Test
    public void testFieldLengthValidation() {
        // 测试字段长度验证
        ProtocolDefinition def = ProtocolDslBuilder.standard()
                .addField("CUSTOM_FIELD1", FieldType.CUSTOM, 1)
                .addField("CUSTOM_FIELD2", FieldType.CUSTOM, 2)
                .addField("CUSTOM_FIELD3", FieldType.CUSTOM, 4)
                .addField("CUSTOM_FIELD4", FieldType.CUSTOM, 8)
                .endFlag(0x16)
                .build();

        assertNotNull(def);
        
        // 验证标准字段长度
        assertEquals(1, def.getFields().get(0).getLength()); // START_FLAG
        assertEquals(1, def.getFields().get(1).getLength()); // FUNCTION_CODE
        assertEquals(2, def.getFields().get(2).getLength()); // LENGTH
        assertEquals(0, def.getFields().get(3).getLength()); // BODY
        assertEquals(1, def.getFields().get(4).getLength()); // END_FLAG
        
        // 验证自定义字段长度是否正确
        assertEquals(1, def.getFields().get(5).getLength());
        assertEquals(2, def.getFields().get(6).getLength());
        assertEquals(4, def.getFields().get(7).getLength());
        assertEquals(8, def.getFields().get(8).getLength());
    }

    @Test
    public void testEndFlagValidation() {
        // 测试结束标志设置
        ProtocolDefinition def = ProtocolDslBuilder.standard()
                .endFlag(0xFF)
                .description("测试结束标志")
                .build();

        assertNotNull(def);
        // 验证结束标志字段存在
        boolean hasEndFlag = false;
        for (FieldDefinition field : def.getFields()) {
            if (field.getType() == FieldType.END_FLAG) {
                hasEndFlag = true;
                break;
            }
        }
        assertTrue(hasEndFlag, "应该包含结束标志字段");
    }

    @Test
    public void testDescriptionSetting() {
        // 测试描述设置
        String description = "这是一个测试协议描述";
        ProtocolDefinition def = ProtocolDslBuilder.standard()
                .endFlag(0x16)
                .description(description)
                .build();

        assertNotNull(def);
        assertEquals(description, def.getDescription());
    }

    @Test
    public void testBuilderChaining() {
        // 测试构建器链式调用
        ProtocolDefinition def = ProtocolDslBuilder.standard()
                .addField("FIELD1", FieldType.CUSTOM, 1)
                .addField("FIELD2", FieldType.CUSTOM, 2)
                .endFlag(0x16)
                .description("链式调用测试")
                .protocolType("TCP")
                .build();

        assertNotNull(def);
        assertEquals(7, def.getFields().size()); // 5个标准字段 + 2个自定义字段
        assertEquals("链式调用测试", def.getDescription());
        assertEquals("TCP", def.getProtocolType());
        
        // 验证结束标志字段存在
        boolean hasEndFlag = false;
        for (FieldDefinition field : def.getFields()) {
            if (field.getType() == FieldType.END_FLAG) {
                hasEndFlag = true;
                break;
            }
        }
        assertTrue(hasEndFlag, "应该包含结束标志字段");
    }

    @Test
    public void testEmptyProtocol() {
        // 测试空协议（只有结束标志）
        ProtocolDefinition def = ProtocolDslBuilder.standard()
                .endFlag(0x16)
                .build();

        assertNotNull(def);
        assertEquals(5, def.getFields().size()); // 标准协议包含5个字段
        assertEquals("", def.getDescription()); // 空字符串而不是null
        assertEquals("TCP", def.getProtocolType());
    }

    @Test
    public void testDuplicateFieldNames() {
        // 测试重复字段名（应该允许，因为可能有业务需求）
        ProtocolDefinition def = ProtocolDslBuilder.standard()
                .addField("FIELD", FieldType.CUSTOM, 1)
                .addField("FIELD", FieldType.CUSTOM, 2)
                .endFlag(0x16)
                .build();

        assertNotNull(def);
        assertEquals(7, def.getFields().size()); // 5个标准字段 + 2个自定义字段
        
        // 验证标准字段
        assertEquals("START", def.getFields().get(0).getName());
        assertEquals("CODE", def.getFields().get(1).getName());
        assertEquals("LEN", def.getFields().get(2).getName());
        assertEquals("BODY", def.getFields().get(3).getName());
        assertEquals("END", def.getFields().get(4).getName());
        
        // 验证自定义字段
        assertEquals("FIELD", def.getFields().get(5).getName());
        assertEquals("FIELD", def.getFields().get(6).getName());
    }
} 