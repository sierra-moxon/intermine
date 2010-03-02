package org.intermine.bio.web;

/*
 * Copyright (C) 2002-2010 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.tiles.ComponentContext;
import org.apache.struts.tiles.actions.TilesAction;
import org.intermine.api.InterMineAPI;
import org.intermine.api.profile.InterMineBag;
import org.intermine.bio.logic.Mine;
import org.intermine.bio.logic.OrthologueLinkManager;
import org.intermine.bio.web.logic.BioUtil;
import org.intermine.web.logic.bag.BagHelper;
import org.intermine.web.logic.session.SessionMethods;



/**
 * Class that generates links to other intermines
 * @author julie
 */
public class OrthologueLinkController  extends TilesAction
{
    private static final String IDENTIFIER_FIELD = "primaryIdentifier";
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ActionForward execute(@SuppressWarnings("unused") ComponentContext context,
                                 @SuppressWarnings("unused") ActionMapping mapping,
                                 @SuppressWarnings("unused") ActionForm form,
                                 HttpServletRequest request,
                                 @SuppressWarnings("unused") HttpServletResponse response) {

        final InterMineAPI im = SessionMethods.getInterMineAPI(request.getSession());
        InterMineBag bag = (InterMineBag) request.getAttribute("bag");
        Properties webProperties = SessionMethods.getWebProperties(request.getSession()
                .getServletContext());

        String identifierList = BagHelper.getIdList(bag, im.getObjectStore(), "", IDENTIFIER_FIELD);
        request.setAttribute("identifierList", identifierList);
        
        OrthologueLinkManager orthologueLinkManager
        = OrthologueLinkManager.getInstance(im, webProperties);
        Collection<String> organismNamesInBag = BioUtil.getOrganisms(im.getObjectStore(), bag, 
                false, "shortName");
        Map<Mine, Map<String, Set[]>> mines 
            = orthologueLinkManager.getMines(organismNamesInBag);

        // test if mine has organisms in our list
        if (!mines.isEmpty()) {
            request.setAttribute("mines", mines);
        }

        return null;
    }
}
