package org.intermine.model.bio;

public interface Phenotype extends org.intermine.model.InterMineObject
{
    public java.lang.String getPrimaryIdentifier();
    public void setPrimaryIdentifier(final java.lang.String primaryIdentifier);

    public java.lang.String getTag();
    public void setTag(final java.lang.String tag);

    public org.intermine.model.bio.OntologyTerm getRelatedSubTerm();
    public void setRelatedSubTerm(final org.intermine.model.bio.OntologyTerm relatedSubTerm);
    public void proxyRelatedSubTerm(final org.intermine.objectstore.proxy.ProxyReference relatedSubTerm);
    public org.intermine.model.InterMineObject proxGetRelatedSubTerm();

    public org.intermine.model.bio.OntologyTerm getSuperTerm();
    public void setSuperTerm(final org.intermine.model.bio.OntologyTerm superTerm);
    public void proxySuperTerm(final org.intermine.objectstore.proxy.ProxyReference superTerm);
    public org.intermine.model.InterMineObject proxGetSuperTerm();

    public org.intermine.model.bio.OntologyTerm getStartStage();
    public void setStartStage(final org.intermine.model.bio.OntologyTerm startStage);
    public void proxyStartStage(final org.intermine.objectstore.proxy.ProxyReference startStage);
    public org.intermine.model.InterMineObject proxGetStartStage();

    public org.intermine.model.bio.OntologyTerm getRelatedSuperTerm();
    public void setRelatedSuperTerm(final org.intermine.model.bio.OntologyTerm relatedSuperTerm);
    public void proxyRelatedSuperTerm(final org.intermine.objectstore.proxy.ProxyReference relatedSuperTerm);
    public org.intermine.model.InterMineObject proxGetRelatedSuperTerm();

    public org.intermine.model.bio.PATOTerm getPhenotypeTerm();
    public void setPhenotypeTerm(final org.intermine.model.bio.PATOTerm phenotypeTerm);
    public void proxyPhenotypeTerm(final org.intermine.objectstore.proxy.ProxyReference phenotypeTerm);
    public org.intermine.model.InterMineObject proxGetPhenotypeTerm();

    public org.intermine.model.bio.OntologyTerm getSubTerm();
    public void setSubTerm(final org.intermine.model.bio.OntologyTerm subTerm);
    public void proxySubTerm(final org.intermine.objectstore.proxy.ProxyReference subTerm);
    public org.intermine.model.InterMineObject proxGetSubTerm();

    public org.intermine.model.bio.GenotypeEnvironment getGenotypeEnvironment();
    public void setGenotypeEnvironment(final org.intermine.model.bio.GenotypeEnvironment genotypeEnvironment);
    public void proxyGenotypeEnvironment(final org.intermine.objectstore.proxy.ProxyReference genotypeEnvironment);
    public org.intermine.model.InterMineObject proxGetGenotypeEnvironment();

    public org.intermine.model.bio.Figure getFigure();
    public void setFigure(final org.intermine.model.bio.Figure figure);
    public void proxyFigure(final org.intermine.objectstore.proxy.ProxyReference figure);
    public org.intermine.model.InterMineObject proxGetFigure();

    public org.intermine.model.bio.OntologyTerm getSubTerm2();
    public void setSubTerm2(final org.intermine.model.bio.OntologyTerm subTerm2);
    public void proxySubTerm2(final org.intermine.objectstore.proxy.ProxyReference subTerm2);
    public org.intermine.model.InterMineObject proxGetSubTerm2();

    public org.intermine.model.bio.OntologyTerm getSuperTerm2();
    public void setSuperTerm2(final org.intermine.model.bio.OntologyTerm superTerm2);
    public void proxySuperTerm2(final org.intermine.objectstore.proxy.ProxyReference superTerm2);
    public org.intermine.model.InterMineObject proxGetSuperTerm2();

    public org.intermine.model.bio.OntologyTerm getEndStage();
    public void setEndStage(final org.intermine.model.bio.OntologyTerm endStage);
    public void proxyEndStage(final org.intermine.objectstore.proxy.ProxyReference endStage);
    public org.intermine.model.InterMineObject proxGetEndStage();

}
