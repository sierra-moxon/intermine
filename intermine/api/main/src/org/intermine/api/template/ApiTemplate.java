package org.intermine.api.template;

import java.util.ArrayList;
import java.util.HashMap;

import org.intermine.api.search.WebSearchable;
import org.intermine.pathquery.PathConstraint;
import org.intermine.pathquery.PathQuery;
import org.intermine.model.userprofile.SavedTemplateQuery;
import org.intermine.template.SwitchOffAbility;
import org.intermine.template.TemplateQuery;

/**
 * This class extends TemplateQuery to provide the features needed by
 * the API - Lucene indexing and linking to Saved-Queries.
 * @author Alex Kalderimis
 *
 */
public class ApiTemplate extends TemplateQuery implements WebSearchable {

    /** SavedTemplateQuery object in the UserProfile database, so we can update summaries. */
    protected SavedTemplateQuery savedTemplateQuery = null;
    
    public ApiTemplate(String name, String title, String comment,
            PathQuery query) {
        super(name, title, comment, query);
    }
    
    public ApiTemplate(TemplateQuery template) {
        super(template.getName(), template.getTitle(), template.getComment(), template);
        this.edited = template.isEdited();
        this.editableConstraints = new ArrayList<PathConstraint>(template.getEditableConstraints());
        this.constraintDescriptions = new HashMap<PathConstraint, String>(template.getConstraintDescriptions());
        this.constraintSwitchOffAbility =
            new HashMap<PathConstraint, SwitchOffAbility>(template.getConstraintSwitchOffAbility());
    }

    /**
     * Clone this ApiQuery
     */
    @Override
    public synchronized ApiTemplate clone() {
        ApiTemplate t = (ApiTemplate) super.clone();
        t.savedTemplateQuery = null;
        return t;
    }
    
    /**
     * Sets the saved template query object.
     *
     * @param savedTemplateQuery the database object
     */
    public void setSavedTemplateQuery(SavedTemplateQuery savedTemplateQuery) {
        this.savedTemplateQuery = savedTemplateQuery;
    }

    /**
     * Gets the saved template query object.
     *
     * @return a SavedTemplateQuery object that represents this TemplateQuery in the userprofile
     * database
     */
    public SavedTemplateQuery getSavedTemplateQuery() {
        return savedTemplateQuery;
    }
    
    // ApiTemplates should compare with strict object equality, to avoid one user's templates clobbering
    // another's.
    @Override
    public boolean equals(Object other) {
        return this == other;
    }

}
