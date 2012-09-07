package org.intermine.model.bio;

public interface GOAnnotation extends org.intermine.model.bio.OntologyAnnotation
{
    public java.lang.String getWithText();
    public void setWithText(final java.lang.String withText);

    public java.util.Set<org.intermine.model.bio.GOEvidence> getEvidence();
    public void setEvidence(final java.util.Set<org.intermine.model.bio.GOEvidence> evidence);
    public void addEvidence(final org.intermine.model.bio.GOEvidence arg);

    public java.util.Set<org.intermine.model.bio.BioEntity> getWith();
    public void setWith(final java.util.Set<org.intermine.model.bio.BioEntity> with);
    public void addWith(final org.intermine.model.bio.BioEntity arg);

}
