package org.intermine.model.bio;

public interface Transcript extends org.intermine.model.bio.SequenceFeature
{
    public java.lang.String getStatus();
    public void setStatus(final java.lang.String status);

    public java.lang.String getType();
    public void setType(final java.lang.String type);

    public java.lang.String getComments();
    public void setComments(final java.lang.String comments);

    public java.lang.String getTypeStatusDefinition();
    public void setTypeStatusDefinition(final java.lang.String typeStatusDefinition);

    public java.lang.String getTypeDefinition();
    public void setTypeDefinition(final java.lang.String typeDefinition);

    public java.lang.String getvegaId();
    public void setvegaId(final java.lang.String VegaId);

    public org.intermine.model.bio.Protein getProtein();
    public void setProtein(final org.intermine.model.bio.Protein protein);
    public void proxyProtein(final org.intermine.objectstore.proxy.ProxyReference protein);
    public org.intermine.model.InterMineObject proxGetProtein();

    public org.intermine.model.bio.Pseudogene getPseudogene();
    public void setPseudogene(final org.intermine.model.bio.Pseudogene pseudogene);
    public void proxyPseudogene(final org.intermine.objectstore.proxy.ProxyReference pseudogene);
    public org.intermine.model.InterMineObject proxGetPseudogene();

    public org.intermine.model.bio.Gene getGene();
    public void setGene(final org.intermine.model.bio.Gene gene);
    public void proxyGene(final org.intermine.objectstore.proxy.ProxyReference gene);
    public org.intermine.model.InterMineObject proxGetGene();

    public java.util.Set<org.intermine.model.bio.Intron> getIntrons();
    public void setIntrons(final java.util.Set<org.intermine.model.bio.Intron> introns);
    public void addIntrons(final org.intermine.model.bio.Intron arg);

    public java.util.Set<org.intermine.model.bio.Construct> getPromotes();
    public void setPromotes(final java.util.Set<org.intermine.model.bio.Construct> promotes);
    public void addPromotes(final org.intermine.model.bio.Construct arg);

    public java.util.Set<org.intermine.model.bio.Clone> getClones();
    public void setClones(final java.util.Set<org.intermine.model.bio.Clone> clones);
    public void addClones(final org.intermine.model.bio.Clone arg);

}
