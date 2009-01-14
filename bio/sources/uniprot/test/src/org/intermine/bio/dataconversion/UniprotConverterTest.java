package org.intermine.bio.dataconversion;

/*
 * Copyright (C) 2002-2009 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import org.intermine.dataconversion.ItemsTestCase;
import org.intermine.dataconversion.MockItemWriter;
import org.intermine.metadata.Model;

public class UniprotConverterTest extends ItemsTestCase
{
    private UniprotConverter converter;
    private MockItemWriter itemWriter;

    public UniprotConverterTest(String arg) {
        super(arg);
    }

    public void setUp() throws Exception {
        itemWriter = new MockItemWriter(new HashMap());
        converter = new UniprotConverter(itemWriter, Model.getInstanceByName("genomic"));
        MockIdResolverFactory resolverFactory = new MockIdResolverFactory("Gene");
        resolverFactory.addResolverEntry("7227", "FBgn0000001", Collections.singleton("CG1111"));
        resolverFactory.addResolverEntry("7227", "FBgn0000002", Collections.singleton("CG2222"));
        converter.resolverFactory = resolverFactory;
        super.setUp();
    }

    public void testProcess() throws Exception {

        File datadir = new File ("./test/resources/datadir");
        converter.setCreateinterpro("true");
        converter.setUniprotOrganisms("7227");
        converter.process(datadir);
        converter.close();

        // uncomment to write out a new target items file
        //writeItemsFile(itemWriter.getItems(), "uniprot-tgt-items.xml");

        Set expected = readItemSet("UniprotConverterTest_tgt.xml");

        assertEquals(expected, itemWriter.getItems());
    }
}
