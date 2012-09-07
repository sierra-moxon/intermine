package org.intermine.model.bio;

public interface Morpholino extends org.intermine.model.bio.SequenceFeature
{
    public java.lang.String getType();
    public void setType(final java.lang.String type);

    public java.lang.String getComments();
    public void setComments(final java.lang.String comments);

    public java.util.Set<org.intermine.model.bio.EnvironmentalCondition> getEnvironmentalConditions();
    public void setEnvironmentalConditions(final java.util.Set<org.intermine.model.bio.EnvironmentalCondition> environmentalConditions);
    public void addEnvironmentalConditions(final org.intermine.model.bio.EnvironmentalCondition arg);

    public java.util.Set<org.intermine.model.bio.Gene> getTargets();
    public void setTargets(final java.util.Set<org.intermine.model.bio.Gene> targets);
    public void addTargets(final org.intermine.model.bio.Gene arg);

}
