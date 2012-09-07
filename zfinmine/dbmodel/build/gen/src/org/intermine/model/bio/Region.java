package org.intermine.model.bio;

public interface Region extends org.intermine.model.InterMineObject
{
    public java.lang.String getSymbol();
    public void setSymbol(final java.lang.String symbol);

    public java.lang.String getType();
    public void setType(final java.lang.String type);

    public java.lang.String getPrimaryIdentifier();
    public void setPrimaryIdentifier(final java.lang.String primaryIdentifier);

    public java.lang.String getComments();
    public void setComments(final java.lang.String comments);

    public java.lang.String getName();
    public void setName(final java.lang.String name);

    public org.intermine.model.bio.Organism getOrganism();
    public void setOrganism(final org.intermine.model.bio.Organism organism);
    public void proxyOrganism(final org.intermine.objectstore.proxy.ProxyReference organism);
    public org.intermine.model.InterMineObject proxGetOrganism();

    public java.util.Set<org.intermine.model.bio.CrossReference> getCrossReferences();
    public void setCrossReferences(final java.util.Set<org.intermine.model.bio.CrossReference> crossReferences);
    public void addCrossReferences(final org.intermine.model.bio.CrossReference arg);

    public java.util.Set<org.intermine.model.bio.Publication> getPublications();
    public void setPublications(final java.util.Set<org.intermine.model.bio.Publication> publications);
    public void addPublications(final org.intermine.model.bio.Publication arg);

    public java.util.Set<org.intermine.model.bio.Construct> getPromotes();
    public void setPromotes(final java.util.Set<org.intermine.model.bio.Construct> promotes);
    public void addPromotes(final org.intermine.model.bio.Construct arg);

    public java.util.Set<org.intermine.model.bio.Construct> getConstructs();
    public void setConstructs(final java.util.Set<org.intermine.model.bio.Construct> constructs);
    public void addConstructs(final org.intermine.model.bio.Construct arg);

}
