package org.intermine.web.struts;

/*
 * Copyright (C) 2002-2009 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.intermine.objectstore.query.ConstraintOp;
import org.intermine.pathquery.Constraint;
import org.intermine.pathquery.Node;
import org.intermine.pathquery.PathNode;
import org.intermine.pathquery.PathQuery;
import org.intermine.web.logic.Constants;
import org.intermine.web.logic.session.SessionMethods;

/**
 * Action to handle button presses on the main tile
 *
 * @author Mark Woodbridge
 */
public class QueryBuilderAction extends InterMineAction
{
    /**
     * Method called when user has finished updating a constraint
     *
     * @param mapping
     *            The ActionMapping used to select this instance
     * @param form
     *            The optional ActionForm bean for this request (if any)
     * @param request
     *            The HTTP request we are processing
     * @param response
     *            The HTTP response we are creating
     * @return an ActionForward object defining where control goes next
     * @exception Exception
     *                if the application business logic throws an exception
     */
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 @SuppressWarnings("unused") HttpServletResponse response)
    throws Exception {
        HttpSession session = request.getSession();
        PathQuery query = (PathQuery) session.getAttribute(Constants.QUERY);
        QueryBuilderForm mf = (QueryBuilderForm) form;

        PathNode node = query.getNodes().get(mf.getPath());

        Integer cindex = (request.getParameter("cindex") != null) ? new Integer(request
                .getParameter("cindex")) : null;

        String label = null, id = null, code = query.getUnusedConstraintCode();
        //boolean editable = false;
        String editingConstraintEditable = request.getParameter("editingConstraintEditable");
        boolean editable = Boolean.parseBoolean(editingConstraintEditable);

        if (cindex != null) {
            // We're updating an existing constraint, just remove the old one
            Constraint c = node.getConstraints().get(cindex.intValue());
            node.removeConstraint(c);
            code = c.getCode();

            if (request.getParameter("template") != null) {
                // We're just updating template settings
                node.getConstraints().add(
                        new Constraint(c.getOp(), c.getValue(), mf.isEditable(), mf
                                .getTemplateLabel(), c.getCode(), mf.getTemplateId(),
                                c.getExtraValue()));
                mf.reset(mapping, request);
                return mapping.findForward("query");
            }

            // current constraint
            label = c.getDescription();
            id = c.getIdentifier();

        }

        if (request.getParameter("attribute") != null) {

            if (cindex == null) {
                // New constraint
                label = mf.getTemplateLabel();
                id = mf.getTemplateId();
                editable = mf.isEditable();
            }
            Locale locale = (Locale) session.getAttribute(Globals.LOCALE_KEY);

            ConstraintOp constraintOp = ConstraintOp.getOpForIndex(Integer.valueOf(mf
                    .getAttributeOp()));
            Object constraintValue = mf.getParsedAttributeValue();

            //String extraValue = mf.getExtraValue();
            if (constraintValue.equals("NULL")) {
                node.getConstraints().add(
                        new Constraint(ConstraintOp.IS_NULL, null, false, label, code, id, null));
            } else {
                Constraint c = new Constraint(constraintOp, constraintValue, editable, label, code,
                        id, mf.getExtraValue());
                node.getConstraints().add(c);
            }
            Node referenceNode = node;
            if (node.isAttribute()) {
                referenceNode = node.getParent();
            }
            if (referenceNode.isReference() && referenceNode.isOuterJoin()) {
                query.flipJoinStyle(referenceNode.getPathString());
            }
        } else if (request.getParameter("bag") != null) {
            ConstraintOp constraintOp = ConstraintOp.getOpForIndex(Integer.valueOf(mf.getBagOp()));
            Object constraintValue = mf.getBagValue();
            // constrain parent object of this node to be in bag or node
            // itself if an object or reference/collection
            PathNode parent;
            if (node.isAttribute() && (node.getPathString().indexOf('.')) >= 0) {
                parent = query.getNodes().get(node.getParent().getPathString());
            } else {
                parent = node;
            }
            Constraint c = new Constraint(constraintOp, constraintValue,
                                          false, label, code, id, null);
            parent.getConstraints().add(c);

            // if no other constraints on the original node, remove it
            if (node.getConstraints().size() == 0) {
                query.getNodes().remove(node.getPathString());
            }
            Node referenceNode = node;
            if (node.isAttribute()) {
                referenceNode = node.getParent();
            }
            if (referenceNode.isReference() && referenceNode.isOuterJoin()) {
                query.flipJoinStyle(referenceNode.getPathString());
            }
        // Loop constraint
        } else if (request.getParameter("loop") != null) {
            ConstraintOp constraintOp = ConstraintOp.getOpForIndex(Integer.valueOf(mf
                    .getLoopQueryOp()));
            Object constraintValue = mf.getLoopQueryValue();
            // Loop constraints can't operate over outer joins, switch all outer joins to normal
            String updatedNodePath = query.setJoinStyleForPath(node.getPathString(), false);
            PathNode updatedNode = query.getNode(updatedNodePath);

            Constraint c = new Constraint(constraintOp, constraintValue, false, label, code, id,
                    null);
            updatedNode.getConstraints().add(c);
            mf.setPath(updatedNodePath);
        } else if (request.getParameter("subclass") != null) {
            node.setType(mf.getSubclassValue());
            session.setAttribute("path", mf.getSubclassValue());
            session.setAttribute("prefix", mf.getPath());
        } else if (request.getParameter("nullnotnull") != null) {
            if (mf.getNullConstraint().equals("NotNULL")) {
                node.getConstraints().add(
                        new Constraint(ConstraintOp.IS_NOT_NULL, null, false, label, code, id,
                            null));
            } else {
                node.getConstraints().add(
                        new Constraint(ConstraintOp.IS_NULL, null, false, label, code, id, null));
            }
            Node referenceNode = node;
            if (node.isAttribute()) {
                referenceNode = node.getParent();
            }
            if (referenceNode.isReference() && referenceNode.isOuterJoin()) {
                query.flipJoinStyle(referenceNode.getPathString());
            }
        } else if (request.getParameter("expression") != null) {
            query.setConstraintLogic(request.getParameter("expr"));
            query.syncLogicExpression(SessionMethods.getDefaultOperator(session));
        } else {
            throw new IllegalArgumentException("Unrecognised action: " + request.getParameterMap());
        }

        if (cindex != null) {
            session.setAttribute(Constants.DEFAULT_OPERATOR, mf.getOperator());
        }

        //if (query.getAllConstraints().size() == previousConstraintCount + 1) {
            query.syncLogicExpression(mf.getOperator());
        //}

        mf.reset(mapping, request);

        return mapping.findForward("query");
    }
}
