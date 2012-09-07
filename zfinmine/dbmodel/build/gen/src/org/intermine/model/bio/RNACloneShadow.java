package org.intermine.model.bio;

import org.intermine.objectstore.ObjectStore;
import org.intermine.objectstore.intermine.NotXmlParser;
import org.intermine.objectstore.intermine.NotXmlRenderer;
import org.intermine.objectstore.proxy.ProxyCollection;
import org.intermine.objectstore.proxy.ProxyReference;
import org.intermine.util.StringConstructor;
import org.intermine.util.TypeUtil;
import org.intermine.model.ShadowClass;

public class RNACloneShadow implements RNAClone, ShadowClass
{
    public static final Class<RNAClone> shadowOf = RNAClone.class;
    // Col: org.intermine.model.bio.RNAClone.expressions
    protected java.util.Set<org.intermine.model.bio.ExpressionResult> expressions = new java.util.HashSet<org.intermine.model.bio.ExpressionResult>();
    public java.util.Set<org.intermine.model.bio.ExpressionResult> getExpressions() { return expressions; }
    public void setExpressions(final java.util.Set<org.intermine.model.bio.ExpressionResult> expressions) { this.expressions = expressions; }
    public void addExpressions(final org.intermine.model.bio.ExpressionResult arg) { expressions.add(arg); }

    // Attr: org.intermine.model.bio.Clone.insertSize
    protected java.lang.String insertSize;
    public java.lang.String getInsertSize() { return insertSize; }
    public void setInsertSize(final java.lang.String insertSize) { this.insertSize = insertSize; }

    // Attr: org.intermine.model.bio.Clone.PCR
    protected java.lang.String PCR;
    public java.lang.String getpCR() { return PCR; }
    public void setpCR(final java.lang.String PCR) { this.PCR = PCR; }

    // Attr: org.intermine.model.bio.Clone.digest
    protected java.lang.String digest;
    public java.lang.String getDigest() { return digest; }
    public void setDigest(final java.lang.String digest) { this.digest = digest; }

    // Attr: org.intermine.model.bio.Clone.ThisseCloneRating
    protected java.lang.String ThisseCloneRating;
    public java.lang.String getthisseCloneRating() { return ThisseCloneRating; }
    public void setthisseCloneRating(final java.lang.String ThisseCloneRating) { this.ThisseCloneRating = ThisseCloneRating; }

    // Attr: org.intermine.model.bio.Clone.polymerase
    protected java.lang.String polymerase;
    public java.lang.String getPolymerase() { return polymerase; }
    public void setPolymerase(final java.lang.String polymerase) { this.polymerase = polymerase; }

    // Attr: org.intermine.model.bio.Clone.cloneSite
    protected java.lang.String cloneSite;
    public java.lang.String getCloneSite() { return cloneSite; }
    public void setCloneSite(final java.lang.String cloneSite) { this.cloneSite = cloneSite; }

    // Attr: org.intermine.model.bio.Clone.type
    protected java.lang.String type;
    public java.lang.String getType() { return type; }
    public void setType(final java.lang.String type) { this.type = type; }

    // Attr: org.intermine.model.bio.Clone.comments
    protected java.lang.String comments;
    public java.lang.String getComments() { return comments; }
    public void setComments(final java.lang.String comments) { this.comments = comments; }

    // Attr: org.intermine.model.bio.Clone.sequenceType
    protected java.lang.String sequenceType;
    public java.lang.String getSequenceType() { return sequenceType; }
    public void setSequenceType(final java.lang.String sequenceType) { this.sequenceType = sequenceType; }

    // Attr: org.intermine.model.bio.Clone.vector
    protected java.lang.String vector;
    public java.lang.String getVector() { return vector; }
    public void setVector(final java.lang.String vector) { this.vector = vector; }

    // Attr: org.intermine.model.bio.Clone.problemType
    protected java.lang.String problemType;
    public java.lang.String getProblemType() { return problemType; }
    public void setProblemType(final java.lang.String problemType) { this.problemType = problemType; }

    // Ref: org.intermine.model.bio.Clone.probeLibrary
    protected org.intermine.model.InterMineObject probeLibrary;
    public org.intermine.model.bio.ProbeLibrary getProbeLibrary() { if (probeLibrary instanceof org.intermine.objectstore.proxy.ProxyReference) { return ((org.intermine.model.bio.ProbeLibrary) ((org.intermine.objectstore.proxy.ProxyReference) probeLibrary).getObject()); }; return (org.intermine.model.bio.ProbeLibrary) probeLibrary; }
    public void setProbeLibrary(final org.intermine.model.bio.ProbeLibrary probeLibrary) { this.probeLibrary = probeLibrary; }
    public void proxyProbeLibrary(final org.intermine.objectstore.proxy.ProxyReference probeLibrary) { this.probeLibrary = probeLibrary; }
    public org.intermine.model.InterMineObject proxGetProbeLibrary() { return probeLibrary; }

    // Col: org.intermine.model.bio.Clone.contains
    protected java.util.Set<org.intermine.model.bio.Clone> contains = new java.util.HashSet<org.intermine.model.bio.Clone>();
    public java.util.Set<org.intermine.model.bio.Clone> getContains() { return contains; }
    public void setContains(final java.util.Set<org.intermine.model.bio.Clone> contains) { this.contains = contains; }
    public void addContains(final org.intermine.model.bio.Clone arg) { contains.add(arg); }

    // Col: org.intermine.model.bio.Clone.genes
    protected java.util.Set<org.intermine.model.bio.Gene> genes = new java.util.HashSet<org.intermine.model.bio.Gene>();
    public java.util.Set<org.intermine.model.bio.Gene> getGenes() { return genes; }
    public void setGenes(final java.util.Set<org.intermine.model.bio.Gene> genes) { this.genes = genes; }
    public void addGenes(final org.intermine.model.bio.Gene arg) { genes.add(arg); }

    // Col: org.intermine.model.bio.Clone.artifacts
    protected java.util.Set<org.intermine.model.bio.Gene> artifacts = new java.util.HashSet<org.intermine.model.bio.Gene>();
    public java.util.Set<org.intermine.model.bio.Gene> getArtifacts() { return artifacts; }
    public void setArtifacts(final java.util.Set<org.intermine.model.bio.Gene> artifacts) { this.artifacts = artifacts; }
    public void addArtifacts(final org.intermine.model.bio.Gene arg) { artifacts.add(arg); }

    // Col: org.intermine.model.bio.Clone.clones
    protected java.util.Set<org.intermine.model.bio.Clone> clones = new java.util.HashSet<org.intermine.model.bio.Clone>();
    public java.util.Set<org.intermine.model.bio.Clone> getClones() { return clones; }
    public void setClones(final java.util.Set<org.intermine.model.bio.Clone> clones) { this.clones = clones; }
    public void addClones(final org.intermine.model.bio.Clone arg) { clones.add(arg); }

    // Col: org.intermine.model.bio.Clone.transcripts
    protected java.util.Set<org.intermine.model.bio.Transcript> transcripts = new java.util.HashSet<org.intermine.model.bio.Transcript>();
    public java.util.Set<org.intermine.model.bio.Transcript> getTranscripts() { return transcripts; }
    public void setTranscripts(final java.util.Set<org.intermine.model.bio.Transcript> transcripts) { this.transcripts = transcripts; }
    public void addTranscripts(final org.intermine.model.bio.Transcript arg) { transcripts.add(arg); }

    // Attr: org.intermine.model.bio.SequenceFeature.length
    protected java.lang.Integer length;
    public java.lang.Integer getLength() { return length; }
    public void setLength(final java.lang.Integer length) { this.length = length; }

    // Attr: org.intermine.model.bio.SequenceFeature.scoreType
    protected java.lang.String scoreType;
    public java.lang.String getScoreType() { return scoreType; }
    public void setScoreType(final java.lang.String scoreType) { this.scoreType = scoreType; }

