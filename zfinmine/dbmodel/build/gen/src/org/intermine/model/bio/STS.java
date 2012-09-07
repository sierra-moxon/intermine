package org.intermine.model.bio;

public interface STS extends org.intermine.model.bio.Tag
{
    public java.lang.String getType();
    public void setType(final java.lang.String type);

    public java.lang.String getComments();
    public void setComments(final java.lang.String comments);

    public java.util.Set<org.intermine.model.bio.Gene> getGenes();
    public void setGenes(final java.util.Set<org.intermine.model.bio.Gene> genes);
    public void addGenes(final org.intermine.model.bio.Gene arg);

}
