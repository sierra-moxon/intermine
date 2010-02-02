package org.intermine.bio.web.widget;

/*
 * Copyright (C) 2002-2010 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.intermine.api.profile.InterMineBag;
import org.intermine.bio.web.logic.BioUtil;
import org.intermine.metadata.Model;
import org.intermine.model.bio.Gene;
import org.intermine.model.bio.Organism;
import org.intermine.model.bio.Protein;
import org.intermine.objectstore.ObjectStore;
import org.intermine.objectstore.query.BagConstraint;
import org.intermine.objectstore.query.ConstraintOp;
import org.intermine.objectstore.query.ConstraintSet;
import org.intermine.objectstore.query.ContainsConstraint;
import org.intermine.objectstore.query.Query;
import org.intermine.objectstore.query.QueryClass;
import org.intermine.objectstore.query.QueryCollectionReference;
import org.intermine.objectstore.query.QueryExpression;
import org.intermine.objectstore.query.QueryField;
import org.intermine.objectstore.query.QueryFunction;
import org.intermine.objectstore.query.QueryObjectReference;
import org.intermine.objectstore.query.QueryValue;
import org.intermine.objectstore.query.SimpleConstraint;
import org.intermine.web.logic.widget.EnrichmentWidgetLdr;

/**
 * 
 * @author Julie Sullivan
 */
public class GoStatLdr extends EnrichmentWidgetLdr
{
    private static final Logger LOG = Logger.getLogger(GoStatLdr.class);
    private Collection<String> taxonIds;
    private InterMineBag bag;
    private String namespace;
    private Model model;

    /**
     * @param extraAttribute the main ontology to filter by (biological_process, molecular_function,
     * or cellular_component)
     * @param bag list of objects for this widget
     * @param os object store
     */
    public GoStatLdr (InterMineBag bag, ObjectStore os, String extraAttribute) {
        this.bag = bag;
        namespace = extraAttribute;
        taxonIds = BioUtil.getOrganisms(os, bag, false, true);
        model = os.getModel();
    }

    // adds 3 main ontologies to array.  these 3 will be excluded from the query
    private String[] getOntologies() {
        String[] ids = new String[3];
        ids[0] = "go:0008150";  // biological_process
        ids[1] = "go:0003674";  // molecular_function
        ids[2] = "go:0005575";  // cellular_component
        return ids;
    }

