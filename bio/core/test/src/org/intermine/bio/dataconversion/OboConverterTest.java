package org.intermine.bio.dataconversion;

/*
 * Copyright (C) 2002-2007 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.util.Arrays;
import java.util.HashMap;

import org.intermine.bio.ontology.OboTerm;
import org.intermine.bio.ontology.OboTermSynonym;
import org.intermine.dataconversion.ItemsTestCase;
import org.intermine.dataconversion.MockItemWriter;
import org.intermine.metadata.Model;

public class OboConverterTest extends ItemsTestCase {
    String NAMESPACE = "http://www.flymine.org/model/genomic#";
    MockItemWriter itemWriter;
    Model model = Model.getInstanceByName("genomic");

    public OboConverterTest(String arg) {
        super(arg);
    }
    
    public void setUp() throws Exception {
        itemWriter = new MockItemWriter(new HashMap());
    }

    public void test1() throws Exception {
        OboConverter converter = new OboConverter(itemWriter, model, "", "SO", "http://www.flymine.org",
                                                  "OntologyTerm");
        OboTerm a = new OboTerm("SO:42", "parent");
        OboTerm b = new OboTerm("SO:43", "child");
        OboTerm c = new OboTerm("SO:44", "partof");
        c.addSynonym(new OboTermSynonym("syn2", "exact_synonym"));
        b.addSynonym(new OboTermSynonym("syn1", "narrow_synonym"));
        b.addSynonym(new OboTermSynonym("syn2", "exact_synonym"));
        a.addChild(b);
        a.addComponent(c);
        converter.process(Arrays.asList(new Object[] {a, b, c}));
        writeItemsFile(itemWriter.getItems(), "tmp");
        assertEquals(readItemSet("OboConverterTest.xml"), itemWriter.getItems());
    }

}
