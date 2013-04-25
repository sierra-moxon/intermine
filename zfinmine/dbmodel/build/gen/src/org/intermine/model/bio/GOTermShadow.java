package org.intermine.model.bio;

import org.intermine.objectstore.ObjectStore;
import org.intermine.objectstore.intermine.NotXmlParser;
import org.intermine.objectstore.intermine.NotXmlRenderer;
import org.intermine.objectstore.proxy.ProxyCollection;
import org.intermine.objectstore.proxy.ProxyReference;
import org.intermine.util.StringConstructor;
import org.intermine.util.TypeUtil;
import org.intermine.model.ShadowClass;

public class GOTermShadow implements GOTerm, ShadowClass
{
    public static final Class<GOTerm> shadowOf = GOTerm.class;
    // Attr: org.intermine.model.bio.OntologyTerm.identifier
    protected java.lang.String identifier;
    public java.lang.String getIdentifier() { return identifier; }
    public void setIdentifier(final java.lang.String identifier) { this.identifier = identifier; }

    // Attr: org.intermine.model.bio.OntologyTerm.stageStartHour
    protected java.lang.String stageStartHour;
    public java.lang.String getStageStartHour() { return stageStartHour; }
    public void setStageStartHour(final java.lang.String stageStartHour) { this.stageStartHour = stageStartHour; }

    // Attr: org.intermine.model.bio.OntologyTerm.obsolete
    protected java.lang.Boolean obsolete;
    public java.lang.Boolean getObsolete() { return obsolete; }
    public void setObsolete(final java.lang.Boolean obsolete) { this.obsolete = obsolete; }

    // Attr: org.intermine.model.bio.OntologyTerm.primaryIdentifier
    protected java.lang.String primaryIdentifier;
    public java.lang.String getPrimaryIdentifier() { return primaryIdentifier; }
    public void setPrimaryIdentifier(final java.lang.String primaryIdentifier) { this.primaryIdentifier = primaryIdentifier; }

    // Attr: org.intermine.model.bio.OntologyTerm.namespace
    protected java.lang.String namespace;
    public java.lang.String getNamespace() { return namespace; }
    public void setNamespace(final java.lang.String namespace) { this.namespace = namespace; }

    // Attr: org.intermine.model.bio.OntologyTerm.name
    protected java.lang.String name;
    public java.lang.String getName() { return name; }
    public void setName(final java.lang.String name) { this.name = name; }

    // Attr: org.intermine.model.bio.OntologyTerm.stageAbbreviation
    protected java.lang.String stageAbbreviation;
    public java.lang.String getStageAbbreviation() { return stageAbbreviation; }
    public void setStageAbbreviation(final java.lang.String stageAbbreviation) { this.stageAbbreviation = stageAbbreviation; }

    // Attr: org.intermine.model.bio.OntologyTerm.description
    protected java.lang.String description;
    public java.lang.String getDescription() { return description; }
    public void setDescription(final java.lang.String description) { this.description = description; }

    // Attr: org.intermine.model.bio.OntologyTerm.stageEndHour
    protected java.lang.String stageEndHour;
    public java.lang.String getStageEndHour() { return stageEndHour; }
    public void setStageEndHour(final java.lang.String stageEndHour) { this.stageEndHour = stageEndHour; }

    // Ref: org.intermine.model.bio.OntologyTerm.ontology
    protected org.intermine.model.InterMineObject ontology;
    public org.intermine.model.bio.Ontology getOntology() { if (ontology instanceof org.intermine.objectstore.proxy.ProxyReference) { return ((org.intermine.model.bio.Ontology) ((org.intermine.objectstore.proxy.ProxyReference) ontology).getObject()); }; return (org.intermine.model.bio.Ontology) ontology; }
    public void setOntology(final org.intermine.model.bio.Ontology ontology) { this.ontology = ontology; }
    public void proxyOntology(final org.intermine.objectstore.proxy.ProxyReference ontology) { this.ontology = ontology; }
    public org.intermine.model.InterMineObject proxGetOntology() { return ontology; }

