package org.intermine.web.struts;

/*
 * Copyright (C) 2002-2008 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */


import java.util.List;

import org.intermine.web.logic.Constants;
import org.intermine.web.logic.query.OrderBy;
import org.intermine.web.logic.query.PathQuery;
import org.intermine.web.logic.session.SessionMethods;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
/**
 * Action to handle changes to sort order on query page
 * @author Julie Sullivan
 */
public class SortOrderChange extends DispatchAction
{
    /**
     * Remove a Node from the sort order
     * @param mapping The ActionMapping used to select this instance
     * @param form The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     * @return an ActionForward object defining where control goes next
     * @exception Exception if the application business logic throws
     */
    public ActionForward removeFromSortOrder(ActionMapping mapping,
                                             @SuppressWarnings("unused") ActionForm form,
                                        HttpServletRequest request,
                                        @SuppressWarnings("unused") HttpServletResponse response)
        throws Exception {
        HttpSession session = request.getSession();
        PathQuery query = (PathQuery) session.getAttribute(Constants.QUERY);

        query.removePathStringFromSortOrder();

        return new ForwardParameters(mapping.findForward("query"))
            .addAnchor("showing").forward();
    }

    /**
     * Add a Node from the sort order
     * @param mapping The ActionMapping used to select this instance
     * @param form The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     * @return an ActionForward object defining where control goes next
     * @exception Exception if the application business logic throws
     */
    public ActionForward addToSortOrder(ActionMapping mapping,
                                        @SuppressWarnings("unused") ActionForm form,
                                        HttpServletRequest request,
                                        @SuppressWarnings("unused") HttpServletResponse response)
        throws Exception {
        HttpSession session = request.getSession();
        String path = request.getParameter("pathString");
        String direction = request.getParameter("direction");
        PathQuery query = (PathQuery) session.getAttribute(Constants.QUERY);

        if (direction == null) {
            direction = "asc";
        }
        query.addPathStringToSortOrder(path, direction);

        return new ForwardParameters(mapping.findForward("query"))
            .addAnchor("showing").forward();
    }

    /**
     * Change sort direction - asc or desc
     * @param mapping The ActionMapping used to select this instance
     * @param form The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     * @return an ActionForward object defining where control goes next
     * @exception Exception if the application business logic throws
     */
    public ActionForward changeDirection(ActionMapping mapping,
                                         @SuppressWarnings("unused") ActionForm form,
                                         HttpServletRequest request,
                                         @SuppressWarnings("unused") HttpServletResponse response)
    throws Exception {
        HttpSession session = request.getSession();
        String direction = request.getParameter("direction");
        PathQuery query = (PathQuery) session.getAttribute(Constants.QUERY);

        if (direction == null) {
            direction = "asc";
        }
        query.changeDirection(direction);

        return new ForwardParameters(mapping.findForward("query"))
        .addAnchor("showing").forward();
    }


    /**
     * Shift a Node left in the view
     * @param mapping The ActionMapping used to select this instance
     * @param form The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     * @return an ActionForward object defining where control goes next
     * @exception Exception if the application business logic throws
     */
    public ActionForward moveLeft(ActionMapping mapping,
                                  @SuppressWarnings("unused") ActionForm form,
                                  HttpServletRequest request,
                                  @SuppressWarnings("unused") HttpServletResponse response)
        throws Exception {
        HttpSession session = request.getSession();
        int index = Integer.parseInt(request.getParameter("index"));

        List<OrderBy> sortOrder = SessionMethods.getEditingSortOrder(session);
        OrderBy o = sortOrder.get(index - 1);
        sortOrder.set(index - 1, sortOrder.get(index));
        sortOrder.set(index, o);

        return new ForwardParameters(mapping.findForward("query"))
            .addAnchor("showing").forward();
    }

    /**
     * Shift a Node right in the view
     * @param mapping The ActionMapping used to select this instance
     * @param form The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     * @return an ActionForward object defining where control goes next
     * @exception Exception if the application business logic throws
     */
    public ActionForward moveRight(ActionMapping mapping,
                                   @SuppressWarnings("unused") ActionForm form,
                                   HttpServletRequest request,
                                   @SuppressWarnings("unused") HttpServletResponse response)
        throws Exception {
        HttpSession session = request.getSession();
        int index = Integer.parseInt(request.getParameter("index"));

        List<OrderBy> sortOrder = SessionMethods.getEditingSortOrder(session);
        OrderBy o = sortOrder.get(index + 1);
        sortOrder.set(index + 1, sortOrder.get(index));
        sortOrder.set(index, o);

        return new ForwardParameters(mapping.findForward("query"))
            .addAnchor("showing").forward();
    }
}
