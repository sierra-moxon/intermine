package org.intermine.model.bio;

public interface ChromosomalDeletion extends org.intermine.model.bio.SequenceAlteration
{
    public java.util.Set<org.intermine.model.bio.Deletion> getDeletions();
    public void setDeletions(final java.util.Set<org.intermine.model.bio.Deletion> deletions);
    public void addDeletions(final org.intermine.model.bio.Deletion arg);

}
