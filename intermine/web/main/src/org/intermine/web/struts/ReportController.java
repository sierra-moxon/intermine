package org.intermine.web.struts;

/*
 * Copyright (C) 2002-2011 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.intermine.api.InterMineAPI;
import org.intermine.api.profile.TagManager;
import org.intermine.api.tag.AspectTagUtil;
import org.intermine.api.tag.TagNames;
import org.intermine.api.template.TemplateManager;
import org.intermine.metadata.ClassDescriptor;
import org.intermine.metadata.FieldDescriptor;
import org.intermine.model.InterMineObject;
import org.intermine.model.userprofile.Tag;
import org.intermine.objectstore.ObjectStore;
import org.intermine.objectstore.ObjectStoreException;
import org.intermine.web.displayer.CustomDisplayer;
import org.intermine.web.logic.PortalHelper;
import org.intermine.web.logic.config.InlineList;
import org.intermine.web.logic.results.DisplayCollection;
import org.intermine.web.logic.results.DisplayField;
import org.intermine.web.logic.results.DisplayReference;
import org.intermine.web.logic.results.ReportObject;
import org.intermine.web.logic.results.ReportObjectFactory;
import org.intermine.web.logic.session.SessionMethods;
import org.jfree.util.Log;

/**
 * New objectDetails.
 *
 * @author Radek Stepan
 */
public class ReportController extends InterMineAction
{