    /**
     * {@inheritDoc}
     */
    public Query getQuery(String action, List<String> keys) {

        String bagType = bag.getType();

        QueryClass qcGene = new QueryClass(Gene.class);
        QueryClass qcGoAnnotation = null;
        QueryClass qcGoChild = null;
        QueryClass qcGoParent = null;

        try {
            qcGoAnnotation = new QueryClass(Class.forName(model.getPackageName() 
                                                          + ".GOAnnotation"));
            qcGoParent = new QueryClass(Class.forName(model.getPackageName() + ".OntologyTerm"));
            qcGoChild = new QueryClass(Class.forName(model.getPackageName() + ".OntologyTerm"));
        } catch (ClassNotFoundException e) {
            LOG.error("Error rendering GO enrichment widget", e);
            // don't throw an exception, return NULL instead.  The widget will display 'no results'.
            // the javascript that renders widgets assumes a valid widget and thus can't handle
            // an exception thrown here.  
            return null;            
        }
        QueryClass qcProtein = new QueryClass(Protein.class);
        QueryClass qcOrganism = new QueryClass(Organism.class);

        QueryField qfQualifier = new QueryField(qcGoAnnotation, "qualifier");
        QueryField qfGeneId = new QueryField(qcGene, "id");
        QueryField qfTaxonId = new QueryField(qcOrganism, "taxonId");
        QueryField qfProteinId = new QueryField(qcProtein, "id");
        QueryField qfPrimaryIdentifier = null;
        QueryField qfId = null;

        if (bagType.equals("Protein")) {
            qfPrimaryIdentifier = new QueryField(qcProtein, "primaryIdentifier");
            qfId = qfProteinId;
        } else {
            qfPrimaryIdentifier = new QueryField(qcGene, "primaryIdentifier");
            qfId = qfGeneId;
        }

        // gene.goAnnotation.ontologyTerm.relations.parentTerm.identifier
        QueryField qfNamespace = new QueryField(qcGoParent, "namespace");
        QueryField qfParentGoIdentifier = new QueryField(qcGoParent, "identifier");
        QueryField qfParentGoName = new QueryField(qcGoParent, "name");

        ConstraintSet cs = new ConstraintSet(ConstraintOp.AND);

        // gene.goAnnotation CONTAINS GOAnnotation
        QueryCollectionReference c1 = new QueryCollectionReference(qcGene, "goAnnotation");
        cs.addConstraint(new ContainsConstraint(c1, ConstraintOp.CONTAINS, qcGoAnnotation));

        // ignore the big three ontologies
        String[] ids = getOntologies();
        QueryExpression c2 = new QueryExpression(QueryExpression.LOWER, qfParentGoIdentifier);
        for (int i = 0; i < ids.length; i++) {
            cs.addConstraint(new SimpleConstraint(c2, ConstraintOp.NOT_EQUALS,
                                                  new QueryValue(ids[i])));
        }

        // GO terms selected by user = gene.goAnnotation.ontologyTerm.identifier
        if (keys != null) {
            cs.addConstraint(new BagConstraint(qfParentGoIdentifier, ConstraintOp.IN, keys));
        }

        // goannotation contains go term
        QueryObjectReference c3 = new QueryObjectReference(qcGoAnnotation, "ontologyTerm");
        cs.addConstraint(new ContainsConstraint(c3, ConstraintOp.CONTAINS, qcGoChild));

        // goannotation contains go terms & parents
        QueryCollectionReference c4 = new QueryCollectionReference(qcGoChild, "parents");
        cs.addConstraint(new ContainsConstraint(c4, ConstraintOp.CONTAINS, qcGoParent));

        // go term is of the specified namespace
        QueryExpression c7 = new QueryExpression(QueryExpression.LOWER, qfNamespace);
        cs.addConstraint(new SimpleConstraint(c7, ConstraintOp.EQUALS,
                                              new QueryValue(namespace.toLowerCase())));

        Collection<Integer> taxonIdInts = new ArrayList();
        // constrained only for memory reasons
        for (String taxonId : taxonIds) {
            try {
                taxonIdInts.add(new Integer(taxonId));
            } catch (NumberFormatException e) {
                LOG.error("Error rendering go stat widget, invalid taxonIds: " + taxonIds);
                // don't throw an exception, return NULL instead.  The widget will display 'no 
                // results'. the javascript that renders widgets assumes a valid widget and thus 
                // can't handle an exception thrown here.  
                return null;
            }
        }
        cs.addConstraint(new BagConstraint(qfTaxonId, ConstraintOp.IN, taxonIdInts));

        // can't be a NOT relationship!
        cs.addConstraint(new SimpleConstraint(qfQualifier, ConstraintOp.IS_NULL));

        // gene is from organism
        QueryObjectReference c9 = new QueryObjectReference(qcGene, "organism");
        cs.addConstraint(new ContainsConstraint(c9, ConstraintOp.CONTAINS, qcOrganism));

        if (!action.startsWith("population")) {
            cs.addConstraint(new BagConstraint(qfId, ConstraintOp.IN, bag.getOsb()));
        }

        if (bagType.equals("Protein")) {
            QueryCollectionReference c10 = new QueryCollectionReference(qcProtein, "genes");
            cs.addConstraint(new ContainsConstraint(c10, ConstraintOp.CONTAINS, qcGene));
        }

        Query q = new Query();
        q.setDistinct(true);
        q.addFrom(qcGene);
        q.addFrom(qcGoAnnotation);
        q.addFrom(qcOrganism);
        q.addFrom(qcGoParent);
        q.addFrom(qcGoChild);

        if (bagType.equals("Protein")) {
            q.addFrom(qcProtein);
        }
        q.setConstraint(cs);

        if (action.equals("analysed")) {
            q.addToSelect(qfId);
        } else if (action.equals("export")) {
            q.addToSelect(qfParentGoIdentifier);
            q.addToSelect(qfPrimaryIdentifier);
            q.addToOrderBy(qfParentGoIdentifier);
        } else if (action.endsWith("Total")) {
            q.addToSelect(qfId);
            Query subQ = q;
            q = new Query();
            q.addFrom(subQ);
            q.addToSelect(new QueryFunction());
        } else {    // calculating enrichment

            /*
            these need to be uniquified because there are duplicates.
            2 go terms can have multiple entries which just the relationship type being
            different.

            the first query gets all of the gene --> go term relationships unique
            the second query then counts the genes per each go term
            */

            // subquery
            Query subq = q;
            subq.addToSelect(qfId);
            subq.addToSelect(qfParentGoIdentifier);

            QueryField qfName = null;
            if (action.equals("sample")) {
                subq.addToSelect(qfParentGoName);
                qfName = new QueryField(subq, qfParentGoName);
            }

            // needed so we can select this field in the parent query
            QueryField qfIdentifier = new QueryField(subq, qfParentGoIdentifier);

            // main query
            q = new Query();
            q.setDistinct(false);
            q.addFrom(subq);
            q.addToSelect(qfIdentifier);
            q.addToSelect(new QueryFunction());
            if (action.equals("sample")) {
                q.addToSelect(qfName);
                q.addToGroupBy(qfName);
            }
            q.addToGroupBy(qfIdentifier);

        }
        return q;
    }

    /**
     * @return the namespace
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * @param namespace the namespace to set
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}



