package org.intermine.model.bio;

import org.intermine.objectstore.ObjectStore;
import org.intermine.objectstore.intermine.NotXmlParser;
import org.intermine.objectstore.intermine.NotXmlRenderer;
import org.intermine.objectstore.proxy.ProxyCollection;
import org.intermine.objectstore.proxy.ProxyReference;
import org.intermine.util.StringConstructor;
import org.intermine.util.TypeUtil;
import org.intermine.model.ShadowClass;

public class ExpressionResultShadow implements ExpressionResult, ShadowClass
{
    public static final Class<ExpressionResult> shadowOf = ExpressionResult.class;
    // Attr: org.intermine.model.bio.ExpressionResult.assay
    protected java.lang.String assay;
    public java.lang.String getAssay() { return assay; }
    public void setAssay(final java.lang.String assay) { this.assay = assay; }

    // Attr: org.intermine.model.bio.ExpressionResult.expressionFound
    protected java.lang.String expressionFound;
    public java.lang.String getExpressionFound() { return expressionFound; }
    public void setExpressionFound(final java.lang.String expressionFound) { this.expressionFound = expressionFound; }

    // Attr: org.intermine.model.bio.ExpressionResult.primaryIdentifier
    protected java.lang.String primaryIdentifier;
    public java.lang.String getPrimaryIdentifier() { return primaryIdentifier; }
    public void setPrimaryIdentifier(final java.lang.String primaryIdentifier) { this.primaryIdentifier = primaryIdentifier; }

    // Ref: org.intermine.model.bio.ExpressionResult.subterm
    protected org.intermine.model.InterMineObject subterm;
    public org.intermine.model.bio.OntologyTerm getSubterm() { if (subterm instanceof org.intermine.objectstore.proxy.ProxyReference) { return ((org.intermine.model.bio.OntologyTerm) ((org.intermine.objectstore.proxy.ProxyReference) subterm).getObject()); }; return (org.intermine.model.bio.OntologyTerm) subterm; }
    public void setSubterm(final org.intermine.model.bio.OntologyTerm subterm) { this.subterm = subterm; }
    public void proxySubterm(final org.intermine.objectstore.proxy.ProxyReference subterm) { this.subterm = subterm; }
    public org.intermine.model.InterMineObject proxGetSubterm() { return subterm; }

    // Ref: org.intermine.model.bio.ExpressionResult.genotypeEnvironment
    protected org.intermine.model.InterMineObject genotypeEnvironment;
    public org.intermine.model.bio.GenotypeEnvironment getGenotypeEnvironment() { if (genotypeEnvironment instanceof org.intermine.objectstore.proxy.ProxyReference) { return ((org.intermine.model.bio.GenotypeEnvironment) ((org.intermine.objectstore.proxy.ProxyReference) genotypeEnvironment).getObject()); }; return (org.intermine.model.bio.GenotypeEnvironment) genotypeEnvironment; }
    public void setGenotypeEnvironment(final org.intermine.model.bio.GenotypeEnvironment genotypeEnvironment) { this.genotypeEnvironment = genotypeEnvironment; }
    public void proxyGenotypeEnvironment(final org.intermine.objectstore.proxy.ProxyReference genotypeEnvironment) { this.genotypeEnvironment = genotypeEnvironment; }
    public org.intermine.model.InterMineObject proxGetGenotypeEnvironment() { return genotypeEnvironment; }

    // Ref: org.intermine.model.bio.ExpressionResult.publication
    protected org.intermine.model.InterMineObject publication;
    public org.intermine.model.bio.Publication getPublication() { if (publication instanceof org.intermine.objectstore.proxy.ProxyReference) { return ((org.intermine.model.bio.Publication) ((org.intermine.objectstore.proxy.ProxyReference) publication).getObject()); }; return (org.intermine.model.bio.Publication) publication; }
    public void setPublication(final org.intermine.model.bio.Publication publication) { this.publication = publication; }
    public void proxyPublication(final org.intermine.objectstore.proxy.ProxyReference publication) { this.publication = publication; }
    public org.intermine.model.InterMineObject proxGetPublication() { return publication; }

