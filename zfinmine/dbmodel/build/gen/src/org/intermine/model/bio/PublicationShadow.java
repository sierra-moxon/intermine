package org.intermine.model.bio;

import org.intermine.objectstore.ObjectStore;
import org.intermine.objectstore.intermine.NotXmlParser;
import org.intermine.objectstore.intermine.NotXmlRenderer;
import org.intermine.objectstore.proxy.ProxyCollection;
import org.intermine.objectstore.proxy.ProxyReference;
import org.intermine.util.StringConstructor;
import org.intermine.util.TypeUtil;
import org.intermine.model.ShadowClass;

public class PublicationShadow implements Publication, ShadowClass
{
    public static final Class<Publication> shadowOf = Publication.class;
    // Attr: org.intermine.model.bio.Publication.accessionNumber
    protected java.lang.String accessionNumber;
    public java.lang.String getAccessionNumber() { return accessionNumber; }
    public void setAccessionNumber(final java.lang.String accessionNumber) { this.accessionNumber = accessionNumber; }

    // Attr: org.intermine.model.bio.Publication.volume
    protected java.lang.String volume;
    public java.lang.String getVolume() { return volume; }
    public void setVolume(final java.lang.String volume) { this.volume = volume; }

    // Attr: org.intermine.model.bio.Publication.issue
    protected java.lang.String issue;
    public java.lang.String getIssue() { return issue; }
    public void setIssue(final java.lang.String issue) { this.issue = issue; }

    // Attr: org.intermine.model.bio.Publication.month
    protected java.lang.String month;
    public java.lang.String getMonth() { return month; }
    public void setMonth(final java.lang.String month) { this.month = month; }

    // Attr: org.intermine.model.bio.Publication.doi
    protected java.lang.String doi;
    public java.lang.String getDoi() { return doi; }
    public void setDoi(final java.lang.String doi) { this.doi = doi; }

    // Attr: org.intermine.model.bio.Publication.title
    protected java.lang.String title;
    public java.lang.String getTitle() { return title; }
    public void setTitle(final java.lang.String title) { this.title = title; }

    // Attr: org.intermine.model.bio.Publication.firstAuthor
    protected java.lang.String firstAuthor;
    public java.lang.String getFirstAuthor() { return firstAuthor; }
    public void setFirstAuthor(final java.lang.String firstAuthor) { this.firstAuthor = firstAuthor; }

    // Attr: org.intermine.model.bio.Publication.abstractText
    protected java.lang.String abstractText;
    public java.lang.String getAbstractText() { return abstractText; }
    public void setAbstractText(final java.lang.String abstractText) { this.abstractText = abstractText; }

    // Attr: org.intermine.model.bio.Publication.pages
    protected java.lang.String pages;
    public java.lang.String getPages() { return pages; }
    public void setPages(final java.lang.String pages) { this.pages = pages; }

    // Attr: org.intermine.model.bio.Publication.year
    protected java.lang.Integer year;
    public java.lang.Integer getYear() { return year; }
    public void setYear(final java.lang.Integer year) { this.year = year; }

    // Attr: org.intermine.model.bio.Publication.type
    protected java.lang.String type;
    public java.lang.String getType() { return type; }
    public void setType(final java.lang.String type) { this.type = type; }

    // Attr: org.intermine.model.bio.Publication.pubMedId
    protected java.lang.String pubMedId;
    public java.lang.String getPubMedId() { return pubMedId; }
    public void setPubMedId(final java.lang.String pubMedId) { this.pubMedId = pubMedId; }

    // Attr: org.intermine.model.bio.Publication.primaryIdentifier
    protected java.lang.String primaryIdentifier;
    public java.lang.String getPrimaryIdentifier() { return primaryIdentifier; }
    public void setPrimaryIdentifier(final java.lang.String primaryIdentifier) { this.primaryIdentifier = primaryIdentifier; }

    // Attr: org.intermine.model.bio.Publication.pubAbstract
    protected java.lang.String pubAbstract;
    public java.lang.String getPubAbstract() { return pubAbstract; }
    public void setPubAbstract(final java.lang.String pubAbstract) { this.pubAbstract = pubAbstract; }

    // Attr: org.intermine.model.bio.Publication.authorNames
    protected java.lang.String authorNames;
    public java.lang.String getAuthorNames() { return authorNames; }
    public void setAuthorNames(final java.lang.String authorNames) { this.authorNames = authorNames; }

    // Ref: org.intermine.model.bio.Publication.journal
    protected org.intermine.model.InterMineObject journal;
    public org.intermine.model.bio.Journal getJournal() { if (journal instanceof org.intermine.objectstore.proxy.ProxyReference) { return ((org.intermine.model.bio.Journal) ((org.intermine.objectstore.proxy.ProxyReference) journal).getObject()); }; return (org.intermine.model.bio.Journal) journal; }
    public void setJournal(final org.intermine.model.bio.Journal journal) { this.journal = journal; }
    public void proxyJournal(final org.intermine.objectstore.proxy.ProxyReference journal) { this.journal = journal; }
    public org.intermine.model.InterMineObject proxGetJournal() { return journal; }

