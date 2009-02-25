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

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import net.sf.cglib.proxy.*;

import org.apache.log4j.Logger;

import org.intermine.model.FastPathObject;
import org.intermine.model.InterMineObject;
import org.intermine.objectstore.intermine.NotXmlRenderer;
import org.intermine.objectstore.proxy.ProxyReference;

/**
 * Class which represents a generic bean
 * @author Andrew Varley
 */
public class DynamicBean implements MethodInterceptor
{
    private static final Logger LOG = Logger.getLogger(DynamicBean.class);
    private Map map = new HashMap();

    /**
     * Construct the interceptor
     */
    public DynamicBean() {
        // empty
    }

    /**
     * Create a DynamicBean
     *
     * @param clazz the class to extend
     * @param inter the interfaces to implement
     * @return the DynamicBean
     */
    public static FastPathObject create(Class clazz, Class [] inter) {
        if ((clazz != null) && clazz.isInterface()) {
            throw new IllegalArgumentException("clazz must not be an interface");
        }
        // If Enhancer.create() called with a null class it will alter java.lang.Object
        // this causes a security exception if run with Kaffe JRE
        //if ( clazz == null) {
        //    clazz = DynamicBean.class;
        //}
        return (FastPathObject) Enhancer.create(clazz, inter, new DynamicBean());
    }

