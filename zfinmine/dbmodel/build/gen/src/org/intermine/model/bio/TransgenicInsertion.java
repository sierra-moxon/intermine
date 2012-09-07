package org.intermine.model.bio;

public interface TransgenicInsertion extends org.intermine.model.bio.Insertion
{
    public java.util.Set<org.intermine.model.bio.Construct> getInsertionConstructs();
    public void setInsertionConstructs(final java.util.Set<org.intermine.model.bio.Construct> insertionConstructs);
    public void addInsertionConstructs(final org.intermine.model.bio.Construct arg);

    public java.util.Set<org.intermine.model.bio.Construct> getInnocuousConstructs();
    public void setInnocuousConstructs(final java.util.Set<org.intermine.model.bio.Construct> innocuousConstructs);
    public void addInnocuousConstructs(final org.intermine.model.bio.Construct arg);

    public java.util.Set<org.intermine.model.bio.Construct> getPhenotypicConstructs();
    public void setPhenotypicConstructs(final java.util.Set<org.intermine.model.bio.Construct> phenotypicConstructs);
    public void addPhenotypicConstructs(final org.intermine.model.bio.Construct arg);

}
