package org.intermine.model.bio;

public interface IntergenicRegion extends org.intermine.model.InterMineObject
{
    public java.util.Set<org.intermine.model.bio.Gene> getAdjacentGenes();
    public void setAdjacentGenes(final java.util.Set<org.intermine.model.bio.Gene> adjacentGenes);
    public void addAdjacentGenes(final org.intermine.model.bio.Gene arg);

}