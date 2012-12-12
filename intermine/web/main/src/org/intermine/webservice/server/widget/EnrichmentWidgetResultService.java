package org.intermine.webservice.server.widget;

/*
 * Copyright (C) 2002-2012 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.io.PrintWriter;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.intermine.api.InterMineAPI;
import org.intermine.api.profile.InterMineBag;
import org.intermine.api.profile.Profile;
import org.intermine.api.profile.TagManager;
import org.intermine.api.profile.TagManager.TagNameException;
import org.intermine.api.profile.TagManager.TagNamePermissionException;
import org.intermine.api.tag.TagNames;
import org.intermine.api.tag.TagTypes;
import org.intermine.model.userprofile.Tag;
import org.intermine.objectstore.ObjectStoreException;
import org.intermine.web.context.InterMineContext;
import org.intermine.web.logic.config.WebConfig;
import org.intermine.web.logic.export.ResponseUtil;
import org.intermine.web.logic.widget.EnrichmentWidget;
import org.intermine.web.logic.widget.config.EnrichmentWidgetConfig;
import org.intermine.web.logic.widget.config.WidgetConfig;
import org.intermine.webservice.server.exceptions.ResourceNotFoundException;
import org.intermine.webservice.server.output.Output;
import org.intermine.webservice.server.output.StreamedOutput;
import org.intermine.webservice.server.output.XMLFormatter;

/**
 * Web service that returns a widget for a given list of identifiers See
 * {@link WidgetsRequestProcessor} for parameter description
 * URL examples: get an EnrichmentWidget
 * /service/widgets?widgetId=go_enrichment&amp;className=Gene&amp;extraAttributes=Bonferroni,0.1
 * ,biological_process&amp;ids=S000000003,S000000004&amp;format=html
 * get a GraphWidget
 * /service/widgets?widgetId=flyatlas
 *   &amp;className=Gene&amp;extraAttributes=
 *   &amp;ids=FBgn0011648,FBgn0011655,FBgn0025800
 *   &amp;format=html
 *
 * @author Alex Kalderimis
 * @author Xavier Watkins
 * @author Daniela Butano
 */
public class EnrichmentWidgetResultService extends WidgetService
{
    private class EnrichmentXMLFormatter extends XMLFormatter
    {

        @Override
        public String formatResult(List<String> resultRow) {
            return StringUtils.join(resultRow, "");
        }

    }

    public EnrichmentWidgetResultService(InterMineAPI im) {
        super(im);
    }

    /**
     * Executes service specific logic.
     *
     * @throws Exception an error has occurred
     */
    @SuppressWarnings("deprecation")
    @Override
    protected void execute() throws Exception {
        WidgetsServiceInput input = getInput();
        InterMineBag imBag = retrieveBag(input.getBagName());
        addOutputListInfo(imBag);

        WebConfig webConfig = InterMineContext.getWebConfig();
        WidgetConfig widgetConfig = webConfig.getWidgets().get(input.getWidgetId());

        if (widgetConfig == null || !(widgetConfig instanceof EnrichmentWidgetConfig)) {
            throw new ResourceNotFoundException("Could not find an enrichment widget called \""
                                                + input.getWidgetId() + "\"");
        }
        addOutputConfig(widgetConfig);

        //filters
        String filterSelectedValue = input.getExtraAttributes().get(0);
        if (filterSelectedValue == null || "".equals(filterSelectedValue)) {
            String filters = widgetConfig.getFiltersValues(im.getObjectStore(), imBag);
            if (filters != null && !"".equals(filters)) {
                filterSelectedValue = filters.split("\\,")[0];
                input.getExtraAttributes().set(0, filterSelectedValue);
            }
        }
        addOutputFilter(widgetConfig, filterSelectedValue, imBag);

        //reference population
        InterMineBag populationBag = getReferencePopulationBag(input);
        addOutputUserLogged();

        //instantiate the widget
        EnrichmentWidget widget = null;
        try {
            widget = (EnrichmentWidget) widgetConfig.getWidget(imBag, populationBag,
                im.getObjectStore(), input.getExtraAttributes());
        } catch (IllegalArgumentException e) {
            addOutputAttribute("message", e.getMessage());
            deleteReferencePopulationPreference(input);
            return;
        } catch (ClassCastException e) {
            throw new ResourceNotFoundException("Could not find an enrichment widget called \""
                                               + input.getWidgetId() + "\"");
        }
        addOutputInfo("notAnalysed", Integer.toString(widget.getNotAnalysed()));
        addOutputPathQuery(widget, widgetConfig);
        addOutputGeneLengthCorrection(input, widget);

        addOutputResult(widget);
    }

