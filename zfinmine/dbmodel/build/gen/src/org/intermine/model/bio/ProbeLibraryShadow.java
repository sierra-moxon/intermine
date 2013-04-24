package org.intermine.model.bio;

import org.intermine.objectstore.ObjectStore;
import org.intermine.objectstore.intermine.NotXmlParser;
import org.intermine.objectstore.intermine.NotXmlRenderer;
import org.intermine.objectstore.proxy.ProxyReference;
import org.intermine.util.StringConstructor;
import org.intermine.util.TypeUtil;
import org.intermine.model.ShadowClass;

public class ProbeLibraryShadow implements ProbeLibrary, ShadowClass
{
    public static final Class<ProbeLibrary> shadowOf = ProbeLibrary.class;
    // Attr: org.intermine.model.bio.ProbeLibrary.tissue
    protected java.lang.String tissue;
    public java.lang.String getTissue() { return tissue; }
    public void setTissue(final java.lang.String tissue) { this.tissue = tissue; }

    // Attr: org.intermine.model.bio.ProbeLibrary.primaryIdentifier
    protected java.lang.String primaryIdentifier;
    public java.lang.String getPrimaryIdentifier() { return primaryIdentifier; }
    public void setPrimaryIdentifier(final java.lang.String primaryIdentifier) { this.primaryIdentifier = primaryIdentifier; }

    // Attr: org.intermine.model.bio.ProbeLibrary.species
    protected java.lang.String species;
    public java.lang.String getSpecies() { return species; }
    public void setSpecies(final java.lang.String species) { this.species = species; }

    // Attr: org.intermine.model.bio.ProbeLibrary.name
    protected java.lang.String name;
    public java.lang.String getName() { return name; }
    public void setName(final java.lang.String name) { this.name = name; }

    // Attr: org.intermine.model.bio.ProbeLibrary.host
    protected java.lang.String host;
    public java.lang.String getHost() { return host; }
    public void setHost(final java.lang.String host) { this.host = host; }

    // Attr: org.intermine.model.bio.ProbeLibrary.stage
    protected java.lang.String stage;
    public java.lang.String getStage() { return stage; }
    public void setStage(final java.lang.String stage) { this.stage = stage; }

    // Attr: org.intermine.model.bio.ProbeLibrary.restrictionSites
    protected java.lang.String restrictionSites;
    public java.lang.String getRestrictionSites() { return restrictionSites; }
    public void setRestrictionSites(final java.lang.String restrictionSites) { this.restrictionSites = restrictionSites; }

    // Ref: org.intermine.model.bio.ProbeLibrary.strain
    protected org.intermine.model.InterMineObject strain;
    public org.intermine.model.bio.Genotype getStrain() { if (strain instanceof org.intermine.objectstore.proxy.ProxyReference) { return ((org.intermine.model.bio.Genotype) ((org.intermine.objectstore.proxy.ProxyReference) strain).getObject()); }; return (org.intermine.model.bio.Genotype) strain; }
    public void setStrain(final org.intermine.model.bio.Genotype strain) { this.strain = strain; }
    public void proxyStrain(final org.intermine.objectstore.proxy.ProxyReference strain) { this.strain = strain; }
    public org.intermine.model.InterMineObject proxGetStrain() { return strain; }

    // Ref: org.intermine.model.bio.ProbeLibrary.organism
    protected org.intermine.model.InterMineObject organism;
    public org.intermine.model.bio.Organism getOrganism() { if (organism instanceof org.intermine.objectstore.proxy.ProxyReference) { return ((org.intermine.model.bio.Organism) ((org.intermine.objectstore.proxy.ProxyReference) organism).getObject()); }; return (org.intermine.model.bio.Organism) organism; }
    public void setOrganism(final org.intermine.model.bio.Organism organism) { this.organism = organism; }
    public void proxyOrganism(final org.intermine.objectstore.proxy.ProxyReference organism) { this.organism = organism; }
    public org.intermine.model.InterMineObject proxGetOrganism() { return organism; }

    // Attr: org.intermine.model.InterMineObject.id
    protected java.lang.Integer id;
    public java.lang.Integer getId() { return id; }
    public void setId(final java.lang.Integer id) { this.id = id; }

