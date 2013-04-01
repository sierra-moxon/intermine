package org.intermine.model.bio;

import org.intermine.objectstore.ObjectStore;
import org.intermine.objectstore.intermine.NotXmlParser;
import org.intermine.objectstore.intermine.NotXmlRenderer;
import org.intermine.objectstore.proxy.ProxyReference;
import org.intermine.util.StringConstructor;
import org.intermine.util.TypeUtil;
import org.intermine.model.ShadowClass;

public class PhenotypeShadow implements Phenotype, ShadowClass
{
    public static final Class<Phenotype> shadowOf = Phenotype.class;
    // Attr: org.intermine.model.bio.Phenotype.primaryIdentifier
    protected java.lang.String primaryIdentifier;
    public java.lang.String getPrimaryIdentifier() { return primaryIdentifier; }
    public void setPrimaryIdentifier(final java.lang.String primaryIdentifier) { this.primaryIdentifier = primaryIdentifier; }

    // Attr: org.intermine.model.bio.Phenotype.tag
    protected java.lang.String tag;
    public java.lang.String getTag() { return tag; }
    public void setTag(final java.lang.String tag) { this.tag = tag; }

    // Ref: org.intermine.model.bio.Phenotype.startStage
    protected org.intermine.model.InterMineObject startStage;
    public org.intermine.model.bio.OntologyTerm getStartStage() { if (startStage instanceof org.intermine.objectstore.proxy.ProxyReference) { return ((org.intermine.model.bio.OntologyTerm) ((org.intermine.objectstore.proxy.ProxyReference) startStage).getObject()); }; return (org.intermine.model.bio.OntologyTerm) startStage; }
    public void setStartStage(final org.intermine.model.bio.OntologyTerm startStage) { this.startStage = startStage; }
    public void proxyStartStage(final org.intermine.objectstore.proxy.ProxyReference startStage) { this.startStage = startStage; }
    public org.intermine.model.InterMineObject proxGetStartStage() { return startStage; }

    // Ref: org.intermine.model.bio.Phenotype.superTerm
    protected org.intermine.model.InterMineObject superTerm;
    public org.intermine.model.bio.OntologyTerm getSuperTerm() { if (superTerm instanceof org.intermine.objectstore.proxy.ProxyReference) { return ((org.intermine.model.bio.OntologyTerm) ((org.intermine.objectstore.proxy.ProxyReference) superTerm).getObject()); }; return (org.intermine.model.bio.OntologyTerm) superTerm; }
    public void setSuperTerm(final org.intermine.model.bio.OntologyTerm superTerm) { this.superTerm = superTerm; }
    public void proxySuperTerm(final org.intermine.objectstore.proxy.ProxyReference superTerm) { this.superTerm = superTerm; }
    public org.intermine.model.InterMineObject proxGetSuperTerm() { return superTerm; }

    // Ref: org.intermine.model.bio.Phenotype.relatedSubTerm
    protected org.intermine.model.InterMineObject relatedSubTerm;
    public org.intermine.model.bio.OntologyTerm getRelatedSubTerm() { if (relatedSubTerm instanceof org.intermine.objectstore.proxy.ProxyReference) { return ((org.intermine.model.bio.OntologyTerm) ((org.intermine.objectstore.proxy.ProxyReference) relatedSubTerm).getObject()); }; return (org.intermine.model.bio.OntologyTerm) relatedSubTerm; }
    public void setRelatedSubTerm(final org.intermine.model.bio.OntologyTerm relatedSubTerm) { this.relatedSubTerm = relatedSubTerm; }
    public void proxyRelatedSubTerm(final org.intermine.objectstore.proxy.ProxyReference relatedSubTerm) { this.relatedSubTerm = relatedSubTerm; }
    public org.intermine.model.InterMineObject proxGetRelatedSubTerm() { return relatedSubTerm; }

