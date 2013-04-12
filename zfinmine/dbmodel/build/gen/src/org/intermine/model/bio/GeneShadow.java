package org.intermine.model.bio;

import org.intermine.objectstore.ObjectStore;
import org.intermine.objectstore.intermine.NotXmlParser;
import org.intermine.objectstore.intermine.NotXmlRenderer;
import org.intermine.objectstore.proxy.ProxyCollection;
import org.intermine.objectstore.proxy.ProxyReference;
import org.intermine.util.StringConstructor;
import org.intermine.util.TypeUtil;
import org.intermine.model.ShadowClass;

public class GeneShadow implements Gene, ShadowClass
{
    public static final Class<Gene> shadowOf = Gene.class;
    // Attr: org.intermine.model.bio.Gene.type
    protected java.lang.String type;
    public java.lang.String getType() { return type; }
    public void setType(final java.lang.String type) { this.type = type; }

    // Attr: org.intermine.model.bio.Gene.briefDescription
    protected java.lang.String briefDescription;
    public java.lang.String getBriefDescription() { return briefDescription; }
    public void setBriefDescription(final java.lang.String briefDescription) { this.briefDescription = briefDescription; }

    // Attr: org.intermine.model.bio.Gene.description
    protected java.lang.String description;
    public java.lang.String getDescription() { return description; }
    public void setDescription(final java.lang.String description) { this.description = description; }

    // Ref: org.intermine.model.bio.Gene.downstreamIntergenicRegion
    protected org.intermine.model.InterMineObject downstreamIntergenicRegion;
    public org.intermine.model.bio.IntergenicRegion getDownstreamIntergenicRegion() { if (downstreamIntergenicRegion instanceof org.intermine.objectstore.proxy.ProxyReference) { return ((org.intermine.model.bio.IntergenicRegion) ((org.intermine.objectstore.proxy.ProxyReference) downstreamIntergenicRegion).getObject()); }; return (org.intermine.model.bio.IntergenicRegion) downstreamIntergenicRegion; }
    public void setDownstreamIntergenicRegion(final org.intermine.model.bio.IntergenicRegion downstreamIntergenicRegion) { this.downstreamIntergenicRegion = downstreamIntergenicRegion; }
    public void proxyDownstreamIntergenicRegion(final org.intermine.objectstore.proxy.ProxyReference downstreamIntergenicRegion) { this.downstreamIntergenicRegion = downstreamIntergenicRegion; }
    public org.intermine.model.InterMineObject proxGetDownstreamIntergenicRegion() { return downstreamIntergenicRegion; }

    // Ref: org.intermine.model.bio.Gene.upstreamIntergenicRegion
    protected org.intermine.model.InterMineObject upstreamIntergenicRegion;
    public org.intermine.model.bio.IntergenicRegion getUpstreamIntergenicRegion() { if (upstreamIntergenicRegion instanceof org.intermine.objectstore.proxy.ProxyReference) { return ((org.intermine.model.bio.IntergenicRegion) ((org.intermine.objectstore.proxy.ProxyReference) upstreamIntergenicRegion).getObject()); }; return (org.intermine.model.bio.IntergenicRegion) upstreamIntergenicRegion; }
    public void setUpstreamIntergenicRegion(final org.intermine.model.bio.IntergenicRegion upstreamIntergenicRegion) { this.upstreamIntergenicRegion = upstreamIntergenicRegion; }
    public void proxyUpstreamIntergenicRegion(final org.intermine.objectstore.proxy.ProxyReference upstreamIntergenicRegion) { this.upstreamIntergenicRegion = upstreamIntergenicRegion; }
    public org.intermine.model.InterMineObject proxGetUpstreamIntergenicRegion() { return upstreamIntergenicRegion; }

    // Col: org.intermine.model.bio.Gene.cloneArtifacts
    protected java.util.Set<org.intermine.model.bio.Clone> cloneArtifacts = new java.util.HashSet<org.intermine.model.bio.Clone>();
    public java.util.Set<org.intermine.model.bio.Clone> getCloneArtifacts() { return cloneArtifacts; }
    public void setCloneArtifacts(final java.util.Set<org.intermine.model.bio.Clone> cloneArtifacts) { this.cloneArtifacts = cloneArtifacts; }
    public void addCloneArtifacts(final org.intermine.model.bio.Clone arg) { cloneArtifacts.add(arg); }

    // Col: org.intermine.model.bio.Gene.goAnnotation
    protected java.util.Set<org.intermine.model.bio.GOAnnotation> goAnnotation = new java.util.HashSet<org.intermine.model.bio.GOAnnotation>();
    public java.util.Set<org.intermine.model.bio.GOAnnotation> getGoAnnotation() { return goAnnotation; }
    public void setGoAnnotation(final java.util.Set<org.intermine.model.bio.GOAnnotation> goAnnotation) { this.goAnnotation = goAnnotation; }
    public void addGoAnnotation(final org.intermine.model.bio.GOAnnotation arg) { goAnnotation.add(arg); }