    // Col: org.intermine.model.bio.Publication.constructs
    protected java.util.Set<org.intermine.model.bio.Construct> constructs = new java.util.HashSet<org.intermine.model.bio.Construct>();
    public java.util.Set<org.intermine.model.bio.Construct> getConstructs() { return constructs; }
    public void setConstructs(final java.util.Set<org.intermine.model.bio.Construct> constructs) { this.constructs = constructs; }
    public void addConstructs(final org.intermine.model.bio.Construct arg) { constructs.add(arg); }

    // Col: org.intermine.model.bio.Publication.bioEntities
    protected java.util.Set<org.intermine.model.bio.BioEntity> bioEntities = new java.util.HashSet<org.intermine.model.bio.BioEntity>();
    public java.util.Set<org.intermine.model.bio.BioEntity> getBioEntities() { return bioEntities; }
    public void setBioEntities(final java.util.Set<org.intermine.model.bio.BioEntity> bioEntities) { this.bioEntities = bioEntities; }
    public void addBioEntities(final org.intermine.model.bio.BioEntity arg) { bioEntities.add(arg); }

    // Col: org.intermine.model.bio.Publication.SSLPs
    protected java.util.Set<org.intermine.model.bio.SimpleSequenceLengthVariation> SSLPs = new java.util.HashSet<org.intermine.model.bio.SimpleSequenceLengthVariation>();
    public java.util.Set<org.intermine.model.bio.SimpleSequenceLengthVariation> getsSLPs() { return SSLPs; }
    public void setsSLPs(final java.util.Set<org.intermine.model.bio.SimpleSequenceLengthVariation> SSLPs) { this.SSLPs = SSLPs; }
    public void addsSLPs(final org.intermine.model.bio.SimpleSequenceLengthVariation arg) { SSLPs.add(arg); }

    // Col: org.intermine.model.bio.Publication.clones
    protected java.util.Set<org.intermine.model.bio.Clone> clones = new java.util.HashSet<org.intermine.model.bio.Clone>();
    public java.util.Set<org.intermine.model.bio.Clone> getClones() { return clones; }
    public void setClones(final java.util.Set<org.intermine.model.bio.Clone> clones) { this.clones = clones; }
    public void addClones(final org.intermine.model.bio.Clone arg) { clones.add(arg); }

    // Col: org.intermine.model.bio.Publication.crossReferences
    protected java.util.Set<org.intermine.model.bio.DatabaseReference> crossReferences = new java.util.HashSet<org.intermine.model.bio.DatabaseReference>();
    public java.util.Set<org.intermine.model.bio.DatabaseReference> getCrossReferences() { return crossReferences; }
    public void setCrossReferences(final java.util.Set<org.intermine.model.bio.DatabaseReference> crossReferences) { this.crossReferences = crossReferences; }
    public void addCrossReferences(final org.intermine.model.bio.DatabaseReference arg) { crossReferences.add(arg); }

    // Col: org.intermine.model.bio.Publication.antibodies
    protected java.util.Set<org.intermine.model.bio.Antibody> antibodies = new java.util.HashSet<org.intermine.model.bio.Antibody>();
    public java.util.Set<org.intermine.model.bio.Antibody> getAntibodies() { return antibodies; }
    public void setAntibodies(final java.util.Set<org.intermine.model.bio.Antibody> antibodies) { this.antibodies = antibodies; }
    public void addAntibodies(final org.intermine.model.bio.Antibody arg) { antibodies.add(arg); }

    // Col: org.intermine.model.bio.Publication.STSs
    protected java.util.Set<org.intermine.model.bio.STS> STSs = new java.util.HashSet<org.intermine.model.bio.STS>();
    public java.util.Set<org.intermine.model.bio.STS> getsTSs() { return STSs; }
    public void setsTSs(final java.util.Set<org.intermine.model.bio.STS> STSs) { this.STSs = STSs; }
    public void addsTSs(final org.intermine.model.bio.STS arg) { STSs.add(arg); }

    // Col: org.intermine.model.bio.Publication.figures
    protected java.util.Set<org.intermine.model.bio.Figure> figures = new java.util.HashSet<org.intermine.model.bio.Figure>();
    public java.util.Set<org.intermine.model.bio.Figure> getFigures() { return figures; }
    public void setFigures(final java.util.Set<org.intermine.model.bio.Figure> figures) { this.figures = figures; }
    public void addFigures(final org.intermine.model.bio.Figure arg) { figures.add(arg); }

    // Col: org.intermine.model.bio.Publication.SNPs
    protected java.util.Set<org.intermine.model.bio.SNP> SNPs = new java.util.HashSet<org.intermine.model.bio.SNP>();
    public java.util.Set<org.intermine.model.bio.SNP> getsNPs() { return SNPs; }
    public void setsNPs(final java.util.Set<org.intermine.model.bio.SNP> SNPs) { this.SNPs = SNPs; }
    public void addsNPs(final org.intermine.model.bio.SNP arg) { SNPs.add(arg); }

    // Col: org.intermine.model.bio.Publication.transcripts
    protected java.util.Set<org.intermine.model.bio.Transcript> transcripts = new java.util.HashSet<org.intermine.model.bio.Transcript>();
    public java.util.Set<org.intermine.model.bio.Transcript> getTranscripts() { return transcripts; }
    public void setTranscripts(final java.util.Set<org.intermine.model.bio.Transcript> transcripts) { this.transcripts = transcripts; }
    public void addTranscripts(final org.intermine.model.bio.Transcript arg) { transcripts.add(arg); }

