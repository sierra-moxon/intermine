package org.intermine.model.bio;

public interface SequenceAlteration extends org.intermine.model.bio.SequenceFeature
{
    public java.lang.String getType();
    public void setType(final java.lang.String type);

    public java.lang.String getFeatureId();
    public void setFeatureId(final java.lang.String featureId);

    public java.lang.String getFeatureZygosity();
    public void setFeatureZygosity(final java.lang.String featureZygosity);

    public java.util.Set<org.intermine.model.bio.SequenceFeature> getMarkersMissing();
    public void setMarkersMissing(final java.util.Set<org.intermine.model.bio.SequenceFeature> markersMissing);
    public void addMarkersMissing(final org.intermine.model.bio.SequenceFeature arg);

    public java.util.Set<org.intermine.model.bio.FeatureMarkerRelationship> getRelatedMarkers();
    public void setRelatedMarkers(final java.util.Set<org.intermine.model.bio.FeatureMarkerRelationship> relatedMarkers);
    public void addRelatedMarkers(final org.intermine.model.bio.FeatureMarkerRelationship arg);

    public java.util.Set<org.intermine.model.bio.Gene> getGenes();
    public void setGenes(final java.util.Set<org.intermine.model.bio.Gene> genes);
    public void addGenes(final org.intermine.model.bio.Gene arg);

    public java.util.Set<org.intermine.model.bio.SequenceFeature> getMarkersMoved();
    public void setMarkersMoved(final java.util.Set<org.intermine.model.bio.SequenceFeature> markersMoved);
    public void addMarkersMoved(final org.intermine.model.bio.SequenceFeature arg);

    public java.util.Set<org.intermine.model.bio.SequenceFeature> getMarkersPresent();
    public void setMarkersPresent(final java.util.Set<org.intermine.model.bio.SequenceFeature> markersPresent);
    public void addMarkersPresent(final org.intermine.model.bio.SequenceFeature arg);

    public java.util.Set<org.intermine.model.bio.Genotype> getGenotypes();
    public void setGenotypes(final java.util.Set<org.intermine.model.bio.Genotype> genotypes);
    public void addGenotypes(final org.intermine.model.bio.Genotype arg);

}