    // Ref: org.intermine.model.bio.ExpressionResult.antibody
    protected org.intermine.model.InterMineObject antibody;
    public org.intermine.model.bio.Antibody getAntibody() { if (antibody instanceof org.intermine.objectstore.proxy.ProxyReference) { return ((org.intermine.model.bio.Antibody) ((org.intermine.objectstore.proxy.ProxyReference) antibody).getObject()); }; return (org.intermine.model.bio.Antibody) antibody; }
    public void setAntibody(final org.intermine.model.bio.Antibody antibody) { this.antibody = antibody; }
    public void proxyAntibody(final org.intermine.objectstore.proxy.ProxyReference antibody) { this.antibody = antibody; }
    public org.intermine.model.InterMineObject proxGetAntibody() { return antibody; }

    // Ref: org.intermine.model.bio.ExpressionResult.EFG
    protected org.intermine.model.InterMineObject EFG;
    public org.intermine.model.bio.EFG geteFG() { if (EFG instanceof org.intermine.objectstore.proxy.ProxyReference) { return ((org.intermine.model.bio.EFG) ((org.intermine.objectstore.proxy.ProxyReference) EFG).getObject()); }; return (org.intermine.model.bio.EFG) EFG; }
    public void seteFG(final org.intermine.model.bio.EFG EFG) { this.EFG = EFG; }
    public void proxyeFG(final org.intermine.objectstore.proxy.ProxyReference EFG) { this.EFG = EFG; }
    public org.intermine.model.InterMineObject proxGeteFG() { return EFG; }

    // Ref: org.intermine.model.bio.ExpressionResult.probe
    protected org.intermine.model.InterMineObject probe;
    public org.intermine.model.bio.RNAClone getProbe() { if (probe instanceof org.intermine.objectstore.proxy.ProxyReference) { return ((org.intermine.model.bio.RNAClone) ((org.intermine.objectstore.proxy.ProxyReference) probe).getObject()); }; return (org.intermine.model.bio.RNAClone) probe; }
    public void setProbe(final org.intermine.model.bio.RNAClone probe) { this.probe = probe; }
    public void proxyProbe(final org.intermine.objectstore.proxy.ProxyReference probe) { this.probe = probe; }
    public org.intermine.model.InterMineObject proxGetProbe() { return probe; }

    // Ref: org.intermine.model.bio.ExpressionResult.startStage
    protected org.intermine.model.InterMineObject startStage;
    public org.intermine.model.bio.ZFATerm getStartStage() { if (startStage instanceof org.intermine.objectstore.proxy.ProxyReference) { return ((org.intermine.model.bio.ZFATerm) ((org.intermine.objectstore.proxy.ProxyReference) startStage).getObject()); }; return (org.intermine.model.bio.ZFATerm) startStage; }
    public void setStartStage(final org.intermine.model.bio.ZFATerm startStage) { this.startStage = startStage; }
    public void proxyStartStage(final org.intermine.objectstore.proxy.ProxyReference startStage) { this.startStage = startStage; }
    public org.intermine.model.InterMineObject proxGetStartStage() { return startStage; }

    // Ref: org.intermine.model.bio.ExpressionResult.gene
    protected org.intermine.model.InterMineObject gene;
    public org.intermine.model.bio.Gene getGene() { if (gene instanceof org.intermine.objectstore.proxy.ProxyReference) { return ((org.intermine.model.bio.Gene) ((org.intermine.objectstore.proxy.ProxyReference) gene).getObject()); }; return (org.intermine.model.bio.Gene) gene; }
    public void setGene(final org.intermine.model.bio.Gene gene) { this.gene = gene; }
    public void proxyGene(final org.intermine.objectstore.proxy.ProxyReference gene) { this.gene = gene; }
    public org.intermine.model.InterMineObject proxGetGene() { return gene; }