    // Col: org.intermine.model.bio.Publication.EFGs
    protected java.util.Set<org.intermine.model.bio.EFG> EFGs = new java.util.HashSet<org.intermine.model.bio.EFG>();
    public java.util.Set<org.intermine.model.bio.EFG> geteFGs() { return EFGs; }
    public void seteFGs(final java.util.Set<org.intermine.model.bio.EFG> EFGs) { this.EFGs = EFGs; }
    public void addeFGs(final org.intermine.model.bio.EFG arg) { EFGs.add(arg); }

    // Col: org.intermine.model.bio.Publication.environments
    protected java.util.Set<org.intermine.model.bio.Environment> environments = new java.util.HashSet<org.intermine.model.bio.Environment>();
    public java.util.Set<org.intermine.model.bio.Environment> getEnvironments() { return environments; }
    public void setEnvironments(final java.util.Set<org.intermine.model.bio.Environment> environments) { this.environments = environments; }
    public void addEnvironments(final org.intermine.model.bio.Environment arg) { environments.add(arg); }

    // Col: org.intermine.model.bio.Publication.expressions
    protected java.util.Set<org.intermine.model.bio.ExpressionResult> expressions = new java.util.HashSet<org.intermine.model.bio.ExpressionResult>();
    public java.util.Set<org.intermine.model.bio.ExpressionResult> getExpressions() { return expressions; }
    public void setExpressions(final java.util.Set<org.intermine.model.bio.ExpressionResult> expressions) { this.expressions = expressions; }
    public void addExpressions(final org.intermine.model.bio.ExpressionResult arg) { expressions.add(arg); }

    // Col: org.intermine.model.bio.Publication.meshTerms
    protected java.util.Set<org.intermine.model.bio.MeshTerm> meshTerms = new java.util.HashSet<org.intermine.model.bio.MeshTerm>();
    public java.util.Set<org.intermine.model.bio.MeshTerm> getMeshTerms() { return meshTerms; }
    public void setMeshTerms(final java.util.Set<org.intermine.model.bio.MeshTerm> meshTerms) { this.meshTerms = meshTerms; }
    public void addMeshTerms(final org.intermine.model.bio.MeshTerm arg) { meshTerms.add(arg); }

    // Col: org.intermine.model.bio.Publication.morpholinos
    protected java.util.Set<org.intermine.model.bio.MorpholinoOligo> morpholinos = new java.util.HashSet<org.intermine.model.bio.MorpholinoOligo>();
    public java.util.Set<org.intermine.model.bio.MorpholinoOligo> getMorpholinos() { return morpholinos; }
    public void setMorpholinos(final java.util.Set<org.intermine.model.bio.MorpholinoOligo> morpholinos) { this.morpholinos = morpholinos; }
    public void addMorpholinos(final org.intermine.model.bio.MorpholinoOligo arg) { morpholinos.add(arg); }

    // Col: org.intermine.model.bio.Publication.regions
    protected java.util.Set<org.intermine.model.bio.Region> regions = new java.util.HashSet<org.intermine.model.bio.Region>();
    public java.util.Set<org.intermine.model.bio.Region> getRegions() { return regions; }
    public void setRegions(final java.util.Set<org.intermine.model.bio.Region> regions) { this.regions = regions; }
    public void addRegions(final org.intermine.model.bio.Region arg) { regions.add(arg); }

    // Col: org.intermine.model.bio.Publication.authors
    protected java.util.Set<org.intermine.model.bio.Author> authors = new java.util.HashSet<org.intermine.model.bio.Author>();
    public java.util.Set<org.intermine.model.bio.Author> getAuthors() { return authors; }
    public void setAuthors(final java.util.Set<org.intermine.model.bio.Author> authors) { this.authors = authors; }
    public void addAuthors(final org.intermine.model.bio.Author arg) { authors.add(arg); }

    // Col: org.intermine.model.bio.Publication.genes
    protected java.util.Set<org.intermine.model.bio.Gene> genes = new java.util.HashSet<org.intermine.model.bio.Gene>();
    public java.util.Set<org.intermine.model.bio.Gene> getGenes() { return genes; }
    public void setGenes(final java.util.Set<org.intermine.model.bio.Gene> genes) { this.genes = genes; }
    public void addGenes(final org.intermine.model.bio.Gene arg) { genes.add(arg); }

    // Attr: org.intermine.model.InterMineObject.id
    protected java.lang.Integer id;
    public java.lang.Integer getId() { return id; }
    public void setId(final java.lang.Integer id) { this.id = id; }

