package org.flymine.task;

/*
 * Copyright (C) 2002-2005 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import org.intermine.dataconversion.DataTranslator;
import org.intermine.dataconversion.ItemReader;
import org.intermine.dataconversion.ItemWriter;
import org.intermine.dataconversion.ObjectStoreItemReader;
import org.intermine.dataconversion.ObjectStoreItemWriter;
import org.intermine.objectstore.ObjectStoreFactory;
import org.intermine.objectstore.ObjectStoreWriterFactory;
import org.intermine.metadata.Model;
import org.intermine.task.DynamicAttributeTask;

/**
 * Task to translate data
 * @author Mark Woodbridge
 */
public class DataTranslatorTask extends DynamicAttributeTask
{
    protected String translator, source, targetAlias, srcModel, tgtModel, organism,
        dataLocation, mapping, organisms;

    /**
     * Set the translator
     * @param translator the translator
     */
    public void setTranslator(String translator) {
        this.translator = translator;
    }

    /**
     * Set the source
     * @param source the source
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * Set the targetAlias
     * @param targetAlias the targetAlias
     */
    public void setTarget(String targetAlias) {
        this.targetAlias = targetAlias;
    }

    /**
     * Set the source model name
     * @param srcModel source model name
     */
    public void setSrcModel(String srcModel) {
        this.srcModel = srcModel;
    }

    /**
     * Set the target model name
     * @param tgtModel target model name
     */
    public void setTgtModel(String tgtModel) {
        this.tgtModel = tgtModel;
    }

    /**
     * Set the organism abbreviation (for ensembl)
     * @param organism the organism abbreviation
     */
    public void setOrganism(String organism) {
        this.organism = organism;
    }

    /**
     * Set a space separated list of taxon ids (for psi)
     * @param organisms a list of taxon ids
     */
//     public void setOrganisms(String organisms) {
//         this.organisms = organisms;
//     }

    /**
     * Set the data location (for protein-structure)
     * @param dataLocation the data location
     */
    public void setDataLocation(String dataLocation) {
        this.dataLocation = dataLocation;
    }

    /**
     * @see Task#execute
     */
    public void execute() throws BuildException {
        if (translator == null) {
            throw new BuildException("translator attribute not set");
        }
        if (source == null) {
            throw new BuildException("source attribute not set");
        }
        if (targetAlias == null) {
            throw new BuildException("target attribute not set");
        }
        if (tgtModel == null) {
            throw new BuildException("tgtModel attribute not set");
        }
        if (srcModel == null) {
            throw new BuildException("srcModel attribute not set");
        }

        try {
            Class cls = Class.forName(translator);
            ItemReader reader = null;
            try {
                Method m = cls.getMethod("getPrefetchDescriptors", null);
                reader = new ObjectStoreItemReader(ObjectStoreFactory.getObjectStore(source),
                                                   (Map) m.invoke(null, null));
            } catch (NoSuchMethodException e) {
                reader = new ObjectStoreItemReader(ObjectStoreFactory.getObjectStore(source));
            }
            Class[] types = null;
            Object[] args = null;
            Properties mappingProps = new Properties();
            InputStream is = getClass().getClassLoader().getResourceAsStream(srcModel
                                                                             + "_mappings");
            if (is != null) {
                mappingProps.load(is);
            } else {
                System .out.println("WARNING: did not find any mappings, searched for: "
                                   + srcModel + "_mappings");
            }
            Model src = Model.getInstanceByName(srcModel);
            Model tgt = Model.getInstanceByName(tgtModel);

            //TODO: Fix this hard coded bodgieness ???
            if ("org.flymine.dataconversion.EnsemblDataTranslator".equals(translator)
                || "org.flymine.dataconversion.EnsemblHumanDataTranslator".equals(translator)) {

                Properties ensemblProps = new Properties();
                InputStream epis = getClass().getClassLoader().getResourceAsStream(
                        "ensembl_config.properties");

                ensemblProps.load(epis);

                if (organism == null) {
                    throw new BuildException("organism attribute not set");
                }
                types = new Class[] {
                    ItemReader.class,
                    Properties.class,
                    Model.class,
                    Model.class,
                    Properties.class,
                    String.class
                };

                args = new Object[] {reader, mappingProps, src, tgt, ensemblProps, organism};



            //TODO: Fix this hard coded bodgieness ???
            } else if ("org.flymine.dataconversion.ProteinStructureDataTranslator"
                       .equals(translator)) {
                if (dataLocation == null) {
                    throw new BuildException("dataLocation attribute not set");
                }
                types = new Class[]
                    {ItemReader.class, Properties.class, Model.class, Model.class, String.class};
                args = new Object[] {reader, mappingProps, src, tgt, dataLocation};
            } else if ("org.flymine.dataconversion.PsiDataTranslator"
                       .equals(translator)) {
//                 if (organisms == null) {
//                     throw new BuildException("organisms attribute not set");
//                 }
                types = new Class[]
                    {ItemReader.class, Properties.class, Model.class, Model.class};
                args = new Object[] {reader, mappingProps, src, tgt};
            } else {
                types = new Class[] {ItemReader.class, Properties.class, Model.class, Model.class};
                args = new Object[] {reader, mappingProps, src, tgt};
            }
            DataTranslator dt = (DataTranslator) cls.getConstructor(types).newInstance(args);
            ItemWriter writer = new ObjectStoreItemWriter(ObjectStoreWriterFactory
                                                          .getObjectStoreWriter(targetAlias));
            configureDynamicAttributes(dt);

            dt.translate(writer);
            writer.close();
        } catch (BuildException e) {
            throw e;
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }
}
