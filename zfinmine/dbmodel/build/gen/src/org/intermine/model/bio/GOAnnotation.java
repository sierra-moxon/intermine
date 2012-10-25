package org.intermine.model.bio;

public interface GOAnnotation extends org.intermine.model.bio.OntologyAnnotation
{
    public java.lang.String getAnnotationExtension();
    public void setAnnotationExtension(final java.lang.String annotationExtension);

    public java.util.Set<org.intermine.model.bio.GOEvidence> getEvidence();
    public void setEvidence(final java.util.Set<org.intermine.model.bio.GOEvidence> evidence);
    public void addEvidence(final org.intermine.model.bio.GOEvidence arg);

}
