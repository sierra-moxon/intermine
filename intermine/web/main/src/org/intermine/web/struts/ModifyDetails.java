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

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.apache.struts.tiles.ComponentContext;
import org.intermine.api.bag.BagManager;
import org.intermine.api.profile.InterMineBag;
import org.intermine.api.profile.Profile;
import org.intermine.api.query.WebResultsExecutor;
import org.intermine.api.results.WebResults;
import org.intermine.api.template.TemplateManager;
import org.intermine.api.template.TemplatePopulator;
import org.intermine.api.template.TemplatePopulatorException;
import org.intermine.api.template.TemplateQuery;
import org.intermine.metadata.ClassDescriptor;
import org.intermine.model.InterMineObject;
import org.intermine.objectstore.ObjectStore;
import org.intermine.web.logic.Constants;
import org.intermine.web.logic.results.DisplayObject;
import org.intermine.web.logic.results.PagedTable;
import org.intermine.web.logic.session.SessionMethods;

/**
 * Action to handle events from the object details page
 *
 * @author Mark Woodbridge
 */
public class ModifyDetails extends DispatchAction
{
    private static final Logger LOG = Logger.getLogger(ModifyDetails.class);
    /**
     * Show in table for inline template queries.
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     * @return an ActionForward object defining where control goes next
     * @exception Exception if the application business logic throws an exception
     */
    public ActionForward runTemplate(ActionMapping mapping,
            @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession();
        ServletContext servletContext = session.getServletContext();
        String name = request.getParameter("name");
        String scope = request.getParameter("scope");
        String bagName = request.getParameter("bagName");
        String idForLookup = request.getParameter("idForLookup");
        Profile profile = (Profile) session
            .getAttribute(Constants.PROFILE);

        TemplateManager templateManager = SessionMethods.getTemplateManager(session);
        TemplateQuery template = templateManager.getTemplate(profile, name, scope);

        TemplateQuery populatedTemplate;
        try {
            if (idForLookup != null && idForLookup.length() != 0) {
                Integer objectId = new Integer(idForLookup);
                ObjectStore os = SessionMethods.getObjectStore(servletContext);
                InterMineObject object = os.getObjectById(objectId);
                populatedTemplate = TemplatePopulator.populateTemplateWithObject(template, object);
            } else {
                populatedTemplate = TemplatePopulator.populateTemplageWithBag(template, bagName);
            }
        } catch (TemplatePopulatorException e) {
            LOG.error("Error running up template '" + template.getName() + "' from report page for"
                    + ((idForLookup == null) ? " bag " + bagName :
                        " object " + idForLookup) + ".");
            return null;
        }     
        String identifier = "itt." + populatedTemplate.getName() + "." + idForLookup;

        
        WebResultsExecutor executor = SessionMethods.getWebResultsExecutor(session);
        WebResults webResults = executor.execute(populatedTemplate);
        PagedTable pagedResults = new PagedTable(webResults, 10);

        SessionMethods.setResultsTable(session, identifier, pagedResults);

        // add results table to trail
        String trail = request.getParameter("trail");
        if (trail != null) {
            trail += "|results." + identifier;
        } else {
            trail = "|results." + identifier;
        }

        return new ForwardParameters(mapping.findForward("results"))
            .addParameter("templateQueryTitle", template.getTitle())
            .addParameter("templateQueryDescription", template.getDescription())
            .addParameter("table", identifier).addParameter("trail", trail).forward();
    }

    /**
     * @param mapping The ActionMapping used to select this instance
     * @param form The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     * @return an ActionForward object defining where control goes next
     * @exception Exception if the application business logic throws an exception
     */
    public ActionForward verbosify(ActionMapping mapping,
            @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession();
        if (session == null) {
            return null;
        }

        String fieldName = request.getParameter("field");
        String trail = request.getParameter("trail");
        String placement = request.getParameter("placement");
        DisplayObject object = getDisplayObject(session, request.getParameter("id"));


        if (object != null) {
            object.setVerbosity(placement + "_" + fieldName, true);
        }

        return forwardToObjectDetails(mapping, request.getParameter("id"), trail);
    }

    /**
     * @param mapping The ActionMapping used to select this instance
     * @param form The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     * @return an ActionForward object defining where control goes next
     * @exception Exception if the application business logic throws an exception
     */
    public ActionForward unverbosify(ActionMapping mapping,
            @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession();
        String fieldName = request.getParameter("field");
        String trail = request.getParameter("trail");
        String placement = request.getParameter("placement");
        DisplayObject object = getDisplayObject(session, request.getParameter("id"));

        object.setVerbosity(placement + "_" + fieldName, false);

        return forwardToObjectDetails(mapping, request.getParameter("id"), trail);
    }