    // Col: org.intermine.model.bio.Gene.transcripts
    protected java.util.Set<org.intermine.model.bio.Transcript> transcripts = new java.util.HashSet<org.intermine.model.bio.Transcript>();
    public java.util.Set<org.intermine.model.bio.Transcript> getTranscripts() { return transcripts; }
    public void setTranscripts(final java.util.Set<org.intermine.model.bio.Transcript> transcripts) { this.transcripts = transcripts; }
    public void addTranscripts(final org.intermine.model.bio.Transcript arg) { transcripts.add(arg); }

    // Col: org.intermine.model.bio.Gene.chromosomes
    protected java.util.Set<org.intermine.model.bio.Chromosome> chromosomes = new java.util.HashSet<org.intermine.model.bio.Chromosome>();
    public java.util.Set<org.intermine.model.bio.Chromosome> getChromosomes() { return chromosomes; }
    public void setChromosomes(final java.util.Set<org.intermine.model.bio.Chromosome> chromosomes) { this.chromosomes = chromosomes; }
    public void addChromosomes(final org.intermine.model.bio.Chromosome arg) { chromosomes.add(arg); }

    // Col: org.intermine.model.bio.Gene.CDSs
    protected java.util.Set<org.intermine.model.bio.CDS> CDSs = new java.util.HashSet<org.intermine.model.bio.CDS>();
    public java.util.Set<org.intermine.model.bio.CDS> getcDSs() { return CDSs; }
    public void setcDSs(final java.util.Set<org.intermine.model.bio.CDS> CDSs) { this.CDSs = CDSs; }
    public void addcDSs(final org.intermine.model.bio.CDS arg) { CDSs.add(arg); }

    // Col: org.intermine.model.bio.Gene.pathways
    protected java.util.Set<org.intermine.model.bio.Pathway> pathways = new java.util.HashSet<org.intermine.model.bio.Pathway>();
    public java.util.Set<org.intermine.model.bio.Pathway> getPathways() { return pathways; }
    public void setPathways(final java.util.Set<org.intermine.model.bio.Pathway> pathways) { this.pathways = pathways; }
    public void addPathways(final org.intermine.model.bio.Pathway arg) { pathways.add(arg); }

    // Col: org.intermine.model.bio.Gene.codingSequenceOf
    protected java.util.Set<org.intermine.model.bio.Construct> codingSequenceOf = new java.util.HashSet<org.intermine.model.bio.Construct>();
    public java.util.Set<org.intermine.model.bio.Construct> getCodingSequenceOf() { return codingSequenceOf; }
    public void setCodingSequenceOf(final java.util.Set<org.intermine.model.bio.Construct> codingSequenceOf) { this.codingSequenceOf = codingSequenceOf; }
    public void addCodingSequenceOf(final org.intermine.model.bio.Construct arg) { codingSequenceOf.add(arg); }

    // Col: org.intermine.model.bio.Gene.encodes
    protected java.util.Set<org.intermine.model.bio.Clone> encodes = new java.util.HashSet<org.intermine.model.bio.Clone>();
    public java.util.Set<org.intermine.model.bio.Clone> getEncodes() { return encodes; }
    public void setEncodes(final java.util.Set<org.intermine.model.bio.Clone> encodes) { this.encodes = encodes; }
    public void addEncodes(final org.intermine.model.bio.Clone arg) { encodes.add(arg); }

    // Col: org.intermine.model.bio.Gene.promotes
    protected java.util.Set<org.intermine.model.bio.Construct> promotes = new java.util.HashSet<org.intermine.model.bio.Construct>();
    public java.util.Set<org.intermine.model.bio.Construct> getPromotes() { return promotes; }
    public void setPromotes(final java.util.Set<org.intermine.model.bio.Construct> promotes) { this.promotes = promotes; }
    public void addPromotes(final org.intermine.model.bio.Construct arg) { promotes.add(arg); }

    // Col: org.intermine.model.bio.Gene.flankingRegions
    protected java.util.Set<org.intermine.model.bio.GeneFlankingRegion> flankingRegions = new java.util.HashSet<org.intermine.model.bio.GeneFlankingRegion>();
    public java.util.Set<org.intermine.model.bio.GeneFlankingRegion> getFlankingRegions() { return flankingRegions; }
    public void setFlankingRegions(final java.util.Set<org.intermine.model.bio.GeneFlankingRegion> flankingRegions) { this.flankingRegions = flankingRegions; }
    public void addFlankingRegions(final org.intermine.model.bio.GeneFlankingRegion arg) { flankingRegions.add(arg); }

