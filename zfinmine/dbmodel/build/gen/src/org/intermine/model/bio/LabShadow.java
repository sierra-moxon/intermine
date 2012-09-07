package org.intermine.model.bio;

import org.intermine.objectstore.ObjectStore;
import org.intermine.objectstore.intermine.NotXmlParser;
import org.intermine.objectstore.intermine.NotXmlRenderer;
import org.intermine.objectstore.proxy.ProxyCollection;
import org.intermine.util.StringConstructor;
import org.intermine.util.TypeUtil;
import org.intermine.model.ShadowClass;

public class LabShadow implements Lab, ShadowClass
{
    public static final Class<Lab> shadowOf = Lab.class;
    // Attr: org.intermine.model.bio.Lab.contactPerson
    protected java.lang.String contactPerson;
    public java.lang.String getContactPerson() { return contactPerson; }
    public void setContactPerson(final java.lang.String contactPerson) { this.contactPerson = contactPerson; }

    // Attr: org.intermine.model.bio.Lab.information
    protected java.lang.String information;
    public java.lang.String getInformation() { return information; }
    public void setInformation(final java.lang.String information) { this.information = information; }

    // Attr: org.intermine.model.bio.Lab.primaryIdentifier
    protected java.lang.String primaryIdentifier;
    public java.lang.String getPrimaryIdentifier() { return primaryIdentifier; }
    public void setPrimaryIdentifier(final java.lang.String primaryIdentifier) { this.primaryIdentifier = primaryIdentifier; }

    // Attr: org.intermine.model.bio.Lab.name
    protected java.lang.String name;
    public java.lang.String getName() { return name; }
    public void setName(final java.lang.String name) { this.name = name; }

    // Col: org.intermine.model.bio.Lab.lines
    protected java.util.Set<org.intermine.model.bio.SequenceFeature> lines = new java.util.HashSet<org.intermine.model.bio.SequenceFeature>();
    public java.util.Set<org.intermine.model.bio.SequenceFeature> getLines() { return lines; }
    public void setLines(final java.util.Set<org.intermine.model.bio.SequenceFeature> lines) { this.lines = lines; }
    public void addLines(final org.intermine.model.bio.SequenceFeature arg) { lines.add(arg); }

    // Attr: org.intermine.model.InterMineObject.id
    protected java.lang.Integer id;
    public java.lang.Integer getId() { return id; }
    public void setId(final java.lang.Integer id) { this.id = id; }

