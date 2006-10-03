package org.intermine.dataloader;

/*
 * Copyright (C) 2002-2005 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.intermine.objectstore.ObjectStore;
import org.intermine.objectstore.ObjectStoreException;
import org.intermine.model.InterMineObject;
import org.intermine.util.DynamicUtil;

/**
 * Class providing EquivalentObjectFetcher functionality that makes use of hints to improve
 * performance.
 *
 * @author Matthew Wakeling
 */
public class HintingFetcher implements EquivalentObjectFetcher
{
    ObjectStore os;
    EquivalentObjectHints hints;
    EquivalentObjectFetcher fetcher;

    /**
     * Constructor
     *
     * @param os the ObjectStore containing the equivalent objects
     * @param fetcher another EquivalentObjectFetcher
     */
    public HintingFetcher(ObjectStore os, EquivalentObjectFetcher fetcher) {
        this.os = os;
        this.hints = new EquivalentObjectHints(os);
        this.fetcher = fetcher;
    }

    /**
     * @see EquivalentObjectFetcher#queryEquivalentObjects
     */
    public Set queryEquivalentObjects(InterMineObject obj, Source source)
        throws ObjectStoreException {
        if (hints.databaseEmpty()) {
            return Collections.EMPTY_SET;
        }
        Iterator classIter = DynamicUtil.decomposeClass(obj.getClass()).iterator();
        while (classIter.hasNext()) {
            Class clazz = (Class) classIter.next();
            if (os.getModel().hasClassDescriptor(clazz.getName())) {
                if (hints.classNotExists(clazz)) {
                    return Collections.EMPTY_SET;
                }
            }
        }
        return fetcher.queryEquivalentObjects(obj, source);
    }
}