    @Override
    protected void addOutputConfig(WidgetConfig config) {
        super.addOutputConfig(config);
        addOutputAttribute("label", ((EnrichmentWidgetConfig) config).getLabel());
        addOutputAttribute("externalLink", ((EnrichmentWidgetConfig) config).getExternalLink());
    }

    private void addOutputPathQuery(EnrichmentWidget widget, WidgetConfig config) {
        addOutputInfo("pathQuery", widget.getPathQuery().toJson());
        addOutputInfo("pathConstraint", widget.getPathConstraint());
        addOutputInfo("pathQueryForMatches", widget.getPathQueryForMatches().toJson());
    }

    private void addOutputUserLogged() {
        if (isProfileLoggedIn()) {
            addOutputAttribute("is_logged", "true");
        } else {
            addOutputAttribute("is_logged", "false");
        }
    }

    /*
     * Set in the output the gene_lenth_correction attribute.
     * The attribute is null if the gene length coefficient correction can't be applicable
     * true if the gene length coefficient correction is selected, false if not selected
     */
    private void addOutputGeneLengthCorrection(WidgetsServiceInput input,
        EnrichmentWidget widget) {
        String geneLengthCorrectionInput = input.getExtraAttributes().get(3);
        if (widget.isGeneLengthCorrectionApplicable()) {
            try {
                double percentageGeneWithLengthNull = widget.getPercentageGeneWithLengthNull();
                if (percentageGeneWithLengthNull != 0) {
                    DecimalFormat df = new DecimalFormat("##.##");
                    df.setRoundingMode(RoundingMode.DOWN);
                    addOutputInfo(WidgetsRequestParser.PERCENTAGE_GENE_LENGTH_NOT_NULL,
                        df.format(percentageGeneWithLengthNull) + "%");
                    addOutputInfo("pathQueryGeneLengthNull",
                        widget.getPathQueryForGenesWithLengthNull(
                            InterMineContext.getWebConfig()).toJson());
                } else {
                    addOutputInfo(WidgetsRequestParser.PERCENTAGE_GENE_LENGTH_NOT_NULL, null);
                    addOutputInfo("pathQueryGeneLengthNull", null);
                }
            } catch (ObjectStoreException os) {
                addOutputInfo(WidgetsRequestParser.GENE_LENGTH_CORRECTION, null);
                addOutputInfo(WidgetsRequestParser.PERCENTAGE_GENE_LENGTH_NOT_NULL, null);
                addOutputInfo("pathQueryGeneLengthNull", null);
                return;
            }
            if (geneLengthCorrectionInput == null) {
                addOutputInfo(WidgetsRequestParser.GENE_LENGTH_CORRECTION, "false");
            } else {
                addOutputInfo(WidgetsRequestParser.GENE_LENGTH_CORRECTION,
                              geneLengthCorrectionInput);
            }
        } else {
            addOutputInfo(WidgetsRequestParser.GENE_LENGTH_CORRECTION, null);
            addOutputInfo(WidgetsRequestParser.PERCENTAGE_GENE_LENGTH_NOT_NULL, null);
            addOutputInfo("pathQueryGeneLengthNull", null);
        }
    }

