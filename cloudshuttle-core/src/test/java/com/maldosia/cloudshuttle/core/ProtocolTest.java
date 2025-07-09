package com.maldosia.cloudshuttle.core;

import com.maldosia.cloudshuttle.core.field.FieldDefinition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Protocol 单元测试
 * 测试协议的核心功能
 */
public class ProtocolTest {

    private Protocol protocol;
    private ProtocolDefinition definition;

    @BeforeEach
    public void setUp() {
        // 创建测试协议定义
        definition = ProtocolDslBuilder.standard()
                .addField("VERSION", FieldType.CUSTOM, 1)
                .endFlag(0x16)
                .description("测试协议")
                .build();
        protocol = new Protocol(definition);
    }

    @Test
    public void testProtocolCreation() {
        // 测试协议创建
        assertNotNull(protocol);
        assertNotNull(protocol.getDefinition());
        assertEquals(definition, protocol.getDefinition());
    }

    @Test
    public void testProtocolDefinitionAccess() {
        // 测试协议定义访问
        ProtocolDefinition def = protocol.getDefinition();
        assertNotNull(def);
        assertEquals("测试协议", def.getDescription());
        assertEquals("TCP", def.getProtocolType());
        assertTrue(def.isStandard());
    }

    @Test
    public void testStandardProtocolFields() {
        // 测试标准协议字段
        ProtocolDefinition def = protocol.getDefinition();
        assertEquals(6, def.getFields().size()); // 5个标准字段 + 1个自定义字段
        
        // 验证标准字段顺序
        assertEquals(FieldType.START_FLAG, def.getFields().get(0).getType());
        assertEquals(FieldType.FUNCTION_CODE, def.getFields().get(1).getType());
        assertEquals(FieldType.LENGTH, def.getFields().get(2).getType());
        assertEquals(FieldType.BODY, def.getFields().get(3).getType());
        assertEquals(FieldType.END_FLAG, def.getFields().get(4).getType());
        assertEquals(FieldType.CUSTOM, def.getFields().get(5).getType());
    }

    @Test
    public void testCustomProtocolCreation() {
        // 测试自定义协议创建
        ProtocolDefinition customDef = ProtocolDslBuilder.custom()
                .addField("CUSTOM1", FieldType.CUSTOM, 2)
                .addField("CUSTOM2", FieldType.CUSTOM, 4)
                .endFlag(0x16)
                .description("自定义协议")
                .build();
        
        Protocol customProtocol = new Protocol(customDef);
        assertNotNull(customProtocol);
        assertFalse(customDef.isStandard());
        assertEquals(3, customDef.getFields().size()); // 2个自定义字段 + 1个结束标志
    }

    @Test
    public void testProtocolWithMultipleCustomFields() {
        // 测试多个自定义字段
        ProtocolDefinition def = ProtocolDslBuilder.standard()
                .addField("VERSION", FieldType.CUSTOM, 1)
                .addField("TIMESTAMP", FieldType.CUSTOM, 4)
                .addField("SEQUENCE", FieldType.CUSTOM, 2)
                .addField("CHECKSUM", FieldType.CUSTOM, 1)
                .endFlag(0x16)
                .description("多字段协议")
                .build();
        
        Protocol multiFieldProtocol = new Protocol(def);
        assertNotNull(multiFieldProtocol);
        assertEquals(9, def.getFields().size()); // 5个标准字段 + 4个自定义字段
        
        // 验证自定义字段
        assertEquals("VERSION", def.getFields().get(5).getName());
        assertEquals("TIMESTAMP", def.getFields().get(6).getName());
        assertEquals("SEQUENCE", def.getFields().get(7).getName());
        assertEquals("CHECKSUM", def.getFields().get(8).getName());
    }

    @Test
    public void testProtocolFieldValidation() {
        // 测试协议字段验证
        ProtocolDefinition def = protocol.getDefinition();
        
        // 验证字段长度
        for (FieldDefinition field : def.getFields()) {
            assertTrue(field.getLength() >= 0, "字段长度应该大于等于0");
            assertNotNull(field.getName(), "字段名不能为空");
            assertNotNull(field.getType(), "字段类型不能为空");
        }
    }

    @Test
    public void testProtocolEquality() {
        // 测试协议相等性
        ProtocolDefinition def1 = ProtocolDslBuilder.standard()
                .addField("VERSION", FieldType.CUSTOM, 1)
                .endFlag(0x16)
                .description("协议1")
                .build();
        
        ProtocolDefinition def2 = ProtocolDslBuilder.standard()
                .addField("VERSION", FieldType.CUSTOM, 1)
                .endFlag(0x16)
                .description("协议1")
                .build();
        
        Protocol protocol1 = new Protocol(def1);
        Protocol protocol2 = new Protocol(def2);
        
        // 协议对象应该相等（如果定义相同）
        assertEquals(protocol1.getDefinition().getDescription(), 
                    protocol2.getDefinition().getDescription());
    }

    @Test
    public void testProtocolWithDifferentEndFlags() {
        // 测试不同结束标志的协议
        ProtocolDefinition def1 = ProtocolDslBuilder.standard()
                .endFlag(0x16)
                .description("协议1")
                .build();
        
        ProtocolDefinition def2 = ProtocolDslBuilder.standard()
                .endFlag(0xFF)
                .description("协议2")
                .build();
        
        Protocol protocol1 = new Protocol(def1);
        Protocol protocol2 = new Protocol(def2);
        
        assertNotNull(protocol1);
        assertNotNull(protocol2);
        
        // 验证结束标志不同
        boolean hasEndFlag1 = false, hasEndFlag2 = false;
        for (FieldDefinition field : def1.getFields()) {
            if (field.getType() == FieldType.END_FLAG) {
                hasEndFlag1 = true;
                break;
            }
        }
        for (FieldDefinition field : def2.getFields()) {
            if (field.getType() == FieldType.END_FLAG) {
                hasEndFlag2 = true;
                break;
            }
        }
        assertTrue(hasEndFlag1 && hasEndFlag2, "两个协议都应该包含结束标志字段");
    }

    @Test
    public void testProtocolWithProtocolType() {
        // 测试协议类型设置
        ProtocolDefinition def = ProtocolDslBuilder.standard()
                .endFlag(0x16)
                .description("UDP协议")
                .protocolType("UDP")
                .build();
        
        Protocol udpProtocol = new Protocol(def);
        assertNotNull(udpProtocol);
        assertEquals("UDP", def.getProtocolType());
    }

    @Test
    public void testProtocolFieldOrder() {
        // 测试字段顺序
        ProtocolDefinition def = protocol.getDefinition();
        
        // 验证字段按添加顺序排列
        for (int i = 0; i < def.getFields().size() - 1; i++) {
            FieldDefinition current = def.getFields().get(i);
            FieldDefinition next = def.getFields().get(i + 1);
            assertTrue(current.getOrder() <= next.getOrder(), 
                      "字段应该按顺序排列");
        }
    }
} 