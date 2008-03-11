package org.intermine.bio.web.export;

/*
 * Copyright (C) 2002-2008 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.io.OutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.biojava.bio.Annotation;
import org.biojava.bio.seq.io.FastaFormat;
import org.biojava.bio.seq.io.SeqIOTools;
import org.biojava.bio.symbol.IllegalSymbolException;
import org.flymine.model.genomic.BioEntity;
import org.flymine.model.genomic.Gene;
import org.flymine.model.genomic.LocatedSequenceFeature;
import org.flymine.model.genomic.Protein;
import org.flymine.model.genomic.Sequence;
import org.flymine.model.genomic.Translation;
import org.intermine.bio.web.biojava.BioSequence;
import org.intermine.bio.web.biojava.BioSequenceFactory;
import org.intermine.metadata.ClassDescriptor;
import org.intermine.metadata.Model;
import org.intermine.model.InterMineObject;
import org.intermine.objectstore.ObjectStore;
import org.intermine.util.TypeUtil;
import org.intermine.web.logic.export.ExportException;
import org.intermine.web.logic.export.ExportHelper;
import org.intermine.web.logic.export.Exporter;
import org.intermine.web.logic.results.ResultElement;


/**
 * Export data in FASTA format. Select cell in each row
 * that can be exported as a sequence and fetch associated sequence.
 * @author Jakub Kulaviak
 **/
public class SequenceExporterLogic implements Exporter
{

    private ObjectStore os;
    private OutputStream out;
    private int featureIndex;

    /**
     * Constructor.
     * @param os object store used for fetching sequence for  exported object
     * @param outputStream output stream
     * @param featureIndex index of cell in row that contains object to be exported 
     */
    public SequenceExporterLogic(ObjectStore os, OutputStream outputStream,
            int featureIndex) {
        this.os = os;
        this.out = outputStream;
        this.featureIndex = featureIndex;
    }
    
    /**
     * {@inheritDoc}}
     */
    public int getWrittenResultsCount() {
        // TODO Auto-generated method stub
        return 0;
    }
    
    /**
     * {@inheritDoc}
     */
    public void export(List<List<ResultElement>> results) {
        // IDs of the features we have successfully output - used to avoid duplicates
        Set<Integer> exportedIDs = new HashSet<Integer>();
        
        try {
            for (int rowIndex = 0; rowIndex < results.size(); rowIndex++) {
                List<ResultElement> row = results.get(rowIndex);

                StringBuffer header = new StringBuffer();

                ResultElement resultElement = row.get(featureIndex);

                BioSequence bioSequence;
                Object object = os.getObjectById(resultElement.getId());
                if (!(object instanceof InterMineObject)) {
                    continue;
                }

                Integer objectId = ((InterMineObject) object).getId();
                if (exportedIDs.contains(objectId)) {
                    // exported already
                    continue;
                }

                if (object instanceof LocatedSequenceFeature) {
                    bioSequence = createLocatedSequenceFeature(header, object);
                } else if (object instanceof Protein) {
                    bioSequence = createProtein(header, object);
                } else if (object instanceof Translation) {
                    Model model = os.getModel();
                    bioSequence = createTranslation(header, object, model);
                } else {
                    // ignore other objects
                    continue;
                }

                if (bioSequence == null) {
                    // the object doesn't have a sequence
                    continue;
                }

                Annotation annotation = bioSequence.getAnnotation();
                String headerString = header.toString();

                if (row.size() > 1 && headerString.length() > 0) {
                    annotation.setProperty(FastaFormat.PROPERTY_DESCRIPTIONLINE, headerString);
                } else {
                    if (object instanceof BioEntity) {
                        annotation.setProperty(FastaFormat.PROPERTY_DESCRIPTIONLINE,
                                               ((BioEntity) object).getPrimaryIdentifier());
                    } else {
                        // last resort
                        annotation.setProperty(FastaFormat.PROPERTY_DESCRIPTIONLINE,
                                               "sequence_" + exportedIDs.size());
                    }
                }

                SeqIOTools.writeFasta(out, bioSequence);
                exportedIDs.add(objectId);
            }

            if (out != null) {
                out.close();
            }
        } catch (Exception e) {
              throw new ExportException("Export failed.", e);
        }
    }