    // Ref: org.intermine.model.bio.OntologyTerm.organism
    protected org.intermine.model.InterMineObject organism;
    public org.intermine.model.bio.Organism getOrganism() { if (organism instanceof org.intermine.objectstore.proxy.ProxyReference) { return ((org.intermine.model.bio.Organism) ((org.intermine.objectstore.proxy.ProxyReference) organism).getObject()); }; return (org.intermine.model.bio.Organism) organism; }
    public void setOrganism(final org.intermine.model.bio.Organism organism) { this.organism = organism; }
    public void proxyOrganism(final org.intermine.objectstore.proxy.ProxyReference organism) { this.organism = organism; }
    public org.intermine.model.InterMineObject proxGetOrganism() { return organism; }

    // Col: org.intermine.model.bio.OntologyTerm.synonyms
    protected java.util.Set<org.intermine.model.bio.OntologyTermSynonym> synonyms = new java.util.HashSet<org.intermine.model.bio.OntologyTermSynonym>();
    public java.util.Set<org.intermine.model.bio.OntologyTermSynonym> getSynonyms() { return synonyms; }
    public void setSynonyms(final java.util.Set<org.intermine.model.bio.OntologyTermSynonym> synonyms) { this.synonyms = synonyms; }
    public void addSynonyms(final org.intermine.model.bio.OntologyTermSynonym arg) { synonyms.add(arg); }

    // Col: org.intermine.model.bio.OntologyTerm.expressionResultsEnd
    protected java.util.Set<org.intermine.model.bio.ExpressionResult> expressionResultsEnd = new java.util.HashSet<org.intermine.model.bio.ExpressionResult>();
    public java.util.Set<org.intermine.model.bio.ExpressionResult> getExpressionResultsEnd() { return expressionResultsEnd; }
    public void setExpressionResultsEnd(final java.util.Set<org.intermine.model.bio.ExpressionResult> expressionResultsEnd) { this.expressionResultsEnd = expressionResultsEnd; }
    public void addExpressionResultsEnd(final org.intermine.model.bio.ExpressionResult arg) { expressionResultsEnd.add(arg); }

    // Col: org.intermine.model.bio.OntologyTerm.ontologyAnnotations
    protected java.util.Set<org.intermine.model.bio.OntologyAnnotation> ontologyAnnotations = new java.util.HashSet<org.intermine.model.bio.OntologyAnnotation>();
    public java.util.Set<org.intermine.model.bio.OntologyAnnotation> getOntologyAnnotations() { return ontologyAnnotations; }
    public void setOntologyAnnotations(final java.util.Set<org.intermine.model.bio.OntologyAnnotation> ontologyAnnotations) { this.ontologyAnnotations = ontologyAnnotations; }
    public void addOntologyAnnotations(final org.intermine.model.bio.OntologyAnnotation arg) { ontologyAnnotations.add(arg); }

    // Col: org.intermine.model.bio.OntologyTerm.relations
    protected java.util.Set<org.intermine.model.bio.OntologyRelation> relations = new java.util.HashSet<org.intermine.model.bio.OntologyRelation>();
    public java.util.Set<org.intermine.model.bio.OntologyRelation> getRelations() { return relations; }
    public void setRelations(final java.util.Set<org.intermine.model.bio.OntologyRelation> relations) { this.relations = relations; }
    public void addRelations(final org.intermine.model.bio.OntologyRelation arg) { relations.add(arg); }

    // Col: org.intermine.model.bio.OntologyTerm.expressionResultSubterms
    protected java.util.Set<org.intermine.model.bio.ExpressionResult> expressionResultSubterms = new java.util.HashSet<org.intermine.model.bio.ExpressionResult>();
    public java.util.Set<org.intermine.model.bio.ExpressionResult> getExpressionResultSubterms() { return expressionResultSubterms; }
    public void setExpressionResultSubterms(final java.util.Set<org.intermine.model.bio.ExpressionResult> expressionResultSubterms) { this.expressionResultSubterms = expressionResultSubterms; }
    public void addExpressionResultSubterms(final org.intermine.model.bio.ExpressionResult arg) { expressionResultSubterms.add(arg); }

    // Col: org.intermine.model.bio.OntologyTerm.phenotypesStartStg
    protected java.util.Set<org.intermine.model.bio.Phenotype> phenotypesStartStg = new java.util.HashSet<org.intermine.model.bio.Phenotype>();
    public java.util.Set<org.intermine.model.bio.Phenotype> getPhenotypesStartStg() { return phenotypesStartStg; }
    public void setPhenotypesStartStg(final java.util.Set<org.intermine.model.bio.Phenotype> phenotypesStartStg) { this.phenotypesStartStg = phenotypesStartStg; }
    public void addPhenotypesStartStg(final org.intermine.model.bio.Phenotype arg) { phenotypesStartStg.add(arg); }

