package org.intermine.util;

/*
 * Copyright (C) 2002-2009 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.sf.cglib.proxy.*;

import org.intermine.model.FastPathObject;
import org.intermine.model.InterMineObject;

/**
 * Utilities to create DynamicBeans
 *
 * @author Andrew Varley
 */
public class DynamicUtil
{
    private static Map<Set<Class>, Class> classMap = new HashMap<Set<Class>, Class>();
    private static HashMap<Class, Set<Class>> decomposeMap = new HashMap<Class, Set<Class>>();
    private static Map<Class, String> friendlyNameMap = new HashMap<Class, String>();
    private static Map<Class, String> simpleNameMap = new HashMap<Class, String>();

    /**
     * Cannot construct
     */
    private DynamicUtil() {
    }

    /**
     * Create a DynamicBean from a Set of Class objects
     *
     * @param classes the classes and interfaces to extend/implement
     * @return the DynamicBean
     * @throws IllegalArgumentException if there is more than one Class, or if fields are not
     * compatible.
     */
    public static synchronized FastPathObject createObject(Set classes)
    throws IllegalArgumentException {
        Class requiredClass = (Class) classMap.get(classes);
        if (requiredClass != null) {
            return createObject(requiredClass);
        } else {
            Iterator<Class> classIter = classes.iterator();
            Class clazz = null;
            Set<Class> interfaces = new HashSet<Class>();
            while (classIter.hasNext()) {
                Class cls = classIter.next();
                if (cls.isInterface()) {
                    interfaces.add(cls);
                } else if ((clazz == null) || clazz.isAssignableFrom(cls)) {
                    clazz = cls;
                } else if (!cls.isAssignableFrom(clazz)) {
                    throw new IllegalArgumentException("Cannot create a class from multiple"
                            + " classes: " + classes);
                }
            }
            if (clazz != null) {
                interfaces.removeAll(Arrays.asList(clazz.getInterfaces()));
            }
            if (interfaces.isEmpty()) {
                if (clazz == null) {
                    throw new IllegalArgumentException("Cannot create an object without a class "
                                                       + "for: " + classes);
                } else {
                    try {
                        classMap.put(classes, clazz);
                        return (FastPathObject) clazz.newInstance();
                    } catch (InstantiationException e) {
                        IllegalArgumentException e2 = new IllegalArgumentException("Problem running"
                                + " constructor");
                        e2.initCause(e);
                        throw e2;
                    } catch (IllegalAccessException e) {
                        IllegalArgumentException e2 = new IllegalArgumentException("Problem running"
                                + " constructor");
                        e2.initCause(e);
                        throw e2;
                    }
                }
            }
            if ((clazz == null) && (interfaces.size() == 1)) {
                try {
                    Class retval = Class.forName(interfaces.iterator().next().getName() + "Shadow");
                    classMap.put(classes, retval);
                    return createObject(retval);
                } catch (ClassNotFoundException e) {
                    // No problem - falling back on dynamic
                }
            }
            FastPathObject retval = DynamicBean.create(clazz, interfaces.toArray(new Class[] {}));
            classMap.put(classes, retval.getClass());
            return retval;
        }
    }

    /**
     * Create a new object given a class.
     *
     * @param clazz the class of the object to instantiate
     * @return the object
     * @throws IllegalArgumentException if an error occurs
     */
    public static FastPathObject createObject(Class clazz) throws IllegalArgumentException {
        FastPathObject retval = null;
        try {
            retval = (FastPathObject) clazz.newInstance();
        } catch (Exception e) {
            IllegalArgumentException e2 = new IllegalArgumentException();
            e2.initCause(e);
            throw e2;
        }
        if (retval instanceof Factory) {
            ((Factory) retval).setCallback(0, new DynamicBean());
        }
        return retval;
    }

    /**
     * Return the Class for the array of Class objects. This method returns a Class which is
     * descriptive of the component classes, but not necessarily valid for instantiating.
     * This means that passing a single interface into this method will return the interface rather
     * than a class composed from it.
     *
     * @param classes the classes and interfaces to extend/implement
     * @return the Class
     * @throws IllegalArgumentException if there is more than one Class, or if the fields are not
     * compatible.
     */
    public static Class composeDescriptiveClass(Class... classes) throws IllegalArgumentException {
        if (classes.length == 1) {
            return classes[0];
        }
        return composeClass(new HashSet(Arrays.asList(classes)));
    }

    /**
     * Return the Class for the array of Class objects.
     *
     * @param classes the classes and interfaces to extend/implement
     * @return the Class
     * @throws IllegalArgumentException is there is more than one Class, or if the fields are not
     * compatible.
     */
    public static Class composeClass(Class... classes) throws IllegalArgumentException {
        return composeClass(new HashSet(Arrays.asList(classes)));
    }

    /**
     * Return the Class for a set of Class objects. NOTE: Creating an instance of this class is not
     * trivial: after calling Class.newInstance(), cast the Object to net.sf.cglib.proxy.Factory,
     * and call interceptor(new org.intermine.util.DynamicBean()) on it.
     *
     * @param classes the classes and interfaces to extend/implement
     * @return the Class
     * @throws IllegalArgumentException if there is more than one Class, or if the fields are not
     * compatible.
     */
    public static synchronized Class composeClass(Set<Class> classes)
        throws IllegalArgumentException {
        Class retval = classMap.get(classes);
        if (retval == null) {
            retval = createObject(classes).getClass();
        }
        return retval;
    }