    // Ref: org.intermine.model.bio.ExpressionResult.anatomy
    protected org.intermine.model.InterMineObject anatomy;
    public org.intermine.model.bio.ZFATerm getAnatomy() { if (anatomy instanceof org.intermine.objectstore.proxy.ProxyReference) { return ((org.intermine.model.bio.ZFATerm) ((org.intermine.objectstore.proxy.ProxyReference) anatomy).getObject()); }; return (org.intermine.model.bio.ZFATerm) anatomy; }
    public void setAnatomy(final org.intermine.model.bio.ZFATerm anatomy) { this.anatomy = anatomy; }
    public void proxyAnatomy(final org.intermine.objectstore.proxy.ProxyReference anatomy) { this.anatomy = anatomy; }
    public org.intermine.model.InterMineObject proxGetAnatomy() { return anatomy; }

    // Ref: org.intermine.model.bio.ExpressionResult.endStage
    protected org.intermine.model.InterMineObject endStage;
    public org.intermine.model.bio.ZFATerm getEndStage() { if (endStage instanceof org.intermine.objectstore.proxy.ProxyReference) { return ((org.intermine.model.bio.ZFATerm) ((org.intermine.objectstore.proxy.ProxyReference) endStage).getObject()); }; return (org.intermine.model.bio.ZFATerm) endStage; }
    public void setEndStage(final org.intermine.model.bio.ZFATerm endStage) { this.endStage = endStage; }
    public void proxyEndStage(final org.intermine.objectstore.proxy.ProxyReference endStage) { this.endStage = endStage; }
    public org.intermine.model.InterMineObject proxGetEndStage() { return endStage; }

    // Col: org.intermine.model.bio.ExpressionResult.figures
    protected java.util.Set<org.intermine.model.bio.Figure> figures = new java.util.HashSet<org.intermine.model.bio.Figure>();
    public java.util.Set<org.intermine.model.bio.Figure> getFigures() { return figures; }
    public void setFigures(final java.util.Set<org.intermine.model.bio.Figure> figures) { this.figures = figures; }
    public void addFigures(final org.intermine.model.bio.Figure arg) { figures.add(arg); }

    // Col: org.intermine.model.bio.ExpressionResult.crossReferences
    protected java.util.Set<org.intermine.model.bio.CrossReference> crossReferences = new java.util.HashSet<org.intermine.model.bio.CrossReference>();
    public java.util.Set<org.intermine.model.bio.CrossReference> getCrossReferences() { return crossReferences; }
    public void setCrossReferences(final java.util.Set<org.intermine.model.bio.CrossReference> crossReferences) { this.crossReferences = crossReferences; }
    public void addCrossReferences(final org.intermine.model.bio.CrossReference arg) { crossReferences.add(arg); }

    // Attr: org.intermine.model.InterMineObject.id
    protected java.lang.Integer id;
    public java.lang.Integer getId() { return id; }
    public void setId(final java.lang.Integer id) { this.id = id; }