    /**
     * Intercept all method calls, and operate on Map.
     * Note that final methods (eg. getClass) cannot be intercepted
     *
     * @param obj the proxy
     * @param method the method called
     * @param args the parameters
     * @param proxy the method proxy
     * @return the return value of the real method call
     * @throws Throwable if an error occurs in executing the real method
     */
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy)
        throws Throwable {
        // java.lang.Object methods
        if (method.getName().equals("equals")) {
            if (args[0] instanceof InterMineObject) {
                Integer otherId = ((InterMineObject) args[0]).getId();
                Integer thisId = (Integer) map.get("id");
                return Boolean.valueOf(thisId != null ? thisId.equals(otherId) : obj == args[0]);
            }
            return Boolean.FALSE;
        }
        if (method.getName().equals("hashCode")) {
            return map.get("id");
        }
        if (method.getName().equals("finalize")) {
            return null;
        }
        if (method.getName().equals("toString")) {
            StringBuffer className = new StringBuffer();
            boolean needComma = false;
            Set classes = DynamicUtil.decomposeClass(obj.getClass());
            Iterator classIter = classes.iterator();
            while (classIter.hasNext()) {
                if (needComma) {
                    className.append(",");
                }
                Class clazz = (Class) classIter.next();
                className.append(TypeUtil.unqualifiedName(clazz.getName()));
            }
            StringBuffer retval = new StringBuffer(className.toString() + " [");
            Map sortedMap = new TreeMap(map);
            Iterator mapIter = sortedMap.entrySet().iterator();
            needComma = false;
            while (mapIter.hasNext()) {
                Map.Entry mapEntry = (Map.Entry) mapIter.next();
                String fieldName = (String) mapEntry.getKey();
                Object fieldValue = mapEntry.getValue();
                if (!(fieldValue instanceof Collection)) {
                    if (needComma) {
                        retval.append(", ");
                    }
                    needComma = true;
                    if (fieldValue instanceof ProxyReference) {
                        retval.append(fieldName + "=" + ((ProxyReference) fieldValue).getId());
                    } else if (fieldValue instanceof InterMineObject) {
                        retval.append(fieldName + "=" + ((InterMineObject) fieldValue).getId());
                    } else {
                        retval.append(fieldName + "=\"" + fieldValue + "\"");
                    }
                }
            }
            return retval.toString() + "]";
        }
        if ("getoBJECT".equals(method.getName()) && (args.length == 0)) {
            return NotXmlRenderer.render(obj);
        }
        if ("getFieldValue".equals(method.getName()) && (args.length == 1)) {
            String fieldName = (String) args[0];
            Object retval = map.get(fieldName);
            if (retval instanceof ProxyReference) {
                try {
                    retval = ((ProxyReference) retval).getObject();
                } catch (NullPointerException e) {
                    NullPointerException e2 = new NullPointerException("Exception while calling "
                            + method.getName() + "(\"" + args[0] + "\") on object with ID "
                            + map.get("id"));
                    e2.initCause(e);
                    throw e2;
                } catch (Exception e) {
                    RuntimeException e2 = new RuntimeException("Exception while calling "
                            + method.getName() + "(\"" + args[0] + "\") on object with ID "
                            + map.get("id"));
                    e2.initCause(e);
                    throw e2;
                }
            }
            if (retval == null) {
                Class fieldType = null;
                try {
                    String methodName = "get" + StringUtil.reverseCapitalisation((String) args[0]);
                    Method getMethod = obj.getClass().getMethod(methodName);
                    fieldType = getMethod.getReturnType();
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException("No such field " + args[0], e);
                }

                if (Collection.class.isAssignableFrom(fieldType)) {
                    retval = new HashSet();
                    map.put(fieldName, retval);
                }
                if (fieldType.isPrimitive()) {
                    if (Boolean.TYPE.equals(fieldType)) {
                        retval = Boolean.FALSE;
                    } else if (Short.TYPE.equals(fieldType)) {
                        retval = new Short((short) 0);
                    } else if (Integer.TYPE.equals(fieldType)) {
                        retval = new Integer(0);
                    } else if (Long.TYPE.equals(fieldType)) {
                        retval = new Long(0);
                    } else if (Float.TYPE.equals(fieldType)) {
                        retval = new Float(0.0);
                    } else if (Double.TYPE.equals(fieldType)) {
                        retval = new Double(0.0);
                    }
                    map.put(fieldName, retval);
                }
            }
            return retval;
        }
        if ("getFieldProxy".equals(method.getName()) && (args.length == 1)) {
            String fieldName = (String) args[0];
            Object retval = map.get(fieldName);
            if (retval == null) {
                Class fieldType = null;
                try {
                    String methodName = "get" + StringUtil.reverseCapitalisation((String) args[0]);
                    Method getMethod = obj.getClass().getMethod(methodName);
                    fieldType = getMethod.getReturnType();
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException("No such field " + args[0], e);
                }

                if (Collection.class.isAssignableFrom(fieldType)) {
                    retval = new HashSet();
                    map.put(fieldName, retval);
                }
                if (fieldType.isPrimitive()) {
                    if (Boolean.TYPE.equals(fieldType)) {
                        retval = Boolean.FALSE;
                    } else if (Short.TYPE.equals(fieldType)) {
                        retval = new Short((short) 0);
                    } else if (Integer.TYPE.equals(fieldType)) {
                        retval = new Integer(0);
                    } else if (Long.TYPE.equals(fieldType)) {
                        retval = new Long(0);
                    } else if (Float.TYPE.equals(fieldType)) {
                        retval = new Float(0.0);
                    } else if (Double.TYPE.equals(fieldType)) {
                        retval = new Double(0.0);
                    }
                    map.put(fieldName, retval);
                }
            }
            return retval;
        }
        if ("setFieldValue".equals(method.getName()) && (args.length == 2)
                && (method.getReturnType() == Void.TYPE)) {
            String fieldName = (String) args[0];
            map.put(fieldName, args[1]);
            return null;
        }
        if ("addCollectionElement".equals(method.getName()) && (args.length == 2)
                && (method.getReturnType() == Void.TYPE)) {
            String fieldName = (String) args[0];
            Collection col = (Collection) map.get(fieldName);
            if (col == null) {
                col = new HashSet();
                map.put(fieldName, col);
            }
            col.add(args[1]);
            return null;
        }
        if ("getFieldType".equals(method.getName()) && (args.length == 1)) {
            try {
                String methodName = "get" + StringUtil.reverseCapitalisation((String) args[0]);
                Method getMethod = obj.getClass().getMethod(methodName);
                return getMethod.getReturnType();
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("No such field " + args[0], e);
            }
        }
        if ("getElementType".equals(method.getName()) && (args.length == 1)) {
            String methodName = "add" + StringUtil.reverseCapitalisation((String) args[0]);
            Method methods[] = obj.getClass().getMethods();
            for (Method addMethod : methods) {
                if (addMethod.getName().equals(methodName)) {
                    return addMethod.getParameterTypes()[0];
                }
            }
            throw new RuntimeException("No such collection " + args[0]);
        }
        // Bean methods
        if (method.getName().startsWith("get") && (args.length == 0)) {
            Object retval = map.get(StringUtil.reverseCapitalisation(method.getName()
                        .substring(3)));
            if (retval instanceof ProxyReference) {
                try {
                    retval = ((ProxyReference) retval).getObject();
                } catch (NullPointerException e) {
                    NullPointerException e2 = new NullPointerException("Exception while calling "
                            + method.getName() + " on object with ID " + map.get("id"));
                    e2.initCause(e);
                    throw e2;
                } catch (Exception e) {
                    RuntimeException e2 = new RuntimeException("Exception while calling "
                            + method.getName() + " on object with ID " + map.get("id"));
                    e2.initCause(e);
                    throw e2;
                }
            }
            if ((retval == null) && Collection.class.isAssignableFrom(method.getReturnType())) {
                retval = new HashSet();
                map.put(StringUtil.reverseCapitalisation(method.getName().substring(3)), retval);
            }
            return retval;
        }
        if (method.getName().startsWith("is")
            && (args.length == 0)) {
            return map.get(StringUtil.reverseCapitalisation(method.getName().substring(2)));
        }
        if (method.getName().startsWith("set") && (args.length == 1)
                && (method.getReturnType() == Void.TYPE)) {
            map.put(StringUtil.reverseCapitalisation(method.getName().substring(3)), args[0]);
            return null;
        }
        if (method.getName().startsWith("proxy") && (args.length == 1)
                && (method.getReturnType() == Void.TYPE)) {
            map.put(StringUtil.reverseCapitalisation(method.getName().substring(5)), args[0]);
            return null;
        }
        if (method.getName().startsWith("proxGet") && (args.length == 0)) {
            return map.get(StringUtil.reverseCapitalisation(method.getName().substring(7)));
        }
        if (method.getName().startsWith("add") && (args.length == 1)
                && (method.getReturnType() == Void.TYPE)) {
            Collection col = (Collection) map.get(StringUtil.reverseCapitalisation(
                        method.getName().substring(3)));
            if (col == null) {
                col = new HashSet();
                map.put(StringUtil.reverseCapitalisation(method.getName().substring(3)), col);
            }
            col.add(args[0]);
            return null;
        }
        throw new IllegalArgumentException("No definition for method " + method);
    }

    /**
     * Getter for the map, for testing purposes
     *
     * @return a map of data for this object
     */
    public Map getMap() {
        return map;
    }
}
