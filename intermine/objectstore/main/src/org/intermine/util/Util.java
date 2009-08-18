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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;


/**
 * Generic utility functions.
 *
 * @author Matthew Wakeling
 */
public class Util
{
    /**
     * Compare two objects, using their .equals method, but comparing null to null as equal.
     *
     * @param a one Object
     * @param b another Object
     * @return true if they are equal or both null
     */
    public static boolean equals(Object a, Object b) {
        if (a == null) {
            return b == null;
        }
        return a.equals(b);
    }

    /**
     * Return a zero hashCode if the object is null, otherwise return the real hashCode
     *
     * @param obj an object
     * @return the hashCode, or zero if the object is null
     */
    public static int hashCode(Object obj) {
        if (obj == null) {
            return 0;
        }
        return obj.hashCode();
    }

    /**
     * Takes an Exception, and returns an Exception of similar type with all available information
     * in the message.
     *
     * @param e an Exception
     * @return a String
     */
    public static Exception verboseException(Exception e) {
        boolean needComma = false;
        StringWriter message = new StringWriter();
        PrintWriter pMessage = new PrintWriter(message);
        Class c = e.getClass();
        while (e != null) {
            if (needComma) {
                pMessage.println("\n---------------NEXT EXCEPTION");
            }
            needComma = true;
            e.printStackTrace(pMessage);
            if (e instanceof SQLException) {
                e = ((SQLException) e).getNextException();
            } else {
                e = null;
            }
        }
        try {
            Constructor cons = c.getConstructor(new Class[] {String.class});
            Exception toThrow = (Exception) cons.newInstance(new Object[] {message.toString()});
            return toThrow;
        } catch (NoSuchMethodException e2) {
            throw new RuntimeException("NoSuchMethodException thrown while handling " + c.getName()
                    + ": " + message.toString());
        } catch (InstantiationException e2) {
            throw new RuntimeException("InstantiationException thrown while handling "
                    + c.getName() + ": " + message.toString());
        } catch (IllegalAccessException e2) {
            throw new RuntimeException("IllegalAccessException thrown while handling "
                    + c.getName() + ": " + message.toString());
        } catch (InvocationTargetException e2) {
            throw new RuntimeException("InvocationTargetException thrown while handling "
                    + c.getName() + ": " + message.toString());
        }
    }

    /**
     * Takes two integers, and returns the greatest common divisor, using euclid's algorithm.
     *
     * @param a an integer
     * @param b an integer
     * @return the gcd of a and b
     */
    public static int gcd(int a, int b) {
        while (b != 0) {
            int t = b;
            b = a % b;
            a = t;
        }
        return a;
    }

    /**
     * Takes two integers, and returns the lowest common multiple.
     *
     * @param a an integer
     * @param b an integer
     * @return the lcm of a and b
     */
    public static int lcm(int a, int b) {
        return (a / gcd(a, b)) * b;
    }

    /**
     * Convert an SQL LIKE/NOT LIKE expression to a * wildcard expression. See
     * wildcardUserToSql method for more information.
     * @param exp  the wildcard expression
     * @return     the SQL LIKE parameter
     */
    public static String wildcardSqlToUser(String exp) {
        StringBuffer sb = new StringBuffer();

        // Java needs backslashes to be backslashed in strings.
        for (int i = 0; i < exp.length(); i++) {
            String substring = exp.substring(i);
            if (substring.startsWith("%")) {
                sb.append("*");
            } else {
                if (substring.startsWith("_")) {
                    sb.append("?");
                } else {
                    if (substring.startsWith("\\%")) {
                        sb.append("%");
                        i++;
                    } else {
                        if (substring.startsWith("\\_")) {
                            sb.append("_");
                            i++;
                        } else {
                            if (substring.startsWith("*")) {
                                sb.append("\\*");
                            } else {
                                if (substring.startsWith("?")) {
                                    sb.append("\\?");
                                } else {
                                    // a single '\' as in Dpse\GA10108
                                    if (substring.startsWith("\\\\")) {
                                        i++;
                                        sb.append("\\");
                                    } else {
                                        sb.append(substring.charAt(0));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return sb.toString();
    }

    /**
     * Turn a user supplied wildcard expression with * into an SQL LIKE/NOT LIKE
     * expression with %'s and other special characters. Please note that constraint
     * value is saved in created java object (constraint) in form with '%' and in
     * this form is saved in xml as well.
     *
     * @param exp  the SQL LIKE parameter
     * @return     the equivalent wildcard expression
     */
    public static String wildcardUserToSql(String exp) {
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < exp.length(); i++) {
            String substring = exp.substring(i);
            if (substring.startsWith("*")) {
                sb.append("%");
            } else if (substring.startsWith("?")) {
                sb.append("_");
            } else if (substring.startsWith("\\*")) {
                sb.append("*");
                i++;
            } else if (substring.startsWith("\\?")) {
                sb.append("?");
                i++;
            } else if (substring.startsWith("%")) {
                sb.append("\\%");
            } else if (substring.startsWith("_")) {
                sb.append("\\_");
            } else if (substring.startsWith("\\")) {
                sb.append("\\\\");
            } else {
                sb.append(substring.charAt(0));
            }
        }

        return sb.toString();
    }

    /**
     * @param sequence sequence to be encoded
     * @return encoded sequence, set to lowercase
     */
    public static String getMd5checksum(String sequence) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        byte[] buffer = sequence.getBytes();
        md5.update(buffer);
        byte[] array = md5.digest();
        String checksum = HexBin.encode(array);
        // perl checksum returns lowercase, uniprot has to match
        return checksum.toLowerCase();
    }

    /**
     * Returns the class (not primitive) associated with the given String type.
     *
     * @param type the String type name
     * @return a Class
     * @throws IllegalArgumentException if the String is an invalid name
     */
    public static Class getClassFromString(String type) throws IllegalArgumentException {
        if (type.equals("short") || type.equals("java.lang.Short")) {
            return Short.class;
        } else if (type.equals("int") || type.equals("java.lang.Integer")) {
            return Integer.class;
        } else if (type.equals("long") || type.equals("java.lang.Long")) {
            return Long.class;
        } else if (type.equals("java.lang.String")) {
            return String.class;
        } else if (type.equals("boolean") || type.equals("java.lang.Boolean")) {
            return Boolean.class;
        } else if (type.equals("float") || type.equals("java.lang.Float")) {
            return Float.class;
        } else if (type.equals("double") || type.equals("java.lang.Double")) {
            return Double.class;
        } else if (type.equals("java.util.Date")) {
            return java.util.Date.class;
        } else if (type.equals("java.math.BigDecimal")) {
            return java.math.BigDecimal.class;
        } else {
            throw new IllegalArgumentException("Unknown type \"" + type + "\"");
        }
    }
}