    /**
     * Convert a set of interface names to a set of Class objects
     *
     * @param names the set of interface names
     * @return set of Class objects
     * @throws ClassNotFoundException if class cannot be found
     */
    protected static Set<Class> convertToClasses(Set<String> names) throws ClassNotFoundException {
        Set<Class> classes = new HashSet<Class>();
        Iterator<String> iter = names.iterator();
        while (iter.hasNext()) {
            classes.add(Class.forName(iter.next()));
        }

        return classes;
    }

    /**
     * Convert a dynamic Class into a Set of Class objects that comprise it.
     *
     * @param clazz the Class to decompose
     * @return a Set of Class objects
     */
    public static synchronized Set<Class> decomposeClass(Class clazz) {
        Set<Class> retval = decomposeMap.get(clazz);
        if (retval == null) {
            if (net.sf.cglib.proxy.Factory.class.isAssignableFrom(clazz)) {
                // Decompose
                retval = new TreeSet<Class>(new ClassNameComparator());
                retval.add(clazz.getSuperclass());
                Class interfs[] = clazz.getInterfaces();
                for (int i = 0; i < interfs.length; i++) {
                    Class inter = interfs[i];
                    if (net.sf.cglib.proxy.Factory.class != inter) {
                        boolean notIn = true;
                        Iterator<Class> inIter = retval.iterator();
                        while (inIter.hasNext() && notIn) {
                            Class in = inIter.next();
                            if (in.isAssignableFrom(inter)) {
                                // That means that the one already in the return value is more
                                // general than the one we are about to put in, so we can get rid
                                // of the one already in.
                                inIter.remove();
                            }
                            if (inter.isAssignableFrom(in)) {
                                // That means that the one already in the return value is more
                                // specific than the one we would have added, so don't bother.
                                notIn = false;
                            }
                        }
                        if (notIn) {
                            retval.add(inter);
                        }
                    }
                }
            } else if (org.intermine.model.ShadowClass.class.isAssignableFrom(clazz)) {
                try {
                    retval = Collections.singleton((Class) clazz.getField("shadowOf").get(null));
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException("ShadowClass " + clazz.getName() + " has no "
                            + "shadowOf method", e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(clazz.getName()
                            + ".shadowOf method is inaccessible", e);
                }
            } else {
                // Normal class - return it.
                retval = Collections.singleton(clazz);
            }
            decomposeMap.put(clazz, retval);
        }
        return retval;
    }

    /**
     * Create an outline business object from a class name and a list of interface names
     * @param className the class name
     * @param implementations a space separated list of interface names
     * @return the materialised business object
     * @throws ClassNotFoundException if className can't be found
     */
    public static FastPathObject instantiateObject(String className, String implementations)
        throws ClassNotFoundException {

        Set<String> classNames = new HashSet<String>();

        if (className != null && !"".equals(className) && !"".equals(className.trim())) {
            classNames.add(className.trim());
        }

        if (implementations != null) {
            classNames.addAll(StringUtil.tokenize(implementations));
        }

        if (classNames.size() == 0) {
            throw new RuntimeException("attempted to create an object without specifying any "
                                       + "classes or interfaces");
        }

        return createObject(convertToClasses(classNames));
    }

    /**
     * Creates a friendly name for a given class.
     *
     * @param clazz the class
     * @return a String describing the class, without package names
     */
    public static synchronized String getFriendlyName(Class clazz) {
        String retval = friendlyNameMap.get(clazz);
        if (retval == null) {
            retval = "";
            Iterator<Class> iter = decomposeClass(clazz).iterator();
            boolean needComma = false;
            while (iter.hasNext()) {
                Class constit = iter.next();
                retval += needComma ? "," : "";
                needComma = true;
                retval += constit.getName().substring(constit.getName().lastIndexOf('.') + 1);
            }
            friendlyNameMap.put(clazz, retval);
        }
        return retval;
    }

    /**
     * Creates a friendly description of an object - that is, the class and the ID (if it has one).
     *
     * @param o the object to be described
     * @return a String description
     */
    public static String getFriendlyDesc(Object o) {
        if (o instanceof InterMineObject) {
            return getFriendlyName(o.getClass()) + ":" + ((InterMineObject) o).getId();
        } else {
            return o.toString();
        }
    }

    /**
     * Returns the simple class name for the given class or throws an exception if
     * there are more than one
     * @param clazz the class
     * @return the simple class name
     */
    public static synchronized String getSimpleClassName(Class clazz) {
        String retval = simpleNameMap.get(clazz);
        if (retval == null) {
            Set<Class> decomposedClass = decomposeClass(clazz);
            if (decomposedClass.size() > 1) {
                throw new IllegalArgumentException("No simple name for class: "
                                                   + getFriendlyName(clazz));
            } else {
                retval = decomposedClass.iterator().next().getName();
                simpleNameMap.put(clazz, retval);
            }

        }
        return retval;
    }

    private static class ClassNameComparator implements Comparator<Class>
    {
        public int compare(Class a, Class b) {
            return a.getName().compareTo(b.getName());
        }
    }
}
