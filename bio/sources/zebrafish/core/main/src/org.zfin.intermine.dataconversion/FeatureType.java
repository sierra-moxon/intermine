package org.zfin.intermine.dataconversion;

public enum FeatureType {
    INSERTION("Insertion"),
    POINT_MUTATION("PointMutation"),
    DELETION("Deletion"),
    DEFICIENCY("ChromosomalDeletion"),
    TRANSLOC("Translocation"),
    INVERSION("Inversion"),
    TRANSGENIC_INSERTION("TransgenicInsertion"),
    SEQUENCE_VARIANT("SequenceAlteration"),
    UNSPECIFIED("SequenceAlteration"),
    COMPLEX_SUBSTITUTION("ComplexSubstitution"),
    TRANSGENIC_UNSPECIFIED("TransgenicInsertion"),;

    private String value;

    FeatureType(String value) {
        this.value = value;
    }

    public static String getFeatureValue(String name) {
        for (FeatureType type : values())
            if (type.toString().equals(name))
                return type.value;
        throw new NullPointerException("No enum found with name " + name);
    }
}
