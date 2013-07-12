package org.intermine.bio.dataconversion;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashSet;

import junit.framework.TestCase;

/**
 * MgiIdentifiersResolverFactory Unit Tests
 *
 * @author Fengyuan Hu
 *
 */
public class MgiIdentifiersResolverFactoryTest extends TestCase {
    MgiIdentifiersResolverFactory factory;
    String mgiDataFile = "resources/mgi.data.sample";

    public MgiIdentifiersResolverFactoryTest() {
    }

    public MgiIdentifiersResolverFactoryTest(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        factory = new MgiIdentifiersResolverFactory();
        IdResolverFactory.resolver = null;
        factory.createIdResolver();

    }

    public void testCreateFromFile() throws Exception {
        File f = new File(mgiDataFile);
        if (!f.exists()) {
            fail("data file not found");
        }

        factory.createFromFile(f);
//        IdResolverFactory.resolver.writeToFile(new File("build/mgi"));
        assertTrue(IdResolverFactory.resolver.getTaxons().size() == 1);
        assertEquals(new LinkedHashSet<String>(Arrays.asList(new String[] {"10090"})), IdResolverFactory.resolver.getTaxons());
        assertEquals("MGI:1914088", IdResolverFactory.resolver.resolveId("10090", "0610009L18Rik").iterator().next());
        assertEquals("MGI:1916316", IdResolverFactory.resolver.resolveId("10090", "gene", "OTTMUSG00000003581").iterator().next());
    }
}
