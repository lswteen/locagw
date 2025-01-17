package com.lottecard.loca.meta;

public enum Profiles {
    LOCAL,
    DEV,
    STG,
    RELEASE;

    public String toLowerCase() {
        return this.name().toLowerCase();
    }
}
