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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.tiles.ComponentContext;
import org.apache.struts.tiles.actions.TilesAction;
import org.intermine.metadata.Model;
import org.intermine.objectstore.ObjectStore;
import org.intermine.web.logic.Constants;
import org.intermine.web.logic.bag.BagQueryConfig;
import org.intermine.web.logic.bag.InterMineBag;
import org.intermine.web.logic.config.Type;
import org.intermine.web.logic.config.WebConfig;
import org.intermine.web.logic.profile.Profile;
import org.intermine.web.logic.results.PagedTable;
import org.intermine.web.logic.search.SearchRepository;
import org.intermine.web.logic.search.WebSearchable;
import org.intermine.web.logic.session.SessionMethods;
import org.intermine.web.logic.tagging.TagTypes;
import org.intermine.web.logic.template.TemplateHelper;
import org.intermine.web.logic.widget.Widget;

/**
 * @author Xavier Watkins
 */
public class BagDetailsController extends TilesAction
{

    /**
     * {@inheritDoc}
     */
    public ActionForward execute(@SuppressWarnings("unused") ComponentContext context, 
                                 @SuppressWarnings("unused") ActionMapping mapping, 
                                 @SuppressWarnings("unused") ActionForm form, 
                                 HttpServletRequest request, 
                                 @SuppressWarnings("unused") HttpServletResponse response) 
                                 throws Exception {

        HttpSession session = request.getSession();
        ServletContext servletContext = session.getServletContext();
        ObjectStore os = (ObjectStore) servletContext.getAttribute(Constants.OBJECTSTORE);

        String bagName = request.getParameter("bagName");
        Boolean myBag = Boolean.FALSE;
        if (bagName == null) {
            bagName = request.getParameter("name");
        }

        InterMineBag imBag = null;
        String scope = request.getParameter("scope");
        if (scope == null) {
            scope = TemplateHelper.ALL_TEMPLATE;
        }

        if (scope.equals(TemplateHelper.USER_TEMPLATE) 
                        || scope.equals(TemplateHelper.ALL_TEMPLATE)) {
            Profile profile = (Profile) session.getAttribute(Constants.PROFILE);
            imBag = profile.getSavedBags().get(bagName);
            if (imBag != null) {
                myBag = Boolean.TRUE;
            }
        }

        if (scope.equals(TemplateHelper.GLOBAL_TEMPLATE)
            || scope.equals(TemplateHelper.ALL_TEMPLATE)) {
            // scope == all or global
            SearchRepository searchRepository = SearchRepository
                            .getGlobalSearchRepository(servletContext);
            Map<String, ? extends WebSearchable> publicBagMap = searchRepository
                            .getWebSearchableMap(TagTypes.BAG);
            if (publicBagMap.get(bagName) != null) {
                imBag = (InterMineBag) publicBagMap.get(bagName);
            }
        }

        /* forward to bag page if this is an invalid bag */
        if (imBag == null) {
            return null;
        }
        
        WebConfig webConfig = (WebConfig) servletContext.getAttribute(Constants.WEBCONFIG);
        Model model = os.getModel();
        Type type = (Type) webConfig.getTypes().get(model.getPackageName() + "." + imBag.getType());

        List<Widget> widgets = type.getWidgets();
        Map<Integer, Map<String, Collection>> widget2extraAttrs 
        = new HashMap<Integer, Map<String, Collection>>();
        for (Widget widget2 : widgets) {
            widget2extraAttrs.put(new Integer(widget2.getId()), widget2.getExtraAttributes(
                            imBag, os));
        }
        request.setAttribute("widgets", widgets);
        request.setAttribute("widget2extraAttrs", widget2extraAttrs);

        PagedTable pagedResults = SessionMethods.getResultsTable(session, "bag." + imBag.getName());
        
        if (pagedResults == null || pagedResults.getExactSize() != imBag.getSize()) {
            pagedResults = SessionMethods.doQueryGetPagedTable(request, servletContext, imBag);
        }

        // TODO this needs to be removed when InterMineBag can store the initial ids of when the
        // bag was made.
        BagQueryConfig bagQueryConfig = (BagQueryConfig) servletContext
                        .getAttribute(Constants.BAG_QUERY_CONFIG);
        Map<String, String[]> additionalConverters = bagQueryConfig.getAdditionalConverters(imBag
                        .getType());
        if (additionalConverters != null) {
            for (String converterClassName : additionalConverters.keySet()) {
                String[] paramArray = additionalConverters.get(converterClassName);
                String[] urlFields = paramArray[0].split(",");
                for (int i = 0; i < urlFields.length; i++) {
                    if (request.getParameter(urlFields[i]) != null) {
                        request.setAttribute("extrafield", urlFields[i]);
                        request.setAttribute(urlFields[i], request.getParameter(urlFields[i]));
                        request.setAttribute("externalids", request.getParameter("externalids"));
                        break;
                    }
                }
            }
        }

        // Set the size
        String pageStr = request.getParameter("page");
        int page = (pageStr == null ? 0 : Integer.parseInt(pageStr));

        pagedResults.setPageAndPageSize(page, 5);
        if ((imBag.getSize() / 4) < page) {
            page = 0;
        }

        request.setAttribute("addparameter", request.getParameter("addparameter"));
        request.setAttribute("myBag", myBag);
        request.setAttribute("bag", imBag);
        request.setAttribute("bagSize", new Integer(imBag.size()));
        request.setAttribute("pagedResults", pagedResults);

        return null;
    }
}

