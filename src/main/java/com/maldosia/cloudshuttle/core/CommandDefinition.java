package com.maldosia.cloudshuttle.core;

public interface CommandDefinition {

    byte[] getStartFlag();

    byte[] getEndFlag();

}