    // Attr: org.intermine.model.bio.SequenceFeature.score
    protected java.lang.Double score;
    public java.lang.Double getScore() { return score; }
    public void setScore(final java.lang.Double score) { this.score = score; }

    // Ref: org.intermine.model.bio.SequenceFeature.chromosome
    protected org.intermine.model.InterMineObject chromosome;
    public org.intermine.model.bio.Chromosome getChromosome() { if (chromosome instanceof org.intermine.objectstore.proxy.ProxyReference) { return ((org.intermine.model.bio.Chromosome) ((org.intermine.objectstore.proxy.ProxyReference) chromosome).getObject()); }; return (org.intermine.model.bio.Chromosome) chromosome; }
    public void setChromosome(final org.intermine.model.bio.Chromosome chromosome) { this.chromosome = chromosome; }
    public void proxyChromosome(final org.intermine.objectstore.proxy.ProxyReference chromosome) { this.chromosome = chromosome; }
    public org.intermine.model.InterMineObject proxGetChromosome() { return chromosome; }

    // Ref: org.intermine.model.bio.SequenceFeature.chromosomeLocation
    protected org.intermine.model.InterMineObject chromosomeLocation;
    public org.intermine.model.bio.Location getChromosomeLocation() { if (chromosomeLocation instanceof org.intermine.objectstore.proxy.ProxyReference) { return ((org.intermine.model.bio.Location) ((org.intermine.objectstore.proxy.ProxyReference) chromosomeLocation).getObject()); }; return (org.intermine.model.bio.Location) chromosomeLocation; }
    public void setChromosomeLocation(final org.intermine.model.bio.Location chromosomeLocation) { this.chromosomeLocation = chromosomeLocation; }
    public void proxyChromosomeLocation(final org.intermine.objectstore.proxy.ProxyReference chromosomeLocation) { this.chromosomeLocation = chromosomeLocation; }
    public org.intermine.model.InterMineObject proxGetChromosomeLocation() { return chromosomeLocation; }

    // Ref: org.intermine.model.bio.SequenceFeature.sequence
    protected org.intermine.model.InterMineObject sequence;
    public org.intermine.model.bio.Sequence getSequence() { if (sequence instanceof org.intermine.objectstore.proxy.ProxyReference) { return ((org.intermine.model.bio.Sequence) ((org.intermine.objectstore.proxy.ProxyReference) sequence).getObject()); }; return (org.intermine.model.bio.Sequence) sequence; }
    public void setSequence(final org.intermine.model.bio.Sequence sequence) { this.sequence = sequence; }
    public void proxySequence(final org.intermine.objectstore.proxy.ProxyReference sequence) { this.sequence = sequence; }
    public org.intermine.model.InterMineObject proxGetSequence() { return sequence; }

    // Ref: org.intermine.model.bio.SequenceFeature.sequenceOntologyTerm
    protected org.intermine.model.InterMineObject sequenceOntologyTerm;
    public org.intermine.model.bio.SOTerm getSequenceOntologyTerm() { if (sequenceOntologyTerm instanceof org.intermine.objectstore.proxy.ProxyReference) { return ((org.intermine.model.bio.SOTerm) ((org.intermine.objectstore.proxy.ProxyReference) sequenceOntologyTerm).getObject()); }; return (org.intermine.model.bio.SOTerm) sequenceOntologyTerm; }
    public void setSequenceOntologyTerm(final org.intermine.model.bio.SOTerm sequenceOntologyTerm) { this.sequenceOntologyTerm = sequenceOntologyTerm; }
    public void proxySequenceOntologyTerm(final org.intermine.objectstore.proxy.ProxyReference sequenceOntologyTerm) { this.sequenceOntologyTerm = sequenceOntologyTerm; }
    public org.intermine.model.InterMineObject proxGetSequenceOntologyTerm() { return sequenceOntologyTerm; }

    // Col: org.intermine.model.bio.SequenceFeature.presentIn
    protected java.util.Set<org.intermine.model.bio.SequenceAlteration> presentIn = new java.util.HashSet<org.intermine.model.bio.SequenceAlteration>();
    public java.util.Set<org.intermine.model.bio.SequenceAlteration> getPresentIn() { return presentIn; }
    public void setPresentIn(final java.util.Set<org.intermine.model.bio.SequenceAlteration> presentIn) { this.presentIn = presentIn; }
    public void addPresentIn(final org.intermine.model.bio.SequenceAlteration arg) { presentIn.add(arg); }

    // Col: org.intermine.model.bio.SequenceFeature.labOfOrigin
    protected java.util.Set<org.intermine.model.bio.Lab> labOfOrigin = new java.util.HashSet<org.intermine.model.bio.Lab>();
    public java.util.Set<org.intermine.model.bio.Lab> getLabOfOrigin() { return labOfOrigin; }
    public void setLabOfOrigin(final java.util.Set<org.intermine.model.bio.Lab> labOfOrigin) { this.labOfOrigin = labOfOrigin; }
    public void addLabOfOrigin(final org.intermine.model.bio.Lab arg) { labOfOrigin.add(arg); }

    // Col: org.intermine.model.bio.SequenceFeature.missingFrom
    protected java.util.Set<org.intermine.model.bio.SequenceAlteration> missingFrom = new java.util.HashSet<org.intermine.model.bio.SequenceAlteration>();
    public java.util.Set<org.intermine.model.bio.SequenceAlteration> getMissingFrom() { return missingFrom; }
    public void setMissingFrom(final java.util.Set<org.intermine.model.bio.SequenceAlteration> missingFrom) { this.missingFrom = missingFrom; }
    public void addMissingFrom(final org.intermine.model.bio.SequenceAlteration arg) { missingFrom.add(arg); }

    // Col: org.intermine.model.bio.SequenceFeature.movedIn
    protected java.util.Set<org.intermine.model.bio.SequenceAlteration> movedIn = new java.util.HashSet<org.intermine.model.bio.SequenceAlteration>();
    public java.util.Set<org.intermine.model.bio.SequenceAlteration> getMovedIn() { return movedIn; }
    public void setMovedIn(final java.util.Set<org.intermine.model.bio.SequenceAlteration> movedIn) { this.movedIn = movedIn; }
    public void addMovedIn(final org.intermine.model.bio.SequenceAlteration arg) { movedIn.add(arg); }

    // Col: org.intermine.model.bio.SequenceFeature.overlappingFeatures
    protected java.util.Set<org.intermine.model.bio.SequenceFeature> overlappingFeatures = new java.util.HashSet<org.intermine.model.bio.SequenceFeature>();
    public java.util.Set<org.intermine.model.bio.SequenceFeature> getOverlappingFeatures() { return overlappingFeatures; }
    public void setOverlappingFeatures(final java.util.Set<org.intermine.model.bio.SequenceFeature> overlappingFeatures) { this.overlappingFeatures = overlappingFeatures; }
    public void addOverlappingFeatures(final org.intermine.model.bio.SequenceFeature arg) { overlappingFeatures.add(arg); }

    // Attr: org.intermine.model.bio.BioEntity.secondaryIdentifier
    protected java.lang.String secondaryIdentifier;
    public java.lang.String getSecondaryIdentifier() { return secondaryIdentifier; }
    public void setSecondaryIdentifier(final java.lang.String secondaryIdentifier) { this.secondaryIdentifier = secondaryIdentifier; }

    // Attr: org.intermine.model.bio.BioEntity.symbol
    protected java.lang.String symbol;
    public java.lang.String getSymbol() { return symbol; }
    public void setSymbol(final java.lang.String symbol) { this.symbol = symbol; }

    // Attr: org.intermine.model.bio.BioEntity.primaryIdentifier
    protected java.lang.String primaryIdentifier;
    public java.lang.String getPrimaryIdentifier() { return primaryIdentifier; }
    public void setPrimaryIdentifier(final java.lang.String primaryIdentifier) { this.primaryIdentifier = primaryIdentifier; }

