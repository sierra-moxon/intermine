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

public class GeneticInteractionURLQuery implements WidgetURLQuery
{

    InterMineBag bag;
    String key;
    ObjectStore os;

    /**
     * @param key value selected by user to display
     * @param bag bag included in query
     * @param os object store
     */
    public GeneticInteractionURLQuery(ObjectStore os, InterMineBag bag, String key) {
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
        
        Path genePrimaryIdentifier = MainHelper.makePath(model, q, "Gene.primaryIdentifier");
        Path geneSymbol = MainHelper.makePath(model, q, "Gene.symbol");
        Path organismName = MainHelper.makePath(model, q, "Gene.organism.shortName");
        
        Path interactionName = MainHelper.makePath(model, 
                                                   q, "Gene.geneticInteractions.shortName");
        Path interactionType = MainHelper.makePath(model, 
                                                   q, "Gene.geneticInteractions.type");
        Path interactionRole = MainHelper.makePath(model, 
                                                   q, "Gene.geneticInteractions.geneRole");
        Path interactor = MainHelper.makePath(model, q,
        "Gene.geneticInteractions.interactingGenes.primaryIdentifier");
        Path experimentName = MainHelper.makePath(model, q,
        "Gene.geneticInteractions.experiment.name");
        
        view.add(genePrimaryIdentifier);
        view.add(geneSymbol);
        view.add(organismName);
        view.add(interactionName);
        view.add(interactionType);
        view.add(interactionRole);
        view.add(interactor);
        view.add(experimentName);
        
        q.setView(view);

        String bagType = bag.getType();
        ConstraintOp constraintOp = ConstraintOp.IN;
        String constraintValue = bag.getName();
        String label = null, id = null, code = q.getUnusedConstraintCode();
        Constraint c = new Constraint(constraintOp, constraintValue, false, label, code, id, null);
        q.addNode(bagType).getConstraints().add(c);


        constraintOp = ConstraintOp.EQUALS;
        code = q.getUnusedConstraintCode();
        PathNode geneNode = 
        q.addNode("Gene.geneticInteractions.interactingGenes.primaryIdentifier");
        Constraint geneConstraint
                        = new Constraint(constraintOp, key, false, label, code, id, null);
        geneNode.getConstraints().add(geneConstraint);

        q.setConstraintLogic("A and B");
        q.syncLogicExpression("and");

        List<OrderBy>  sortOrder = new ArrayList<OrderBy>();
        sortOrder.add(new OrderBy(organismName, "asc"));
        sortOrder.add(new OrderBy(genePrimaryIdentifier, "asc"));
        sortOrder.add(new OrderBy(interactionName, "asc"));
        sortOrder.add(new OrderBy(interactor, "asc"));
        q.setSortOrder(sortOrder);
        
        return q;
    }
}

