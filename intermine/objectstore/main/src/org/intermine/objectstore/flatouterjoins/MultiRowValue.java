package org.intermine.objectstore.flatouterjoins;

/*
 * Copyright (C) 2002-2008 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

/**
 * An object representing an entry in a MultiRow.
 *
 * @author Matthew Wakeling
 */
public abstract class MultiRowValue
{
    /**
     * Returns the value of the entry.
     *
     * @return an object
     */
    public abstract Object getValue();
}
