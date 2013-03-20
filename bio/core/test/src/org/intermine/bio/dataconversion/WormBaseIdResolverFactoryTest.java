package org.intermine.bio.dataconversion;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashSet;

import junit.framework.TestCase;

/**
 * WormBaseIdResolverFactory Unit Tests
 *
 * @author Fengyuan Hu
 *
 */
public class WormBaseIdResolverFactoryTest extends TestCase {
    WormBaseIdResolverFactory factory;
    String wormDataFile = "resources/worm.data.sample";

    public WormBaseIdResolverFactoryTest() {
    }

    public WormBaseIdResolverFactoryTest(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        factory = new WormBaseIdResolverFactory();
        IdResolverFactory.resolver = null;
        factory.createIdResolver();
    }

    public void testCreateFromFile() throws Exception {
        File f = new File(wormDataFile);
        if (!f.exists()) {
            fail("data file not found");
        }

        factory.createFromWormIdFile(f);
        // IdResolverFactory.resolver.writeToFile(new File("build/worm"));
        assertEquals(new LinkedHashSet<String>(Arrays.asList(new String[] {"6239"})), IdResolverFactory.resolver.getTaxons());
        assertTrue(IdResolverFactory.resolver.isPrimaryIdentifier("6239", "WBGene00000006"));
        assertEquals("WBGene00000011", IdResolverFactory.resolver.resolveId("6239", "abc-1").iterator().next());
        assertEquals("WBGene00000008", IdResolverFactory.resolver.resolveId("6239", "gene", "F54D12.3").iterator().next());
    }
}
