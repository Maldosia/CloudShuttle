package com.maldosia.mobile;

import com.maldosia.cloudshuttle.core.FieldAssembler;

public class MyFieldAssembler implements FieldAssembler<String> {
    @Override
    public byte[] assemble(String value) {
        return new byte[0];
    }
}