    // Col: org.intermine.model.bio.Gene.antibodies
    protected java.util.Set<org.intermine.model.bio.Antibody> antibodies = new java.util.HashSet<org.intermine.model.bio.Antibody>();
    public java.util.Set<org.intermine.model.bio.Antibody> getAntibodies() { return antibodies; }
    public void setAntibodies(final java.util.Set<org.intermine.model.bio.Antibody> antibodies) { this.antibodies = antibodies; }
    public void addAntibodies(final org.intermine.model.bio.Antibody arg) { antibodies.add(arg); }

    // Col: org.intermine.model.bio.Gene.morpholinos
    protected java.util.Set<org.intermine.model.bio.MorpholinoOligo> morpholinos = new java.util.HashSet<org.intermine.model.bio.MorpholinoOligo>();
    public java.util.Set<org.intermine.model.bio.MorpholinoOligo> getMorpholinos() { return morpholinos; }
    public void setMorpholinos(final java.util.Set<org.intermine.model.bio.MorpholinoOligo> morpholinos) { this.morpholinos = morpholinos; }
    public void addMorpholinos(final org.intermine.model.bio.MorpholinoOligo arg) { morpholinos.add(arg); }

    // Col: org.intermine.model.bio.Gene.features
    protected java.util.Set<org.intermine.model.bio.SequenceAlteration> features = new java.util.HashSet<org.intermine.model.bio.SequenceAlteration>();
    public java.util.Set<org.intermine.model.bio.SequenceAlteration> getFeatures() { return features; }
    public void setFeatures(final java.util.Set<org.intermine.model.bio.SequenceAlteration> features) { this.features = features; }
    public void addFeatures(final org.intermine.model.bio.SequenceAlteration arg) { features.add(arg); }

    // Col: org.intermine.model.bio.Gene.expressions
    protected java.util.Set<org.intermine.model.bio.ExpressionResult> expressions = new java.util.HashSet<org.intermine.model.bio.ExpressionResult>();
    public java.util.Set<org.intermine.model.bio.ExpressionResult> getExpressions() { return expressions; }
    public void setExpressions(final java.util.Set<org.intermine.model.bio.ExpressionResult> expressions) { this.expressions = expressions; }
    public void addExpressions(final org.intermine.model.bio.ExpressionResult arg) { expressions.add(arg); }

    // Col: org.intermine.model.bio.Gene.proteins
    protected java.util.Set<org.intermine.model.bio.Protein> proteins = new java.util.HashSet<org.intermine.model.bio.Protein>();
    public java.util.Set<org.intermine.model.bio.Protein> getProteins() { return proteins; }
    public void setProteins(final java.util.Set<org.intermine.model.bio.Protein> proteins) { this.proteins = proteins; }
    public void addProteins(final org.intermine.model.bio.Protein arg) { proteins.add(arg); }

    // Col: org.intermine.model.bio.Gene.UTRs
    protected java.util.Set<org.intermine.model.bio.UTR> UTRs = new java.util.HashSet<org.intermine.model.bio.UTR>();
    public java.util.Set<org.intermine.model.bio.UTR> getuTRs() { return UTRs; }
    public void setuTRs(final java.util.Set<org.intermine.model.bio.UTR> UTRs) { this.UTRs = UTRs; }
    public void adduTRs(final org.intermine.model.bio.UTR arg) { UTRs.add(arg); }

    // Col: org.intermine.model.bio.Gene.homologues
    protected java.util.Set<org.intermine.model.bio.Homologue> homologues = new java.util.HashSet<org.intermine.model.bio.Homologue>();
    public java.util.Set<org.intermine.model.bio.Homologue> getHomologues() { return homologues; }
    public void setHomologues(final java.util.Set<org.intermine.model.bio.Homologue> homologues) { this.homologues = homologues; }
    public void addHomologues(final org.intermine.model.bio.Homologue arg) { homologues.add(arg); }

    // Col: org.intermine.model.bio.Gene.exons
    protected java.util.Set<org.intermine.model.bio.Exon> exons = new java.util.HashSet<org.intermine.model.bio.Exon>();
    public java.util.Set<org.intermine.model.bio.Exon> getExons() { return exons; }
    public void setExons(final java.util.Set<org.intermine.model.bio.Exon> exons) { this.exons = exons; }
    public void addExons(final org.intermine.model.bio.Exon arg) { exons.add(arg); }

    // Col: org.intermine.model.bio.Gene.introns
    protected java.util.Set<org.intermine.model.bio.Intron> introns = new java.util.HashSet<org.intermine.model.bio.Intron>();
    public java.util.Set<org.intermine.model.bio.Intron> getIntrons() { return introns; }
    public void setIntrons(final java.util.Set<org.intermine.model.bio.Intron> introns) { this.introns = introns; }
    public void addIntrons(final org.intermine.model.bio.Intron arg) { introns.add(arg); }