    @Override public boolean equals(Object o) { return (o instanceof ExpressionResult && id != null) ? id.equals(((ExpressionResult)o).getId()) : this == o; }
    @Override public int hashCode() { return (id != null) ? id.hashCode() : super.hashCode(); }
    @Override public String toString() { return "ExpressionResult [EFG=" + (EFG == null ? "null" : (EFG.getId() == null ? "no id" : EFG.getId().toString())) + ", anatomy=" + (anatomy == null ? "null" : (anatomy.getId() == null ? "no id" : anatomy.getId().toString())) + ", antibody=" + (antibody == null ? "null" : (antibody.getId() == null ? "no id" : antibody.getId().toString())) + ", assay=\"" + assay + "\", endStage=" + (endStage == null ? "null" : (endStage.getId() == null ? "no id" : endStage.getId().toString())) + ", expressionFound=\"" + expressionFound + "\", gene=" + (gene == null ? "null" : (gene.getId() == null ? "no id" : gene.getId().toString())) + ", genotypeEnvironment=" + (genotypeEnvironment == null ? "null" : (genotypeEnvironment.getId() == null ? "no id" : genotypeEnvironment.getId().toString())) + ", id=\"" + id + "\", primaryIdentifier=\"" + primaryIdentifier + "\", probe=" + (probe == null ? "null" : (probe.getId() == null ? "no id" : probe.getId().toString())) + ", publication=" + (publication == null ? "null" : (publication.getId() == null ? "no id" : publication.getId().toString())) + ", startStage=" + (startStage == null ? "null" : (startStage.getId() == null ? "no id" : startStage.getId().toString())) + ", subterm=" + (subterm == null ? "null" : (subterm.getId() == null ? "no id" : subterm.getId().toString())) + "]"; }
    public Object getFieldValue(final String fieldName) throws IllegalAccessException {
        if ("assay".equals(fieldName)) {
            return assay;
        }
        if ("expressionFound".equals(fieldName)) {
            return expressionFound;
        }
        if ("primaryIdentifier".equals(fieldName)) {
            return primaryIdentifier;
        }
        if ("subterm".equals(fieldName)) {
            if (subterm instanceof ProxyReference) {
                return ((ProxyReference) subterm).getObject();
            } else {
                return subterm;
            }
        }
        if ("genotypeEnvironment".equals(fieldName)) {
            if (genotypeEnvironment instanceof ProxyReference) {
                return ((ProxyReference) genotypeEnvironment).getObject();
            } else {
                return genotypeEnvironment;
            }
        }
        if ("publication".equals(fieldName)) {
            if (publication instanceof ProxyReference) {
                return ((ProxyReference) publication).getObject();
            } else {
                return publication;
            }
        }
        if ("antibody".equals(fieldName)) {
            if (antibody instanceof ProxyReference) {
                return ((ProxyReference) antibody).getObject();
            } else {
                return antibody;
            }
        }
        if ("EFG".equals(fieldName)) {
            if (EFG instanceof ProxyReference) {
                return ((ProxyReference) EFG).getObject();
            } else {
                return EFG;
            }
        }
        if ("probe".equals(fieldName)) {
            if (probe instanceof ProxyReference) {
                return ((ProxyReference) probe).getObject();
            } else {
                return probe;
            }
        }
        if ("startStage".equals(fieldName)) {
            if (startStage instanceof ProxyReference) {
                return ((ProxyReference) startStage).getObject();
            } else {
                return startStage;
            }
        }
        if ("gene".equals(fieldName)) {
            if (gene instanceof ProxyReference) {
                return ((ProxyReference) gene).getObject();
            } else {
                return gene;
            }
        }
        if ("anatomy".equals(fieldName)) {
            if (anatomy instanceof ProxyReference) {
                return ((ProxyReference) anatomy).getObject();
            } else {
                return anatomy;
            }
        }
        if ("endStage".equals(fieldName)) {
            if (endStage instanceof ProxyReference) {
                return ((ProxyReference) endStage).getObject();
            } else {
                return endStage;
            }
        }
        if ("figures".equals(fieldName)) {
            return figures;
        }
        if ("crossReferences".equals(fieldName)) {
            return crossReferences;
        }
        if ("id".equals(fieldName)) {
            return id;
        }
        if (!org.intermine.model.bio.ExpressionResult.class.equals(getClass())) {
            return TypeUtil.getFieldValue(this, fieldName);
        }
        throw new IllegalArgumentException("Unknown field " + fieldName);
    }
    public Object getFieldProxy(final String fieldName) throws IllegalAccessException {
        if ("assay".equals(fieldName)) {
            return assay;
        }
        if ("expressionFound".equals(fieldName)) {
            return expressionFound;
        }
        if ("primaryIdentifier".equals(fieldName)) {
            return primaryIdentifier;
        }
        if ("subterm".equals(fieldName)) {
            return subterm;
        }
        if ("genotypeEnvironment".equals(fieldName)) {
            return genotypeEnvironment;
        }
        if ("publication".equals(fieldName)) {
            return publication;
        }
        if ("antibody".equals(fieldName)) {
            return antibody;
        }
        if ("EFG".equals(fieldName)) {
            return EFG;
        }
        if ("probe".equals(fieldName)) {
            return probe;
        }
        if ("startStage".equals(fieldName)) {
            return startStage;
        }
        if ("gene".equals(fieldName)) {
            return gene;
        }
        if ("anatomy".equals(fieldName)) {
            return anatomy;
        }
        if ("endStage".equals(fieldName)) {
            return endStage;
        }
        if ("figures".equals(fieldName)) {
            return figures;
        }
        if ("crossReferences".equals(fieldName)) {
            return crossReferences;
        }
        if ("id".equals(fieldName)) {
            return id;
        }
        if (!org.intermine.model.bio.ExpressionResult.class.equals(getClass())) {
            return TypeUtil.getFieldProxy(this, fieldName);
        }
        throw new IllegalArgumentException("Unknown field " + fieldName);
    }
    public void setFieldValue(final String fieldName, final Object value) {
        if ("assay".equals(fieldName)) {
            assay = (java.lang.String) value;
        } else if ("expressionFound".equals(fieldName)) {
            expressionFound = (java.lang.String) value;
        } else if ("primaryIdentifier".equals(fieldName)) {
            primaryIdentifier = (java.lang.String) value;
        } else if ("subterm".equals(fieldName)) {
            subterm = (org.intermine.model.InterMineObject) value;
        } else if ("genotypeEnvironment".equals(fieldName)) {
            genotypeEnvironment = (org.intermine.model.InterMineObject) value;
        } else if ("publication".equals(fieldName)) {
            publication = (org.intermine.model.InterMineObject) value;
        } else if ("antibody".equals(fieldName)) {
            antibody = (org.intermine.model.InterMineObject) value;
        } else if ("EFG".equals(fieldName)) {
            EFG = (org.intermine.model.InterMineObject) value;
        } else if ("probe".equals(fieldName)) {
            probe = (org.intermine.model.InterMineObject) value;
        } else if ("startStage".equals(fieldName)) {
            startStage = (org.intermine.model.InterMineObject) value;
        } else if ("gene".equals(fieldName)) {
            gene = (org.intermine.model.InterMineObject) value;
        } else if ("anatomy".equals(fieldName)) {
            anatomy = (org.intermine.model.InterMineObject) value;
        } else if ("endStage".equals(fieldName)) {
            endStage = (org.intermine.model.InterMineObject) value;
        } else if ("figures".equals(fieldName)) {
            figures = (java.util.Set) value;
        } else if ("crossReferences".equals(fieldName)) {
            crossReferences = (java.util.Set) value;
        } else if ("id".equals(fieldName)) {
            id = (java.lang.Integer) value;
        } else {
            if (!org.intermine.model.bio.ExpressionResult.class.equals(getClass())) {
                TypeUtil.setFieldValue(this, fieldName, value);
                return;
            }
            throw new IllegalArgumentException("Unknown field " + fieldName);
        }
    }
    public Class<?> getFieldType(final String fieldName) {
        if ("assay".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("expressionFound".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("primaryIdentifier".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("subterm".equals(fieldName)) {
            return org.intermine.model.bio.OntologyTerm.class;
        }
        if ("genotypeEnvironment".equals(fieldName)) {
            return org.intermine.model.bio.GenotypeEnvironment.class;
        }
        if ("publication".equals(fieldName)) {
            return org.intermine.model.bio.Publication.class;
        }
        if ("antibody".equals(fieldName)) {
            return org.intermine.model.bio.Antibody.class;
        }
        if ("EFG".equals(fieldName)) {
            return org.intermine.model.bio.EFG.class;
        }
        if ("probe".equals(fieldName)) {
            return org.intermine.model.bio.RNAClone.class;
        }
        if ("startStage".equals(fieldName)) {
            return org.intermine.model.bio.ZFATerm.class;
        }
        if ("gene".equals(fieldName)) {
            return org.intermine.model.bio.Gene.class;
        }
        if ("anatomy".equals(fieldName)) {
            return org.intermine.model.bio.ZFATerm.class;
        }
        if ("endStage".equals(fieldName)) {
            return org.intermine.model.bio.ZFATerm.class;
        }
        if ("figures".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("crossReferences".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("id".equals(fieldName)) {
            return java.lang.Integer.class;
        }
        if (!org.intermine.model.bio.ExpressionResult.class.equals(getClass())) {
            return TypeUtil.getFieldType(org.intermine.model.bio.ExpressionResult.class, fieldName);
        }
        throw new IllegalArgumentException("Unknown field " + fieldName);
    }
    public StringConstructor getoBJECT() {
        if (!org.intermine.model.bio.ExpressionResultShadow.class.equals(getClass())) {
            return NotXmlRenderer.render(this);
        }
        StringConstructor sb = new StringConstructor();
        sb.append("$_^org.intermine.model.bio.ExpressionResult");
        if (assay != null) {
            sb.append("$_^aassay$_^");
            String string = assay;
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
        if (expressionFound != null) {
            sb.append("$_^aexpressionFound$_^");
            String string = expressionFound;
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
        if (subterm != null) {
            sb.append("$_^rsubterm$_^").append(subterm.getId());
        }
        if (genotypeEnvironment != null) {
            sb.append("$_^rgenotypeEnvironment$_^").append(genotypeEnvironment.getId());
        }
        if (publication != null) {
            sb.append("$_^rpublication$_^").append(publication.getId());
        }
        if (antibody != null) {
            sb.append("$_^rantibody$_^").append(antibody.getId());
        }
        if (EFG != null) {
            sb.append("$_^rEFG$_^").append(EFG.getId());
        }
        if (probe != null) {
            sb.append("$_^rprobe$_^").append(probe.getId());
        }
        if (startStage != null) {
            sb.append("$_^rstartStage$_^").append(startStage.getId());
        }
        if (gene != null) {
            sb.append("$_^rgene$_^").append(gene.getId());
        }
        if (anatomy != null) {
            sb.append("$_^ranatomy$_^").append(anatomy.getId());
        }
        if (endStage != null) {
            sb.append("$_^rendStage$_^").append(endStage.getId());
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
        if (!org.intermine.model.bio.ExpressionResultShadow.class.equals(getClass())) {
            throw new IllegalStateException("Class " + getClass().getName() + " does not match code (org.intermine.model.bio.ExpressionResult)");
        }
        for (int i = 2; i < notXml.length;) {
            int startI = i;
            if ((i < notXml.length) && "aassay".equals(notXml[i])) {
                i++;
                StringBuilder string = null;
                while ((i + 1 < notXml.length) && (notXml[i + 1].charAt(0) == 'd')) {
                    if (string == null) string = new StringBuilder(notXml[i]);
                    i++;
                    string.append("$_^").append(notXml[i].substring(1));
                }
                assay = string == null ? notXml[i] : string.toString();
                i++;
            }
            if ((i < notXml.length) && "aexpressionFound".equals(notXml[i])) {
                i++;
                StringBuilder string = null;
                while ((i + 1 < notXml.length) && (notXml[i + 1].charAt(0) == 'd')) {
                    if (string == null) string = new StringBuilder(notXml[i]);
                    i++;
                    string.append("$_^").append(notXml[i].substring(1));
                }
                expressionFound = string == null ? notXml[i] : string.toString();
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
            if ((i < notXml.length) &&"rsubterm".equals(notXml[i])) {
                i++;
                subterm = new ProxyReference(os, Integer.valueOf(notXml[i]), org.intermine.model.bio.OntologyTerm.class);
                i++;
            };
            if ((i < notXml.length) &&"rgenotypeEnvironment".equals(notXml[i])) {
                i++;
                genotypeEnvironment = new ProxyReference(os, Integer.valueOf(notXml[i]), org.intermine.model.bio.GenotypeEnvironment.class);
                i++;
            };
            if ((i < notXml.length) &&"rpublication".equals(notXml[i])) {
                i++;
                publication = new ProxyReference(os, Integer.valueOf(notXml[i]), org.intermine.model.bio.Publication.class);
                i++;
            };
            if ((i < notXml.length) &&"rantibody".equals(notXml[i])) {
                i++;
                antibody = new ProxyReference(os, Integer.valueOf(notXml[i]), org.intermine.model.bio.Antibody.class);
                i++;
            };
            if ((i < notXml.length) &&"rEFG".equals(notXml[i])) {
                i++;
                EFG = new ProxyReference(os, Integer.valueOf(notXml[i]), org.intermine.model.bio.EFG.class);
                i++;
            };
            if ((i < notXml.length) &&"rprobe".equals(notXml[i])) {
                i++;
                probe = new ProxyReference(os, Integer.valueOf(notXml[i]), org.intermine.model.bio.RNAClone.class);
                i++;
            };
            if ((i < notXml.length) &&"rstartStage".equals(notXml[i])) {
                i++;
                startStage = new ProxyReference(os, Integer.valueOf(notXml[i]), org.intermine.model.bio.ZFATerm.class);
                i++;
            };
            if ((i < notXml.length) &&"rgene".equals(notXml[i])) {
                i++;
                gene = new ProxyReference(os, Integer.valueOf(notXml[i]), org.intermine.model.bio.Gene.class);
                i++;
            };
            if ((i < notXml.length) &&"ranatomy".equals(notXml[i])) {
                i++;
                anatomy = new ProxyReference(os, Integer.valueOf(notXml[i]), org.intermine.model.bio.ZFATerm.class);
                i++;
            };
            if ((i < notXml.length) &&"rendStage".equals(notXml[i])) {
                i++;
                endStage = new ProxyReference(os, Integer.valueOf(notXml[i]), org.intermine.model.bio.ZFATerm.class);
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
        figures = new ProxyCollection<org.intermine.model.bio.Figure>(os, this, "figures", org.intermine.model.bio.Figure.class);
        crossReferences = new ProxyCollection<org.intermine.model.bio.CrossReference>(os, this, "crossReferences", org.intermine.model.bio.CrossReference.class);
    }
    public void addCollectionElement(final String fieldName, final org.intermine.model.InterMineObject element) {
        if ("figures".equals(fieldName)) {
            figures.add((org.intermine.model.bio.Figure) element);
        } else if ("crossReferences".equals(fieldName)) {
            crossReferences.add((org.intermine.model.bio.CrossReference) element);
        } else {
            if (!org.intermine.model.bio.ExpressionResult.class.equals(getClass())) {
                TypeUtil.addCollectionElement(this, fieldName, element);
                return;
            }
            throw new IllegalArgumentException("Unknown collection " + fieldName);
        }
    }
    public Class<?> getElementType(final String fieldName) {
        if ("figures".equals(fieldName)) {
            return org.intermine.model.bio.Figure.class;
        }
        if ("crossReferences".equals(fieldName)) {
            return org.intermine.model.bio.CrossReference.class;
        }
        if (!org.intermine.model.bio.ExpressionResult.class.equals(getClass())) {
            return TypeUtil.getElementType(org.intermine.model.bio.ExpressionResult.class, fieldName);
        }
        throw new IllegalArgumentException("Unknown field " + fieldName);
    }
}
