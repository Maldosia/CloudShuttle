package com.maldosia.mobile;

import com.maldosia.cloudshuttle.core.FrameHeader;
import com.maldosia.cloudshuttle.core.FunctionCode;
import com.maldosia.cloudshuttle.core.Message;
import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;

/**
 * 示例消息类 - 登录命令
 */
public class LoginCommand implements Message, FunctionCode {
    private FrameHeader header;  // 帧头
    private String username;     // 用户名
    private String password;     // 密码

    @Override
    public byte[] getCode() {
        // 功能码: 0x01 0x00 0x00 0x00
        return new byte[]{0x01, 0x00, 0x00, 0x00};
    }

    @Override
    public void setFrameHeader(FrameHeader header) {
        this.header = header;
    }

    @Override
    public FrameHeader getFrameHeader() {
        return header;
    }

    @Override
    public void deserialize(ByteBuf body) {
        // 读取用户名长度和内容
        int usernameLen = body.readInt();
        byte[] usernameBytes = new byte[usernameLen];
        body.readBytes(usernameBytes);
        username = new String(usernameBytes, StandardCharsets.UTF_8);

        // 读取密码长度和内容
        int passwordLen = body.readInt();
        byte[] passwordBytes = new byte[passwordLen];
        body.readBytes(passwordBytes);
        password = new String(passwordBytes, StandardCharsets.UTF_8);
    }

    @Override
    public void serialize(ByteBuf buf) {
        // 写入用户名
        byte[] usernameBytes = username.getBytes(StandardCharsets.UTF_8);
        buf.writeInt(usernameBytes.length);
        buf.writeBytes(usernameBytes);

        // 写入密码
        byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
        buf.writeInt(passwordBytes.length);
        buf.writeBytes(passwordBytes);
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取帧头中的版本号
     * @return 版本号
     */
    public int getVersion() {
        return header.getFieldAs("version", Integer.class);
    }
}