    // Ref: org.intermine.model.bio.Phenotype.relatedSuperTerm
    protected org.intermine.model.InterMineObject relatedSuperTerm;
    public org.intermine.model.bio.OntologyTerm getRelatedSuperTerm() { if (relatedSuperTerm instanceof org.intermine.objectstore.proxy.ProxyReference) { return ((org.intermine.model.bio.OntologyTerm) ((org.intermine.objectstore.proxy.ProxyReference) relatedSuperTerm).getObject()); }; return (org.intermine.model.bio.OntologyTerm) relatedSuperTerm; }
    public void setRelatedSuperTerm(final org.intermine.model.bio.OntologyTerm relatedSuperTerm) { this.relatedSuperTerm = relatedSuperTerm; }
    public void proxyRelatedSuperTerm(final org.intermine.objectstore.proxy.ProxyReference relatedSuperTerm) { this.relatedSuperTerm = relatedSuperTerm; }
    public org.intermine.model.InterMineObject proxGetRelatedSuperTerm() { return relatedSuperTerm; }

    // Ref: org.intermine.model.bio.Phenotype.phenotypeTerm
    protected org.intermine.model.InterMineObject phenotypeTerm;
    public org.intermine.model.bio.PATOTerm getPhenotypeTerm() { if (phenotypeTerm instanceof org.intermine.objectstore.proxy.ProxyReference) { return ((org.intermine.model.bio.PATOTerm) ((org.intermine.objectstore.proxy.ProxyReference) phenotypeTerm).getObject()); }; return (org.intermine.model.bio.PATOTerm) phenotypeTerm; }
    public void setPhenotypeTerm(final org.intermine.model.bio.PATOTerm phenotypeTerm) { this.phenotypeTerm = phenotypeTerm; }
    public void proxyPhenotypeTerm(final org.intermine.objectstore.proxy.ProxyReference phenotypeTerm) { this.phenotypeTerm = phenotypeTerm; }
    public org.intermine.model.InterMineObject proxGetPhenotypeTerm() { return phenotypeTerm; }

    // Ref: org.intermine.model.bio.Phenotype.subTerm
    protected org.intermine.model.InterMineObject subTerm;
    public org.intermine.model.bio.OntologyTerm getSubTerm() { if (subTerm instanceof org.intermine.objectstore.proxy.ProxyReference) { return ((org.intermine.model.bio.OntologyTerm) ((org.intermine.objectstore.proxy.ProxyReference) subTerm).getObject()); }; return (org.intermine.model.bio.OntologyTerm) subTerm; }
    public void setSubTerm(final org.intermine.model.bio.OntologyTerm subTerm) { this.subTerm = subTerm; }
    public void proxySubTerm(final org.intermine.objectstore.proxy.ProxyReference subTerm) { this.subTerm = subTerm; }
    public org.intermine.model.InterMineObject proxGetSubTerm() { return subTerm; }

    // Ref: org.intermine.model.bio.Phenotype.genotypeEnvironment
    protected org.intermine.model.InterMineObject genotypeEnvironment;
    public org.intermine.model.bio.GenotypeEnvironment getGenotypeEnvironment() { if (genotypeEnvironment instanceof org.intermine.objectstore.proxy.ProxyReference) { return ((org.intermine.model.bio.GenotypeEnvironment) ((org.intermine.objectstore.proxy.ProxyReference) genotypeEnvironment).getObject()); }; return (org.intermine.model.bio.GenotypeEnvironment) genotypeEnvironment; }
    public void setGenotypeEnvironment(final org.intermine.model.bio.GenotypeEnvironment genotypeEnvironment) { this.genotypeEnvironment = genotypeEnvironment; }
    public void proxyGenotypeEnvironment(final org.intermine.objectstore.proxy.ProxyReference genotypeEnvironment) { this.genotypeEnvironment = genotypeEnvironment; }
    public org.intermine.model.InterMineObject proxGetGenotypeEnvironment() { return genotypeEnvironment; }