    protected WidgetResultProcessor getProcessor() {
        if (formatIsJSON()) {
            return EnrichmentJSONProcessor.instance();
        } else if (formatIsXML()) {
            return EnrichmentXMLProcessor.instance();
        } else {
            return FlatFileWidgetResultProcessor.instance();
        }
    }

    protected Output makeXMLOutput(PrintWriter out) {
        ResponseUtil.setXMLHeader(response, "result.xml");
        return new StreamedOutput(out, new EnrichmentXMLFormatter());
    }

    private WidgetsServiceInput getInput() {
        return new WidgetsRequestParser(request).getInput();
    }

    private boolean isProfileLoggedIn() {
        Profile profile = getPermission().getProfile();
        if (profile.isLoggedIn()) {
            return true;
        }
        return false;
    }

    private InterMineBag getReferencePopulationBag(WidgetsServiceInput input)
        throws TagNamePermissionException, TagNameException {
        String populationBagName = input.getPopulationBagName();
        if (populationBagName == null) {
            //get preferences
            populationBagName = getReferencePopulationPreference(input);
        }
        if ("".equals(populationBagName)) {
            //json formatter doesn't format empty string
            addOutputInfo(WidgetsRequestParser.POPULATION_BAG_NAME, null);
        } else {
            addOutputInfo(WidgetsRequestParser.POPULATION_BAG_NAME, populationBagName);
        }
        InterMineBag populationBag = null;
        populationBag = retrieveBag(populationBagName);
        saveReferencePopulationPreference(input);
        return populationBag;
    }

    private void saveReferencePopulationPreference(WidgetsServiceInput input)
        throws TagNamePermissionException, TagNameException {
        if (input.isSavePopulation()) {
            if (isProfileLoggedIn()) {
                TagManager tm = im.getTagManager();
                String tagName = TagNames.IM_WIDGET + TagNames.SEPARATOR + input.getWidgetId()
                           + TagNames.SEPARATOR + input.getPopulationBagName();
                deleteReferencePopulationPreference(input);
                if (!"".equals(input.getPopulationBagName())) {
                    tm.addTag(tagName, input.getBagName(), TagTypes.BAG,
                              getPermission().getProfile());
                }
            }
        }
    }

    private void deleteReferencePopulationPreference(WidgetsServiceInput input) {
        if (isProfileLoggedIn()) {
            TagManager tm = im.getTagManager();
            List<Tag> currentTags = getReferencePopulationTags(input);
            for (Tag tag : currentTags) {
                tm.deleteTag(tag);
            }
        }
    }

    private List<Tag> getReferencePopulationTags(WidgetsServiceInput input) {
        List<Tag> populationTags = new ArrayList<Tag>();
        if (isProfileLoggedIn()) {
            Profile profile = getPermission().getProfile();
            TagManager tm = im.getTagManager();
            String prefixTagPopulation = TagNames.IM_WIDGET + TagNames.SEPARATOR
                                       + input.getWidgetId() + TagNames.SEPARATOR;
            List<Tag> tags = tm.getTags(null, null,
                             TagTypes.BAG, profile.getUsername());
            for (Tag tag : tags) {
                if (tag.getObjectIdentifier().equals(input.getBagName())
                    && tag.getTagName().startsWith(prefixTagPopulation)) {
                    populationTags.add(tag);
                }
            }
        }
        return populationTags;
    }

    private String getReferencePopulationPreference(WidgetsServiceInput input) {
        if (isProfileLoggedIn()) {
            List<Tag> populationTags = getReferencePopulationTags(input);
            if (!populationTags.isEmpty()) {
                String prefixTagPopulation = TagNames.IM_WIDGET + TagNames.SEPARATOR
                                           + input.getWidgetId() + TagNames.SEPARATOR;
                String tagName = populationTags.get(0).getTagName();
                return tagName.replace(prefixTagPopulation, "");
            }
        }
        return "";
    }
}
