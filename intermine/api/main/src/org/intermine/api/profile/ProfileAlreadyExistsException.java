package org.intermine.api.profile;

/*
 * Copyright (C) 2002-2009 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */
import java.util.Collection;

/**
 * Exception thrown when attempting to create a profile element with a name that already exists.
 * @author Richard Smith
 *
 */
public class ProfileAlreadyExistsException extends RuntimeException 
{
    private Collection<String> types;
   
    /**
     * Constructs an ProfileNameAlreadyExistsExceptionException with the specified detail message.
     *
     * @param msg the detail message
     */
    public ProfileAlreadyExistsException(String msg) {
        super(msg);
    }

    /**
     * Constructs an ProfileAlreadyExistsExceptionException with the specified nested throwable.
     *
     * @param t the nested throwable
     */
    public ProfileAlreadyExistsException(Throwable t) {
        super(t);
    }

    /**
     * Constructs an ProfileAlreadyExistsExceptionException with the specified detail message and nested
     * throwable.
     *
     * @param msg the detail message
     * @param t the nested throwable
     */
    public ProfileAlreadyExistsException(String msg, Throwable t) {
        super(msg, t);
    }
    
    /**
     * Set the bag types that were incompatible.
     * @param types the bag types
     */
    public void setTypes(Collection<String> types) {
        this.types = types;
    }
    
    /**
     * Get the bag types that were incompatible.
     * @return the incompatible types
     */
    public Collection<String> getTypes() {
        return this.types;
    }
}

