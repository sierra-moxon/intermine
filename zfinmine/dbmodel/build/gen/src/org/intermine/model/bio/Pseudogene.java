package org.intermine.model.bio;

public interface Pseudogene extends org.intermine.model.bio.SequenceFeature
{
    public java.lang.String getType();
    public void setType(final java.lang.String type);

    public java.lang.String getComments();
    public void setComments(final java.lang.String comments);

    public java.util.Set<org.intermine.model.bio.Protein> getProteins();
    public void setProteins(final java.util.Set<org.intermine.model.bio.Protein> proteins);
    public void addProteins(final org.intermine.model.bio.Protein arg);

    public java.util.Set<org.intermine.model.bio.Clone> getEncodes();
    public void setEncodes(final java.util.Set<org.intermine.model.bio.Clone> encodes);
    public void addEncodes(final org.intermine.model.bio.Clone arg);

    public java.util.Set<org.intermine.model.bio.PseudogenicTranscript> getPseudogenicTranscripts();
    public void setPseudogenicTranscripts(final java.util.Set<org.intermine.model.bio.PseudogenicTranscript> pseudogenicTranscripts);
    public void addPseudogenicTranscripts(final org.intermine.model.bio.PseudogenicTranscript arg);

}
