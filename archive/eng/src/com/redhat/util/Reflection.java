/*
 * Copyright (C) 2004 Red Hat, Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package com.redhat.util;

import java.lang.reflect.*;
import java.util.*;

public class Reflection {
    /**
     * Returns the public method with the specified name, for the specified
     * class, best matching the specified argument array. Null is returned if
     * no method matches.
     */
    public static Method dispatch(Class cl, String methodName, Object[] args) {
        Method best = null;
        int bestScore = 0;
        Method[] options = cl.getMethods();

        for (int i = 0; i < options.length; i++) {
            Method method = options[i];
            if (method.getName().equals(methodName)) {
                Class[] declared = method.getParameterTypes();
                if (compatible(declared, args)) {
                    int score = score(declared, args);
                    if (best == null || score < bestScore) {
                        best = method;
                        bestScore = score;
                    }
                }
            }
        }

        return best;
    }

    /**
     * Returns the public constructor for the specified class matching the
     * specified argument array. Null is returned if no constructor matches.
     */
    public static Constructor dispatchConstructor(Class cl, Object[] args) {
        Constructor best = null;
        int bestScore = 0;
        Constructor[] options = cl.getConstructors();

        for (int i = 0; i < options.length; i++) {
            Constructor cons = options[i];
            Class[] declared = cons.getParameterTypes();
            if (compatible(declared, args)) {
                int score = score(declared, args);
                if (best == null || score < bestScore) {
                    best = cons;
                    bestScore = score;
                }
            }
        }

        return best;
    }

    private static final Map s_primitives;

    /*
     * 5.1.2 of java language spec specified the following conversions
     *   byte to short, int, long, float, or double
     *   short to int, long, float, or double
     *   char to int, long, float, or double
     *   int to long, float, or double
     *   long to float or double
     *   float to double
     */
    static {
        s_primitives = new HashMap();
        Set booleanCompat = new HashSet();
        s_primitives.put(Boolean.TYPE, booleanCompat);
        booleanCompat.add(Boolean.class);

        Set characterCompat = new HashSet();
        characterCompat.add(Character.class);
        s_primitives.put(Character.TYPE, characterCompat);

        Set byteCompat = new HashSet();
        s_primitives.put(Byte.TYPE, byteCompat);
        byteCompat.add(Byte.class);

        Set shortCompat = new HashSet();
        s_primitives.put(Short.TYPE, shortCompat);
        shortCompat.add(Short.class);
        shortCompat.add(Byte.class);

        Set integerCompat = new HashSet();
        s_primitives.put(Integer.TYPE, integerCompat);
        integerCompat.add(Integer.class);
        integerCompat.addAll(shortCompat);
        integerCompat.add(Character.class);

        Set longCompat = new HashSet();
        s_primitives.put(Long.TYPE, longCompat);
        longCompat.add(Long.class);
        longCompat.addAll(integerCompat);

        Set floatCompat = new HashSet();
        s_primitives.put(Float.TYPE, floatCompat);
        floatCompat.add(Float.class);
        floatCompat.addAll(longCompat);

        Set doubleCompat = new HashSet();
        s_primitives.put(Double.TYPE, doubleCompat);
        doubleCompat.add(Double.class);
        doubleCompat.addAll(floatCompat);
    }


    static boolean compatible(Class[] formal, Object[] args) {
        if (formal.length != args.length) {
            return false;
        }

        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
                continue;
            }

            Class cl = args[i].getClass();

            if (formal[i].isPrimitive()) {
                Set compats = (Set) s_primitives.get(formal[i]);
                if (compats.contains(cl)) {
                    continue;
                }
            } else if (formal[i].isAssignableFrom(cl)) {
                continue;
            }

            return false;
        }

        return true;
    }

    static int score(Class[] formal, Object[] args) {
        int score = 0;
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
                continue;
            }

            Class cl = args[i].getClass();

            if (formal[i].isPrimitive()) {
                // assume the primitive boxes are final and get score 0
                continue;
            }

            boolean isInterface = formal[i].isInterface();
            for (;;) {
                if (isInterface) {
                    int ifaceScore = scoreInterfaces(formal[i], cl);
                    if (ifaceScore >= 0) {
                        score += ifaceScore;
                        break;
                    }
                } else if (cl.equals(formal[i])) {
                    break;
                }

                score++;
                cl = cl.getSuperclass();
                if (cl == null) {
                    throw new IllegalStateException
                        ("could not score " + args[i].getClass()
                         + " relative to " + formal[i]);
                }
            }
        }

        return score;
    }

    private static int scoreInterfaces(Class declaredIface, Class cl) {
        final List queue = new ArrayList();
        queue.addAll(Arrays.asList(cl.getInterfaces()));

        for (int i = 0, scoreBoundary = 0, score = -1; i < queue.size(); i++) {
            if (i == scoreBoundary) {
                scoreBoundary = queue.size();
                score++;
            }

            Class iface = (Class) queue.get(i);
            if (iface.equals(declaredIface)) {
                return score;
            }

            Class[] superifaces = iface.getInterfaces();
            for (int j = 0; j < superifaces.length; j++) {
                queue.add(superifaces[j]);
            }
        }

        return -1;
    }

    /**
     * Instantiates object of specified class using appropriate constructor
     * for specified args. The specified argument array can not be null. Boxed
     * primitive instances in the argument array will be unboxed when
     * necessary.
     *
     * @return null if no constructor is found, new object of specified class
     * otherwise
     */
    public static Object construct(Class cl, Object[] args)
        throws InstantiationException, IllegalAccessException,
        InvocationTargetException {
        Constructor cons = dispatchConstructor(cl, args);
        if (cons == null) {
            return null;
        }
        return cons.newInstance(args);
    }
}