    @Override public boolean equals(Object o) { return (o instanceof ProbeLibrary && id != null) ? id.equals(((ProbeLibrary)o).getId()) : this == o; }
    @Override public int hashCode() { return (id != null) ? id.hashCode() : super.hashCode(); }
    @Override public String toString() { return "ProbeLibrary [host=\"" + host + "\", id=\"" + id + "\", name=\"" + name + "\", organism=" + (organism == null ? "null" : (organism.getId() == null ? "no id" : organism.getId().toString())) + ", primaryIdentifier=\"" + primaryIdentifier + "\", restrictionSites=\"" + restrictionSites + "\", species=\"" + species + "\", stage=\"" + stage + "\", strain=" + (strain == null ? "null" : (strain.getId() == null ? "no id" : strain.getId().toString())) + ", tissue=\"" + tissue + "\"]"; }
    public Object getFieldValue(final String fieldName) throws IllegalAccessException {
        if ("tissue".equals(fieldName)) {
            return tissue;
        }
        if ("primaryIdentifier".equals(fieldName)) {
            return primaryIdentifier;
        }
        if ("species".equals(fieldName)) {
            return species;
        }
        if ("name".equals(fieldName)) {
            return name;
        }
        if ("host".equals(fieldName)) {
            return host;
        }
        if ("stage".equals(fieldName)) {
            return stage;
        }
        if ("restrictionSites".equals(fieldName)) {
            return restrictionSites;
        }
        if ("strain".equals(fieldName)) {
            if (strain instanceof ProxyReference) {
                return ((ProxyReference) strain).getObject();
            } else {
                return strain;
            }
        }
        if ("organism".equals(fieldName)) {
            if (organism instanceof ProxyReference) {
                return ((ProxyReference) organism).getObject();
            } else {
                return organism;
            }
        }
        if ("id".equals(fieldName)) {
            return id;
        }
        if (!org.intermine.model.bio.ProbeLibrary.class.equals(getClass())) {
            return TypeUtil.getFieldValue(this, fieldName);
        }
        throw new IllegalArgumentException("Unknown field " + fieldName);
    }
    public Object getFieldProxy(final String fieldName) throws IllegalAccessException {
        if ("tissue".equals(fieldName)) {
            return tissue;
        }
        if ("primaryIdentifier".equals(fieldName)) {
            return primaryIdentifier;
        }
        if ("species".equals(fieldName)) {
            return species;
        }
        if ("name".equals(fieldName)) {
            return name;
        }
        if ("host".equals(fieldName)) {
            return host;
        }
        if ("stage".equals(fieldName)) {
            return stage;
        }
        if ("restrictionSites".equals(fieldName)) {
            return restrictionSites;
        }
        if ("strain".equals(fieldName)) {
            return strain;
        }
        if ("organism".equals(fieldName)) {
            return organism;
        }
        if ("id".equals(fieldName)) {
            return id;
        }
        if (!org.intermine.model.bio.ProbeLibrary.class.equals(getClass())) {
            return TypeUtil.getFieldProxy(this, fieldName);
        }
        throw new IllegalArgumentException("Unknown field " + fieldName);
    }
    public void setFieldValue(final String fieldName, final Object value) {
        if ("tissue".equals(fieldName)) {
            tissue = (java.lang.String) value;
        } else if ("primaryIdentifier".equals(fieldName)) {
            primaryIdentifier = (java.lang.String) value;
        } else if ("species".equals(fieldName)) {
            species = (java.lang.String) value;
        } else if ("name".equals(fieldName)) {
            name = (java.lang.String) value;
        } else if ("host".equals(fieldName)) {
            host = (java.lang.String) value;
        } else if ("stage".equals(fieldName)) {
            stage = (java.lang.String) value;
        } else if ("restrictionSites".equals(fieldName)) {
            restrictionSites = (java.lang.String) value;
        } else if ("strain".equals(fieldName)) {
            strain = (org.intermine.model.InterMineObject) value;
        } else if ("organism".equals(fieldName)) {
            organism = (org.intermine.model.InterMineObject) value;
        } else if ("id".equals(fieldName)) {
            id = (java.lang.Integer) value;
        } else {
            if (!org.intermine.model.bio.ProbeLibrary.class.equals(getClass())) {
                TypeUtil.setFieldValue(this, fieldName, value);
                return;
            }
            throw new IllegalArgumentException("Unknown field " + fieldName);
        }
    }
    public Class<?> getFieldType(final String fieldName) {
        if ("tissue".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("primaryIdentifier".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("species".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("name".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("host".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("stage".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("restrictionSites".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("strain".equals(fieldName)) {
            return org.intermine.model.bio.Genotype.class;
        }
        if ("organism".equals(fieldName)) {
            return org.intermine.model.bio.Organism.class;
        }
        if ("id".equals(fieldName)) {
            return java.lang.Integer.class;
        }
        if (!org.intermine.model.bio.ProbeLibrary.class.equals(getClass())) {
            return TypeUtil.getFieldType(org.intermine.model.bio.ProbeLibrary.class, fieldName);
        }
        throw new IllegalArgumentException("Unknown field " + fieldName);
    }
    public StringConstructor getoBJECT() {
        if (!org.intermine.model.bio.ProbeLibraryShadow.class.equals(getClass())) {
            return NotXmlRenderer.render(this);
        }
        StringConstructor sb = new StringConstructor();
        sb.append("$_^org.intermine.model.bio.ProbeLibrary");
        if (tissue != null) {
            sb.append("$_^atissue$_^");
            String string = tissue;
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
        if (species != null) {
            sb.append("$_^aspecies$_^");
            String string = species;
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
        if (host != null) {
            sb.append("$_^ahost$_^");
            String string = host;
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
        if (stage != null) {
            sb.append("$_^astage$_^");
            String string = stage;
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
        if (restrictionSites != null) {
            sb.append("$_^arestrictionSites$_^");
            String string = restrictionSites;
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
        if (strain != null) {
            sb.append("$_^rstrain$_^").append(strain.getId());
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
        if (!org.intermine.model.bio.ProbeLibraryShadow.class.equals(getClass())) {
            throw new IllegalStateException("Class " + getClass().getName() + " does not match code (org.intermine.model.bio.ProbeLibrary)");
        }
        for (int i = 2; i < notXml.length;) {
            int startI = i;
            if ((i < notXml.length) && "atissue".equals(notXml[i])) {
                i++;
                StringBuilder string = null;
                while ((i + 1 < notXml.length) && (notXml[i + 1].charAt(0) == 'd')) {
                    if (string == null) string = new StringBuilder(notXml[i]);
                    i++;
                    string.append("$_^").append(notXml[i].substring(1));
                }
                tissue = string == null ? notXml[i] : string.toString();
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
            if ((i < notXml.length) && "aspecies".equals(notXml[i])) {
                i++;
                StringBuilder string = null;
                while ((i + 1 < notXml.length) && (notXml[i + 1].charAt(0) == 'd')) {
                    if (string == null) string = new StringBuilder(notXml[i]);
                    i++;
                    string.append("$_^").append(notXml[i].substring(1));
                }
                species = string == null ? notXml[i] : string.toString();
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
            if ((i < notXml.length) && "ahost".equals(notXml[i])) {
                i++;
                StringBuilder string = null;
                while ((i + 1 < notXml.length) && (notXml[i + 1].charAt(0) == 'd')) {
                    if (string == null) string = new StringBuilder(notXml[i]);
                    i++;
                    string.append("$_^").append(notXml[i].substring(1));
                }
                host = string == null ? notXml[i] : string.toString();
                i++;
            }
            if ((i < notXml.length) && "astage".equals(notXml[i])) {
                i++;
                StringBuilder string = null;
                while ((i + 1 < notXml.length) && (notXml[i + 1].charAt(0) == 'd')) {
                    if (string == null) string = new StringBuilder(notXml[i]);
                    i++;
                    string.append("$_^").append(notXml[i].substring(1));
                }
                stage = string == null ? notXml[i] : string.toString();
                i++;
            }
            if ((i < notXml.length) && "arestrictionSites".equals(notXml[i])) {
                i++;
                StringBuilder string = null;
                while ((i + 1 < notXml.length) && (notXml[i + 1].charAt(0) == 'd')) {
                    if (string == null) string = new StringBuilder(notXml[i]);
                    i++;
                    string.append("$_^").append(notXml[i].substring(1));
                }
                restrictionSites = string == null ? notXml[i] : string.toString();
                i++;
            }
            if ((i < notXml.length) &&"rstrain".equals(notXml[i])) {
                i++;
                strain = new ProxyReference(os, Integer.valueOf(notXml[i]), org.intermine.model.bio.Genotype.class);
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
    }
    public void addCollectionElement(final String fieldName, final org.intermine.model.InterMineObject element) {
        {
            if (!org.intermine.model.bio.ProbeLibrary.class.equals(getClass())) {
                TypeUtil.addCollectionElement(this, fieldName, element);
                return;
            }
            throw new IllegalArgumentException("Unknown collection " + fieldName);
        }
    }
    public Class<?> getElementType(final String fieldName) {
        if (!org.intermine.model.bio.ProbeLibrary.class.equals(getClass())) {
            return TypeUtil.getElementType(org.intermine.model.bio.ProbeLibrary.class, fieldName);
        }
        throw new IllegalArgumentException("Unknown field " + fieldName);
    }
}
