package org.intermine.model.bio;

public interface Pathway extends org.intermine.model.InterMineObject
{
    public java.lang.String getShortName();
    public void setShortName(final java.lang.String shortName);

    public java.lang.String getIdentifier();
    public void setIdentifier(final java.lang.String identifier);

    public java.lang.Boolean getCurated();
    public void setCurated(final java.lang.Boolean curated);

    public java.lang.String getName();
    public void setName(final java.lang.String name);

    public java.lang.String getDescription();
    public void setDescription(final java.lang.String description);

    public java.util.Set<org.intermine.model.bio.Protein> getProteins();
    public void setProteins(final java.util.Set<org.intermine.model.bio.Protein> proteins);
    public void addProteins(final org.intermine.model.bio.Protein arg);

    public java.util.Set<org.intermine.model.bio.Gene> getGenes();
    public void setGenes(final java.util.Set<org.intermine.model.bio.Gene> genes);
    public void addGenes(final org.intermine.model.bio.Gene arg);

    public java.util.Set<org.intermine.model.bio.DataSet> getDataSets();
    public void setDataSets(final java.util.Set<org.intermine.model.bio.DataSet> dataSets);
    public void addDataSets(final org.intermine.model.bio.DataSet arg);

}