    // Ref: org.intermine.model.bio.Phenotype.figure
    protected org.intermine.model.InterMineObject figure;
    public org.intermine.model.bio.Figure getFigure() { if (figure instanceof org.intermine.objectstore.proxy.ProxyReference) { return ((org.intermine.model.bio.Figure) ((org.intermine.objectstore.proxy.ProxyReference) figure).getObject()); }; return (org.intermine.model.bio.Figure) figure; }
    public void setFigure(final org.intermine.model.bio.Figure figure) { this.figure = figure; }
    public void proxyFigure(final org.intermine.objectstore.proxy.ProxyReference figure) { this.figure = figure; }
    public org.intermine.model.InterMineObject proxGetFigure() { return figure; }

    // Ref: org.intermine.model.bio.Phenotype.subTerm2
    protected org.intermine.model.InterMineObject subTerm2;
    public org.intermine.model.bio.OntologyTerm getSubTerm2() { if (subTerm2 instanceof org.intermine.objectstore.proxy.ProxyReference) { return ((org.intermine.model.bio.OntologyTerm) ((org.intermine.objectstore.proxy.ProxyReference) subTerm2).getObject()); }; return (org.intermine.model.bio.OntologyTerm) subTerm2; }
    public void setSubTerm2(final org.intermine.model.bio.OntologyTerm subTerm2) { this.subTerm2 = subTerm2; }
    public void proxySubTerm2(final org.intermine.objectstore.proxy.ProxyReference subTerm2) { this.subTerm2 = subTerm2; }
    public org.intermine.model.InterMineObject proxGetSubTerm2() { return subTerm2; }

    // Ref: org.intermine.model.bio.Phenotype.endStage
    protected org.intermine.model.InterMineObject endStage;
    public org.intermine.model.bio.OntologyTerm getEndStage() { if (endStage instanceof org.intermine.objectstore.proxy.ProxyReference) { return ((org.intermine.model.bio.OntologyTerm) ((org.intermine.objectstore.proxy.ProxyReference) endStage).getObject()); }; return (org.intermine.model.bio.OntologyTerm) endStage; }
    public void setEndStage(final org.intermine.model.bio.OntologyTerm endStage) { this.endStage = endStage; }
    public void proxyEndStage(final org.intermine.objectstore.proxy.ProxyReference endStage) { this.endStage = endStage; }
    public org.intermine.model.InterMineObject proxGetEndStage() { return endStage; }

    // Ref: org.intermine.model.bio.Phenotype.superTerm2
    protected org.intermine.model.InterMineObject superTerm2;
    public org.intermine.model.bio.OntologyTerm getSuperTerm2() { if (superTerm2 instanceof org.intermine.objectstore.proxy.ProxyReference) { return ((org.intermine.model.bio.OntologyTerm) ((org.intermine.objectstore.proxy.ProxyReference) superTerm2).getObject()); }; return (org.intermine.model.bio.OntologyTerm) superTerm2; }
    public void setSuperTerm2(final org.intermine.model.bio.OntologyTerm superTerm2) { this.superTerm2 = superTerm2; }
    public void proxySuperTerm2(final org.intermine.objectstore.proxy.ProxyReference superTerm2) { this.superTerm2 = superTerm2; }
    public org.intermine.model.InterMineObject proxGetSuperTerm2() { return superTerm2; }

    // Attr: org.intermine.model.InterMineObject.id
    protected java.lang.Integer id;
    public java.lang.Integer getId() { return id; }
    public void setId(final java.lang.Integer id) { this.id = id; }