    /**
     * @param mapping The ActionMapping used to select this instance
     * @param form The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     * @return an ActionForward object defining where control goes next
     * @exception Exception if the application business logic throws an exception
     */
    public ActionForward ajaxVerbosify(ActionMapping mapping,
            @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession();
        String fieldName = request.getParameter("field");
        String trail = request.getParameter("trail");
        String placement = request.getParameter("placement");
        DisplayObject object = getDisplayObject(session, request.getParameter("id"));
        Object collection = object.getRefsAndCollections().get(fieldName);

        String key = placement + "_" + fieldName;

        object.setVerbosity(key, !object.isVerbose(key));

        request.setAttribute("object", object);
        request.setAttribute("trail", trail);
        request.setAttribute("collection", collection);
        request.setAttribute("fieldName", fieldName);

        if (object.isVerbose(key)) {
            return mapping.findForward("objectDetailsCollectionTable");
        }
        return null;
    }

    /**
     * Count number of results for a template on the object details page.
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     * @return an ActionForward object defining where control goes next
     * @exception Exception if the application business logic throws an exception
     */
    public ActionForward ajaxTemplateCount(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession();
        ServletContext sc = session.getServletContext();
        Profile profile = (Profile) session.getAttribute(Constants.PROFILE);
        String type = request.getParameter("type");
        String id = request.getParameter("id");
        String templateName = request.getParameter("template");
        String detailsType = request.getParameter("detailsType");
        ObjectStore os = (ObjectStore) sc.getAttribute(Constants.OBJECTSTORE);

        TemplateManager templateManager = SessionMethods.getTemplateManager(session);
        TemplateQuery tq = templateManager.getTemplate(profile, templateName, type);

        ComponentContext cc = new ComponentContext();

        if (detailsType.equals("object")) {
            InterMineObject o = os.getObjectById(new Integer(id));
            Map displayObjects = (Map) session.getAttribute(Constants.DISPLAY_OBJECT_CACHE);
            DisplayObject obj = (DisplayObject) displayObjects.get(o);
            cc.putAttribute("displayObject", obj);
            cc.putAttribute("templateQuery", tq);
            cc.putAttribute("placement", request.getParameter("placement"));

            new ObjectDetailsTemplateController().execute(cc, mapping, form, request, response);
            request.setAttribute("org.apache.struts.taglib.tiles.CompContext", cc);
            return mapping.findForward("objectDetailsTemplateTable");
        }
        BagManager bagManager = SessionMethods.getBagManager(sc);

        InterMineBag interMineBag = bagManager.getUserOrGlobalBag(profile, id);
        cc.putAttribute("interMineIdBag", interMineBag);
        cc.putAttribute("templateQuery", tq);
        cc.putAttribute("placement", request.getParameter("placement"));

        new ObjectDetailsTemplateController().execute(cc, mapping, form, request, response);
        request.setAttribute("org.apache.struts.taglib.tiles.CompContext", cc);
        return mapping.findForward("objectDetailsTemplateTable");
    }

    /**
     * For a dynamic class, find the class descriptor from which a field is derived
     *
     * @param clds the class descriptors for the dynamic class
     * @param fieldName the field name
     * @return the relevant class descriptor
     */
    protected ClassDescriptor cldContainingField(Set clds, String fieldName) {
        for (Iterator i = clds.iterator(); i.hasNext();) {
            ClassDescriptor cld = (ClassDescriptor) i.next();
            if (cld.getFieldDescriptorByName(fieldName) != null) {
                return cld;
            }
        }
        return null;
    }

    /**
     * Construct an ActionForward to the object details page.
     */
    private ActionForward forwardToObjectDetails(ActionMapping mapping, String id, String trail) {
        ForwardParameters forward = new ForwardParameters(mapping.findForward("objectDetails"));
        forward.addParameter("id", id);
        forward.addParameter("trail", trail);
        return forward.forward();
    }

    /**
     * Get a DisplayObject from the session given the object id as a string.
     *
     * @param session the current http session
     * @param idString intermine object id
     * @return DisplayObject for the intermine object
     */
    protected DisplayObject getDisplayObject(HttpSession session, String idString) {
        Map displayObjects = (Map) session.getAttribute("displayObjects");
        if (displayObjects != null && displayObjects.get(new Integer(idString)) != null) {
            return (DisplayObject) displayObjects.get(new Integer(idString));
        }
        LOG.error("Could not find DisplayObject on session for id " + idString);
        return null;
    }
}