    // Col: org.intermine.model.bio.Gene.stss
    protected java.util.Set<org.intermine.model.bio.STS> stss = new java.util.HashSet<org.intermine.model.bio.STS>();
    public java.util.Set<org.intermine.model.bio.STS> getStss() { return stss; }
    public void setStss(final java.util.Set<org.intermine.model.bio.STS> stss) { this.stss = stss; }
    public void addStss(final org.intermine.model.bio.STS arg) { stss.add(arg); }

    // Col: org.intermine.model.bio.Gene.genotypes
    protected java.util.Set<org.intermine.model.bio.Genotype> genotypes = new java.util.HashSet<org.intermine.model.bio.Genotype>();
    public java.util.Set<org.intermine.model.bio.Genotype> getGenotypes() { return genotypes; }
    public void setGenotypes(final java.util.Set<org.intermine.model.bio.Genotype> genotypes) { this.genotypes = genotypes; }
    public void addGenotypes(final org.intermine.model.bio.Genotype arg) { genotypes.add(arg); }

    // Col: org.intermine.model.bio.Gene.snps
    protected java.util.Set<org.intermine.model.bio.SNP> snps = new java.util.HashSet<org.intermine.model.bio.SNP>();
    public java.util.Set<org.intermine.model.bio.SNP> getSnps() { return snps; }
    public void setSnps(final java.util.Set<org.intermine.model.bio.SNP> snps) { this.snps = snps; }
    public void addSnps(final org.intermine.model.bio.SNP arg) { snps.add(arg); }

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