    @Override public boolean equals(Object o) { return (o instanceof Lab && id != null) ? id.equals(((Lab)o).getId()) : this == o; }
    @Override public int hashCode() { return (id != null) ? id.hashCode() : super.hashCode(); }
    @Override public String toString() { return "Lab [contactPerson=\"" + contactPerson + "\", id=\"" + id + "\", information=\"" + information + "\", name=\"" + name + "\", primaryIdentifier=\"" + primaryIdentifier + "\"]"; }
    public Object getFieldValue(final String fieldName) throws IllegalAccessException {
        if ("contactPerson".equals(fieldName)) {
            return contactPerson;
        }
        if ("information".equals(fieldName)) {
            return information;
        }
        if ("primaryIdentifier".equals(fieldName)) {
            return primaryIdentifier;
        }
        if ("name".equals(fieldName)) {
            return name;
        }
        if ("lines".equals(fieldName)) {
            return lines;
        }
        if ("id".equals(fieldName)) {
            return id;
        }
        if (!org.intermine.model.bio.Lab.class.equals(getClass())) {
            return TypeUtil.getFieldValue(this, fieldName);
        }
        throw new IllegalArgumentException("Unknown field " + fieldName);
    }
    public Object getFieldProxy(final String fieldName) throws IllegalAccessException {
        if ("contactPerson".equals(fieldName)) {
            return contactPerson;
        }
        if ("information".equals(fieldName)) {
            return information;
        }
        if ("primaryIdentifier".equals(fieldName)) {
            return primaryIdentifier;
        }
        if ("name".equals(fieldName)) {
            return name;
        }
        if ("lines".equals(fieldName)) {
            return lines;
        }
        if ("id".equals(fieldName)) {
            return id;
        }
        if (!org.intermine.model.bio.Lab.class.equals(getClass())) {
            return TypeUtil.getFieldProxy(this, fieldName);
        }
        throw new IllegalArgumentException("Unknown field " + fieldName);
    }
    public void setFieldValue(final String fieldName, final Object value) {
        if ("contactPerson".equals(fieldName)) {
            contactPerson = (java.lang.String) value;
        } else if ("information".equals(fieldName)) {
            information = (java.lang.String) value;
        } else if ("primaryIdentifier".equals(fieldName)) {
            primaryIdentifier = (java.lang.String) value;
        } else if ("name".equals(fieldName)) {
            name = (java.lang.String) value;
        } else if ("lines".equals(fieldName)) {
            lines = (java.util.Set) value;
        } else if ("id".equals(fieldName)) {
            id = (java.lang.Integer) value;
        } else {
            if (!org.intermine.model.bio.Lab.class.equals(getClass())) {
                TypeUtil.setFieldValue(this, fieldName, value);
                return;
            }
            throw new IllegalArgumentException("Unknown field " + fieldName);
        }
    }
    public Class<?> getFieldType(final String fieldName) {
        if ("contactPerson".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("information".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("primaryIdentifier".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("name".equals(fieldName)) {
            return java.lang.String.class;
        }
        if ("lines".equals(fieldName)) {
            return java.util.Set.class;
        }
        if ("id".equals(fieldName)) {
            return java.lang.Integer.class;
        }
        if (!org.intermine.model.bio.Lab.class.equals(getClass())) {
            return TypeUtil.getFieldType(org.intermine.model.bio.Lab.class, fieldName);
        }
        throw new IllegalArgumentException("Unknown field " + fieldName);
    }
    public StringConstructor getoBJECT() {
        if (!org.intermine.model.bio.LabShadow.class.equals(getClass())) {
            return NotXmlRenderer.render(this);
        }
        StringConstructor sb = new StringConstructor();
        sb.append("$_^org.intermine.model.bio.Lab");
        if (contactPerson != null) {
            sb.append("$_^acontactPerson$_^");
            String string = contactPerson;
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
        if (information != null) {
            sb.append("$_^ainformation$_^");
            String string = information;
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
        if (id != null) {
            sb.append("$_^aid$_^").append(id);
        }
        return sb;
    }
    public void setoBJECT(String notXml, ObjectStore os) {
        setoBJECT(NotXmlParser.SPLITTER.split(notXml), os);
    }
    public void setoBJECT(final String[] notXml, final ObjectStore os) {
        if (!org.intermine.model.bio.LabShadow.class.equals(getClass())) {
            throw new IllegalStateException("Class " + getClass().getName() + " does not match code (org.intermine.model.bio.Lab)");
        }
        for (int i = 2; i < notXml.length;) {
            int startI = i;
            if ((i < notXml.length) && "acontactPerson".equals(notXml[i])) {
                i++;
                StringBuilder string = null;
                while ((i + 1 < notXml.length) && (notXml[i + 1].charAt(0) == 'd')) {
                    if (string == null) string = new StringBuilder(notXml[i]);
                    i++;
                    string.append("$_^").append(notXml[i].substring(1));
                }
                contactPerson = string == null ? notXml[i] : string.toString();
                i++;
            }
            if ((i < notXml.length) && "ainformation".equals(notXml[i])) {
                i++;
                StringBuilder string = null;
                while ((i + 1 < notXml.length) && (notXml[i + 1].charAt(0) == 'd')) {
                    if (string == null) string = new StringBuilder(notXml[i]);
                    i++;
                    string.append("$_^").append(notXml[i].substring(1));
                }
                information = string == null ? notXml[i] : string.toString();
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
            if ((i < notXml.length) && "aid".equals(notXml[i])) {
                i++;
                id = Integer.valueOf(notXml[i]);
                i++;
            }
            if (startI == i) {
                throw new IllegalArgumentException("Unknown field " + notXml[i]);
            }
        }
        lines = new ProxyCollection<org.intermine.model.bio.SequenceFeature>(os, this, "lines", org.intermine.model.bio.SequenceFeature.class);
    }
    public void addCollectionElement(final String fieldName, final org.intermine.model.InterMineObject element) {
        if ("lines".equals(fieldName)) {
            lines.add((org.intermine.model.bio.SequenceFeature) element);
        } else {
            if (!org.intermine.model.bio.Lab.class.equals(getClass())) {
                TypeUtil.addCollectionElement(this, fieldName, element);
                return;
            }
            throw new IllegalArgumentException("Unknown collection " + fieldName);
        }
    }
    public Class<?> getElementType(final String fieldName) {
        if ("lines".equals(fieldName)) {
            return org.intermine.model.bio.SequenceFeature.class;
        }
        if (!org.intermine.model.bio.Lab.class.equals(getClass())) {
            return TypeUtil.getElementType(org.intermine.model.bio.Lab.class, fieldName);
        }
        throw new IllegalArgumentException("Unknown field " + fieldName);
    }
}
