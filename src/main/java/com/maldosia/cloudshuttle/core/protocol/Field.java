package com.maldosia.cloudshuttle.core.protocol;

/**
 * @author Maldosia
 * @since 2025/6/30
 */
public class Field {

    private final String name;
    private final int length;
    private final boolean isDelimiter;

    public Field(String name, int length) {
        this(name, length, false);
    }

    public Field(String name, int length, boolean isDelimiter) {
        this.name = name;
        this.length = length;
        this.isDelimiter = isDelimiter;
    }

    public String getName() {
        return name;
    }

    public int getLength() {
        return length;
    }

    public boolean isDelimiter() {
        return isDelimiter;
    }
}