    // Col: org.intermine.model.bio.OntologyTerm.phenotypesEndStg
    protected java.util.Set<org.intermine.model.bio.Phenotype> phenotypesEndStg = new java.util.HashSet<org.intermine.model.bio.Phenotype>();
    public java.util.Set<org.intermine.model.bio.Phenotype> getPhenotypesEndStg() { return phenotypesEndStg; }
    public void setPhenotypesEndStg(final java.util.Set<org.intermine.model.bio.Phenotype> phenotypesEndStg) { this.phenotypesEndStg = phenotypesEndStg; }
    public void addPhenotypesEndStg(final org.intermine.model.bio.Phenotype arg) { phenotypesEndStg.add(arg); }

    // Col: org.intermine.model.bio.OntologyTerm.parents
    protected java.util.Set<org.intermine.model.bio.OntologyTerm> parents = new java.util.HashSet<org.intermine.model.bio.OntologyTerm>();
    public java.util.Set<org.intermine.model.bio.OntologyTerm> getParents() { return parents; }
    public void setParents(final java.util.Set<org.intermine.model.bio.OntologyTerm> parents) { this.parents = parents; }
    public void addParents(final org.intermine.model.bio.OntologyTerm arg) { parents.add(arg); }

    // Col: org.intermine.model.bio.OntologyTerm.dataSets
    protected java.util.Set<org.intermine.model.bio.DataSet> dataSets = new java.util.HashSet<org.intermine.model.bio.DataSet>();
    public java.util.Set<org.intermine.model.bio.DataSet> getDataSets() { return dataSets; }
    public void setDataSets(final java.util.Set<org.intermine.model.bio.DataSet> dataSets) { this.dataSets = dataSets; }
    public void addDataSets(final org.intermine.model.bio.DataSet arg) { dataSets.add(arg); }

    // Col: org.intermine.model.bio.OntologyTerm.expressionResultsStart
    protected java.util.Set<org.intermine.model.bio.ExpressionResult> expressionResultsStart = new java.util.HashSet<org.intermine.model.bio.ExpressionResult>();
    public java.util.Set<org.intermine.model.bio.ExpressionResult> getExpressionResultsStart() { return expressionResultsStart; }
    public void setExpressionResultsStart(final java.util.Set<org.intermine.model.bio.ExpressionResult> expressionResultsStart) { this.expressionResultsStart = expressionResultsStart; }
    public void addExpressionResultsStart(final org.intermine.model.bio.ExpressionResult arg) { expressionResultsStart.add(arg); }

    // Attr: org.intermine.model.InterMineObject.id
    protected java.lang.Integer id;
    public java.lang.Integer getId() { return id; }
    public void setId(final java.lang.Integer id) { this.id = id; }

