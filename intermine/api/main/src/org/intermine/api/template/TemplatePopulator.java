package org.intermine.api.template;

/*
 * Copyright (C) 2002-2009 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.intermine.metadata.Model;
import org.intermine.model.InterMineObject;
import org.intermine.objectstore.query.BagConstraint;
import org.intermine.objectstore.query.ConstraintOp;
import org.intermine.pathquery.Constraint;
import org.intermine.pathquery.ConstraintValueParser;
import org.intermine.pathquery.ParseValueException;
import org.intermine.pathquery.PathNode;
import org.intermine.pathquery.PathQuery;
import org.intermine.util.TypeUtil;


/**
 * Configures original template. Old constraints are replaced with the similar
 * new constraints, that have different values.
 * @author Jakub Kulaviak
 **/
public class TemplatePopulator
{

    /**
     * Given a template and a map of values for editable constraints on each editable node create
     * a copy of the template query with the values filled in.  This may alter the query when e.g.
     * bag constraints are applied to a class rather than an attribute.
     * @param origTemplate the template to populate with values
     * @param newConstraints a map from editable node to a list of values for each editable
     *  constraint
     * @return a copy of the template with values filled in
     */
    public static TemplateQuery getPopulatedTemplate(TemplateQuery origTemplate,
            Map<String, List<TemplateValue>> newConstraints) {
        TemplateQuery template = (TemplateQuery) origTemplate.clone();

        checkPaths(origTemplate.getModel(), newConstraints.values(), template);

        for (PathNode node : template.getEditableNodes()) {
            List<Constraint> constraints = template.getEditableConstraints(node);
            List<TemplateValue> values = newConstraints.get(node.getPathString());

            if (values == null) {
                throw new TemplatePopulatorException("There are no specified constraint values "
                        + "for path " + node.getPathString());
            }
            if (constraints.size() > values.size()) {
                throw new TemplatePopulatorException("Values were not provided for all editable "
                        + "constraints on path " + node.getPathString());
            }
            if (constraints.size() < values.size()) {
                throw new TemplatePopulatorException("There were more values provided than "
                        + "  there are editable constraints on the path " + node.getPathString());
            }

            for (Constraint con : constraints) {
                boolean found = false;
                for (TemplateValue templateValue : values) {
                    // TODO do we need test for null?
                    if (templateValue != null && con.getCode().equals(templateValue.getCode())) {
                        setConstraint(template, node, con, templateValue);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    throw new TemplatePopulatorException("Did not find a value for constraint "
                            + " with code '" + con.getCode() + "' on path " 
                            + node.getPathString());
                }
            }
        }

        return template;
    }


    private static void checkPaths(Model model, Collection<List<TemplateValue>> collection,
            TemplateQuery templateQuery) {
        for (List<TemplateValue> col : collection) {
            for (TemplateValue value : col) {
                // will throw a PathError if not valid
                PathQuery.makePath(model, templateQuery, value.getPath());
            }
        }
    }

    private static void setConstraint(TemplateQuery template, PathNode node, Constraint c, TemplateValue templateValue) {
        int constraintIndex = node.getConstraints().indexOf(c);

        Object extraValue = getExtraValue(c, templateValue, node);
        Object value = getValue(c, templateValue, node);
        Constraint newConstraint = new Constraint(templateValue.getOperation(), value,
                true, c.getDescription(), c.getCode(), c.getIdentifier(), extraValue);

        if (templateValue.isBagConstraint()) {
            if (!BagConstraint.VALID_OPS.contains(templateValue.getOperation())) {
                // TODO list the bag ops in error message ...
                throw new TemplatePopulatorException("A bag (list) constraint on path "
                        + node.getPathString() + " with value " + c.getValue() + " does not have a "
                        + "valid constraint type, must be ...");
            }

            // TODO should we check the bag exists here?
        } 
        
        // if this is a bag constraint we may need to switch to the parent node
        if (templateValue.isBagConstraint() && node.isAttribute()) {	
            PathNode parentNode = template.addNode(node.getParent().getPathString());
            parentNode.getConstraints().add(newConstraint);
            node.removeConstraint(c);
        } else if (templateValue.isObjectConstraint()) {
            String basePath = node.getPathString();
            if (node.isAttribute())  {
                basePath = node.getParent().getPathString();
            }
            String idPath = basePath + ".id";
            PathNode idNode = template.addNode(idPath);
            Integer idValue = ((InterMineObject) value).getId();
            Constraint idConstraint = new Constraint(ConstraintOp.EQUALS, idValue,
                    true, c.getDescription(), c.getCode(), c.getIdentifier(), null);
            idNode.getConstraints().add(idConstraint);

            // TODO any other constraints on this node are now irrelevant
            node.removeConstraint(c);
        } else {
            node.getConstraints().set(constraintIndex, newConstraint);
        }
    }

    private static Object getValue(Constraint c, TemplateValue templateValue, PathNode node) {
        Object ret = templateValue.getValue();
        if (ret instanceof String) {
            try {
                ret = ConstraintValueParser.parse((String) ret, getType(node),
                        templateValue.getOperation());
            } catch (ParseValueException ex) {
                throw new RuntimeException("invalid value: " + templateValue.getValue() + ". "
                        + ex.getMessage());
            }
        }
        return ret;
    }

    private static Object getExtraValue(Constraint c, TemplateValue templateValue, PathNode node) {
        Object ret = templateValue.getExtraValue();
        if (ret instanceof String) {
            if (templateValue.getExtraValue() == null || ((String) ret).trim().length() == 0) {
                return c.getExtraValue();
            }
            try {
                ret = ConstraintValueParser.parse((String) ret, getType(node),
                        templateValue.getOperation());
            } catch (ParseValueException ex) {
                throw new RuntimeException("invalid value: " + templateValue.getExtraValue() + ". "
                        + ex.getMessage());
            }
        }
        return ret;
    }

    private static Class getType(PathNode node) {
        Class fieldClass;
        if (node.isAttribute()) {
            fieldClass = TypeUtil.getClass(node.getType());
        } else {
            fieldClass = String.class;
        }
        return fieldClass;
    }
}

