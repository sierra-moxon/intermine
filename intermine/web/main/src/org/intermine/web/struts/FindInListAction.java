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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.intermine.objectstore.query.BagConstraint;
import org.intermine.objectstore.query.ConstraintOp;
import org.intermine.objectstore.query.ConstraintSet;
import org.intermine.objectstore.query.Query;
import org.intermine.objectstore.query.QueryClass;
import org.intermine.objectstore.query.QueryField;
import org.intermine.objectstore.query.QueryValue;
import org.intermine.objectstore.query.Results;
import org.intermine.objectstore.query.ResultsRow;
import org.intermine.objectstore.query.SimpleConstraint;

import org.intermine.metadata.AttributeDescriptor;
import org.intermine.metadata.ClassDescriptor;
import org.intermine.metadata.FieldDescriptor;
import org.intermine.metadata.Model;
import org.intermine.objectstore.ObjectStore;
import org.intermine.web.logic.ClassKeyHelper;
import org.intermine.web.logic.Constants;
import org.intermine.web.logic.WebUtil;
import org.intermine.web.logic.bag.InterMineBag;
import org.intermine.web.logic.profile.Profile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Action to search a list for an identifier and then highlight it on the list details page.
 * @author Kim Rutherford
 */
public class FindInListAction extends InterMineAction
{
    /**
     * Method called when user has submitted the search form.
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     * @return an ActionForward object defining where control goes next
     * @exception Exception if the application business logic throws
     *  an exception
     */
    @Override
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 @SuppressWarnings("unused") HttpServletResponse response)
        throws Exception {
        HttpSession session = request.getSession();
        ServletContext context = session.getServletContext();
        FindInListForm qsf = (FindInListForm) form;
        String textToFind = qsf.getTextToFind().trim();
        String bagName = qsf.getBagName();
        Profile profile = ((Profile) session.getAttribute(Constants.PROFILE));
        Map<String, InterMineBag> allBags = WebUtil.getAllBags(profile.getSavedBags(), context);
        InterMineBag bag = allBags.get(bagName);
        ForwardParameters forwardParameters =
            new ForwardParameters(mapping.findForward("bagDetails"));
        forwardParameters.addParameter("name", bagName);

        if (bag != null) {
            Map<String, List<FieldDescriptor>> classKeys =
                (Map<String, List<FieldDescriptor>>) context.getAttribute(Constants.CLASS_KEYS);
            ObjectStore os = (ObjectStore) context.getAttribute(Constants.OBJECTSTORE);
            String bagQualifiedType = bag.getQualifiedType();
            Collection<String> keyFields =
                ClassKeyHelper.getKeyFieldNames(classKeys, bagQualifiedType);
            int foundId = -1;
            if (keyFields.size() > 0) {
                Query q = makeQuery(textToFind, bag, keyFields);
                foundId = findFirst(os, q);
            }
            if (foundId == -1) {
                // no class key fields match so try all keys
                List<String> allStringFields = getStringFields(os, bagQualifiedType);
                Query q = makeQuery(textToFind, bag, allStringFields);
                foundId = findFirst(os, q);
            }
            if (foundId != -1) {
                forwardParameters.addParameter("highlightId", foundId + "");
                forwardParameters.addParameter("gotoHighlighted", "true");
            }
        }

        return forwardParameters.forward();
    }

    private List<String> getStringFields(ObjectStore os, String bagQualifiedType) {
        List<String> retList = new ArrayList<String>();
        Model model = os.getModel();
        ClassDescriptor cd = model.getClassDescriptorByName(bagQualifiedType);
        for (AttributeDescriptor ad: cd.getAllAttributeDescriptors()) {
            if (ad.getType().equals(String.class.getName())) {
                retList.add(ad.getName());
            }
        }
        return retList;
    }

    private Query makeQuery(String searchTerm, InterMineBag bag,
                            Collection<String> identifierFieldNames) {
        Query q = new Query();
        QueryClass qc;
        try {
            qc = new QueryClass(Class.forName(bag.getQualifiedType()));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("class not found", e);
        }
        QueryField idQF = new QueryField(qc, "id");
        ConstraintSet cs = new ConstraintSet(ConstraintOp.AND);
        q.addFrom(qc);
        q.addToSelect(idQF);

        BagConstraint bagConstraint = new BagConstraint(idQF, ConstraintOp.IN, bag.getOsb());
        cs.addConstraint(bagConstraint);

        ConstraintSet fieldCS = new ConstraintSet(ConstraintOp.OR);

        for (String fieldName: identifierFieldNames) {
            QueryField qf = new QueryField(qc, fieldName);
            SimpleConstraint sc =
                new SimpleConstraint(qf, ConstraintOp.EQUALS, new QueryValue(searchTerm));
            fieldCS.addConstraint(sc);
        }

        cs.addConstraint(fieldCS);

        q.setConstraint(cs);

        return q;
    }

    /**
     * Return the id of the first object in the output, or -1 if there aren't any rows.
     */
    private int findFirst(ObjectStore os, Query q) {
        Results res = os.execute(q);
        try {
            return (Integer) ((ResultsRow) res.get(0)).get(0);
        } catch (IndexOutOfBoundsException e) {
            return -1;
        }
    }
}