    // Attr: org.intermine.model.bio.BioEntity.name
    protected java.lang.String name;
    public java.lang.String getName() { return name; }
    public void setName(final java.lang.String name) { this.name = name; }

    // Ref: org.intermine.model.bio.BioEntity.organism
    protected org.intermine.model.InterMineObject organism;
    public org.intermine.model.bio.Organism getOrganism() { if (organism instanceof org.intermine.objectstore.proxy.ProxyReference) { return ((org.intermine.model.bio.Organism) ((org.intermine.objectstore.proxy.ProxyReference) organism).getObject()); }; return (org.intermine.model.bio.Organism) organism; }
    public void setOrganism(final org.intermine.model.bio.Organism organism) { this.organism = organism; }
    public void proxyOrganism(final org.intermine.objectstore.proxy.ProxyReference organism) { this.organism = organism; }
    public org.intermine.model.InterMineObject proxGetOrganism() { return organism; }

    // Col: org.intermine.model.bio.BioEntity.locatedFeatures
    protected java.util.Set<org.intermine.model.bio.Location> locatedFeatures = new java.util.HashSet<org.intermine.model.bio.Location>();
    public java.util.Set<org.intermine.model.bio.Location> getLocatedFeatures() { return locatedFeatures; }
    public void setLocatedFeatures(final java.util.Set<org.intermine.model.bio.Location> locatedFeatures) { this.locatedFeatures = locatedFeatures; }
    public void addLocatedFeatures(final org.intermine.model.bio.Location arg) { locatedFeatures.add(arg); }

    // Col: org.intermine.model.bio.BioEntity.locations
    protected java.util.Set<org.intermine.model.bio.Location> locations = new java.util.HashSet<org.intermine.model.bio.Location>();
    public java.util.Set<org.intermine.model.bio.Location> getLocations() { return locations; }
    public void setLocations(final java.util.Set<org.intermine.model.bio.Location> locations) { this.locations = locations; }
    public void addLocations(final org.intermine.model.bio.Location arg) { locations.add(arg); }

    // Col: org.intermine.model.bio.BioEntity.ontologyAnnotations
    protected java.util.Set<org.intermine.model.bio.OntologyAnnotation> ontologyAnnotations = new java.util.HashSet<org.intermine.model.bio.OntologyAnnotation>();
    public java.util.Set<org.intermine.model.bio.OntologyAnnotation> getOntologyAnnotations() { return ontologyAnnotations; }
    public void setOntologyAnnotations(final java.util.Set<org.intermine.model.bio.OntologyAnnotation> ontologyAnnotations) { this.ontologyAnnotations = ontologyAnnotations; }
    public void addOntologyAnnotations(final org.intermine.model.bio.OntologyAnnotation arg) { ontologyAnnotations.add(arg); }

    // Col: org.intermine.model.bio.BioEntity.synonyms
    protected java.util.Set<org.intermine.model.bio.Synonym> synonyms = new java.util.HashSet<org.intermine.model.bio.Synonym>();
    public java.util.Set<org.intermine.model.bio.Synonym> getSynonyms() { return synonyms; }
    public void setSynonyms(final java.util.Set<org.intermine.model.bio.Synonym> synonyms) { this.synonyms = synonyms; }
    public void addSynonyms(final org.intermine.model.bio.Synonym arg) { synonyms.add(arg); }

    // Col: org.intermine.model.bio.BioEntity.dataSets
    protected java.util.Set<org.intermine.model.bio.DataSet> dataSets = new java.util.HashSet<org.intermine.model.bio.DataSet>();
    public java.util.Set<org.intermine.model.bio.DataSet> getDataSets() { return dataSets; }
    public void setDataSets(final java.util.Set<org.intermine.model.bio.DataSet> dataSets) { this.dataSets = dataSets; }
    public void addDataSets(final org.intermine.model.bio.DataSet arg) { dataSets.add(arg); }

    // Col: org.intermine.model.bio.BioEntity.publications
    protected java.util.Set<org.intermine.model.bio.Publication> publications = new java.util.HashSet<org.intermine.model.bio.Publication>();
    public java.util.Set<org.intermine.model.bio.Publication> getPublications() { return publications; }
    public void setPublications(final java.util.Set<org.intermine.model.bio.Publication> publications) { this.publications = publications; }
    public void addPublications(final org.intermine.model.bio.Publication arg) { publications.add(arg); }

    // Col: org.intermine.model.bio.BioEntity.crossReferences
    protected java.util.Set<org.intermine.model.bio.CrossReference> crossReferences = new java.util.HashSet<org.intermine.model.bio.CrossReference>();
    public java.util.Set<org.intermine.model.bio.CrossReference> getCrossReferences() { return crossReferences; }
    public void setCrossReferences(final java.util.Set<org.intermine.model.bio.CrossReference> crossReferences) { this.crossReferences = crossReferences; }
    public void addCrossReferences(final org.intermine.model.bio.CrossReference arg) { crossReferences.add(arg); }

    // Attr: org.intermine.model.InterMineObject.id
    protected java.lang.Integer id;
    public java.lang.Integer getId() { return id; }
    public void setId(final java.lang.Integer id) { this.id = id; }

