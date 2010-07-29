package org.flymine.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.intermine.api.config.ClassKeyHelper;
import org.intermine.metadata.ClassDescriptor;
import org.intermine.metadata.FieldDescriptor;
import org.intermine.model.InterMineObject;
import org.intermine.util.TypeUtil;
import org.intermine.web.logic.config.FieldConfig;
import org.intermine.web.logic.config.FieldConfigHelper;
import org.intermine.web.logic.config.WebConfig;
import org.jfree.util.Log;

/**
 * Container for a single result row from the keyword search
 * @author nils
 */
public class KeywordSearchResult
{
    private static final Logger LOG = Logger.getLogger(KeywordSearchResult.class);

    final WebConfig webconfig;
    final InterMineObject object;

    final int id;
    final String type;
    final float score;
    final int points;

    final HashMap<String, FieldConfig> fieldConfigs;
    final Vector<String> keyFields;
    final Vector<String> additionalFields;
    final HashMap<String, Object> fieldValues;

    /**
     * create the container object - automatically reads fields and saves the results in members
     * @param webconfig webconfig
     * @param object the object this result should contain
     * @param classKeys keys associated with this class
     * @param classDescriptor descriptor for this class
     * @param score score for this hit
     */
    public KeywordSearchResult(WebConfig webconfig, InterMineObject object,
            Map<String, List<FieldDescriptor>> classKeys, ClassDescriptor classDescriptor,
            float score) {
        super();

        List<FieldConfig> fieldConfigList = FieldConfigHelper.getClassFieldConfigs(webconfig,
                classDescriptor);
        this.fieldConfigs = new HashMap<String, FieldConfig>();
        this.keyFields = new Vector<String>();
        this.additionalFields = new Vector<String>();
        this.fieldValues = new HashMap<String, Object>();

        for (FieldConfig fieldConfig : fieldConfigList) {
            if (fieldConfig.getShowInSummary()) {
                fieldConfigs.put(fieldConfig.getFieldExpr(), fieldConfig);

                if (ClassKeyHelper.isKeyField(classKeys, classDescriptor.getName(), fieldConfig
                        .getFieldExpr())) {
                    this.keyFields.add(fieldConfig.getFieldExpr());
                } else {
                    this.additionalFields.add(fieldConfig.getFieldExpr());
                }

                if (fieldConfig.getDisplayer() == null) {
                    fieldValues.put(fieldConfig.getFieldExpr(), getValueForField(object,
                            fieldConfig.getFieldExpr()));
                }
            }
        }

        this.webconfig = webconfig;
        this.object = object;
        this.id = object.getId();
        this.type = classDescriptor.getUnqualifiedName();
        this.score = score;
        this.points = Math.round(Math.max(0.1F, Math.min(1, getScore())) * 10); // range
        // 1..10
    }

    private Object getValueForField(InterMineObject object, String expression) {
        LOG.debug("Getting field " + object.getClass().getName() + " -> " + expression);
        Object value = null;

        try {
            int dot = expression.indexOf('.');
            if (dot > -1) {
                String subExpression = expression.substring(dot + 1);
                Object reference = TypeUtil.getFieldValue(object, expression.substring(0, dot));
                LOG.debug("Reference=" + reference);

                // recurse into next object
                if (reference != null) {
                    if (reference instanceof InterMineObject) {
                        value = getValueForField((InterMineObject) reference, subExpression);
                    } else {
                        LOG.warn("Reference is not an IMO in " + object.getClass().getName()
                                + " -> " + expression);
                    }
                }
            } else {
                value = TypeUtil.getFieldValue(object, expression);
            }
        } catch (IllegalAccessException e) {
            Log.warn(null, e);
        }

        return value;
    }

    /**
     * returns original intermine object
     * @return object
     */
    public InterMineObject getObject() {
        return object;
    }

    /**
     * intermine ID
     * @return x
     */
    public int getId() {
        return id;
    }

    /**
     * returns the name of the class for this object (category)
     * @return
     */
    public String getType() {
        return type;
    }

    /**
     * return score
     * @return ...
     */
    public float getScore() {
        return score;
    }

    /**
     * return points
     * @return 1..10
     */
    public int getPoints() {
        return points;
    }

    public WebConfig getWebconfig() {
        return webconfig;
    }

    public HashMap<String, FieldConfig> getFieldConfigs() {
        return fieldConfigs;
    }

    public final Vector<String> getKeyFields() {
        return keyFields;
    }

    public final Vector<String> getAdditionalFields() {
        return additionalFields;
    }

    public HashMap<String, Object> getFieldValues() {
        return fieldValues;
    }

}
