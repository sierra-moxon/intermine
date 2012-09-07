package org.intermine.model.bio;

public interface Fish extends org.intermine.model.InterMineObject
{
    public java.lang.String getLongName();
    public void setLongName(final java.lang.String longName);

    public java.lang.String getPrimaryIdentifier();
    public void setPrimaryIdentifier(final java.lang.String primaryIdentifier);

    public java.lang.String getName();
    public void setName(final java.lang.String name);

    public java.lang.String getFishId();
    public void setFishId(final java.lang.String fishId);

    public java.util.Set<org.intermine.model.bio.Gene> getGenes();
    public void setGenes(final java.util.Set<org.intermine.model.bio.Gene> genes);
    public void addGenes(final org.intermine.model.bio.Gene arg);

    public java.util.Set<org.intermine.model.bio.SequenceFeature> getAffectors();
    public void setAffectors(final java.util.Set<org.intermine.model.bio.SequenceFeature> affectors);
    public void addAffectors(final org.intermine.model.bio.SequenceFeature arg);

    public java.util.Set<org.intermine.model.bio.Construct> getConstructs();
    public void setConstructs(final java.util.Set<org.intermine.model.bio.Construct> constructs);
    public void addConstructs(final org.intermine.model.bio.Construct arg);

    public java.util.Set<org.intermine.model.bio.Phenotype> getPhenotypes();
    public void setPhenotypes(final java.util.Set<org.intermine.model.bio.Phenotype> phenotypes);
    public void addPhenotypes(final org.intermine.model.bio.Phenotype arg);

}