    @Override public boolean equals(Object o) { return (o instanceof RNAClone && id != null) ? id.equals(((RNAClone)o).getId()) : this == o; }
    @Override public int hashCode() { return (id != null) ? id.hashCode() : super.hashCode(); }
    @Override public String toString() { return "RNAClone [PCR=\"" + PCR + "\", ThisseCloneRating=\"" + ThisseCloneRating + "\", chromosome=" + (chromosome == null ? "null" : (chromosome.getId() == null ? "no id" : chromosome.getId().toString())) + ", chromosomeLocation=" + (chromosomeLocation == null ? "null" : (chromosomeLocation.getId() == null ? "no id" : chromosomeLocation.getId().toString())) + ", cloneSite=\"" + cloneSite + "\", comments=\"" + comments + "\", digest=\"" + digest + "\", id=\"" + id + "\", insertSize=\"" + insertSize + "\", length=\"" + length + "\", name=\"" + name + "\", organism=" + (organism == null ? "null" : (organism.getId() == null ? "no id" : organism.getId().toString())) + ", polymerase=\"" + polymerase + "\", primaryIdentifier=\"" + primaryIdentifier + "\", probeLibrary=" + (probeLibrary == null ? "null" : (probeLibrary.getId() == null ? "no id" : probeLibrary.getId().toString())) + ", problemType=\"" + problemType + "\", score=\"" + score + "\", scoreType=\"" + scoreType + "\", secondaryIdentifier=\"" + secondaryIdentifier + "\", sequence=" + (sequence == null ? "null" : (sequence.getId() == null ? "no id" : sequence.getId().toString())) + ", sequenceOntologyTerm=" + (sequenceOntologyTerm == null ? "null" : (sequenceOntologyTerm.getId() == null ? "no id" : sequenceOntologyTerm.getId().toString())) + ", sequenceType=\"" + sequenceType + "\", symbol=\"" + symbol + "\", type=\"" + type + "\", vector=\"" + vector + "\"]"; }
    public Object getFieldValue(final String fieldName) throws IllegalAccessException {
        if ("expressions".equals(fieldName)) {
            return expressions;
        }
        if ("insertSize".equals(fieldName)) {
            return insertSize;
        }
        if ("PCR".equals(fieldName)) {
            return PCR;
        }
        if ("digest".equals(fieldName)) {
            return digest;
        }
        if ("ThisseCloneRating".equals(fieldName)) {
            return ThisseCloneRating;
        }
        if ("polymerase".equals(fieldName)) {
            return polymerase;
        }
        if ("cloneSite".equals(fieldName)) {
            return cloneSite;
        }
        if ("type".equals(fieldName)) {
            return type;
        }
        if ("comments".equals(fieldName)) {
            return comments;
        }
        if ("sequenceType".equals(fieldName)) {
            return sequenceType;
        }
        if ("vector".equals(fieldName)) {
            return vector;
        }
        if ("problemType".equals(fieldName)) {
            return problemType;
        }
        if ("probeLibrary".equals(fieldName)) {
            if (probeLibrary instanceof ProxyReference) {
                return ((ProxyReference) probeLibrary).getObject();
            } else {
                return probeLibrary;
            }
        }
        if ("contains".equals(fieldName)) {
            return contains;
        }
        if ("genes".equals(fieldName)) {
            return genes;
        }
        if ("artifacts".equals(fieldName)) {
            return artifacts;
        }
        if ("clones".equals(fieldName)) {
            return clones;
        }
        if ("transcripts".equals(fieldName)) {
            return transcripts;
        }
        if ("length".equals(fieldName)) {
            return length;
        }
        if ("scoreType".equals(fieldName)) {
            return scoreType;
        }
        if ("score".equals(fieldName)) {
            return score;
        }
        if ("chromosome".equals(fieldName)) {
            if (chromosome instanceof ProxyReference) {
                return ((ProxyReference) chromosome).getObject();
            } else {
                return chromosome;
            }
        }
        if ("chromosomeLocation".equals(fieldName)) {
            if (chromosomeLocation instanceof ProxyReference) {
                return ((ProxyReference) chromosomeLocation).getObject();
            } else {
                return chromosomeLocation;
            }
        }
        if ("sequence".equals(fieldName)) {
            if (sequence instanceof ProxyReference) {
                return ((ProxyReference) sequence).getObject();
            } else {
                return sequence;
            }
        }
        if ("sequenceOntologyTerm".equals(fieldName)) {
            if (sequenceOntologyTerm instanceof ProxyReference) {
                return ((ProxyReference) sequenceOntologyTerm).getObject();
            } else {
                return sequenceOntologyTerm;
            }
        }
        if ("presentIn".equals(fieldName)) {
            return presentIn;
        }
        if ("labOfOrigin".equals(fieldName)) {
            return labOfOrigin;
        }
        if ("missingFrom".equals(fieldName)) {
            return missingFrom;
        }
        if ("movedIn".equals(fieldName)) {
            return movedIn;
        }
        if ("overlappingFeatures".equals(fieldName)) {
            return overlappingFeatures;
        }
        if ("secondaryIdentifier".equals(fieldName)) {
            return secondaryIdentifier;
        }
        if ("symbol".equals(fieldName)) {
            return symbol;
        }
        if ("primaryIdentifier".equals(fieldName)) {
            return primaryIdentifier;
        }
        if ("name".equals(fieldName)) {
            return name;
        }
        if ("organism".equals(fieldName)) {
            if (organism instanceof ProxyReference) {
                return ((ProxyReference) organism).getObject();
            } else {
                return organism;
            }
        }
        if ("locatedFeatures".equals(fieldName)) {
            return locatedFeatures;
        }
        if ("locations".equals(fieldName)) {
            return locations;
        }
        if ("ontologyAnnotations".equals(fieldName)) {
            return ontologyAnnotations;
        }
        if ("synonyms".equals(fieldName)) {
            return synonyms;
        }
        if ("dataSets".equals(fieldName)) {
            return dataSets;
        }
        if ("publications".equals(fieldName)) {
            return publications;
        }
        if ("crossReferences".equals(fieldName)) {
            return crossReferences;
        }
        if ("id".equals(fieldName)) {
            return id;
        }
        if (!org.intermine.model.bio.RNAClone.class.equals(getClass())) {
            return TypeUtil.getFieldValue(this, fieldName);
        }
        throw new IllegalArgumentException("Unknown field " + fieldName);
    }
    public Object getFieldProxy(final String fieldName) throws IllegalAccessException {
        if ("expressions".equals(fieldName)) {
            return expressions;
        }
        if ("insertSize".equals(fieldName)) {
            return insertSize;
        }
        if ("PCR".equals(fieldName)) {
            return PCR;
        }
        if ("digest".equals(fieldName)) {
            return digest;
        }
        if ("ThisseCloneRating".equals(fieldName)) {
            return ThisseCloneRating;
        }
        if ("polymerase".equals(fieldName)) {
            return polymerase;
        }
        if ("cloneSite".equals(fieldName)) {
            return cloneSite;
        }
        if ("type".equals(fieldName)) {
            return type;
        }
        if ("comments".equals(fieldName)) {
            return comments;
        }
        if ("sequenceType".equals(fieldName)) {
            return sequenceType;
        }
        if ("vector".equals(fieldName)) {
            return vector;
        }
        if ("problemType".equals(fieldName)) {
            return problemType;
        }
        if ("probeLibrary".equals(fieldName)) {
            return probeLibrary;
        }
        if ("contains".equals(fieldName)) {
            return contains;
        }
        if ("genes".equals(fieldName)) {
            return genes;
        }
        if ("artifacts".equals(fieldName)) {
            return artifacts;
        }
        if ("clones".equals(fieldName)) {
            return clones;
        }
        if ("transcripts".equals(fieldName)) {
            return transcripts;
        }
        if ("length".equals(fieldName)) {
            return length;
        }
        if ("scoreType".equals(fieldName)) {
            return scoreType;
        }
        if ("score".equals(fieldName)) {
            return score;
        }
        if ("chromosome".equals(fieldName)) {
            return chromosome;
        }
        if ("chromosomeLocation".equals(fieldName)) {
            return chromosomeLocation;
        }
        if ("sequence".equals(fieldName)) {
            return sequence;
        }
        if ("sequenceOntologyTerm".equals(fieldName)) {
            return sequenceOntologyTerm;
        }
        if ("presentIn".equals(fieldName)) {
            return presentIn;
        }
        if ("labOfOrigin".equals(fieldName)) {
            return labOfOrigin;
        }
        if ("missingFrom".equals(fieldName)) {
            return missingFrom;
        }
        if ("movedIn".equals(fieldName)) {
            return movedIn;
        }
        if ("overlappingFeatures".equals(fieldName)) {
            return overlappingFeatures;
        }
        if ("secondaryIdentifier".equals(fieldName)) {
            return secondaryIdentifier;
        }
        if ("symbol".equals(fieldName)) {
            return symbol;
        }
        if ("primaryIdentifier".equals(fieldName)) {
            return primaryIdentifier;
        }
        if ("name".equals(fieldName)) {
            return name;
        }
        if ("organism".equals(fieldName)) {
            return organism;
        }
        if ("locatedFeatures".equals(fieldName)) {
            return locatedFeatures;
        }
        if ("locations".equals(fieldName)) {
            return locations;
        }
        if ("ontologyAnnotations".equals(fieldName)) {
            return ontologyAnnotations;
        }
        if ("synonyms".equals(fieldName)) {
            return synonyms;
        }
        if ("dataSets".equals(fieldName)) {
            return dataSets;
        }
        if ("publications".equals(fieldName)) {
            return publications;
        }
        if ("crossReferences".equals(fieldName)) {
            return crossReferences;
        }
        if ("id".equals(fieldName)) {
            return id;
        }
        if (!org.intermine.model.bio.RNAClone.class.equals(getClass())) {
            return TypeUtil.getFieldProxy(this, fieldName);
        }
        throw new IllegalArgumentException("Unknown field " + fieldName);
    }
    public void setFieldValue(final String fieldName, final Object value) {
        if ("expressions".equals(fieldName)) {
            expressions = (java.util.Set) value;
        } else if ("insertSize".equals(fieldName)) {
            insertSize = (java.lang.String) value;
        } else if ("PCR".equals(fieldName)) {
            PCR = (java.lang.String) value;
        } else if ("digest".equals(fieldName)) {
            digest = (java.lang.String) value;
        } else if ("ThisseCloneRating".equals(fieldName)) {
            ThisseCloneRating = (java.lang.String) value;
        } else if ("polymerase".equals(fieldName)) {
            polymerase = (java.lang.String) value;
        } else if ("cloneSite".equals(fieldName)) {
            cloneSite = (java.lang.String) value;
        } else if ("type".equals(fieldName)) {
            type = (java.lang.String) value;
        } else if ("comments".equals(fieldName)) {
            comments = (java.lang.String) value;
        } else if ("sequenceType".equals(fieldName)) {
            sequenceType = (java.lang.String) value;
        } else if ("vector".equals(fieldName)) {
            vector = (java.lang.String) value;
        } else if ("problemType".equals(fieldName)) {
            problemType = (java.lang.String) value;
        } else if ("probeLibrary".equals(fieldName)) {
            probeLibrary = (org.intermine.model.InterMineObject) value;
        } else if ("contains".equals(fieldName)) {
            contains = (java.util.Set) value;
        } else if ("genes".equals(fieldName)) {
            genes = (java.util.Set) value;
        } else if ("artifacts".equals(fieldName)) {
            artifacts = (java.util.Set) value;
        } else if ("clones".equals(fieldName)) {
            clones = (java.util.Set) value;
        } else if ("transcripts".equals(fieldName)) {
            transcripts = (java.util.Set) value;
        } else if ("length".equals(fieldName)) {
            length = (java.lang.Integer) value;
        } else if ("scoreType".equals(fieldName)) {
            scoreType = (java.lang.String) value;
        } else if ("score".equals(fieldName)) {
            score = (java.lang.Double) value;
        } else if ("chromosome".equals(fieldName)) {
            chromosome = (org.intermine.model.InterMineObject) value;
        } else if ("chromosomeLocation".equals(fieldName)) {
            chromosomeLocation = (org.intermine.model.InterMineObject) value;
        } else if ("sequence".equals(fieldName)) {
            sequence = (org.intermine.model.InterMineObject) value;
        } else if ("sequenceOntologyTerm".equals(fieldName)) {
            sequenceOntologyTerm = (org.intermine.model.InterMineObject) value;
        } else if ("presentIn".equals(fieldName)) {
            presentIn = (java.util.Set) value;
        } else if ("labOfOrigin".equals(fieldName)) {
            labOfOrigin = (java.util.Set) value;
        } else if ("missingFrom".equals(fieldName)) {
            missingFrom = (java.util.Set) value;
        } else if ("movedIn".equals(fieldName)) {
            movedIn = (java.util.Set) value;
        } else if ("overlappingFeatures".equals(fieldName)) {
            overlappingFeatures = (java.util.Set) value;
        } else if ("secondaryIdentifier".equals(fieldName)) {
            secondaryIdentifier = (java.lang.String) value;
        } else if ("symbol".equals(fieldName)) {
            symbol = (java.lang.String) value;
        } else if ("primaryIdentifier".equals(fieldName)) {
            primaryIdentifier = (java.lang.String) value;
        } else if ("name".equals(fieldName)) {
            name = (java.lang.String) value;
        } else if ("organism".equals(fieldName)) {
            organism = (org.intermine.model.InterMineObject) value;
        } else if ("locatedFeatures".equals(fieldName)) {
            locatedFeatures = (java.util.Set) value;
        } else if ("locations".equals(fieldName)) {
            locations = (java.util.Set) value;
        } else if ("ontologyAnnotations".equals(fieldName)) {
            ontologyAnnotations = (java.util.Set) value;
        } else if ("synonyms".equals(fieldName)) {
            synonyms = (java.util.Set) value;
        } else if ("dataSets".equals(fieldName)) {
            dataSets = (java.util.Set) value;
        } else if ("publications".equals(fieldName)) {
            publications = (java.util.Set) value;
        } else if ("crossReferences".equals(fieldName)) {
            crossReferences = (java.util.Set) value;
        } else if ("id".equals(fieldName)) {
            id = (java.lang.Integer) value;
        } else {
            if (!org.intermine.model.bio.RNAClone.class.equals(getClass())) {
                TypeUtil.setFieldValue(this, fieldName, value);
                return;
            }
            throw new IllegalArgumentException("Unknown field " + fieldName);
        }
    }
    public Class<?> getFieldType(final String fieldName) {
        if ("expressions".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("insertSize".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("PCR".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("digest".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("ThisseCloneRating".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("polymerase".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("cloneSite".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("type".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("comments".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("sequenceType".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("vector".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("problemType".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("probeLibrary".equals(fieldName)) {
            return org.intermine.model.bio.ProbeLibrary.class;
        }
        if ("contains".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("genes".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("artifacts".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("clones".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("transcripts".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("length".equals(fieldName)) {
            return java.lang.Integer.class;
        }
        if ("scoreType".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("score".equals(fieldName)) {
            return java.lang.Double.class;
        }
        if ("chromosome".equals(fieldName)) {
            return org.intermine.model.bio.Chromosome.class;
        }
        if ("chromosomeLocation".equals(fieldName)) {
            return org.intermine.model.bio.Location.class;
        }
        if ("sequence".equals(fieldName)) {
            return org.intermine.model.bio.Sequence.class;
        }
        if ("sequenceOntologyTerm".equals(fieldName)) {
            return org.intermine.model.bio.SOTerm.class;
        }
        if ("presentIn".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("labOfOrigin".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("missingFrom".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("movedIn".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("overlappingFeatures".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("secondaryIdentifier".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("symbol".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("primaryIdentifier".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("name".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("organism".equals(fieldName)) {
            return org.intermine.model.bio.Organism.class;
        }
        if ("locatedFeatures".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("locations".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("ontologyAnnotations".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("synonyms".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("dataSets".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("publications".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("crossReferences".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("id".equals(fieldName)) {
            return java.lang.Integer.class;
        }
        if (!org.intermine.model.bio.RNAClone.class.equals(getClass())) {
            return TypeUtil.getFieldType(org.intermine.model.bio.RNAClone.class, fieldName);
        }
        throw new IllegalArgumentException("Unknown field " + fieldName);
    }
    public StringConstructor getoBJECT() {
        if (!org.intermine.model.bio.RNACloneShadow.class.equals(getClass())) {
            return NotXmlRenderer.render(this);
        }
        StringConstructor sb = new StringConstructor();
        sb.append("$_^org.intermine.model.bio.RNAClone");
        if (insertSize != null) {
            sb.append("$_^ainsertSize$_^");
            String string = insertSize;
            while (string != null) {
                int delimPosition = string.indexOf("$_^");
                if (delimPosition == -1) {
                    sb.append(string);
                    string = null;
                } else {
                    sb.append(string.substring(0, delimPosition + 3));
                    sb.append("d");
                    string = string.substring(delimPosition + 3);
                }
            }
        }
        if (PCR != null) {
            sb.append("$_^aPCR$_^");
            String string = PCR;
            while (string != null) {
                int delimPosition = string.indexOf("$_^");
                if (delimPosition == -1) {
                    sb.append(string);
                    string = null;
                } else {
                    sb.append(string.substring(0, delimPosition + 3));
                    sb.append("d");
                    string = string.substring(delimPosition + 3);
                }
            }
        }
        if (digest != null) {
            sb.append("$_^adigest$_^");
            String string = digest;
            while (string != null) {
                int delimPosition = string.indexOf("$_^");
                if (delimPosition == -1) {
                    sb.append(string);
                    string = null;
                } else {
                    sb.append(string.substring(0, delimPosition + 3));
                    sb.append("d");
                    string = string.substring(delimPosition + 3);
                }
            }
        }
        if (ThisseCloneRating != null) {
            sb.append("$_^aThisseCloneRating$_^");
            String string = ThisseCloneRating;
            while (string != null) {
                int delimPosition = string.indexOf("$_^");
                if (delimPosition == -1) {
                    sb.append(string);
                    string = null;
                } else {
                    sb.append(string.substring(0, delimPosition + 3));
                    sb.append("d");
                    string = string.substring(delimPosition + 3);
                }
            }
        }
        if (polymerase != null) {
            sb.append("$_^apolymerase$_^");
            String string = polymerase;
            while (string != null) {
                int delimPosition = string.indexOf("$_^");
                if (delimPosition == -1) {
                    sb.append(string);
                    string = null;
                } else {
                    sb.append(string.substring(0, delimPosition + 3));
                    sb.append("d");
                    string = string.substring(delimPosition + 3);
                }
            }
        }
        if (cloneSite != null) {
            sb.append("$_^acloneSite$_^");
            String string = cloneSite;
            while (string != null) {
                int delimPosition = string.indexOf("$_^");
                if (delimPosition == -1) {
                    sb.append(string);
                    string = null;
                } else {
                    sb.append(string.substring(0, delimPosition + 3));
                    sb.append("d");
                    string = string.substring(delimPosition + 3);
                }
            }
        }
        if (type != null) {
            sb.append("$_^atype$_^");
            String string = type;
            while (string != null) {
                int delimPosition = string.indexOf("$_^");
                if (delimPosition == -1) {
                    sb.append(string);
                    string = null;
                } else {
                    sb.append(string.substring(0, delimPosition + 3));
                    sb.append("d");
                    string = string.substring(delimPosition + 3);
                }
            }
        }
        if (comments != null) {
            sb.append("$_^acomments$_^");
            String string = comments;
            while (string != null) {
                int delimPosition = string.indexOf("$_^");
                if (delimPosition == -1) {
                    sb.append(string);
                    string = null;
                } else {
                    sb.append(string.substring(0, delimPosition + 3));
                    sb.append("d");
                    string = string.substring(delimPosition + 3);
                }
            }
        }
        if (sequenceType != null) {
            sb.append("$_^asequenceType$_^");
            String string = sequenceType;
            while (string != null) {
                int delimPosition = string.indexOf("$_^");
                if (delimPosition == -1) {
                    sb.append(string);
                    string = null;
                } else {
                    sb.append(string.substring(0, delimPosition + 3));
                    sb.append("d");
                    string = string.substring(delimPosition + 3);
                }
            }
        }
        if (vector != null) {
            sb.append("$_^avector$_^");
            String string = vector;
            while (string != null) {
                int delimPosition = string.indexOf("$_^");
                if (delimPosition == -1) {
                    sb.append(string);
                    string = null;
                } else {
                    sb.append(string.substring(0, delimPosition + 3));
                    sb.append("d");
                    string = string.substring(delimPosition + 3);
                }
            }
        }
        if (problemType != null) {
            sb.append("$_^aproblemType$_^");
            String string = problemType;
            while (string != null) {
                int delimPosition = string.indexOf("$_^");
                if (delimPosition == -1) {
                    sb.append(string);
                    string = null;
                } else {
                    sb.append(string.substring(0, delimPosition + 3));
                    sb.append("d");
                    string = string.substring(delimPosition + 3);
                }
            }
        }
        if (probeLibrary != null) {
            sb.append("$_^rprobeLibrary$_^").append(probeLibrary.getId());
        }
        if (length != null) {
            sb.append("$_^alength$_^").append(length);
        }
        if (scoreType != null) {
            sb.append("$_^ascoreType$_^");
            String string = scoreType;
            while (string != null) {
                int delimPosition = string.indexOf("$_^");
                if (delimPosition == -1) {
                    sb.append(string);
                    string = null;
                } else {
                    sb.append(string.substring(0, delimPosition + 3));
                    sb.append("d");
                    string = string.substring(delimPosition + 3);
                }
            }
        }
        if (score != null) {
            sb.append("$_^ascore$_^").append(score);
        }
        if (chromosome != null) {
            sb.append("$_^rchromosome$_^").append(chromosome.getId());
        }
        if (chromosomeLocation != null) {
            sb.append("$_^rchromosomeLocation$_^").append(chromosomeLocation.getId());
        }
        if (sequence != null) {
            sb.append("$_^rsequence$_^").append(sequence.getId());
        }
        if (sequenceOntologyTerm != null) {
            sb.append("$_^rsequenceOntologyTerm$_^").append(sequenceOntologyTerm.getId());
        }
        if (secondaryIdentifier != null) {
            sb.append("$_^asecondaryIdentifier$_^");
            String string = secondaryIdentifier;
            while (string != null) {
                int delimPosition = string.indexOf("$_^");
                if (delimPosition == -1) {
                    sb.append(string);
                    string = null;
                } else {
                    sb.append(string.substring(0, delimPosition + 3));
                    sb.append("d");
                    string = string.substring(delimPosition + 3);
                }
            }
        }
        if (symbol != null) {
            sb.append("$_^asymbol$_^");
            String string = symbol;
            while (string != null) {
                int delimPosition = string.indexOf("$_^");
                if (delimPosition == -1) {
                    sb.append(string);
                    string = null;
                } else {
                    sb.append(string.substring(0, delimPosition + 3));
                    sb.append("d");
                    string = string.substring(delimPosition + 3);
                }
            }
        }
        if (primaryIdentifier != null) {
            sb.append("$_^aprimaryIdentifier$_^");
            String string = primaryIdentifier;
            while (string != null) {
                int delimPosition = string.indexOf("$_^");
                if (delimPosition == -1) {
                    sb.append(string);
                    string = null;
                } else {
                    sb.append(string.substring(0, delimPosition + 3));
                    sb.append("d");
                    string = string.substring(delimPosition + 3);
                }
            }
        }
        if (name != null) {
            sb.append("$_^aname$_^");
            String string = name;
            while (string != null) {
                int delimPosition = string.indexOf("$_^");
                if (delimPosition == -1) {
                    sb.append(string);
                    string = null;
                } else {
                    sb.append(string.substring(0, delimPosition + 3));
                    sb.append("d");
                    string = string.substring(delimPosition + 3);
                }
            }
        }
        if (organism != null) {
            sb.append("$_^rorganism$_^").append(organism.getId());
        }
        if (id != null) {
            sb.append("$_^aid$_^").append(id);
        }
        return sb;
    }
    public void setoBJECT(String notXml, ObjectStore os) {
        setoBJECT(NotXmlParser.SPLITTER.split(notXml), os);
    }
    public void setoBJECT(final String[] notXml, final ObjectStore os) {
        if (!org.intermine.model.bio.RNACloneShadow.class.equals(getClass())) {
            throw new IllegalStateException("Class " + getClass().getName() + " does not match code (org.intermine.model.bio.RNAClone)");
        }
        for (int i = 2; i < notXml.length;) {
            int startI = i;
            if ((i < notXml.length) && "ainsertSize".equals(notXml[i])) {
                i++;
                StringBuilder string = null;
                while ((i + 1 < notXml.length) && (notXml[i + 1].charAt(0) == 'd')) {
                    if (string == null) string = new StringBuilder(notXml[i]);
                    i++;
                    string.append("$_^").append(notXml[i].substring(1));
                }
                insertSize = string == null ? notXml[i] : string.toString();
                i++;
            }
            if ((i < notXml.length) && "aPCR".equals(notXml[i])) {
                i++;
                StringBuilder string = null;
                while ((i + 1 < notXml.length) && (notXml[i + 1].charAt(0) == 'd')) {
                    if (string == null) string = new StringBuilder(notXml[i]);
                    i++;
                    string.append("$_^").append(notXml[i].substring(1));
                }
                PCR = string == null ? notXml[i] : string.toString();
                i++;
            }
            if ((i < notXml.length) && "adigest".equals(notXml[i])) {
                i++;
                StringBuilder string = null;
                while ((i + 1 < notXml.length) && (notXml[i + 1].charAt(0) == 'd')) {
                    if (string == null) string = new StringBuilder(notXml[i]);
                    i++;
                    string.append("$_^").append(notXml[i].substring(1));
                }
                digest = string == null ? notXml[i] : string.toString();
                i++;
            }
            if ((i < notXml.length) && "aThisseCloneRating".equals(notXml[i])) {
                i++;
                StringBuilder string = null;
                while ((i + 1 < notXml.length) && (notXml[i + 1].charAt(0) == 'd')) {
                    if (string == null) string = new StringBuilder(notXml[i]);
                    i++;
                    string.append("$_^").append(notXml[i].substring(1));
                }
                ThisseCloneRating = string == null ? notXml[i] : string.toString();
                i++;
            }
            if ((i < notXml.length) && "apolymerase".equals(notXml[i])) {
                i++;
                StringBuilder string = null;
                while ((i + 1 < notXml.length) && (notXml[i + 1].charAt(0) == 'd')) {
                    if (string == null) string = new StringBuilder(notXml[i]);
                    i++;
                    string.append("$_^").append(notXml[i].substring(1));
                }
                polymerase = string == null ? notXml[i] : string.toString();
                i++;
            }
            if ((i < notXml.length) && "acloneSite".equals(notXml[i])) {
                i++;
                StringBuilder string = null;
                while ((i + 1 < notXml.length) && (notXml[i + 1].charAt(0) == 'd')) {
                    if (string == null) string = new StringBuilder(notXml[i]);
                    i++;
                    string.append("$_^").append(notXml[i].substring(1));
                }
                cloneSite = string == null ? notXml[i] : string.toString();
                i++;
            }
            if ((i < notXml.length) && "atype".equals(notXml[i])) {
                i++;
                StringBuilder string = null;
                while ((i + 1 < notXml.length) && (notXml[i + 1].charAt(0) == 'd')) {
                    if (string == null) string = new StringBuilder(notXml[i]);
                    i++;
                    string.append("$_^").append(notXml[i].substring(1));
                }
                type = string == null ? notXml[i] : string.toString();
                i++;
            }
            if ((i < notXml.length) && "acomments".equals(notXml[i])) {
                i++;
                StringBuilder string = null;
                while ((i + 1 < notXml.length) && (notXml[i + 1].charAt(0) == 'd')) {
                    if (string == null) string = new StringBuilder(notXml[i]);
                    i++;
                    string.append("$_^").append(notXml[i].substring(1));
                }
                comments = string == null ? notXml[i] : string.toString();
                i++;
            }
            if ((i < notXml.length) && "asequenceType".equals(notXml[i])) {
                i++;
                StringBuilder string = null;
                while ((i + 1 < notXml.length) && (notXml[i + 1].charAt(0) == 'd')) {
                    if (string == null) string = new StringBuilder(notXml[i]);
                    i++;
                    string.append("$_^").append(notXml[i].substring(1));
                }
                sequenceType = string == null ? notXml[i] : string.toString();
                i++;
            }
            if ((i < notXml.length) && "avector".equals(notXml[i])) {
                i++;
                StringBuilder string = null;
                while ((i + 1 < notXml.length) && (notXml[i + 1].charAt(0) == 'd')) {
                    if (string == null) string = new StringBuilder(notXml[i]);
                    i++;
                    string.append("$_^").append(notXml[i].substring(1));
                }
                vector = string == null ? notXml[i] : string.toString();
                i++;
            }
            if ((i < notXml.length) && "aproblemType".equals(notXml[i])) {
                i++;
                StringBuilder string = null;
                while ((i + 1 < notXml.length) && (notXml[i + 1].charAt(0) == 'd')) {
                    if (string == null) string = new StringBuilder(notXml[i]);
                    i++;
                    string.append("$_^").append(notXml[i].substring(1));
                }
                problemType = string == null ? notXml[i] : string.toString();
                i++;
            }
            if ((i < notXml.length) &&"rprobeLibrary".equals(notXml[i])) {
                i++;
                probeLibrary = new ProxyReference(os, Integer.valueOf(notXml[i]), org.intermine.model.bio.ProbeLibrary.class);
                i++;
            };
            if ((i < notXml.length) && "alength".equals(notXml[i])) {
                i++;
                length = Integer.valueOf(notXml[i]);
                i++;
            }
            if ((i < notXml.length) && "ascoreType".equals(notXml[i])) {
                i++;
                StringBuilder string = null;
                while ((i + 1 < notXml.length) && (notXml[i + 1].charAt(0) == 'd')) {
                    if (string == null) string = new StringBuilder(notXml[i]);
                    i++;
                    string.append("$_^").append(notXml[i].substring(1));
                }
                scoreType = string == null ? notXml[i] : string.toString();
                i++;
            }
            if ((i < notXml.length) && "ascore".equals(notXml[i])) {
                i++;
                score = Double.valueOf(notXml[i]);
                i++;
            }
            if ((i < notXml.length) &&"rchromosome".equals(notXml[i])) {
                i++;
                chromosome = new ProxyReference(os, Integer.valueOf(notXml[i]), org.intermine.model.bio.Chromosome.class);
                i++;
            };
            if ((i < notXml.length) &&"rchromosomeLocation".equals(notXml[i])) {
                i++;
                chromosomeLocation = new ProxyReference(os, Integer.valueOf(notXml[i]), org.intermine.model.bio.Location.class);
                i++;
            };
            if ((i < notXml.length) &&"rsequence".equals(notXml[i])) {
                i++;
                sequence = new ProxyReference(os, Integer.valueOf(notXml[i]), org.intermine.model.bio.Sequence.class);
                i++;
            };
            if ((i < notXml.length) &&"rsequenceOntologyTerm".equals(notXml[i])) {
                i++;
                sequenceOntologyTerm = new ProxyReference(os, Integer.valueOf(notXml[i]), org.intermine.model.bio.SOTerm.class);
                i++;
            };
            if ((i < notXml.length) && "asecondaryIdentifier".equals(notXml[i])) {
                i++;
                StringBuilder string = null;
                while ((i + 1 < notXml.length) && (notXml[i + 1].charAt(0) == 'd')) {
                    if (string == null) string = new StringBuilder(notXml[i]);
                    i++;
                    string.append("$_^").append(notXml[i].substring(1));
                }
                secondaryIdentifier = string == null ? notXml[i] : string.toString();
                i++;
            }
            if ((i < notXml.length) && "asymbol".equals(notXml[i])) {
                i++;
                StringBuilder string = null;
                while ((i + 1 < notXml.length) && (notXml[i + 1].charAt(0) == 'd')) {
                    if (string == null) string = new StringBuilder(notXml[i]);
                    i++;
                    string.append("$_^").append(notXml[i].substring(1));
                }
                symbol = string == null ? notXml[i] : string.toString();
                i++;
            }
            if ((i < notXml.length) && "aprimaryIdentifier".equals(notXml[i])) {
                i++;
                StringBuilder string = null;
                while ((i + 1 < notXml.length) && (notXml[i + 1].charAt(0) == 'd')) {
                    if (string == null) string = new StringBuilder(notXml[i]);
                    i++;
                    string.append("$_^").append(notXml[i].substring(1));
                }
                primaryIdentifier = string == null ? notXml[i] : string.toString();
                i++;
            }
            if ((i < notXml.length) && "aname".equals(notXml[i])) {
                i++;
                StringBuilder string = null;
                while ((i + 1 < notXml.length) && (notXml[i + 1].charAt(0) == 'd')) {
                    if (string == null) string = new StringBuilder(notXml[i]);
                    i++;
                    string.append("$_^").append(notXml[i].substring(1));
                }
                name = string == null ? notXml[i] : string.toString();
                i++;
            }
            if ((i < notXml.length) &&"rorganism".equals(notXml[i])) {
                i++;
                organism = new ProxyReference(os, Integer.valueOf(notXml[i]), org.intermine.model.bio.Organism.class);
                i++;
            };
            if ((i < notXml.length) && "aid".equals(notXml[i])) {
                i++;
                id = Integer.valueOf(notXml[i]);
                i++;
            }
            if (startI == i) {
                throw new IllegalArgumentException("Unknown field " + notXml[i]);
            }
        }
        expressions = new ProxyCollection<org.intermine.model.bio.ExpressionResult>(os, this, "expressions", org.intermine.model.bio.ExpressionResult.class);
        contains = new ProxyCollection<org.intermine.model.bio.Clone>(os, this, "contains", org.intermine.model.bio.Clone.class);
        genes = new ProxyCollection<org.intermine.model.bio.Gene>(os, this, "genes", org.intermine.model.bio.Gene.class);
        artifacts = new ProxyCollection<org.intermine.model.bio.Gene>(os, this, "artifacts", org.intermine.model.bio.Gene.class);
        clones = new ProxyCollection<org.intermine.model.bio.Clone>(os, this, "clones", org.intermine.model.bio.Clone.class);
        transcripts = new ProxyCollection<org.intermine.model.bio.Transcript>(os, this, "transcripts", org.intermine.model.bio.Transcript.class);
        presentIn = new ProxyCollection<org.intermine.model.bio.SequenceAlteration>(os, this, "presentIn", org.intermine.model.bio.SequenceAlteration.class);
        labOfOrigin = new ProxyCollection<org.intermine.model.bio.Lab>(os, this, "labOfOrigin", org.intermine.model.bio.Lab.class);
        missingFrom = new ProxyCollection<org.intermine.model.bio.SequenceAlteration>(os, this, "missingFrom", org.intermine.model.bio.SequenceAlteration.class);
        movedIn = new ProxyCollection<org.intermine.model.bio.SequenceAlteration>(os, this, "movedIn", org.intermine.model.bio.SequenceAlteration.class);
        overlappingFeatures = new ProxyCollection<org.intermine.model.bio.SequenceFeature>(os, this, "overlappingFeatures", org.intermine.model.bio.SequenceFeature.class);
        locatedFeatures = new ProxyCollection<org.intermine.model.bio.Location>(os, this, "locatedFeatures", org.intermine.model.bio.Location.class);
        locations = new ProxyCollection<org.intermine.model.bio.Location>(os, this, "locations", org.intermine.model.bio.Location.class);
        ontologyAnnotations = new ProxyCollection<org.intermine.model.bio.OntologyAnnotation>(os, this, "ontologyAnnotations", org.intermine.model.bio.OntologyAnnotation.class);
        synonyms = new ProxyCollection<org.intermine.model.bio.Synonym>(os, this, "synonyms", org.intermine.model.bio.Synonym.class);
        dataSets = new ProxyCollection<org.intermine.model.bio.DataSet>(os, this, "dataSets", org.intermine.model.bio.DataSet.class);
        publications = new ProxyCollection<org.intermine.model.bio.Publication>(os, this, "publications", org.intermine.model.bio.Publication.class);
        crossReferences = new ProxyCollection<org.intermine.model.bio.CrossReference>(os, this, "crossReferences", org.intermine.model.bio.CrossReference.class);
    }
    public void addCollectionElement(final String fieldName, final org.intermine.model.InterMineObject element) {
        if ("expressions".equals(fieldName)) {
            expressions.add((org.intermine.model.bio.ExpressionResult) element);
        } else if ("contains".equals(fieldName)) {
            contains.add((org.intermine.model.bio.Clone) element);
        } else if ("genes".equals(fieldName)) {
            genes.add((org.intermine.model.bio.Gene) element);
        } else if ("artifacts".equals(fieldName)) {
            artifacts.add((org.intermine.model.bio.Gene) element);
        } else if ("clones".equals(fieldName)) {
            clones.add((org.intermine.model.bio.Clone) element);
        } else if ("transcripts".equals(fieldName)) {
            transcripts.add((org.intermine.model.bio.Transcript) element);
        } else if ("presentIn".equals(fieldName)) {
            presentIn.add((org.intermine.model.bio.SequenceAlteration) element);
        } else if ("labOfOrigin".equals(fieldName)) {
            labOfOrigin.add((org.intermine.model.bio.Lab) element);
        } else if ("missingFrom".equals(fieldName)) {
            missingFrom.add((org.intermine.model.bio.SequenceAlteration) element);
        } else if ("movedIn".equals(fieldName)) {
            movedIn.add((org.intermine.model.bio.SequenceAlteration) element);
        } else if ("overlappingFeatures".equals(fieldName)) {
            overlappingFeatures.add((org.intermine.model.bio.SequenceFeature) element);
        } else if ("locatedFeatures".equals(fieldName)) {
            locatedFeatures.add((org.intermine.model.bio.Location) element);
        } else if ("locations".equals(fieldName)) {
            locations.add((org.intermine.model.bio.Location) element);
        } else if ("ontologyAnnotations".equals(fieldName)) {
            ontologyAnnotations.add((org.intermine.model.bio.OntologyAnnotation) element);
        } else if ("synonyms".equals(fieldName)) {
            synonyms.add((org.intermine.model.bio.Synonym) element);
        } else if ("dataSets".equals(fieldName)) {
            dataSets.add((org.intermine.model.bio.DataSet) element);
        } else if ("publications".equals(fieldName)) {
            publications.add((org.intermine.model.bio.Publication) element);
        } else if ("crossReferences".equals(fieldName)) {
            crossReferences.add((org.intermine.model.bio.CrossReference) element);
        } else {
            if (!org.intermine.model.bio.RNAClone.class.equals(getClass())) {
                TypeUtil.addCollectionElement(this, fieldName, element);
                return;
            }
            throw new IllegalArgumentException("Unknown collection " + fieldName);
        }
    }
    public Class<?> getElementType(final String fieldName) {
        if ("expressions".equals(fieldName)) {
            return org.intermine.model.bio.ExpressionResult.class;
        }
        if ("contains".equals(fieldName)) {
            return org.intermine.model.bio.Clone.class;
        }
        if ("genes".equals(fieldName)) {
            return org.intermine.model.bio.Gene.class;
        }
        if ("artifacts".equals(fieldName)) {
            return org.intermine.model.bio.Gene.class;
        }
        if ("clones".equals(fieldName)) {
            return org.intermine.model.bio.Clone.class;
        }
        if ("transcripts".equals(fieldName)) {
            return org.intermine.model.bio.Transcript.class;
        }
        if ("presentIn".equals(fieldName)) {
            return org.intermine.model.bio.SequenceAlteration.class;
        }
        if ("labOfOrigin".equals(fieldName)) {
            return org.intermine.model.bio.Lab.class;
        }
        if ("missingFrom".equals(fieldName)) {
            return org.intermine.model.bio.SequenceAlteration.class;
        }
        if ("movedIn".equals(fieldName)) {
            return org.intermine.model.bio.SequenceAlteration.class;
        }
        if ("overlappingFeatures".equals(fieldName)) {
            return org.intermine.model.bio.SequenceFeature.class;
        }
        if ("locatedFeatures".equals(fieldName)) {
            return org.intermine.model.bio.Location.class;
        }
        if ("locations".equals(fieldName)) {
            return org.intermine.model.bio.Location.class;
        }
        if ("ontologyAnnotations".equals(fieldName)) {
            return org.intermine.model.bio.OntologyAnnotation.class;
        }
        if ("synonyms".equals(fieldName)) {
            return org.intermine.model.bio.Synonym.class;
        }
        if ("dataSets".equals(fieldName)) {
            return org.intermine.model.bio.DataSet.class;
        }
        if ("publications".equals(fieldName)) {
            return org.intermine.model.bio.Publication.class;
        }
        if ("crossReferences".equals(fieldName)) {
            return org.intermine.model.bio.CrossReference.class;
        }
        if (!org.intermine.model.bio.RNAClone.class.equals(getClass())) {
            return TypeUtil.getElementType(org.intermine.model.bio.RNAClone.class, fieldName);
        }
        throw new IllegalArgumentException("Unknown field " + fieldName);
    }
}
