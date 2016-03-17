package org.zfin.intermine.dataconversion;

public enum ZdbPkId {
    LAB("SequenceAlteration"),
    PERS("SequenceAlteration"),
    COMPANY("ComplexSubstitution"),;

    private String value;

    ZdbPkId(String value) {
        this.value = value;
    }

    public String getValue(){
        return value;
    }

    public static String getFeatureValue(String name) {
        for (ZdbPkId type : values())
            if (type.toString().equals(name))
                return type.value;
        throw new NullPointerException("No enum found with name " + name);
    }
}
