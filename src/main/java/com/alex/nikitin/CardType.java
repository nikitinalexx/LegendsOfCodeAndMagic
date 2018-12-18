package com.alex.nikitin;

public enum CardType {
    CREATURE(0), GREEN(1), RED(2), BLUE(3);

    public int value;

    CardType(int value) {
        this.value = value;
    }

    public static CardType ofValue(int value) {
        CardType[] values = values();
        for (int i = 0; i < values.length; i++) {
            if (values[i].value == value) {
                return values[i];
            }
        }
        throw new IllegalArgumentException("Invalid enum value");
    }
}
