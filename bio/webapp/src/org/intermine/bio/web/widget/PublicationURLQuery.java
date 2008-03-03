package org.intermine.bio.web.widget;

/*
 * Copyright (C) 2002-2008 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.util.ArrayList;
import java.util.List;

import org.intermine.objectstore.query.ConstraintOp;

import org.intermine.metadata.Model;
import org.intermine.objectstore.ObjectStore;
import org.intermine.path.Path;
import org.intermine.web.logic.bag.InterMineBag;
import org.intermine.web.logic.query.Constraint;
import org.intermine.web.logic.query.MainHelper;
import org.intermine.web.logic.query.OrderBy;
import org.intermine.web.logic.query.PathNode;
import org.intermine.web.logic.query.PathQuery;
import org.intermine.web.logic.widget.WidgetURLQuery;

/**
 * Builds a pathquery.  Used when a user clicks on a results record in an enrichment widget.
 * @author Julie Sullivan
 */
public class PublicationURLQuery implements WidgetURLQuery
{

    InterMineBag bag;
    String key;
    ObjectStore os;

    /**
     * @param key value selected by user to display
     * @param bag bag included in query
     * @param os object store
     */
    public PublicationURLQuery(ObjectStore os, InterMineBag bag, String key) {
        this.bag = bag;
        this.key = key;
        this.os = os;
    }

    /**
     * {@inheritDoc}
     */
    public PathQuery generatePathQuery() {

        Model model = os.getModel();
        PathQuery q = new PathQuery(model);

        List<Path> view = new ArrayList<Path>();
        
        Path geneSecondaryIdentifier = MainHelper.makePath(model, q, "Gene.secondaryIdentifier");
        Path genePrimaryIdentifier = MainHelper.makePath(model, q, "Gene.primaryIdentifier");
        Path geneName = MainHelper.makePath(model, q, "Gene.name");
        Path organismName = MainHelper.makePath(model, q, "Gene.organism.name");        
        Path title = MainHelper.makePath(model, q, "Gene.publications.title");
        Path author = MainHelper.makePath(model, q, "Gene.publications.firstAuthor");
        Path journal = MainHelper.makePath(model, q, "Gene.publications.journal");
        Path year = MainHelper.makePath(model, q, "Gene.publications.year");
        Path pubmedid = MainHelper.makePath(model, q, "Gene.publications.pubMedId");
        
        view.add(genePrimaryIdentifier);
        view.add(geneSecondaryIdentifier);
        view.add(geneName);
        view.add(organismName);
        view.add(title);
        view.add(author);
        view.add(journal);
        view.add(year);
        view.add(pubmedid);
        
        q.setView(view);

        String bagType = bag.getType();
        ConstraintOp constraintOp = ConstraintOp.IN;
        String constraintValue = bag.getName();
        String label = null, id = null, code = q.getUnusedConstraintCode();
        Constraint c = new Constraint(constraintOp, constraintValue, false, label, code, id, null);
        q.addNode(bagType).getConstraints().add(c);
        // pubmedid
        constraintOp = ConstraintOp.LOOKUP;
        code = q.getUnusedConstraintCode();
        PathNode expressedNode = q.addNode("Gene.publications");
        Constraint expressedConstraint
                        = new Constraint(constraintOp, key, false, label, code, id, null);
        expressedNode.getConstraints().add(expressedConstraint);

        q.setConstraintLogic("A and B");
        q.syncLogicExpression("and");

        List<OrderBy>  sortOrder = new ArrayList<OrderBy>();
        sortOrder.add(new OrderBy(pubmedid, "asc"));
        sortOrder.add(new OrderBy(genePrimaryIdentifier, "asc"));
        
        q.setSortOrder(sortOrder);
        
        return q;
    }
}