    @Override public boolean equals(Object o) { return (o instanceof Gene && id != null) ? id.equals(((Gene)o).getId()) : this == o; }
    @Override public int hashCode() { return (id != null) ? id.hashCode() : super.hashCode(); }
    @Override public String toString() { return "Gene [briefDescription=\"" + briefDescription + "\", chromosome=" + (chromosome == null ? "null" : (chromosome.getId() == null ? "no id" : chromosome.getId().toString())) + ", chromosomeLocation=" + (chromosomeLocation == null ? "null" : (chromosomeLocation.getId() == null ? "no id" : chromosomeLocation.getId().toString())) + ", description=\"" + description + "\", downstreamIntergenicRegion=" + (downstreamIntergenicRegion == null ? "null" : (downstreamIntergenicRegion.getId() == null ? "no id" : downstreamIntergenicRegion.getId().toString())) + ", id=\"" + id + "\", length=\"" + length + "\", name=\"" + name + "\", organism=" + (organism == null ? "null" : (organism.getId() == null ? "no id" : organism.getId().toString())) + ", primaryIdentifier=\"" + primaryIdentifier + "\", score=\"" + score + "\", scoreType=\"" + scoreType + "\", secondaryIdentifier=\"" + secondaryIdentifier + "\", sequence=" + (sequence == null ? "null" : (sequence.getId() == null ? "no id" : sequence.getId().toString())) + ", sequenceOntologyTerm=" + (sequenceOntologyTerm == null ? "null" : (sequenceOntologyTerm.getId() == null ? "no id" : sequenceOntologyTerm.getId().toString())) + ", symbol=\"" + symbol + "\", type=\"" + type + "\", upstreamIntergenicRegion=" + (upstreamIntergenicRegion == null ? "null" : (upstreamIntergenicRegion.getId() == null ? "no id" : upstreamIntergenicRegion.getId().toString())) + "]"; }
    public Object getFieldValue(final String fieldName) throws IllegalAccessException {
        if ("type".equals(fieldName)) {
            return type;
        }
        if ("briefDescription".equals(fieldName)) {
            return briefDescription;
        }
        if ("description".equals(fieldName)) {
            return description;
        }
        if ("downstreamIntergenicRegion".equals(fieldName)) {
            if (downstreamIntergenicRegion instanceof ProxyReference) {
                return ((ProxyReference) downstreamIntergenicRegion).getObject();
            } else {
                return downstreamIntergenicRegion;
            }
        }
        if ("upstreamIntergenicRegion".equals(fieldName)) {
            if (upstreamIntergenicRegion instanceof ProxyReference) {
                return ((ProxyReference) upstreamIntergenicRegion).getObject();
            } else {
                return upstreamIntergenicRegion;
            }
        }
        if ("cloneArtifacts".equals(fieldName)) {
            return cloneArtifacts;
        }
        if ("goAnnotation".equals(fieldName)) {
            return goAnnotation;
        }
        if ("transcripts".equals(fieldName)) {
            return transcripts;
        }
        if ("chromosomes".equals(fieldName)) {
            return chromosomes;
        }
        if ("CDSs".equals(fieldName)) {
            return CDSs;
        }
        if ("pathways".equals(fieldName)) {
            return pathways;
        }
        if ("codingSequenceOf".equals(fieldName)) {
            return codingSequenceOf;
        }
        if ("encodes".equals(fieldName)) {
            return encodes;
        }
        if ("promotes".equals(fieldName)) {
            return promotes;
        }
        if ("flankingRegions".equals(fieldName)) {
            return flankingRegions;
        }
        if ("antibodies".equals(fieldName)) {
            return antibodies;
        }
        if ("morpholinos".equals(fieldName)) {
            return morpholinos;
        }
        if ("features".equals(fieldName)) {
            return features;
        }
        if ("expressions".equals(fieldName)) {
            return expressions;
        }
        if ("proteins".equals(fieldName)) {
            return proteins;
        }
        if ("UTRs".equals(fieldName)) {
            return UTRs;
        }
        if ("homologues".equals(fieldName)) {
            return homologues;
        }
        if ("exons".equals(fieldName)) {
            return exons;
        }
        if ("introns".equals(fieldName)) {
            return introns;
        }
        if ("stss".equals(fieldName)) {
            return stss;
        }
        if ("genotypes".equals(fieldName)) {
            return genotypes;
        }
        if ("snps".equals(fieldName)) {
            return snps;
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
        if (!org.intermine.model.bio.Gene.class.equals(getClass())) {
            return TypeUtil.getFieldValue(this, fieldName);
        }
        throw new IllegalArgumentException("Unknown field " + fieldName);
    }
    public Object getFieldProxy(final String fieldName) throws IllegalAccessException {
        if ("type".equals(fieldName)) {
            return type;
        }
        if ("briefDescription".equals(fieldName)) {
            return briefDescription;
        }
        if ("description".equals(fieldName)) {
            return description;
        }
        if ("downstreamIntergenicRegion".equals(fieldName)) {
            return downstreamIntergenicRegion;
        }
        if ("upstreamIntergenicRegion".equals(fieldName)) {
            return upstreamIntergenicRegion;
        }
        if ("cloneArtifacts".equals(fieldName)) {
            return cloneArtifacts;
        }
        if ("goAnnotation".equals(fieldName)) {
            return goAnnotation;
        }
        if ("transcripts".equals(fieldName)) {
            return transcripts;
        }
        if ("chromosomes".equals(fieldName)) {
            return chromosomes;
        }
        if ("CDSs".equals(fieldName)) {
            return CDSs;
        }
        if ("pathways".equals(fieldName)) {
            return pathways;
        }
        if ("codingSequenceOf".equals(fieldName)) {
            return codingSequenceOf;
        }
        if ("encodes".equals(fieldName)) {
            return encodes;
        }
        if ("promotes".equals(fieldName)) {
            return promotes;
        }
        if ("flankingRegions".equals(fieldName)) {
            return flankingRegions;
        }
        if ("antibodies".equals(fieldName)) {
            return antibodies;
        }
        if ("morpholinos".equals(fieldName)) {
            return morpholinos;
        }
        if ("features".equals(fieldName)) {
            return features;
        }
        if ("expressions".equals(fieldName)) {
            return expressions;
        }
        if ("proteins".equals(fieldName)) {
            return proteins;
        }
        if ("UTRs".equals(fieldName)) {
            return UTRs;
        }
        if ("homologues".equals(fieldName)) {
            return homologues;
        }
        if ("exons".equals(fieldName)) {
            return exons;
        }
        if ("introns".equals(fieldName)) {
            return introns;
        }
        if ("stss".equals(fieldName)) {
            return stss;
        }
        if ("genotypes".equals(fieldName)) {
            return genotypes;
        }
        if ("snps".equals(fieldName)) {
            return snps;
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
        if (!org.intermine.model.bio.Gene.class.equals(getClass())) {
            return TypeUtil.getFieldProxy(this, fieldName);
        }
        throw new IllegalArgumentException("Unknown field " + fieldName);
    }
    public void setFieldValue(final String fieldName, final Object value) {
        if ("type".equals(fieldName)) {
            type = (java.lang.String) value;
        } else if ("briefDescription".equals(fieldName)) {
            briefDescription = (java.lang.String) value;
        } else if ("description".equals(fieldName)) {
            description = (java.lang.String) value;
        } else if ("downstreamIntergenicRegion".equals(fieldName)) {
            downstreamIntergenicRegion = (org.intermine.model.InterMineObject) value;
        } else if ("upstreamIntergenicRegion".equals(fieldName)) {
            upstreamIntergenicRegion = (org.intermine.model.InterMineObject) value;
        } else if ("cloneArtifacts".equals(fieldName)) {
            cloneArtifacts = (java.util.Set) value;
        } else if ("goAnnotation".equals(fieldName)) {
            goAnnotation = (java.util.Set) value;
        } else if ("transcripts".equals(fieldName)) {
            transcripts = (java.util.Set) value;
        } else if ("chromosomes".equals(fieldName)) {
            chromosomes = (java.util.Set) value;
        } else if ("CDSs".equals(fieldName)) {
            CDSs = (java.util.Set) value;
        } else if ("pathways".equals(fieldName)) {
            pathways = (java.util.Set) value;
        } else if ("codingSequenceOf".equals(fieldName)) {
            codingSequenceOf = (java.util.Set) value;
        } else if ("encodes".equals(fieldName)) {
            encodes = (java.util.Set) value;
        } else if ("promotes".equals(fieldName)) {
            promotes = (java.util.Set) value;
        } else if ("flankingRegions".equals(fieldName)) {
            flankingRegions = (java.util.Set) value;
        } else if ("antibodies".equals(fieldName)) {
            antibodies = (java.util.Set) value;
        } else if ("morpholinos".equals(fieldName)) {
            morpholinos = (java.util.Set) value;
        } else if ("features".equals(fieldName)) {
            features = (java.util.Set) value;
        } else if ("expressions".equals(fieldName)) {
            expressions = (java.util.Set) value;
        } else if ("proteins".equals(fieldName)) {
            proteins = (java.util.Set) value;
        } else if ("UTRs".equals(fieldName)) {
            UTRs = (java.util.Set) value;
        } else if ("homologues".equals(fieldName)) {
            homologues = (java.util.Set) value;
        } else if ("exons".equals(fieldName)) {
            exons = (java.util.Set) value;
        } else if ("introns".equals(fieldName)) {
            introns = (java.util.Set) value;
        } else if ("stss".equals(fieldName)) {
            stss = (java.util.Set) value;
        } else if ("genotypes".equals(fieldName)) {
            genotypes = (java.util.Set) value;
        } else if ("snps".equals(fieldName)) {
            snps = (java.util.Set) value;
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
            if (!org.intermine.model.bio.Gene.class.equals(getClass())) {
                TypeUtil.setFieldValue(this, fieldName, value);
                return;
            }
            throw new IllegalArgumentException("Unknown field " + fieldName);
        }
    }
    public Class<?> getFieldType(final String fieldName) {
        if ("type".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("briefDescription".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("description".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("downstreamIntergenicRegion".equals(fieldName)) {
            return org.intermine.model.bio.IntergenicRegion.class;
        }
        if ("upstreamIntergenicRegion".equals(fieldName)) {
            return org.intermine.model.bio.IntergenicRegion.class;
        }
        if ("cloneArtifacts".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("goAnnotation".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("transcripts".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("chromosomes".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("CDSs".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("pathways".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("codingSequenceOf".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("encodes".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("promotes".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("flankingRegions".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("antibodies".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("morpholinos".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("features".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("expressions".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("proteins".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("UTRs".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("homologues".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("exons".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("introns".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("stss".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("genotypes".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("snps".equals(fieldName)) {
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
        if (!org.intermine.model.bio.Gene.class.equals(getClass())) {
            return TypeUtil.getFieldType(org.intermine.model.bio.Gene.class, fieldName);
        }
        throw new IllegalArgumentException("Unknown field " + fieldName);
    }
    public StringConstructor getoBJECT() {
        if (!org.intermine.model.bio.GeneShadow.class.equals(getClass())) {
            return NotXmlRenderer.render(this);
        }
        StringConstructor sb = new StringConstructor();
        sb.append("$_^org.intermine.model.bio.Gene");
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
        if (briefDescription != null) {
            sb.append("$_^abriefDescription$_^");
            String string = briefDescription;
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
        if (description != null) {
            sb.append("$_^adescription$_^");
            String string = description;
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
        if (downstreamIntergenicRegion != null) {
            sb.append("$_^rdownstreamIntergenicRegion$_^").append(downstreamIntergenicRegion.getId());
        }
        if (upstreamIntergenicRegion != null) {
            sb.append("$_^rupstreamIntergenicRegion$_^").append(upstreamIntergenicRegion.getId());
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
        if (!org.intermine.model.bio.GeneShadow.class.equals(getClass())) {
            throw new IllegalStateException("Class " + getClass().getName() + " does not match code (org.intermine.model.bio.Gene)");
        }
        for (int i = 2; i < notXml.length;) {
            int startI = i;
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
            if ((i < notXml.length) && "abriefDescription".equals(notXml[i])) {
                i++;
                StringBuilder string = null;
                while ((i + 1 < notXml.length) && (notXml[i + 1].charAt(0) == 'd')) {
                    if (string == null) string = new StringBuilder(notXml[i]);
                    i++;
                    string.append("$_^").append(notXml[i].substring(1));
                }
                briefDescription = string == null ? notXml[i] : string.toString();
                i++;
            }
            if ((i < notXml.length) && "adescription".equals(notXml[i])) {
                i++;
                StringBuilder string = null;
                while ((i + 1 < notXml.length) && (notXml[i + 1].charAt(0) == 'd')) {
                    if (string == null) string = new StringBuilder(notXml[i]);
                    i++;
                    string.append("$_^").append(notXml[i].substring(1));
                }
                description = string == null ? notXml[i] : string.toString();
                i++;
            }
            if ((i < notXml.length) &&"rdownstreamIntergenicRegion".equals(notXml[i])) {
                i++;
                downstreamIntergenicRegion = new ProxyReference(os, Integer.valueOf(notXml[i]), org.intermine.model.bio.IntergenicRegion.class);
                i++;
            };
            if ((i < notXml.length) &&"rupstreamIntergenicRegion".equals(notXml[i])) {
                i++;
                upstreamIntergenicRegion = new ProxyReference(os, Integer.valueOf(notXml[i]), org.intermine.model.bio.IntergenicRegion.class);
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
        cloneArtifacts = new ProxyCollection<org.intermine.model.bio.Clone>(os, this, "cloneArtifacts", org.intermine.model.bio.Clone.class);
        goAnnotation = new ProxyCollection<org.intermine.model.bio.GOAnnotation>(os, this, "goAnnotation", org.intermine.model.bio.GOAnnotation.class);
        transcripts = new ProxyCollection<org.intermine.model.bio.Transcript>(os, this, "transcripts", org.intermine.model.bio.Transcript.class);
        chromosomes = new ProxyCollection<org.intermine.model.bio.Chromosome>(os, this, "chromosomes", org.intermine.model.bio.Chromosome.class);
        CDSs = new ProxyCollection<org.intermine.model.bio.CDS>(os, this, "CDSs", org.intermine.model.bio.CDS.class);
        pathways = new ProxyCollection<org.intermine.model.bio.Pathway>(os, this, "pathways", org.intermine.model.bio.Pathway.class);
        codingSequenceOf = new ProxyCollection<org.intermine.model.bio.Construct>(os, this, "codingSequenceOf", org.intermine.model.bio.Construct.class);
        encodes = new ProxyCollection<org.intermine.model.bio.Clone>(os, this, "encodes", org.intermine.model.bio.Clone.class);
        promotes = new ProxyCollection<org.intermine.model.bio.Construct>(os, this, "promotes", org.intermine.model.bio.Construct.class);
        flankingRegions = new ProxyCollection<org.intermine.model.bio.GeneFlankingRegion>(os, this, "flankingRegions", org.intermine.model.bio.GeneFlankingRegion.class);
        antibodies = new ProxyCollection<org.intermine.model.bio.Antibody>(os, this, "antibodies", org.intermine.model.bio.Antibody.class);
        morpholinos = new ProxyCollection<org.intermine.model.bio.MorpholinoOligo>(os, this, "morpholinos", org.intermine.model.bio.MorpholinoOligo.class);
        features = new ProxyCollection<org.intermine.model.bio.SequenceAlteration>(os, this, "features", org.intermine.model.bio.SequenceAlteration.class);
        expressions = new ProxyCollection<org.intermine.model.bio.ExpressionResult>(os, this, "expressions", org.intermine.model.bio.ExpressionResult.class);
        proteins = new ProxyCollection<org.intermine.model.bio.Protein>(os, this, "proteins", org.intermine.model.bio.Protein.class);
        UTRs = new ProxyCollection<org.intermine.model.bio.UTR>(os, this, "UTRs", org.intermine.model.bio.UTR.class);
        homologues = new ProxyCollection<org.intermine.model.bio.Homologue>(os, this, "homologues", org.intermine.model.bio.Homologue.class);
        exons = new ProxyCollection<org.intermine.model.bio.Exon>(os, this, "exons", org.intermine.model.bio.Exon.class);
        introns = new ProxyCollection<org.intermine.model.bio.Intron>(os, this, "introns", org.intermine.model.bio.Intron.class);
        stss = new ProxyCollection<org.intermine.model.bio.STS>(os, this, "stss", org.intermine.model.bio.STS.class);
        genotypes = new ProxyCollection<org.intermine.model.bio.Genotype>(os, this, "genotypes", org.intermine.model.bio.Genotype.class);
        snps = new ProxyCollection<org.intermine.model.bio.SNP>(os, this, "snps", org.intermine.model.bio.SNP.class);
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
        if ("cloneArtifacts".equals(fieldName)) {
            cloneArtifacts.add((org.intermine.model.bio.Clone) element);
        } else if ("goAnnotation".equals(fieldName)) {
            goAnnotation.add((org.intermine.model.bio.GOAnnotation) element);
        } else if ("transcripts".equals(fieldName)) {
            transcripts.add((org.intermine.model.bio.Transcript) element);
        } else if ("chromosomes".equals(fieldName)) {
            chromosomes.add((org.intermine.model.bio.Chromosome) element);
        } else if ("CDSs".equals(fieldName)) {
            CDSs.add((org.intermine.model.bio.CDS) element);
        } else if ("pathways".equals(fieldName)) {
            pathways.add((org.intermine.model.bio.Pathway) element);
        } else if ("codingSequenceOf".equals(fieldName)) {
            codingSequenceOf.add((org.intermine.model.bio.Construct) element);
        } else if ("encodes".equals(fieldName)) {
            encodes.add((org.intermine.model.bio.Clone) element);
        } else if ("promotes".equals(fieldName)) {
            promotes.add((org.intermine.model.bio.Construct) element);
        } else if ("flankingRegions".equals(fieldName)) {
            flankingRegions.add((org.intermine.model.bio.GeneFlankingRegion) element);
        } else if ("antibodies".equals(fieldName)) {
            antibodies.add((org.intermine.model.bio.Antibody) element);
        } else if ("morpholinos".equals(fieldName)) {
            morpholinos.add((org.intermine.model.bio.MorpholinoOligo) element);
        } else if ("features".equals(fieldName)) {
            features.add((org.intermine.model.bio.SequenceAlteration) element);
        } else if ("expressions".equals(fieldName)) {
            expressions.add((org.intermine.model.bio.ExpressionResult) element);
        } else if ("proteins".equals(fieldName)) {
            proteins.add((org.intermine.model.bio.Protein) element);
        } else if ("UTRs".equals(fieldName)) {
            UTRs.add((org.intermine.model.bio.UTR) element);
        } else if ("homologues".equals(fieldName)) {
            homologues.add((org.intermine.model.bio.Homologue) element);
        } else if ("exons".equals(fieldName)) {
            exons.add((org.intermine.model.bio.Exon) element);
        } else if ("introns".equals(fieldName)) {
            introns.add((org.intermine.model.bio.Intron) element);
        } else if ("stss".equals(fieldName)) {
            stss.add((org.intermine.model.bio.STS) element);
        } else if ("genotypes".equals(fieldName)) {
            genotypes.add((org.intermine.model.bio.Genotype) element);
        } else if ("snps".equals(fieldName)) {
            snps.add((org.intermine.model.bio.SNP) element);
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
            if (!org.intermine.model.bio.Gene.class.equals(getClass())) {
                TypeUtil.addCollectionElement(this, fieldName, element);
                return;
            }
            throw new IllegalArgumentException("Unknown collection " + fieldName);
        }
    }
    public Class<?> getElementType(final String fieldName) {
        if ("cloneArtifacts".equals(fieldName)) {
            return org.intermine.model.bio.Clone.class;
        }
        if ("goAnnotation".equals(fieldName)) {
            return org.intermine.model.bio.GOAnnotation.class;
        }
        if ("transcripts".equals(fieldName)) {
            return org.intermine.model.bio.Transcript.class;
        }
        if ("chromosomes".equals(fieldName)) {
            return org.intermine.model.bio.Chromosome.class;
        }
        if ("CDSs".equals(fieldName)) {
            return org.intermine.model.bio.CDS.class;
        }
        if ("pathways".equals(fieldName)) {
            return org.intermine.model.bio.Pathway.class;
        }
        if ("codingSequenceOf".equals(fieldName)) {
            return org.intermine.model.bio.Construct.class;
        }
        if ("encodes".equals(fieldName)) {
            return org.intermine.model.bio.Clone.class;
        }
        if ("promotes".equals(fieldName)) {
            return org.intermine.model.bio.Construct.class;
        }
        if ("flankingRegions".equals(fieldName)) {
            return org.intermine.model.bio.GeneFlankingRegion.class;
        }
        if ("antibodies".equals(fieldName)) {
            return org.intermine.model.bio.Antibody.class;
        }
        if ("morpholinos".equals(fieldName)) {
            return org.intermine.model.bio.MorpholinoOligo.class;
        }
        if ("features".equals(fieldName)) {
            return org.intermine.model.bio.SequenceAlteration.class;
        }
        if ("expressions".equals(fieldName)) {
            return org.intermine.model.bio.ExpressionResult.class;
        }
        if ("proteins".equals(fieldName)) {
            return org.intermine.model.bio.Protein.class;
        }
        if ("UTRs".equals(fieldName)) {
            return org.intermine.model.bio.UTR.class;
        }
        if ("homologues".equals(fieldName)) {
            return org.intermine.model.bio.Homologue.class;
        }
        if ("exons".equals(fieldName)) {
            return org.intermine.model.bio.Exon.class;
        }
        if ("introns".equals(fieldName)) {
            return org.intermine.model.bio.Intron.class;
        }
        if ("stss".equals(fieldName)) {
            return org.intermine.model.bio.STS.class;
        }
        if ("genotypes".equals(fieldName)) {
            return org.intermine.model.bio.Genotype.class;
        }
        if ("snps".equals(fieldName)) {
            return org.intermine.model.bio.SNP.class;
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
        if (!org.intermine.model.bio.Gene.class.equals(getClass())) {
            return TypeUtil.getElementType(org.intermine.model.bio.Gene.class, fieldName);
        }
        throw new IllegalArgumentException("Unknown field " + fieldName);
    }
}
