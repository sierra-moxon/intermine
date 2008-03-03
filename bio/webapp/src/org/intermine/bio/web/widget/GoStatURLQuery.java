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
 * {@inheritDoc}
 * @author Julie Sullivan
 */
public class GoStatURLQuery implements WidgetURLQuery
{
    ObjectStore os;
    InterMineBag bag;
    String key;

    /**
     * @param os object store
     * @param key go terms user selected
     * @param bag bag page they were on
     */
     public GoStatURLQuery(ObjectStore os, InterMineBag bag, String key) {
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

         Path geneSecondaryIdentifier = null;
         Path genePrimaryIdentifier =  null;
         Path geneName =  null;
         Path organismName =  null;
         Path goId = null;
         Path goName = null;
         Path actualGoName = null;
         Path actualGoId = null;

         if (bag.getType().equalsIgnoreCase("protein")) {

             geneSecondaryIdentifier 
                         = MainHelper.makePath(model, q, "Protein.genes.secondaryIdentifier");
             genePrimaryIdentifier 
                         = MainHelper.makePath(model, q, "Protein.genes.primaryIdentifier");
             geneName = MainHelper.makePath(model, q, "Protein.genes.name");
             organismName = MainHelper.makePath(model, q, "Protein.genes.organism.name");
             goId = MainHelper.makePath(model, q, "Protein.genes.allGoAnnotation.identifier");
             goName = MainHelper.makePath(model, q, "Protein.genes.allGoAnnotation.name");
             actualGoName = MainHelper.makePath(
                            model, q, "Protein.genes.allGoAnnotation.actualGoTerms.name");
             actualGoId = MainHelper.makePath(
                          model, q, "Protein.genes.allGoAnnotation.actualGoTerms.identifier");

             view.add(MainHelper.makePath(model, q, "Protein.identifier"));
             view.add(MainHelper.makePath(model, q, "Protein.primaryAccession"));

         } else {

             geneSecondaryIdentifier = MainHelper.makePath(model, q, "Gene.secondaryIdentifier");
             genePrimaryIdentifier = MainHelper.makePath(model, q, "Gene.primaryIdentifier");
             geneName = MainHelper.makePath(model, q, "Gene.name");
             organismName = MainHelper.makePath(model, q, "Gene.organism.name");
             goId = MainHelper.makePath(model, q, "Gene.allGoAnnotation.identifier");
             goName = MainHelper.makePath(model, q, "Gene.allGoAnnotation.name");
             actualGoName = MainHelper.makePath(model,
                                                q, "Gene.allGoAnnotation.actualGoTerms.name");
             actualGoId = MainHelper.makePath(model,
                                              q, "Gene.allGoAnnotation.actualGoTerms.identifier");
         }
         
         view.add(genePrimaryIdentifier);
         view.add(geneSecondaryIdentifier);
         view.add(geneName);
         view.add(organismName);
         view.add(goId);
         view.add(goName);
         view.add(actualGoName);
         view.add(actualGoId);

         q.setView(view);

         String bagType = bag.getType();
         ConstraintOp constraintOp = ConstraintOp.IN;
         String constraintValue = bag.getName();
         String label = null, id = null, code = q.getUnusedConstraintCode();
         Constraint c = new Constraint(constraintOp, constraintValue, false, label, code, id, null);
         q.addNode(bagType).getConstraints().add(c);

         // can't be a NOT relationship!
         constraintOp = ConstraintOp.IS_NULL;
         code = q.getUnusedConstraintCode();
         PathNode qualifierNode = null;
         if (bag.getType().equalsIgnoreCase("protein")) {
             qualifierNode = q.addNode("Protein.genes.allGoAnnotation.qualifier");
         } else {
             qualifierNode = q.addNode("Gene.allGoAnnotation.qualifier");
         }
         Constraint qualifierConstraint
         = new Constraint(constraintOp, null, false, label, code, id, null);
         qualifierNode.getConstraints().add(qualifierConstraint);

         // go term
         constraintOp = ConstraintOp.LOOKUP;
         code = q.getUnusedConstraintCode();
         PathNode goTermNode = null;
         if (bag.getType().equalsIgnoreCase("protein")) {
             goTermNode = q.addNode("Protein.genes.allGoAnnotation.property");
         } else {
             goTermNode  = q.addNode("Gene.allGoAnnotation.property");
         }
         goTermNode.setType("GOTerm");

         Constraint goTermConstraint
                         = new Constraint(constraintOp, key, false, label, code, id, null);
         goTermNode.getConstraints().add(goTermConstraint);

         q.setConstraintLogic("A and B and C");
         q.syncLogicExpression("and");

         List<OrderBy>  sortOrder = new ArrayList<OrderBy>();
         sortOrder.add(new OrderBy(goId, "asc"));
         sortOrder.add(new OrderBy(goName, "asc"));
         sortOrder.add(new OrderBy(actualGoName, "asc"));
         sortOrder.add(new OrderBy(actualGoId, "asc"));
         sortOrder.add(new OrderBy(organismName, "asc"));
         sortOrder.add(new OrderBy(genePrimaryIdentifier, "asc"));
         q.setSortOrder(sortOrder);
        
        return q;
    }
}
