package org.intermine.model.bio;

public interface Construct extends org.intermine.model.InterMineObject
{
    public java.lang.String getSymbol();
    public void setSymbol(final java.lang.String symbol);

    public java.lang.String getType();
    public void setType(final java.lang.String type);

    public java.lang.String getPrimaryIdentifier();
    public void setPrimaryIdentifier(final java.lang.String primaryIdentifier);

    public java.lang.String getName();
    public void setName(final java.lang.String name);

    public org.intermine.model.bio.Organism getOrganism();
    public void setOrganism(final org.intermine.model.bio.Organism organism);
    public void proxyOrganism(final org.intermine.objectstore.proxy.ProxyReference organism);
    public org.intermine.model.InterMineObject proxGetOrganism();

    public java.util.Set<org.intermine.model.bio.Gene> getcDS();
    public void setcDS(final java.util.Set<org.intermine.model.bio.Gene> CDS);
    public void addcDS(final org.intermine.model.bio.Gene arg);

    public java.util.Set<org.intermine.model.bio.Gene> getpromoter();
    public void setpromoter(final java.util.Set<org.intermine.model.bio.Gene> Promoter);
    public void addpromoter(final org.intermine.model.bio.Gene arg);

    public java.util.Set<org.intermine.model.bio.Publication> getPublications();
    public void setPublications(final java.util.Set<org.intermine.model.bio.Publication> publications);
    public void addPublications(final org.intermine.model.bio.Publication arg);

    public java.util.Set<org.intermine.model.bio.EFG> geteFGs();
    public void seteFGs(final java.util.Set<org.intermine.model.bio.EFG> EFGs);
    public void addeFGs(final org.intermine.model.bio.EFG arg);

    public java.util.Set<org.intermine.model.bio.Gene> getCodingSequences();
    public void setCodingSequences(final java.util.Set<org.intermine.model.bio.Gene> codingSequences);
    public void addCodingSequences(final org.intermine.model.bio.Gene arg);

    public java.util.Set<org.intermine.model.bio.Region> getContains();
    public void setContains(final java.util.Set<org.intermine.model.bio.Region> contains);
    public void addContains(final org.intermine.model.bio.Region arg);

    public java.util.Set<org.intermine.model.bio.Gene> getPromoters();
    public void setPromoters(final java.util.Set<org.intermine.model.bio.Gene> promoters);
    public void addPromoters(final org.intermine.model.bio.Gene arg);

    public java.util.Set<org.intermine.model.bio.CrossReference> getCrossReferences();
    public void setCrossReferences(final java.util.Set<org.intermine.model.bio.CrossReference> crossReferences);
    public void addCrossReferences(final org.intermine.model.bio.CrossReference arg);

    public java.util.Set<org.intermine.model.bio.TransgenicInsertion> getPhenotypicAlleles();
    public void setPhenotypicAlleles(final java.util.Set<org.intermine.model.bio.TransgenicInsertion> phenotypicAlleles);
    public void addPhenotypicAlleles(final org.intermine.model.bio.TransgenicInsertion arg);

    public java.util.Set<org.intermine.model.bio.TransgenicInsertion> getInnocuousAlleles();
    public void setInnocuousAlleles(final java.util.Set<org.intermine.model.bio.TransgenicInsertion> innocuousAlleles);
    public void addInnocuousAlleles(final org.intermine.model.bio.TransgenicInsertion arg);

    public java.util.Set<org.intermine.model.bio.TransgenicInsertion> getInsertionAlleles();
    public void setInsertionAlleles(final java.util.Set<org.intermine.model.bio.TransgenicInsertion> insertionAlleles);
    public void addInsertionAlleles(final org.intermine.model.bio.TransgenicInsertion arg);

}