    @Override public boolean equals(Object o) { return (o instanceof Phenotype && id != null) ? id.equals(((Phenotype)o).getId()) : this == o; }
    @Override public int hashCode() { return (id != null) ? id.hashCode() : super.hashCode(); }
    @Override public String toString() { return "Phenotype [endStage=" + (endStage == null ? "null" : (endStage.getId() == null ? "no id" : endStage.getId().toString())) + ", figure=" + (figure == null ? "null" : (figure.getId() == null ? "no id" : figure.getId().toString())) + ", genotypeEnvironment=" + (genotypeEnvironment == null ? "null" : (genotypeEnvironment.getId() == null ? "no id" : genotypeEnvironment.getId().toString())) + ", id=\"" + id + "\", phenotypeTerm=" + (phenotypeTerm == null ? "null" : (phenotypeTerm.getId() == null ? "no id" : phenotypeTerm.getId().toString())) + ", primaryIdentifier=\"" + primaryIdentifier + "\", relatedSubTerm=" + (relatedSubTerm == null ? "null" : (relatedSubTerm.getId() == null ? "no id" : relatedSubTerm.getId().toString())) + ", relatedSuperTerm=" + (relatedSuperTerm == null ? "null" : (relatedSuperTerm.getId() == null ? "no id" : relatedSuperTerm.getId().toString())) + ", startStage=" + (startStage == null ? "null" : (startStage.getId() == null ? "no id" : startStage.getId().toString())) + ", subTerm=" + (subTerm == null ? "null" : (subTerm.getId() == null ? "no id" : subTerm.getId().toString())) + ", subTerm2=" + (subTerm2 == null ? "null" : (subTerm2.getId() == null ? "no id" : subTerm2.getId().toString())) + ", superTerm=" + (superTerm == null ? "null" : (superTerm.getId() == null ? "no id" : superTerm.getId().toString())) + ", superTerm2=" + (superTerm2 == null ? "null" : (superTerm2.getId() == null ? "no id" : superTerm2.getId().toString())) + ", tag=\"" + tag + "\"]"; }
    public Object getFieldValue(final String fieldName) throws IllegalAccessException {
        if ("primaryIdentifier".equals(fieldName)) {
            return primaryIdentifier;
        }
        if ("tag".equals(fieldName)) {
            return tag;
        }
        if ("startStage".equals(fieldName)) {
            if (startStage instanceof ProxyReference) {
                return ((ProxyReference) startStage).getObject();
            } else {
                return startStage;
            }
        }
        if ("superTerm".equals(fieldName)) {
            if (superTerm instanceof ProxyReference) {
                return ((ProxyReference) superTerm).getObject();
            } else {
                return superTerm;
            }
        }
        if ("relatedSubTerm".equals(fieldName)) {
            if (relatedSubTerm instanceof ProxyReference) {
                return ((ProxyReference) relatedSubTerm).getObject();
            } else {
                return relatedSubTerm;
            }
        }
        if ("relatedSuperTerm".equals(fieldName)) {
            if (relatedSuperTerm instanceof ProxyReference) {
                return ((ProxyReference) relatedSuperTerm).getObject();
            } else {
                return relatedSuperTerm;
            }
        }
        if ("phenotypeTerm".equals(fieldName)) {
            if (phenotypeTerm instanceof ProxyReference) {
                return ((ProxyReference) phenotypeTerm).getObject();
            } else {
                return phenotypeTerm;
            }
        }
        if ("subTerm".equals(fieldName)) {
            if (subTerm instanceof ProxyReference) {
                return ((ProxyReference) subTerm).getObject();
            } else {
                return subTerm;
            }
        }
        if ("genotypeEnvironment".equals(fieldName)) {
            if (genotypeEnvironment instanceof ProxyReference) {
                return ((ProxyReference) genotypeEnvironment).getObject();
            } else {
                return genotypeEnvironment;
            }
        }
        if ("figure".equals(fieldName)) {
            if (figure instanceof ProxyReference) {
                return ((ProxyReference) figure).getObject();
            } else {
                return figure;
            }
        }
        if ("subTerm2".equals(fieldName)) {
            if (subTerm2 instanceof ProxyReference) {
                return ((ProxyReference) subTerm2).getObject();
            } else {
                return subTerm2;
            }
        }
        if ("endStage".equals(fieldName)) {
            if (endStage instanceof ProxyReference) {
                return ((ProxyReference) endStage).getObject();
            } else {
                return endStage;
            }
        }
        if ("superTerm2".equals(fieldName)) {
            if (superTerm2 instanceof ProxyReference) {
                return ((ProxyReference) superTerm2).getObject();
            } else {
                return superTerm2;
            }
        }
        if ("id".equals(fieldName)) {
            return id;
        }
        if (!org.intermine.model.bio.Phenotype.class.equals(getClass())) {
            return TypeUtil.getFieldValue(this, fieldName);
        }
        throw new IllegalArgumentException("Unknown field " + fieldName);
    }
    public Object getFieldProxy(final String fieldName) throws IllegalAccessException {
        if ("primaryIdentifier".equals(fieldName)) {
            return primaryIdentifier;
        }
        if ("tag".equals(fieldName)) {
            return tag;
        }
        if ("startStage".equals(fieldName)) {
            return startStage;
        }
        if ("superTerm".equals(fieldName)) {
            return superTerm;
        }
        if ("relatedSubTerm".equals(fieldName)) {
            return relatedSubTerm;
        }
        if ("relatedSuperTerm".equals(fieldName)) {
            return relatedSuperTerm;
        }
        if ("phenotypeTerm".equals(fieldName)) {
            return phenotypeTerm;
        }
        if ("subTerm".equals(fieldName)) {
            return subTerm;
        }
        if ("genotypeEnvironment".equals(fieldName)) {
            return genotypeEnvironment;
        }
        if ("figure".equals(fieldName)) {
            return figure;
        }
        if ("subTerm2".equals(fieldName)) {
            return subTerm2;
        }
        if ("endStage".equals(fieldName)) {
            return endStage;
        }
        if ("superTerm2".equals(fieldName)) {
            return superTerm2;
        }
        if ("id".equals(fieldName)) {
            return id;
        }
        if (!org.intermine.model.bio.Phenotype.class.equals(getClass())) {
            return TypeUtil.getFieldProxy(this, fieldName);
        }
        throw new IllegalArgumentException("Unknown field " + fieldName);
    }
    public void setFieldValue(final String fieldName, final Object value) {
        if ("primaryIdentifier".equals(fieldName)) {
            primaryIdentifier = (java.lang.String) value;
        } else if ("tag".equals(fieldName)) {
            tag = (java.lang.String) value;
        } else if ("startStage".equals(fieldName)) {
            startStage = (org.intermine.model.InterMineObject) value;
        } else if ("superTerm".equals(fieldName)) {
            superTerm = (org.intermine.model.InterMineObject) value;
        } else if ("relatedSubTerm".equals(fieldName)) {
            relatedSubTerm = (org.intermine.model.InterMineObject) value;
        } else if ("relatedSuperTerm".equals(fieldName)) {
            relatedSuperTerm = (org.intermine.model.InterMineObject) value;
        } else if ("phenotypeTerm".equals(fieldName)) {
            phenotypeTerm = (org.intermine.model.InterMineObject) value;
        } else if ("subTerm".equals(fieldName)) {
            subTerm = (org.intermine.model.InterMineObject) value;
        } else if ("genotypeEnvironment".equals(fieldName)) {
            genotypeEnvironment = (org.intermine.model.InterMineObject) value;
        } else if ("figure".equals(fieldName)) {
            figure = (org.intermine.model.InterMineObject) value;
        } else if ("subTerm2".equals(fieldName)) {
            subTerm2 = (org.intermine.model.InterMineObject) value;
        } else if ("endStage".equals(fieldName)) {
            endStage = (org.intermine.model.InterMineObject) value;
        } else if ("superTerm2".equals(fieldName)) {
            superTerm2 = (org.intermine.model.InterMineObject) value;
        } else if ("id".equals(fieldName)) {
            id = (java.lang.Integer) value;
        } else {
            if (!org.intermine.model.bio.Phenotype.class.equals(getClass())) {
                TypeUtil.setFieldValue(this, fieldName, value);
                return;
            }
            throw new IllegalArgumentException("Unknown field " + fieldName);
        }
    }
    public Class<?> getFieldType(final String fieldName) {
        if ("primaryIdentifier".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("tag".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("startStage".equals(fieldName)) {
            return org.intermine.model.bio.OntologyTerm.class;
        }
        if ("superTerm".equals(fieldName)) {
            return org.intermine.model.bio.OntologyTerm.class;
        }
        if ("relatedSubTerm".equals(fieldName)) {
            return org.intermine.model.bio.OntologyTerm.class;
        }
        if ("relatedSuperTerm".equals(fieldName)) {
            return org.intermine.model.bio.OntologyTerm.class;
        }
        if ("phenotypeTerm".equals(fieldName)) {
            return org.intermine.model.bio.PATOTerm.class;
        }
        if ("subTerm".equals(fieldName)) {
            return org.intermine.model.bio.OntologyTerm.class;
        }
        if ("genotypeEnvironment".equals(fieldName)) {
            return org.intermine.model.bio.GenotypeEnvironment.class;
        }
        if ("figure".equals(fieldName)) {
            return org.intermine.model.bio.Figure.class;
        }
        if ("subTerm2".equals(fieldName)) {
            return org.intermine.model.bio.OntologyTerm.class;
        }
        if ("endStage".equals(fieldName)) {
            return org.intermine.model.bio.OntologyTerm.class;
        }
        if ("superTerm2".equals(fieldName)) {
            return org.intermine.model.bio.OntologyTerm.class;
        }
        if ("id".equals(fieldName)) {
            return java.lang.Integer.class;
        }
        if (!org.intermine.model.bio.Phenotype.class.equals(getClass())) {
            return TypeUtil.getFieldType(org.intermine.model.bio.Phenotype.class, fieldName);
        }
        throw new IllegalArgumentException("Unknown field " + fieldName);
    }
    public StringConstructor getoBJECT() {
        if (!org.intermine.model.bio.PhenotypeShadow.class.equals(getClass())) {
            return NotXmlRenderer.render(this);
        }
        StringConstructor sb = new StringConstructor();
        sb.append("$_^org.intermine.model.bio.Phenotype");
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
        if (tag != null) {
            sb.append("$_^atag$_^");
            String string = tag;
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
        if (startStage != null) {
            sb.append("$_^rstartStage$_^").append(startStage.getId());
        }
        if (superTerm != null) {
            sb.append("$_^rsuperTerm$_^").append(superTerm.getId());
        }
        if (relatedSubTerm != null) {
            sb.append("$_^rrelatedSubTerm$_^").append(relatedSubTerm.getId());
        }
        if (relatedSuperTerm != null) {
            sb.append("$_^rrelatedSuperTerm$_^").append(relatedSuperTerm.getId());
        }
        if (phenotypeTerm != null) {
            sb.append("$_^rphenotypeTerm$_^").append(phenotypeTerm.getId());
        }
        if (subTerm != null) {
            sb.append("$_^rsubTerm$_^").append(subTerm.getId());
        }
        if (genotypeEnvironment != null) {
            sb.append("$_^rgenotypeEnvironment$_^").append(genotypeEnvironment.getId());
        }
        if (figure != null) {
            sb.append("$_^rfigure$_^").append(figure.getId());
        }
        if (subTerm2 != null) {
            sb.append("$_^rsubTerm2$_^").append(subTerm2.getId());
        }
        if (endStage != null) {
            sb.append("$_^rendStage$_^").append(endStage.getId());
        }
        if (superTerm2 != null) {
            sb.append("$_^rsuperTerm2$_^").append(superTerm2.getId());
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
        if (!org.intermine.model.bio.PhenotypeShadow.class.equals(getClass())) {
            throw new IllegalStateException("Class " + getClass().getName() + " does not match code (org.intermine.model.bio.Phenotype)");
        }
        for (int i = 2; i < notXml.length;) {
            int startI = i;
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
            if ((i < notXml.length) && "atag".equals(notXml[i])) {
                i++;
                StringBuilder string = null;
                while ((i + 1 < notXml.length) && (notXml[i + 1].charAt(0) == 'd')) {
                    if (string == null) string = new StringBuilder(notXml[i]);
                    i++;
                    string.append("$_^").append(notXml[i].substring(1));
                }
                tag = string == null ? notXml[i] : string.toString();
                i++;
            }
            if ((i < notXml.length) &&"rstartStage".equals(notXml[i])) {
                i++;
                startStage = new ProxyReference(os, Integer.valueOf(notXml[i]), org.intermine.model.bio.OntologyTerm.class);
                i++;
            };
            if ((i < notXml.length) &&"rsuperTerm".equals(notXml[i])) {
                i++;
                superTerm = new ProxyReference(os, Integer.valueOf(notXml[i]), org.intermine.model.bio.OntologyTerm.class);
                i++;
            };
            if ((i < notXml.length) &&"rrelatedSubTerm".equals(notXml[i])) {
                i++;
                relatedSubTerm = new ProxyReference(os, Integer.valueOf(notXml[i]), org.intermine.model.bio.OntologyTerm.class);
                i++;
            };
            if ((i < notXml.length) &&"rrelatedSuperTerm".equals(notXml[i])) {
                i++;
                relatedSuperTerm = new ProxyReference(os, Integer.valueOf(notXml[i]), org.intermine.model.bio.OntologyTerm.class);
                i++;
            };
            if ((i < notXml.length) &&"rphenotypeTerm".equals(notXml[i])) {
                i++;
                phenotypeTerm = new ProxyReference(os, Integer.valueOf(notXml[i]), org.intermine.model.bio.PATOTerm.class);
                i++;
            };
            if ((i < notXml.length) &&"rsubTerm".equals(notXml[i])) {
                i++;
                subTerm = new ProxyReference(os, Integer.valueOf(notXml[i]), org.intermine.model.bio.OntologyTerm.class);
                i++;
            };
            if ((i < notXml.length) &&"rgenotypeEnvironment".equals(notXml[i])) {
                i++;
                genotypeEnvironment = new ProxyReference(os, Integer.valueOf(notXml[i]), org.intermine.model.bio.GenotypeEnvironment.class);
                i++;
            };
            if ((i < notXml.length) &&"rfigure".equals(notXml[i])) {
                i++;
                figure = new ProxyReference(os, Integer.valueOf(notXml[i]), org.intermine.model.bio.Figure.class);
                i++;
            };
            if ((i < notXml.length) &&"rsubTerm2".equals(notXml[i])) {
                i++;
                subTerm2 = new ProxyReference(os, Integer.valueOf(notXml[i]), org.intermine.model.bio.OntologyTerm.class);
                i++;
            };
            if ((i < notXml.length) &&"rendStage".equals(notXml[i])) {
                i++;
                endStage = new ProxyReference(os, Integer.valueOf(notXml[i]), org.intermine.model.bio.OntologyTerm.class);
                i++;
            };
            if ((i < notXml.length) &&"rsuperTerm2".equals(notXml[i])) {
                i++;
                superTerm2 = new ProxyReference(os, Integer.valueOf(notXml[i]), org.intermine.model.bio.OntologyTerm.class);
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
    }
    public void addCollectionElement(final String fieldName, final org.intermine.model.InterMineObject element) {
        {
            if (!org.intermine.model.bio.Phenotype.class.equals(getClass())) {
                TypeUtil.addCollectionElement(this, fieldName, element);
                return;
            }
            throw new IllegalArgumentException("Unknown collection " + fieldName);
        }
    }
    public Class<?> getElementType(final String fieldName) {
        if (!org.intermine.model.bio.Phenotype.class.equals(getClass())) {
            return TypeUtil.getElementType(org.intermine.model.bio.Phenotype.class, fieldName);
        }
        throw new IllegalArgumentException("Unknown field " + fieldName);
    }
}
