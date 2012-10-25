package org.intermine.model.bio;

public interface ZFATerm extends org.intermine.model.bio.OntologyTerm
{
    public java.util.Set<org.intermine.model.bio.CrossReference> getCrossReferences();
    public void setCrossReferences(final java.util.Set<org.intermine.model.bio.CrossReference> crossReferences);
    public void addCrossReferences(final org.intermine.model.bio.CrossReference arg);

}