    private BioSequence createTranslation(StringBuffer header, Object object,
            Model model) throws IllegalSymbolException {
        BioSequence bioSequence;
        ClassDescriptor cld = model.getClassDescriptorByName(model.getPackageName()
                                                             + "." + "Translation");
        if (cld.getReferenceDescriptorByName("sequence", true) != null) {
            Translation translation = (Translation) object;
            bioSequence = BioSequenceFactory.make(translation);
            header.append(translation.getPrimaryIdentifier());
            header.append(' ');
            if (translation.getName() == null) {
                header.append("[unknown_name]");
            } else {
                header.append(translation.getName());
            }
            if (translation.getGene() != null) {
                Gene gene = translation.getGene();
                String geneIdentifier = gene.getPrimaryIdentifier();
                if (geneIdentifier != null) {
                    header.append(' ');
                    header.append("gene:");
                    header.append(geneIdentifier);
                }
            }
        } else {
            bioSequence = null;
        }
        return bioSequence;
    }

    private BioSequence createProtein(StringBuffer header, Object object)
            throws IllegalSymbolException {
        BioSequence bioSequence;
        Protein protein = (Protein) object;
        bioSequence = BioSequenceFactory.make(protein);
        header.append(protein.getPrimaryIdentifier());
        header.append(' ');
        if (protein.getName() == null) {
            header.append("[unknown_name]");
        } else {
            header.append(protein.getName());
        }
        Iterator iter = protein.getGenes().iterator();
        while (iter.hasNext()) {
            Gene gene = (Gene) iter.next();
            String geneIdentifier = gene.getPrimaryIdentifier();
            if (geneIdentifier != null) {
                header.append(' ');
                header.append("gene:");
                header.append(geneIdentifier);
            }

        }
        return bioSequence;
    }

    private BioSequence createLocatedSequenceFeature(StringBuffer header,
            Object object) throws IllegalSymbolException {
        BioSequence bioSequence;
        LocatedSequenceFeature feature = (LocatedSequenceFeature) object;
        bioSequence = BioSequenceFactory.make(feature);
        if (feature.getPrimaryIdentifier() == null) {
            if (feature instanceof Gene) {
                header.append(((Gene) feature).getPrimaryIdentifier());
            } else {
                header.append("[unknown_identifier]");
            }
        } else {
            header.append(feature.getPrimaryIdentifier());
        }
        header.append(' ');
        if (feature.getName() == null) {
            header.append("[unknown_name]");
        } else {
            header.append(feature.getName());
        }
        if (feature.getChromosomeLocation() != null) {
            header.append(' ').append(feature.getChromosome().getPrimaryIdentifier());
            header.append(':').append(feature.getChromosomeLocation().getStart());
            header.append('-').append(feature.getChromosomeLocation().getEnd());
            header.append(' ').append(feature.getLength());
        }
        try {
            Gene gene = (Gene) TypeUtil.getFieldValue(feature, "gene");
            if (gene != null) {
                String geneIdentifier = gene.getPrimaryIdentifier();
                if (geneIdentifier != null) {
                    header.append(' ').append("gene:").append(geneIdentifier);
                }
            }
        } catch (IllegalAccessException e) {
            // ignore
        }
        return bioSequence;
    }

    /**
     * {@inheritDoc}
     */
    public boolean canExport(List<Class> clazzes) {
        return canExport2(clazzes);
    }

    /**
     * @param clazzes classes of result
     * @return true if this exporter can export result composed of specified classes 
     */
    public static boolean canExport2(List<Class> clazzes) {        
        return (
                ExportHelper.getFirstColumnForClass(clazzes, LocatedSequenceFeature.class) >= 0
                || ExportHelper.getFirstColumnForClass(clazzes, Protein.class) >= 0
                || ExportHelper.getFirstColumnForClass(clazzes, Translation.class) >= 0 
                || ExportHelper
                        .getFirstColumnForClass(clazzes, Sequence.class) >= 0);
    }
}
