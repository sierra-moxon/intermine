package org.intermine.web;

/*
 * Copyright (C) 2002-2005 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.ServletContext;

import org.intermine.metadata.Model;
import org.intermine.model.InterMineObject;
import org.intermine.model.userprofile.Tag;
import org.intermine.objectstore.ObjectStore;
import org.intermine.util.DynamicUtil;
import org.intermine.util.TypeUtil;
import org.intermine.web.tagging.TagTypes;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Helper methods for template lists.
 * @author Thomas Riley
 */
public class TemplateListHelper
{
    private static final Logger LOG = Logger.getLogger(TemplateListHelper.class);
    private static final TemplateComparator TEMPLATE_COMPARATOR = new TemplateComparator();
    
    /**
     * Get the Set of templates for a given aspect.
     * @param aspect aspect name
     * @param context ServletContext
     * @return Set of TemplateQuerys
     */
    public static List getAspectTemplates(String aspect, ServletContext context) {
        String sup = (String) context.getAttribute(Constants.SUPERUSER_ACCOUNT);
        ProfileManager pm = SessionMethods.getProfileManager(context);
        Profile p = pm.getProfile(sup);
        
        Map templates = new TreeMap();
        List tags = pm.getTags(null, null, TagTypes.TEMPLATE, sup);
        
        for (Iterator iter = tags.iterator(); iter.hasNext(); ) {
            Tag tag = (Tag) iter.next();
            if (tag.getTagName().startsWith("aspect:")) {
                String aspectFromTagName = tag.getTagName().substring(7).trim();

                if (StringUtils.equals(aspect, aspectFromTagName)) {
                    TemplateQuery tq = (TemplateQuery) 
                        p.getSavedTemplates().get(tag.getObjectIdentifier());
                    if (tq != null) {
                        templates.put(tq.getName(), tq);
                    }
                }
            }
        }
        
        List retList = new ArrayList(templates.values());

        return retList;
    }

    /**
     * Get the Set of templates for a given aspect that contains constraints
     * that can be filled in with an attribute from the given InterMineObject.
     * @param aspect aspect name
     * @param context ServletContext
     * @param object InterMineObject
     * @param fieldExprsOut field expressions to fill in
     * @return Set of TemplateQuerys
     */
    public static List getAspectTemplateForClass(String aspect,
                                                ServletContext context,
                                                InterMineObject object,
                                                Map fieldExprsOut) {
        List templates = new ArrayList();
        ObjectStore os = (ObjectStore) context.getAttribute(Constants.OBJECTSTORE);
        List all = getAspectTemplates(aspect, context);
        Set types = new HashSet();
        types.addAll(DynamicUtil.decomposeClass(object.getClass()));
        types.addAll(Arrays.asList(object.getClass().getInterfaces()));
        Class sc = object.getClass();
        while (sc != null) {
            types.add(sc);
            sc = sc.getSuperclass();
        }
        
        
        for (Iterator iter = all.iterator(); iter.hasNext(); ) {
            TemplateQuery template = (TemplateQuery) iter.next();
            List constraints = template.getAllConstraints();
            Model model = os.getModel();
            Iterator constraintIter = constraints.iterator();
            while (constraintIter.hasNext()) {
                Constraint c = (Constraint) constraintIter.next();

                String constraintIdentifier = c.getIdentifier();
                String[] bits = constraintIdentifier.split("\\.");
                
                if (bits.length == 2) {
                    String className = model.getPackageName() + "." + bits[0];
                    String fieldName = bits[1];
                    String fieldExpr = TypeUtil.unqualifiedName(className) + "." + fieldName;
                    try {
                        Class iface = Class.forName(className);
                        if (types.contains(iface)
                            && model.getClassDescriptorByName(className)
                                .getFieldDescriptorByName(fieldName) != null) {
                            templates.add(template);
                            
                            List fieldExprs = (List) fieldExprsOut.get(template);
                            if (fieldExprs == null) {
                                fieldExprs = new ArrayList();
                                fieldExprsOut.put(template, fieldExprs);
                            }
                            fieldExprs.add(fieldExpr);
                        }
                    } catch (ClassNotFoundException err) {
                        LOG.error(err);
                    }
                }
            }
        }
        
        Collections.sort(templates, TEMPLATE_COMPARATOR);
        
        return templates;
    }
}
