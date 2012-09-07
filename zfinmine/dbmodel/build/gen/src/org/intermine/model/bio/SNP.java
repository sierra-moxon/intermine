package org.intermine.model.bio;

public interface SNP extends org.intermine.model.bio.Substitution
{
    public java.lang.String getComments();
    public void setComments(final java.lang.String comments);

    public org.intermine.model.bio.Gene getGene();
    public void setGene(final org.intermine.model.bio.Gene gene);
    public void proxyGene(final org.intermine.objectstore.proxy.ProxyReference gene);
    public org.intermine.model.InterMineObject proxGetGene();

}
