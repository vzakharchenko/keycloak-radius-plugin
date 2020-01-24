package com.github.vzakharchenko.radius.models;

public final class Attribute26Holder {
    private int v;
    private String attrName;
    private int nType;
    private String valueAsString;

    private Attribute26Holder() {
    }

    public static Attribute26Holder create() {
        return new Attribute26Holder();
    }

    public Attribute26Holder vendor(int vendor) {
        this.v = vendor;
        return this;
    }

    public Attribute26Holder attributeName(String name) {
        this.attrName = name;
        return this;
    }

    public Attribute26Holder newType(int newType) {
        this.nType = newType;
        return this;
    }

    public Attribute26Holder value(String value) {
        this.valueAsString = value;
        return this;
    }

    public int getVendor() {
        return v;
    }

    public String getAttributeName() {
        return attrName;
    }

    public int getNewType() {
        return nType;
    }

    public String getValue() {
        return valueAsString;
    }
}
