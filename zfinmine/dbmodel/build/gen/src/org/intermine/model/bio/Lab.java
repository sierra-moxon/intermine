package org.intermine.model.bio;

public interface Lab extends org.intermine.model.InterMineObject
{
    public java.lang.String getContactPerson();
    public void setContactPerson(final java.lang.String contactPerson);

    public java.lang.String getInformation();
    public void setInformation(final java.lang.String information);

    public java.lang.String getPrimaryIdentifier();
    public void setPrimaryIdentifier(final java.lang.String primaryIdentifier);

    public java.lang.String getName();
    public void setName(final java.lang.String name);

    public java.util.Set<org.intermine.model.bio.SequenceFeature> getLines();
    public void setLines(final java.util.Set<org.intermine.model.bio.SequenceFeature> lines);
    public void addLines(final org.intermine.model.bio.SequenceFeature arg);

}
