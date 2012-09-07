package org.intermine.model.bio;

public interface Deletion extends org.intermine.model.bio.SequenceAlteration
{
    public org.intermine.model.bio.ChromosomalDeletion getChromosomalDeletion();
    public void setChromosomalDeletion(final org.intermine.model.bio.ChromosomalDeletion chromosomalDeletion);
    public void proxyChromosomalDeletion(final org.intermine.objectstore.proxy.ProxyReference chromosomalDeletion);
    public org.intermine.model.InterMineObject proxGetChromosomalDeletion();

}
