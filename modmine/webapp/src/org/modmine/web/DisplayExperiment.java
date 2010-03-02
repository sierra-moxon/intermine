package org.modmine.web;

/*
 * Copyright (C) 2002-2010 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.intermine.model.bio.Experiment;
import org.intermine.model.bio.ExperimentalFactor;
import org.intermine.model.bio.Organism;
import org.intermine.model.bio.Project;
import org.intermine.model.bio.Submission;
import org.intermine.objectstore.ObjectStore;
import org.intermine.util.StringUtil;


/**
 * Wrap an experiment and its submissions to make display code simpler.
 * @author Richard Smith
 *
 */
public class DisplayExperiment
{

    private String name;
    private List<Submission> submissions = new ArrayList<Submission>();
    private String projectName;
    private String pi;
    private String description = null;
    private Set<String> factorTypes = new HashSet<String>();
    private Set<String> organisms = new HashSet<String>();
    private Map<String, Long> featureCounts;
    private ObjectStore os;
    private String experimentType;
    
    /**
     * Construct with objects from database and feature counts summary map. 
     * @param exp the experiment
     * @param project the experiment's project
     * @param featureCounts a map of feature type to count
     * @param os the objectstore
     */
    public DisplayExperiment(Experiment exp, Project project, Map<String, Long> featureCounts,
            ObjectStore os) {
        initialise(exp, project);
        this.featureCounts = featureCounts;
        this.os = os;
    }
    
    
    private void initialise(Experiment exp, Project proj) {
        this.name = exp.getName();
        if (name.indexOf('&') > 0) {
            name = name.substring(0, name.indexOf('&'));
        }
        
        this.pi = proj.getNamePI() + " " + proj.getSurnamePI();
        this.projectName = proj.getName();

        Set<String> expTypes = new HashSet<String>();
        
        for (Submission submission : exp.getSubmissions()) {
            if (this.description == null) {
                this.description = submission.getDescription();
            }
            submissions.add(submission);
            for (ExperimentalFactor factor : submission.getExperimentalFactors()) {
                factorTypes.add(factor.getType());
            }
            
            if (submission.getExperimentType() != null) {
                expTypes.add(submission.getExperimentType());
            }
        }
        
        this.experimentType = StringUtil.prettyList(expTypes);
        
        for (Organism organism : proj.getOrganisms()) {
            organisms.add(organism.getShortName());
        }
    }

        
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }


    /**
     * @return the projectName
     */
    public String getProjectName() {
        return projectName;
    }


    /**
     * @return the pi
     */
    public String getPi() {
        return pi;
    }


    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the submissions
     */
    public List<Submission> getSubmissions() {
        return submissions;
    }

    /**
     * @return the submissions Ids
     */
    public List<String> getSubmissionsDccId() {
        List<String> subDccIds = new ArrayList<String>();
        for (Submission sub : submissions) {
            subDccIds.add(sub.getdCCid().toString());
        }
        return subDccIds;
    }

    /**
     * @return submissions and a map of feature type to count
     */
    public Map<Submission, Map<String, Long>> getSubmissionsAndFeatureCounts() {
        Map<Submission, Map<String, Long>> subMap = new HashMap<Submission, Map<String, Long>>();
        for (Submission sub : submissions) {
            subMap.put(sub, MetadataCache.getSubmissionFeatureCounts(os, sub.getdCCid()));
        }
        return subMap;
    }
    
    /**
     * @return the factorTypes
     */
    public Set<String> getFactorTypes() {
        return factorTypes;
    }


    /**
     * @return the organisms
     */
    public Set<String> getOrganisms() {
        return organisms;
    }
    
    /**
     * @return the count of submissions
     */
    public int getSubmissionCount() {
        return submissions.size();
    }


    /**
     * @return the featureCounts
     */
    public Map<String, Long> getFeatureCounts() {
        return featureCounts;
    }

    
    /**
     * @return the number of experimental factors
     */
    public int getFactorCount() {
        return factorTypes.size();
    }


    /**
     * @return the experimentType
     */
    public String getExperimentType() {
        return experimentType;
    }
    
    
}