    /**
     * {@inheritDoc}
     */
    @Override
    public ActionForward execute(@SuppressWarnings("unused") ActionMapping mapping,
            @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        HttpSession session = request.getSession();
        InterMineAPI im = SessionMethods.getInterMineAPI(session);

        // fetch & set requested object
        InterMineObject requestedObject = getRequestedObject(im, request);
        if (requestedObject != null) {
            ReportObjectFactory reportObjectFactory = SessionMethods.getReportObjects(session);
            ReportObject reportObject = reportObjectFactory.get(requestedObject);
            //request.setAttribute("reportObject", reportObject);
            request.setAttribute("object", reportObject);
            session.setAttribute("reportObject", reportObject);

            request.setAttribute("requestedObject", requestedObject);

            // hell starts here
            TagManager tagManager = im.getTagManager();
            ServletContext servletContext = session.getServletContext();
            ObjectStore os = im.getObjectStore();
            String superuser = im.getProfileManager().getSuperuser();

            // place InlineLists based on TagManager, reportObject is cached while Controller is not
            Map<String, List<InlineList>> placedInlineLists =
                new TreeMap<String, List<InlineList>>();
            // traverse all unplaced (non-header) InlineLists
            List<InlineList> unplacedInlineLists = reportObject.getNormalInlineLists();
            if (unplacedInlineLists != null) {
                for (Integer i = 0; i < unplacedInlineLists.size(); i++) {
                    InlineList list = unplacedInlineLists.get(i);
                    // Descriptor, assume not null
                    FieldDescriptor fd = list.getDescriptor();
                    // type
                    String taggedType = null;
                    if (fd.isCollection()) {
                        taggedType = "collection";
                    } else if (fd.isReference()) {
                        taggedType = "reference";
                    } else if (fd.isAttribute()) {
                        taggedType = "attribute";
                    } else {
                        // OMG, now what?
                    }
                    List<Tag> tags =
                        tagManager.getTags(null, fd.getClassDescriptor().getUnqualifiedName()
                                + "." + fd.getName(), taggedType, superuser);
                    for (Tag tag : tags) {
                        String tagName = tag.getTagName();
                        // aspect much?
                        if (AspectTagUtil.isAspectTag(tagName)) {
                            List<InlineList> l = null;
                            if (placedInlineLists.containsKey(tagName)) {
                                // we already have aspect like this...
                                l = placedInlineLists.get(tagName);
                            } else {
                                // we do not
                                l = new ArrayList<InlineList>();
                            }
                            // save, save, save!
                            l.add(list);
                            placedInlineLists.put(tagName, l);
                            // remove!
                            unplacedInlineLists.remove(list);
                        }
                    }
                }
            }
            session.setAttribute("mapOfInlineLists", placedInlineLists);
            session.setAttribute("listOfUnplacedInlineLists", unplacedInlineLists);

            Map<String, Map<String, DisplayField>> placementRefsAndCollections = new TreeMap<String,
                Map<String, DisplayField>>();
            Set<String> aspects =
                new LinkedHashSet<String>(SessionMethods.getCategories(servletContext));

            Set<ClassDescriptor> cds =
                os.getModel().getClassDescriptorsForClass(requestedObject.getClass());

            for (String aspect : aspects) {
                placementRefsAndCollections.put(TagNames.IM_ASPECT_PREFIX + aspect,
                        new TreeMap<String, DisplayField>(String.CASE_INSENSITIVE_ORDER));
            }

            Map<String, DisplayField> miscRefs = new TreeMap<String, DisplayField>(
                    reportObject.getRefsAndCollections());
            placementRefsAndCollections.put(TagNames.IM_ASPECT_MISC, miscRefs);

            for (Iterator<Entry<String, DisplayField>> iter
                    = reportObject.getRefsAndCollections().entrySet().iterator(); iter.hasNext();) {
                Map.Entry<String, DisplayField> entry = iter.next();
                DisplayField df = entry.getValue();
                if (df instanceof DisplayReference) {
                    categoriseBasedOnTags(((DisplayReference) df).getDescriptor(),
                            "reference", df, miscRefs, tagManager, superuser,
                            placementRefsAndCollections, SessionMethods.isSuperUser(session));
                } else if (df instanceof DisplayCollection) {
                    categoriseBasedOnTags(((DisplayCollection) df).getDescriptor(),
                            "collection", df, miscRefs, tagManager, superuser,
                            placementRefsAndCollections, SessionMethods.isSuperUser(session));
                }
            }

            // remove any fields overridden by displayers
            removeFieldsReplacedByCustomDisplayers(reportObject, placementRefsAndCollections);

            request.setAttribute("placementRefsAndCollections", placementRefsAndCollections);

            String type = reportObject.getType();
            request.setAttribute("objectType", type);

            String stableLink =
                PortalHelper.generatePortalLink(reportObject.getObject(), im, request);
            if (stableLink != null) {
                request.setAttribute("stableLink", stableLink);
            }

            // attach only non empty categories
            Set<String> allClasses = new HashSet<String>();
            for (ClassDescriptor cld : cds) {
                allClasses.add(cld.getUnqualifiedName());
            }
            TemplateManager templateManager = im.getTemplateManager();
            Map<String, List<CustomDisplayer>> displayerMap = reportObject.getReportDisplayers();

            List<String> categories = new LinkedList<String>();
            for (String aspect : aspects) {
                // 1) Displayers
                // 2) Inline Lists
                if (
                        (displayerMap != null
                                && displayerMap.containsKey(aspect))
                        || placedInlineLists.containsKey(aspect)) {
                    categories.add(aspect);
                } else {
                    // 3) Templates
                    if (!templateManager.getReportPageTemplatesForAspect(aspect, allClasses)
                            .isEmpty()) {
                        categories.add(aspect);
                    } else {
                        // 4) References & Collections
                        if (placementRefsAndCollections.containsKey("im:aspect:" + aspect)
                                && placementRefsAndCollections.get("im:aspect:" + aspect) != null) {
                            for (DisplayField df : placementRefsAndCollections.get(
                                    "im:aspect:" + aspect).values()) {
                                if (df.getSize() > 0) {
                                    categories.add(aspect);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            if (!categories.isEmpty()) {
                request.setAttribute("categories", categories);
            }
        }

        return null;
    }

    private InterMineObject getRequestedObject(InterMineAPI im, HttpServletRequest request) {

        String idString = request.getParameter("id");
        Integer id = new Integer(Integer.parseInt(idString));
        ObjectStore os = im.getObjectStore();
        InterMineObject requestedObject = null;
        try {
            requestedObject = os.getObjectById(id);
        } catch (ObjectStoreException e) {
            Log.warn("Accessed report page with id: " + id + " but failed to find object.", e);
        }
        return requestedObject;
    }

    /**
     * For a given FieldDescriptor, look up its 'aspect:' tags and place it in
     * the correct map within placementRefsAndCollections. If categorised,
     * remove it from the supplied miscRefs map.
     *
     * @param fd the FieldDecriptor (a references or collection)
     * @param taggedType 'reference' or 'collection'
     * @param dispRef the corresponding DisplayReference or DisplayCollection
     * @param miscRefs map that contains dispRef (may be removed by this method)
     * @param tagManager the tag manager
     * @param sup  the superuser account name
     * @param placementRefsAndCollections take from the ReportObject
     * @param isSuperUser if current user is superuser
     */
    public static void categoriseBasedOnTags(FieldDescriptor fd,
            String taggedType, DisplayField dispRef, Map<String, DisplayField> miscRefs,
            TagManager tagManager, String sup, Map<String, Map<String, DisplayField>>
            placementRefsAndCollections, boolean isSuperUser) {
        List<Tag> tags = tagManager.getTags(null, fd.getClassDescriptor()
                .getUnqualifiedName()
                + "." + fd.getName(), taggedType, sup);
        for (Tag tag : tags) {
            String tagName = tag.getTagName();
            if (!isSuperUser && tagName.equals(TagNames.IM_HIDDEN)) {
                //miscRefs.remove(fd.getName());
                // Maybe it was added already to some placement and
                // that's why it must be removed
                removeField(fd.getName(), placementRefsAndCollections);
                return;
            }
            if (AspectTagUtil.isAspectTag(tagName)) {
                Map<String, DisplayField> refs = placementRefsAndCollections.get(tagName);
                if (refs != null) {
                    refs.put(fd.getName(), dispRef);
                    //miscRefs.remove(fd.getName());
                }
            } else if (tagName.equals(TagNames.IM_SUMMARY)) {
                //miscRefs.remove(fd.getName());
            }
        }
    }

    private void removeFieldsReplacedByCustomDisplayers(ReportObject reportObject,
            Map<String, Map<String, DisplayField>> placementRefsAndCollections) {
        for (String fieldName : reportObject.getReplacedFieldExprs()) {
            removeField(fieldName, placementRefsAndCollections);
        }
    }

    /**
     * Removes field from placements.
     *
     * @param name
     * @param placementRefsAndCollections
     */
    private static void removeField(String name,
            Map<String, Map<String, DisplayField>> placementRefsAndCollections) {
        Iterator<Entry<String, Map<String, DisplayField>>> it = placementRefsAndCollections
                .entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, Map<String, DisplayField>> entry = it.next();
            entry.getValue().remove(name);
        }
    }


}