    @Override public boolean equals(Object o) { return (o instanceof Publication && id != null) ? id.equals(((Publication)o).getId()) : this == o; }
    @Override public int hashCode() { return (id != null) ? id.hashCode() : super.hashCode(); }
    @Override public String toString() { return "Publication [abstractText=\"" + abstractText + "\", accessionNumber=\"" + accessionNumber + "\", authorNames=\"" + authorNames + "\", doi=\"" + doi + "\", firstAuthor=\"" + firstAuthor + "\", id=\"" + id + "\", issue=\"" + issue + "\", journal=" + (journal == null ? "null" : (journal.getId() == null ? "no id" : journal.getId().toString())) + ", month=\"" + month + "\", pages=\"" + pages + "\", primaryIdentifier=\"" + primaryIdentifier + "\", pubAbstract=\"" + pubAbstract + "\", pubMedId=\"" + pubMedId + "\", title=\"" + title + "\", type=\"" + type + "\", volume=\"" + volume + "\", year=\"" + year + "\"]"; }
    public Object getFieldValue(final String fieldName) throws IllegalAccessException {
        if ("accessionNumber".equals(fieldName)) {
            return accessionNumber;
        }
        if ("volume".equals(fieldName)) {
            return volume;
        }
        if ("issue".equals(fieldName)) {
            return issue;
        }
        if ("month".equals(fieldName)) {
            return month;
        }
        if ("doi".equals(fieldName)) {
            return doi;
        }
        if ("title".equals(fieldName)) {
            return title;
        }
        if ("firstAuthor".equals(fieldName)) {
            return firstAuthor;
        }
        if ("abstractText".equals(fieldName)) {
            return abstractText;
        }
        if ("pages".equals(fieldName)) {
            return pages;
        }
        if ("year".equals(fieldName)) {
            return year;
        }
        if ("type".equals(fieldName)) {
            return type;
        }
        if ("pubMedId".equals(fieldName)) {
            return pubMedId;
        }
        if ("primaryIdentifier".equals(fieldName)) {
            return primaryIdentifier;
        }
        if ("pubAbstract".equals(fieldName)) {
            return pubAbstract;
        }
        if ("authorNames".equals(fieldName)) {
            return authorNames;
        }
        if ("journal".equals(fieldName)) {
            if (journal instanceof ProxyReference) {
                return ((ProxyReference) journal).getObject();
            } else {
                return journal;
            }
        }
        if ("constructs".equals(fieldName)) {
            return constructs;
        }
        if ("bioEntities".equals(fieldName)) {
            return bioEntities;
        }
        if ("SSLPs".equals(fieldName)) {
            return SSLPs;
        }
        if ("clones".equals(fieldName)) {
            return clones;
        }
        if ("crossReferences".equals(fieldName)) {
            return crossReferences;
        }
        if ("antibodies".equals(fieldName)) {
            return antibodies;
        }
        if ("STSs".equals(fieldName)) {
            return STSs;
        }
        if ("figures".equals(fieldName)) {
            return figures;
        }
        if ("SNPs".equals(fieldName)) {
            return SNPs;
        }
        if ("transcripts".equals(fieldName)) {
            return transcripts;
        }
        if ("EFGs".equals(fieldName)) {
            return EFGs;
        }
        if ("environments".equals(fieldName)) {
            return environments;
        }
        if ("expressions".equals(fieldName)) {
            return expressions;
        }
        if ("meshTerms".equals(fieldName)) {
            return meshTerms;
        }
        if ("morpholinos".equals(fieldName)) {
            return morpholinos;
        }
        if ("regions".equals(fieldName)) {
            return regions;
        }
        if ("authors".equals(fieldName)) {
            return authors;
        }
        if ("genes".equals(fieldName)) {
            return genes;
        }
        if ("id".equals(fieldName)) {
            return id;
        }
        if (!org.intermine.model.bio.Publication.class.equals(getClass())) {
            return TypeUtil.getFieldValue(this, fieldName);
        }
        throw new IllegalArgumentException("Unknown field " + fieldName);
    }
    public Object getFieldProxy(final String fieldName) throws IllegalAccessException {
        if ("accessionNumber".equals(fieldName)) {
            return accessionNumber;
        }
        if ("volume".equals(fieldName)) {
            return volume;
        }
        if ("issue".equals(fieldName)) {
            return issue;
        }
        if ("month".equals(fieldName)) {
            return month;
        }
        if ("doi".equals(fieldName)) {
            return doi;
        }
        if ("title".equals(fieldName)) {
            return title;
        }
        if ("firstAuthor".equals(fieldName)) {
            return firstAuthor;
        }
        if ("abstractText".equals(fieldName)) {
            return abstractText;
        }
        if ("pages".equals(fieldName)) {
            return pages;
        }
        if ("year".equals(fieldName)) {
            return year;
        }
        if ("type".equals(fieldName)) {
            return type;
        }
        if ("pubMedId".equals(fieldName)) {
            return pubMedId;
        }
        if ("primaryIdentifier".equals(fieldName)) {
            return primaryIdentifier;
        }
        if ("pubAbstract".equals(fieldName)) {
            return pubAbstract;
        }
        if ("authorNames".equals(fieldName)) {
            return authorNames;
        }
        if ("journal".equals(fieldName)) {
            return journal;
        }
        if ("constructs".equals(fieldName)) {
            return constructs;
        }
        if ("bioEntities".equals(fieldName)) {
            return bioEntities;
        }
        if ("SSLPs".equals(fieldName)) {
            return SSLPs;
        }
        if ("clones".equals(fieldName)) {
            return clones;
        }
        if ("crossReferences".equals(fieldName)) {
            return crossReferences;
        }
        if ("antibodies".equals(fieldName)) {
            return antibodies;
        }
        if ("STSs".equals(fieldName)) {
            return STSs;
        }
        if ("figures".equals(fieldName)) {
            return figures;
        }
        if ("SNPs".equals(fieldName)) {
            return SNPs;
        }
        if ("transcripts".equals(fieldName)) {
            return transcripts;
        }
        if ("EFGs".equals(fieldName)) {
            return EFGs;
        }
        if ("environments".equals(fieldName)) {
            return environments;
        }
        if ("expressions".equals(fieldName)) {
            return expressions;
        }
        if ("meshTerms".equals(fieldName)) {
            return meshTerms;
        }
        if ("morpholinos".equals(fieldName)) {
            return morpholinos;
        }
        if ("regions".equals(fieldName)) {
            return regions;
        }
        if ("authors".equals(fieldName)) {
            return authors;
        }
        if ("genes".equals(fieldName)) {
            return genes;
        }
        if ("id".equals(fieldName)) {
            return id;
        }
        if (!org.intermine.model.bio.Publication.class.equals(getClass())) {
            return TypeUtil.getFieldProxy(this, fieldName);
        }
        throw new IllegalArgumentException("Unknown field " + fieldName);
    }
    public void setFieldValue(final String fieldName, final Object value) {
        if ("accessionNumber".equals(fieldName)) {
            accessionNumber = (java.lang.String) value;
        } else if ("volume".equals(fieldName)) {
            volume = (java.lang.String) value;
        } else if ("issue".equals(fieldName)) {
            issue = (java.lang.String) value;
        } else if ("month".equals(fieldName)) {
            month = (java.lang.String) value;
        } else if ("doi".equals(fieldName)) {
            doi = (java.lang.String) value;
        } else if ("title".equals(fieldName)) {
            title = (java.lang.String) value;
        } else if ("firstAuthor".equals(fieldName)) {
            firstAuthor = (java.lang.String) value;
        } else if ("abstractText".equals(fieldName)) {
            abstractText = (java.lang.String) value;
        } else if ("pages".equals(fieldName)) {
            pages = (java.lang.String) value;
        } else if ("year".equals(fieldName)) {
            year = (java.lang.Integer) value;
        } else if ("type".equals(fieldName)) {
            type = (java.lang.String) value;
        } else if ("pubMedId".equals(fieldName)) {
            pubMedId = (java.lang.String) value;
        } else if ("primaryIdentifier".equals(fieldName)) {
            primaryIdentifier = (java.lang.String) value;
        } else if ("pubAbstract".equals(fieldName)) {
            pubAbstract = (java.lang.String) value;
        } else if ("authorNames".equals(fieldName)) {
            authorNames = (java.lang.String) value;
        } else if ("journal".equals(fieldName)) {
            journal = (org.intermine.model.InterMineObject) value;
        } else if ("constructs".equals(fieldName)) {
            constructs = (java.util.Set) value;
        } else if ("bioEntities".equals(fieldName)) {
            bioEntities = (java.util.Set) value;
        } else if ("SSLPs".equals(fieldName)) {
            SSLPs = (java.util.Set) value;
        } else if ("clones".equals(fieldName)) {
            clones = (java.util.Set) value;
        } else if ("crossReferences".equals(fieldName)) {
            crossReferences = (java.util.Set) value;
        } else if ("antibodies".equals(fieldName)) {
            antibodies = (java.util.Set) value;
        } else if ("STSs".equals(fieldName)) {
            STSs = (java.util.Set) value;
        } else if ("figures".equals(fieldName)) {
            figures = (java.util.Set) value;
        } else if ("SNPs".equals(fieldName)) {
            SNPs = (java.util.Set) value;
        } else if ("transcripts".equals(fieldName)) {
            transcripts = (java.util.Set) value;
        } else if ("EFGs".equals(fieldName)) {
            EFGs = (java.util.Set) value;
        } else if ("environments".equals(fieldName)) {
            environments = (java.util.Set) value;
        } else if ("expressions".equals(fieldName)) {
            expressions = (java.util.Set) value;
        } else if ("meshTerms".equals(fieldName)) {
            meshTerms = (java.util.Set) value;
        } else if ("morpholinos".equals(fieldName)) {
            morpholinos = (java.util.Set) value;
        } else if ("regions".equals(fieldName)) {
            regions = (java.util.Set) value;
        } else if ("authors".equals(fieldName)) {
            authors = (java.util.Set) value;
        } else if ("genes".equals(fieldName)) {
            genes = (java.util.Set) value;
        } else if ("id".equals(fieldName)) {
            id = (java.lang.Integer) value;
        } else {
            if (!org.intermine.model.bio.Publication.class.equals(getClass())) {
                TypeUtil.setFieldValue(this, fieldName, value);
                return;
            }
            throw new IllegalArgumentException("Unknown field " + fieldName);
        }
    }
    public Class<?> getFieldType(final String fieldName) {
        if ("accessionNumber".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("volume".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("issue".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("month".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("doi".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("title".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("firstAuthor".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("abstractText".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("pages".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("year".equals(fieldName)) {
            return java.lang.Integer.class;
        }
        if ("type".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("pubMedId".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("primaryIdentifier".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("pubAbstract".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("authorNames".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("journal".equals(fieldName)) {
            return org.intermine.model.bio.Journal.class;
        }
        if ("constructs".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("bioEntities".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("SSLPs".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("clones".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("crossReferences".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("antibodies".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("STSs".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("figures".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("SNPs".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("transcripts".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("EFGs".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("environments".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("expressions".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("meshTerms".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("morpholinos".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("regions".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("authors".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("genes".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("id".equals(fieldName)) {
            return java.lang.Integer.class;
        }
        if (!org.intermine.model.bio.Publication.class.equals(getClass())) {
            return TypeUtil.getFieldType(org.intermine.model.bio.Publication.class, fieldName);
        }
        throw new IllegalArgumentException("Unknown field " + fieldName);
    }
    public StringConstructor getoBJECT() {
        if (!org.intermine.model.bio.PublicationShadow.class.equals(getClass())) {
            return NotXmlRenderer.render(this);
        }
        StringConstructor sb = new StringConstructor();
        sb.append("$_^org.intermine.model.bio.Publication");
        if (accessionNumber != null) {
            sb.append("$_^aaccessionNumber$_^");
            String string = accessionNumber;
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
        if (volume != null) {
            sb.append("$_^avolume$_^");
            String string = volume;
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
        if (issue != null) {
            sb.append("$_^aissue$_^");
            String string = issue;
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
        if (month != null) {
            sb.append("$_^amonth$_^");
            String string = month;
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
        if (doi != null) {
            sb.append("$_^adoi$_^");
            String string = doi;
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
        if (title != null) {
            sb.append("$_^atitle$_^");
            String string = title;
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
        if (firstAuthor != null) {
            sb.append("$_^afirstAuthor$_^");
            String string = firstAuthor;
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
        if (abstractText != null) {
            sb.append("$_^aabstractText$_^");
            String string = abstractText;
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
        if (pages != null) {
            sb.append("$_^apages$_^");
            String string = pages;
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
        if (year != null) {
            sb.append("$_^ayear$_^").append(year);
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
        if (pubMedId != null) {
            sb.append("$_^apubMedId$_^");
            String string = pubMedId;
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
        if (pubAbstract != null) {
            sb.append("$_^apubAbstract$_^");
            String string = pubAbstract;
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
        if (authorNames != null) {
            sb.append("$_^aauthorNames$_^");
            String string = authorNames;
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
        if (journal != null) {
            sb.append("$_^rjournal$_^").append(journal.getId());
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
        if (!org.intermine.model.bio.PublicationShadow.class.equals(getClass())) {
            throw new IllegalStateException("Class " + getClass().getName() + " does not match code (org.intermine.model.bio.Publication)");
        }
        for (int i = 2; i < notXml.length;) {
            int startI = i;
            if ((i < notXml.length) && "aaccessionNumber".equals(notXml[i])) {
                i++;
                StringBuilder string = null;
                while ((i + 1 < notXml.length) && (notXml[i + 1].charAt(0) == 'd')) {
                    if (string == null) string = new StringBuilder(notXml[i]);
                    i++;
                    string.append("$_^").append(notXml[i].substring(1));
                }
                accessionNumber = string == null ? notXml[i] : string.toString();
                i++;
            }
            if ((i < notXml.length) && "avolume".equals(notXml[i])) {
                i++;
                StringBuilder string = null;
                while ((i + 1 < notXml.length) && (notXml[i + 1].charAt(0) == 'd')) {
                    if (string == null) string = new StringBuilder(notXml[i]);
                    i++;
                    string.append("$_^").append(notXml[i].substring(1));
                }
                volume = string == null ? notXml[i] : string.toString();
                i++;
            }
            if ((i < notXml.length) && "aissue".equals(notXml[i])) {
                i++;
                StringBuilder string = null;
                while ((i + 1 < notXml.length) && (notXml[i + 1].charAt(0) == 'd')) {
                    if (string == null) string = new StringBuilder(notXml[i]);
                    i++;
                    string.append("$_^").append(notXml[i].substring(1));
                }
                issue = string == null ? notXml[i] : string.toString();
                i++;
            }
            if ((i < notXml.length) && "amonth".equals(notXml[i])) {
                i++;
                StringBuilder string = null;
                while ((i + 1 < notXml.length) && (notXml[i + 1].charAt(0) == 'd')) {
                    if (string == null) string = new StringBuilder(notXml[i]);
                    i++;
                    string.append("$_^").append(notXml[i].substring(1));
                }
                month = string == null ? notXml[i] : string.toString();
                i++;
            }
            if ((i < notXml.length) && "adoi".equals(notXml[i])) {
                i++;
                StringBuilder string = null;
                while ((i + 1 < notXml.length) && (notXml[i + 1].charAt(0) == 'd')) {
                    if (string == null) string = new StringBuilder(notXml[i]);
                    i++;
                    string.append("$_^").append(notXml[i].substring(1));
                }
                doi = string == null ? notXml[i] : string.toString();
                i++;
            }
            if ((i < notXml.length) && "atitle".equals(notXml[i])) {
                i++;
                StringBuilder string = null;
                while ((i + 1 < notXml.length) && (notXml[i + 1].charAt(0) == 'd')) {
                    if (string == null) string = new StringBuilder(notXml[i]);
                    i++;
                    string.append("$_^").append(notXml[i].substring(1));
                }
                title = string == null ? notXml[i] : string.toString();
                i++;
            }
            if ((i < notXml.length) && "afirstAuthor".equals(notXml[i])) {
                i++;
                StringBuilder string = null;
                while ((i + 1 < notXml.length) && (notXml[i + 1].charAt(0) == 'd')) {
                    if (string == null) string = new StringBuilder(notXml[i]);
                    i++;
                    string.append("$_^").append(notXml[i].substring(1));
                }
                firstAuthor = string == null ? notXml[i] : string.toString();
                i++;
            }
            if ((i < notXml.length) && "aabstractText".equals(notXml[i])) {
                i++;
                StringBuilder string = null;
                while ((i + 1 < notXml.length) && (notXml[i + 1].charAt(0) == 'd')) {
                    if (string == null) string = new StringBuilder(notXml[i]);
                    i++;
                    string.append("$_^").append(notXml[i].substring(1));
                }
                abstractText = string == null ? notXml[i] : string.toString();
                i++;
            }
            if ((i < notXml.length) && "apages".equals(notXml[i])) {
                i++;
                StringBuilder string = null;
                while ((i + 1 < notXml.length) && (notXml[i + 1].charAt(0) == 'd')) {
                    if (string == null) string = new StringBuilder(notXml[i]);
                    i++;
                    string.append("$_^").append(notXml[i].substring(1));
                }
                pages = string == null ? notXml[i] : string.toString();
                i++;
            }
            if ((i < notXml.length) && "ayear".equals(notXml[i])) {
                i++;
                year = Integer.valueOf(notXml[i]);
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
            if ((i < notXml.length) && "apubMedId".equals(notXml[i])) {
                i++;
                StringBuilder string = null;
                while ((i + 1 < notXml.length) && (notXml[i + 1].charAt(0) == 'd')) {
                    if (string == null) string = new StringBuilder(notXml[i]);
                    i++;
                    string.append("$_^").append(notXml[i].substring(1));
                }
                pubMedId = string == null ? notXml[i] : string.toString();
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
            if ((i < notXml.length) && "apubAbstract".equals(notXml[i])) {
                i++;
                StringBuilder string = null;
                while ((i + 1 < notXml.length) && (notXml[i + 1].charAt(0) == 'd')) {
                    if (string == null) string = new StringBuilder(notXml[i]);
                    i++;
                    string.append("$_^").append(notXml[i].substring(1));
                }
                pubAbstract = string == null ? notXml[i] : string.toString();
                i++;
            }
            if ((i < notXml.length) && "aauthorNames".equals(notXml[i])) {
                i++;
                StringBuilder string = null;
                while ((i + 1 < notXml.length) && (notXml[i + 1].charAt(0) == 'd')) {
                    if (string == null) string = new StringBuilder(notXml[i]);
                    i++;
                    string.append("$_^").append(notXml[i].substring(1));
                }
                authorNames = string == null ? notXml[i] : string.toString();
                i++;
            }
            if ((i < notXml.length) &&"rjournal".equals(notXml[i])) {
                i++;
                journal = new ProxyReference(os, Integer.valueOf(notXml[i]), org.intermine.model.bio.Journal.class);
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
        constructs = new ProxyCollection<org.intermine.model.bio.Construct>(os, this, "constructs", org.intermine.model.bio.Construct.class);
        bioEntities = new ProxyCollection<org.intermine.model.bio.BioEntity>(os, this, "bioEntities", org.intermine.model.bio.BioEntity.class);
        SSLPs = new ProxyCollection<org.intermine.model.bio.SimpleSequenceLengthVariation>(os, this, "SSLPs", org.intermine.model.bio.SimpleSequenceLengthVariation.class);
        clones = new ProxyCollection<org.intermine.model.bio.Clone>(os, this, "clones", org.intermine.model.bio.Clone.class);
        crossReferences = new ProxyCollection<org.intermine.model.bio.DatabaseReference>(os, this, "crossReferences", org.intermine.model.bio.DatabaseReference.class);
        antibodies = new ProxyCollection<org.intermine.model.bio.Antibody>(os, this, "antibodies", org.intermine.model.bio.Antibody.class);
        STSs = new ProxyCollection<org.intermine.model.bio.STS>(os, this, "STSs", org.intermine.model.bio.STS.class);
        figures = new ProxyCollection<org.intermine.model.bio.Figure>(os, this, "figures", org.intermine.model.bio.Figure.class);
        SNPs = new ProxyCollection<org.intermine.model.bio.SNP>(os, this, "SNPs", org.intermine.model.bio.SNP.class);
        transcripts = new ProxyCollection<org.intermine.model.bio.Transcript>(os, this, "transcripts", org.intermine.model.bio.Transcript.class);
        EFGs = new ProxyCollection<org.intermine.model.bio.EFG>(os, this, "EFGs", org.intermine.model.bio.EFG.class);
        environments = new ProxyCollection<org.intermine.model.bio.Environment>(os, this, "environments", org.intermine.model.bio.Environment.class);
        expressions = new ProxyCollection<org.intermine.model.bio.ExpressionResult>(os, this, "expressions", org.intermine.model.bio.ExpressionResult.class);
        meshTerms = new ProxyCollection<org.intermine.model.bio.MeshTerm>(os, this, "meshTerms", org.intermine.model.bio.MeshTerm.class);
        morpholinos = new ProxyCollection<org.intermine.model.bio.MorpholinoOligo>(os, this, "morpholinos", org.intermine.model.bio.MorpholinoOligo.class);
        regions = new ProxyCollection<org.intermine.model.bio.Region>(os, this, "regions", org.intermine.model.bio.Region.class);
        authors = new ProxyCollection<org.intermine.model.bio.Author>(os, this, "authors", org.intermine.model.bio.Author.class);
        genes = new ProxyCollection<org.intermine.model.bio.Gene>(os, this, "genes", org.intermine.model.bio.Gene.class);
    }
    public void addCollectionElement(final String fieldName, final org.intermine.model.InterMineObject element) {
        if ("constructs".equals(fieldName)) {
            constructs.add((org.intermine.model.bio.Construct) element);
        } else if ("bioEntities".equals(fieldName)) {
            bioEntities.add((org.intermine.model.bio.BioEntity) element);
        } else if ("SSLPs".equals(fieldName)) {
            SSLPs.add((org.intermine.model.bio.SimpleSequenceLengthVariation) element);
        } else if ("clones".equals(fieldName)) {
            clones.add((org.intermine.model.bio.Clone) element);
        } else if ("crossReferences".equals(fieldName)) {
            crossReferences.add((org.intermine.model.bio.DatabaseReference) element);
        } else if ("antibodies".equals(fieldName)) {
            antibodies.add((org.intermine.model.bio.Antibody) element);
        } else if ("STSs".equals(fieldName)) {
            STSs.add((org.intermine.model.bio.STS) element);
        } else if ("figures".equals(fieldName)) {
            figures.add((org.intermine.model.bio.Figure) element);
        } else if ("SNPs".equals(fieldName)) {
            SNPs.add((org.intermine.model.bio.SNP) element);
        } else if ("transcripts".equals(fieldName)) {
            transcripts.add((org.intermine.model.bio.Transcript) element);
        } else if ("EFGs".equals(fieldName)) {
            EFGs.add((org.intermine.model.bio.EFG) element);
        } else if ("environments".equals(fieldName)) {
            environments.add((org.intermine.model.bio.Environment) element);
        } else if ("expressions".equals(fieldName)) {
            expressions.add((org.intermine.model.bio.ExpressionResult) element);
        } else if ("meshTerms".equals(fieldName)) {
            meshTerms.add((org.intermine.model.bio.MeshTerm) element);
        } else if ("morpholinos".equals(fieldName)) {
            morpholinos.add((org.intermine.model.bio.MorpholinoOligo) element);
        } else if ("regions".equals(fieldName)) {
            regions.add((org.intermine.model.bio.Region) element);
        } else if ("authors".equals(fieldName)) {
            authors.add((org.intermine.model.bio.Author) element);
        } else if ("genes".equals(fieldName)) {
            genes.add((org.intermine.model.bio.Gene) element);
        } else {
            if (!org.intermine.model.bio.Publication.class.equals(getClass())) {
                TypeUtil.addCollectionElement(this, fieldName, element);
                return;
            }
            throw new IllegalArgumentException("Unknown collection " + fieldName);
        }
    }
    public Class<?> getElementType(final String fieldName) {
        if ("constructs".equals(fieldName)) {
            return org.intermine.model.bio.Construct.class;
        }
        if ("bioEntities".equals(fieldName)) {
            return org.intermine.model.bio.BioEntity.class;
        }
        if ("SSLPs".equals(fieldName)) {
            return org.intermine.model.bio.SimpleSequenceLengthVariation.class;
        }
        if ("clones".equals(fieldName)) {
            return org.intermine.model.bio.Clone.class;
        }
        if ("crossReferences".equals(fieldName)) {
            return org.intermine.model.bio.DatabaseReference.class;
        }
        if ("antibodies".equals(fieldName)) {
            return org.intermine.model.bio.Antibody.class;
        }
        if ("STSs".equals(fieldName)) {
            return org.intermine.model.bio.STS.class;
        }
        if ("figures".equals(fieldName)) {
            return org.intermine.model.bio.Figure.class;
        }
        if ("SNPs".equals(fieldName)) {
            return org.intermine.model.bio.SNP.class;
        }
        if ("transcripts".equals(fieldName)) {
            return org.intermine.model.bio.Transcript.class;
        }
        if ("EFGs".equals(fieldName)) {
            return org.intermine.model.bio.EFG.class;
        }
        if ("environments".equals(fieldName)) {
            return org.intermine.model.bio.Environment.class;
        }
        if ("expressions".equals(fieldName)) {
            return org.intermine.model.bio.ExpressionResult.class;
        }
        if ("meshTerms".equals(fieldName)) {
            return org.intermine.model.bio.MeshTerm.class;
        }
        if ("morpholinos".equals(fieldName)) {
            return org.intermine.model.bio.MorpholinoOligo.class;
        }
        if ("regions".equals(fieldName)) {
            return org.intermine.model.bio.Region.class;
        }
        if ("authors".equals(fieldName)) {
            return org.intermine.model.bio.Author.class;
        }
        if ("genes".equals(fieldName)) {
            return org.intermine.model.bio.Gene.class;
        }
        if (!org.intermine.model.bio.Publication.class.equals(getClass())) {
            return TypeUtil.getElementType(org.intermine.model.bio.Publication.class, fieldName);
        }
        throw new IllegalArgumentException("Unknown field " + fieldName);
    }
}