    @Override public boolean equals(Object o) { return (o instanceof GOTerm && id != null) ? id.equals(((GOTerm)o).getId()) : this == o; }
    @Override public int hashCode() { return (id != null) ? id.hashCode() : super.hashCode(); }
    @Override public String toString() { return "GOTerm [description=\"" + description + "\", id=\"" + id + "\", identifier=\"" + identifier + "\", name=\"" + name + "\", namespace=\"" + namespace + "\", obsolete=\"" + obsolete + "\", ontology=" + (ontology == null ? "null" : (ontology.getId() == null ? "no id" : ontology.getId().toString())) + ", organism=" + (organism == null ? "null" : (organism.getId() == null ? "no id" : organism.getId().toString())) + ", primaryIdentifier=\"" + primaryIdentifier + "\", stageAbbreviation=\"" + stageAbbreviation + "\", stageEndHour=\"" + stageEndHour + "\", stageStartHour=\"" + stageStartHour + "\"]"; }
    public Object getFieldValue(final String fieldName) throws IllegalAccessException {
        if ("identifier".equals(fieldName)) {
            return identifier;
        }
        if ("stageStartHour".equals(fieldName)) {
            return stageStartHour;
        }
        if ("obsolete".equals(fieldName)) {
            return obsolete;
        }
        if ("primaryIdentifier".equals(fieldName)) {
            return primaryIdentifier;
        }
        if ("namespace".equals(fieldName)) {
            return namespace;
        }
        if ("name".equals(fieldName)) {
            return name;
        }
        if ("stageAbbreviation".equals(fieldName)) {
            return stageAbbreviation;
        }
        if ("description".equals(fieldName)) {
            return description;
        }
        if ("stageEndHour".equals(fieldName)) {
            return stageEndHour;
        }
        if ("ontology".equals(fieldName)) {
            if (ontology instanceof ProxyReference) {
                return ((ProxyReference) ontology).getObject();
            } else {
                return ontology;
            }
        }
        if ("organism".equals(fieldName)) {
            if (organism instanceof ProxyReference) {
                return ((ProxyReference) organism).getObject();
            } else {
                return organism;
            }
        }
        if ("synonyms".equals(fieldName)) {
            return synonyms;
        }
        if ("expressionResultsEnd".equals(fieldName)) {
            return expressionResultsEnd;
        }
        if ("ontologyAnnotations".equals(fieldName)) {
            return ontologyAnnotations;
        }
        if ("relations".equals(fieldName)) {
            return relations;
        }
        if ("expressionResultSubterms".equals(fieldName)) {
            return expressionResultSubterms;
        }
        if ("phenotypesStartStg".equals(fieldName)) {
            return phenotypesStartStg;
        }
        if ("phenotypesEndStg".equals(fieldName)) {
            return phenotypesEndStg;
        }
        if ("parents".equals(fieldName)) {
            return parents;
        }
        if ("dataSets".equals(fieldName)) {
            return dataSets;
        }
        if ("expressionResultsStart".equals(fieldName)) {
            return expressionResultsStart;
        }
        if ("id".equals(fieldName)) {
            return id;
        }
        if (!org.intermine.model.bio.GOTerm.class.equals(getClass())) {
            return TypeUtil.getFieldValue(this, fieldName);
        }
        throw new IllegalArgumentException("Unknown field " + fieldName);
    }
    public Object getFieldProxy(final String fieldName) throws IllegalAccessException {
        if ("identifier".equals(fieldName)) {
            return identifier;
        }
        if ("stageStartHour".equals(fieldName)) {
            return stageStartHour;
        }
        if ("obsolete".equals(fieldName)) {
            return obsolete;
        }
        if ("primaryIdentifier".equals(fieldName)) {
            return primaryIdentifier;
        }
        if ("namespace".equals(fieldName)) {
            return namespace;
        }
        if ("name".equals(fieldName)) {
            return name;
        }
        if ("stageAbbreviation".equals(fieldName)) {
            return stageAbbreviation;
        }
        if ("description".equals(fieldName)) {
            return description;
        }
        if ("stageEndHour".equals(fieldName)) {
            return stageEndHour;
        }
        if ("ontology".equals(fieldName)) {
            return ontology;
        }
        if ("organism".equals(fieldName)) {
            return organism;
        }
        if ("synonyms".equals(fieldName)) {
            return synonyms;
        }
        if ("expressionResultsEnd".equals(fieldName)) {
            return expressionResultsEnd;
        }
        if ("ontologyAnnotations".equals(fieldName)) {
            return ontologyAnnotations;
        }
        if ("relations".equals(fieldName)) {
            return relations;
        }
        if ("expressionResultSubterms".equals(fieldName)) {
            return expressionResultSubterms;
        }
        if ("phenotypesStartStg".equals(fieldName)) {
            return phenotypesStartStg;
        }
        if ("phenotypesEndStg".equals(fieldName)) {
            return phenotypesEndStg;
        }
        if ("parents".equals(fieldName)) {
            return parents;
        }
        if ("dataSets".equals(fieldName)) {
            return dataSets;
        }
        if ("expressionResultsStart".equals(fieldName)) {
            return expressionResultsStart;
        }
        if ("id".equals(fieldName)) {
            return id;
        }
        if (!org.intermine.model.bio.GOTerm.class.equals(getClass())) {
            return TypeUtil.getFieldProxy(this, fieldName);
        }
        throw new IllegalArgumentException("Unknown field " + fieldName);
    }
    public void setFieldValue(final String fieldName, final Object value) {
        if ("identifier".equals(fieldName)) {
            identifier = (java.lang.String) value;
        } else if ("stageStartHour".equals(fieldName)) {
            stageStartHour = (java.lang.String) value;
        } else if ("obsolete".equals(fieldName)) {
            obsolete = (java.lang.Boolean) value;
        } else if ("primaryIdentifier".equals(fieldName)) {
            primaryIdentifier = (java.lang.String) value;
        } else if ("namespace".equals(fieldName)) {
            namespace = (java.lang.String) value;
        } else if ("name".equals(fieldName)) {
            name = (java.lang.String) value;
        } else if ("stageAbbreviation".equals(fieldName)) {
            stageAbbreviation = (java.lang.String) value;
        } else if ("description".equals(fieldName)) {
            description = (java.lang.String) value;
        } else if ("stageEndHour".equals(fieldName)) {
            stageEndHour = (java.lang.String) value;
        } else if ("ontology".equals(fieldName)) {
            ontology = (org.intermine.model.InterMineObject) value;
        } else if ("organism".equals(fieldName)) {
            organism = (org.intermine.model.InterMineObject) value;
        } else if ("synonyms".equals(fieldName)) {
            synonyms = (java.util.Set) value;
        } else if ("expressionResultsEnd".equals(fieldName)) {
            expressionResultsEnd = (java.util.Set) value;
        } else if ("ontologyAnnotations".equals(fieldName)) {
            ontologyAnnotations = (java.util.Set) value;
        } else if ("relations".equals(fieldName)) {
            relations = (java.util.Set) value;
        } else if ("expressionResultSubterms".equals(fieldName)) {
            expressionResultSubterms = (java.util.Set) value;
        } else if ("phenotypesStartStg".equals(fieldName)) {
            phenotypesStartStg = (java.util.Set) value;
        } else if ("phenotypesEndStg".equals(fieldName)) {
            phenotypesEndStg = (java.util.Set) value;
        } else if ("parents".equals(fieldName)) {
            parents = (java.util.Set) value;
        } else if ("dataSets".equals(fieldName)) {
            dataSets = (java.util.Set) value;
        } else if ("expressionResultsStart".equals(fieldName)) {
            expressionResultsStart = (java.util.Set) value;
        } else if ("id".equals(fieldName)) {
            id = (java.lang.Integer) value;
        } else {
            if (!org.intermine.model.bio.GOTerm.class.equals(getClass())) {
                TypeUtil.setFieldValue(this, fieldName, value);
                return;
            }
            throw new IllegalArgumentException("Unknown field " + fieldName);
        }
    }
    public Class<?> getFieldType(final String fieldName) {
        if ("identifier".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("stageStartHour".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("obsolete".equals(fieldName)) {
            return java.lang.Boolean.class;
        }
        if ("primaryIdentifier".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("namespace".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("name".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("stageAbbreviation".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("description".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("stageEndHour".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("ontology".equals(fieldName)) {
            return org.intermine.model.bio.Ontology.class;
        }
        if ("organism".equals(fieldName)) {
            return org.intermine.model.bio.Organism.class;
        }
        if ("synonyms".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("expressionResultsEnd".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("ontologyAnnotations".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("relations".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("expressionResultSubterms".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("phenotypesStartStg".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("phenotypesEndStg".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("parents".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("dataSets".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("expressionResultsStart".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("id".equals(fieldName)) {
            return java.lang.Integer.class;
        }
        if (!org.intermine.model.bio.GOTerm.class.equals(getClass())) {
            return TypeUtil.getFieldType(org.intermine.model.bio.GOTerm.class, fieldName);
        }
        throw new IllegalArgumentException("Unknown field " + fieldName);
    }
    public StringConstructor getoBJECT() {
        if (!org.intermine.model.bio.GOTermShadow.class.equals(getClass())) {
            return NotXmlRenderer.render(this);
        }
        StringConstructor sb = new StringConstructor();
        sb.append("$_^org.intermine.model.bio.GOTerm");
        if (identifier != null) {
            sb.append("$_^aidentifier$_^");
            String string = identifier;
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
        if (stageStartHour != null) {
            sb.append("$_^astageStartHour$_^");
            String string = stageStartHour;
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
        if (obsolete != null) {
            sb.append("$_^aobsolete$_^").append(obsolete);
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
        if (namespace != null) {
            sb.append("$_^anamespace$_^");
            String string = namespace;
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
        if (stageAbbreviation != null) {
            sb.append("$_^astageAbbreviation$_^");
            String string = stageAbbreviation;
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
        if (stageEndHour != null) {
            sb.append("$_^astageEndHour$_^");
            String string = stageEndHour;
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
        if (ontology != null) {
            sb.append("$_^rontology$_^").append(ontology.getId());
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
        if (!org.intermine.model.bio.GOTermShadow.class.equals(getClass())) {
            throw new IllegalStateException("Class " + getClass().getName() + " does not match code (org.intermine.model.bio.GOTerm)");
        }
        for (int i = 2; i < notXml.length;) {
            int startI = i;
            if ((i < notXml.length) && "aidentifier".equals(notXml[i])) {
                i++;
                StringBuilder string = null;
                while ((i + 1 < notXml.length) && (notXml[i + 1].charAt(0) == 'd')) {
                    if (string == null) string = new StringBuilder(notXml[i]);
                    i++;
                    string.append("$_^").append(notXml[i].substring(1));
                }
                identifier = string == null ? notXml[i] : string.toString();
                i++;
            }
            if ((i < notXml.length) && "astageStartHour".equals(notXml[i])) {
                i++;
                StringBuilder string = null;
                while ((i + 1 < notXml.length) && (notXml[i + 1].charAt(0) == 'd')) {
                    if (string == null) string = new StringBuilder(notXml[i]);
                    i++;
                    string.append("$_^").append(notXml[i].substring(1));
                }
                stageStartHour = string == null ? notXml[i] : string.toString();
                i++;
            }
            if ((i < notXml.length) && "aobsolete".equals(notXml[i])) {
                i++;
                obsolete = Boolean.valueOf(notXml[i]);
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
            if ((i < notXml.length) && "anamespace".equals(notXml[i])) {
                i++;
                StringBuilder string = null;
                while ((i + 1 < notXml.length) && (notXml[i + 1].charAt(0) == 'd')) {
                    if (string == null) string = new StringBuilder(notXml[i]);
                    i++;
                    string.append("$_^").append(notXml[i].substring(1));
                }
                namespace = string == null ? notXml[i] : string.toString();
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
            if ((i < notXml.length) && "astageAbbreviation".equals(notXml[i])) {
                i++;
                StringBuilder string = null;
                while ((i + 1 < notXml.length) && (notXml[i + 1].charAt(0) == 'd')) {
                    if (string == null) string = new StringBuilder(notXml[i]);
                    i++;
                    string.append("$_^").append(notXml[i].substring(1));
                }
                stageAbbreviation = string == null ? notXml[i] : string.toString();
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
            if ((i < notXml.length) && "astageEndHour".equals(notXml[i])) {
                i++;
                StringBuilder string = null;
                while ((i + 1 < notXml.length) && (notXml[i + 1].charAt(0) == 'd')) {
                    if (string == null) string = new StringBuilder(notXml[i]);
                    i++;
                    string.append("$_^").append(notXml[i].substring(1));
                }
                stageEndHour = string == null ? notXml[i] : string.toString();
                i++;
            }
            if ((i < notXml.length) &&"rontology".equals(notXml[i])) {
                i++;
                ontology = new ProxyReference(os, Integer.valueOf(notXml[i]), org.intermine.model.bio.Ontology.class);
                i++;
            };
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
        synonyms = new ProxyCollection<org.intermine.model.bio.OntologyTermSynonym>(os, this, "synonyms", org.intermine.model.bio.OntologyTermSynonym.class);
        expressionResultsEnd = new ProxyCollection<org.intermine.model.bio.ExpressionResult>(os, this, "expressionResultsEnd", org.intermine.model.bio.ExpressionResult.class);
        ontologyAnnotations = new ProxyCollection<org.intermine.model.bio.OntologyAnnotation>(os, this, "ontologyAnnotations", org.intermine.model.bio.OntologyAnnotation.class);
        relations = new ProxyCollection<org.intermine.model.bio.OntologyRelation>(os, this, "relations", org.intermine.model.bio.OntologyRelation.class);
        expressionResultSubterms = new ProxyCollection<org.intermine.model.bio.ExpressionResult>(os, this, "expressionResultSubterms", org.intermine.model.bio.ExpressionResult.class);
        phenotypesStartStg = new ProxyCollection<org.intermine.model.bio.Phenotype>(os, this, "phenotypesStartStg", org.intermine.model.bio.Phenotype.class);
        phenotypesEndStg = new ProxyCollection<org.intermine.model.bio.Phenotype>(os, this, "phenotypesEndStg", org.intermine.model.bio.Phenotype.class);
        parents = new ProxyCollection<org.intermine.model.bio.OntologyTerm>(os, this, "parents", org.intermine.model.bio.OntologyTerm.class);
        dataSets = new ProxyCollection<org.intermine.model.bio.DataSet>(os, this, "dataSets", org.intermine.model.bio.DataSet.class);
        expressionResultsStart = new ProxyCollection<org.intermine.model.bio.ExpressionResult>(os, this, "expressionResultsStart", org.intermine.model.bio.ExpressionResult.class);
    }
    public void addCollectionElement(final String fieldName, final org.intermine.model.InterMineObject element) {
        if ("synonyms".equals(fieldName)) {
            synonyms.add((org.intermine.model.bio.OntologyTermSynonym) element);
        } else if ("expressionResultsEnd".equals(fieldName)) {
            expressionResultsEnd.add((org.intermine.model.bio.ExpressionResult) element);
        } else if ("ontologyAnnotations".equals(fieldName)) {
            ontologyAnnotations.add((org.intermine.model.bio.OntologyAnnotation) element);
        } else if ("relations".equals(fieldName)) {
            relations.add((org.intermine.model.bio.OntologyRelation) element);
        } else if ("expressionResultSubterms".equals(fieldName)) {
            expressionResultSubterms.add((org.intermine.model.bio.ExpressionResult) element);
        } else if ("phenotypesStartStg".equals(fieldName)) {
            phenotypesStartStg.add((org.intermine.model.bio.Phenotype) element);
        } else if ("phenotypesEndStg".equals(fieldName)) {
            phenotypesEndStg.add((org.intermine.model.bio.Phenotype) element);
        } else if ("parents".equals(fieldName)) {
            parents.add((org.intermine.model.bio.OntologyTerm) element);
        } else if ("dataSets".equals(fieldName)) {
            dataSets.add((org.intermine.model.bio.DataSet) element);
        } else if ("expressionResultsStart".equals(fieldName)) {
            expressionResultsStart.add((org.intermine.model.bio.ExpressionResult) element);
        } else {
            if (!org.intermine.model.bio.GOTerm.class.equals(getClass())) {
                TypeUtil.addCollectionElement(this, fieldName, element);
                return;
            }
            throw new IllegalArgumentException("Unknown collection " + fieldName);
        }
    }
    public Class<?> getElementType(final String fieldName) {
        if ("synonyms".equals(fieldName)) {
            return org.intermine.model.bio.OntologyTermSynonym.class;
        }
        if ("expressionResultsEnd".equals(fieldName)) {
            return org.intermine.model.bio.ExpressionResult.class;
        }
        if ("ontologyAnnotations".equals(fieldName)) {
            return org.intermine.model.bio.OntologyAnnotation.class;
        }
        if ("relations".equals(fieldName)) {
            return org.intermine.model.bio.OntologyRelation.class;
        }
        if ("expressionResultSubterms".equals(fieldName)) {
            return org.intermine.model.bio.ExpressionResult.class;
        }
        if ("phenotypesStartStg".equals(fieldName)) {
            return org.intermine.model.bio.Phenotype.class;
        }
        if ("phenotypesEndStg".equals(fieldName)) {
            return org.intermine.model.bio.Phenotype.class;
        }
        if ("parents".equals(fieldName)) {
            return org.intermine.model.bio.OntologyTerm.class;
        }
        if ("dataSets".equals(fieldName)) {
            return org.intermine.model.bio.DataSet.class;
        }
        if ("expressionResultsStart".equals(fieldName)) {
            return org.intermine.model.bio.ExpressionResult.class;
        }
        if (!org.intermine.model.bio.GOTerm.class.equals(getClass())) {
            return TypeUtil.getElementType(org.intermine.model.bio.GOTerm.class, fieldName);
        }
        throw new IllegalArgumentException("Unknown field " + fieldName);
    }
}