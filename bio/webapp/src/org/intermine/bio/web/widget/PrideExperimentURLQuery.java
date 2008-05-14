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
import java.util.Collection;
import java.util.List;

import org.intermine.metadata.Model;
import org.intermine.model.InterMineObject;
import org.intermine.objectstore.ObjectStore;
import org.intermine.objectstore.query.ConstraintOp;
import org.intermine.path.Path;
import org.intermine.web.logic.bag.InterMineBag;
import org.intermine.web.logic.query.Constraint;
import org.intermine.web.logic.query.MainHelper;
import org.intermine.web.logic.query.OrderBy;
import org.intermine.web.logic.query.PathNode;
import org.intermine.web.logic.query.PathQuery;
import org.intermine.web.logic.widget.WidgetURLQuery;

/**
 * Builds a query to get all the employees (in bag) associated with specified dept.
 * @author Dominik Grimm and Michael Menden
 */

public class PrideExperimentURLQuery implements WidgetURLQuery
{
    InterMineBag bag;
    String key;
    ObjectStore os;

    /**
     * @param key value selected by user to display
     * @param bag bag included in query
     * @param os object store
     */
    public PrideExperimentURLQuery(ObjectStore os, InterMineBag bag, String key) {
        this.bag = bag;
        this.key = key;
        this.os = os;
    }

    /**
     * {@inheritDoc}
     */
    public PathQuery generatePathQuery(Collection<InterMineObject> keys) {

        Model model = os.getModel();
        PathQuery q = new PathQuery(model);

        List<Path> view = new ArrayList<Path>();

        Path pride = MainHelper.makePath(model, q,
                "Protein.proteinIdentifications.prideExperiment.title");
        Path proteinId = MainHelper.makePath(model, q, "Protein.primaryIdentifier");
        Path proteinAcc = MainHelper.makePath(model, q, "Protein.primaryAccession");
        Path proteinName = MainHelper.makePath(model, q, "Protein.name");

        view.add(pride);
        view.add(proteinId);
        view.add(proteinAcc);
        view.add(proteinName);

        q.setView(view);

        String bagType = bag.getType();
        ConstraintOp constraintOp = ConstraintOp.IN;
        String constraintValue = bag.getName();
        String label = null, id = null, code = q.getUnusedConstraintCode();
        Constraint c = new Constraint(constraintOp, constraintValue, false, label, code, id, null);
        q.addNode(bagType).getConstraints().add(c);

        if (keys != null) {
            constraintOp = ConstraintOp.NOT_IN;
            code = q.getUnusedConstraintCode();
            c = new Constraint(constraintOp, keys, false, label, code, id, null);
            q.getNode(bagType).getConstraints().add(c);
        } else {
            // prideExperiment
            constraintOp = ConstraintOp.EQUALS;
            code = q.getUnusedConstraintCode();
            PathNode node = q.addNode("Protein.proteinIdentifications.prideExperiment.title");
            c = new Constraint(constraintOp, key, false, label, code, id, null);
            node.getConstraints().add(c);
        }

        q.setConstraintLogic("A and B");
        q.syncLogicExpression("and");

        List<OrderBy>  sortOrder = new ArrayList<OrderBy>();
        sortOrder.add(new OrderBy(pride, "asc"));
        q.setSortOrder(sortOrder);

        return q;
    }
}
