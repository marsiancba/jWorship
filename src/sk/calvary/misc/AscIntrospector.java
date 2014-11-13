package sk.calvary.misc;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Vector;

/**
 * This class is used to access properties of objects by property name (string)
 * and object reference. <BR>
 * Property can be: <BR>
 * setXXX() and getXXX function, where XXX is name of property <BR>
 * public field <BR>
 * property accessed through getProperty(name) and setProperty(name, value)
 * functions.
 */
public class AscIntrospector {
    /**
     * Properties constructor comment.
     */
    private AscIntrospector() {
        super();
    }

    public static Vector arrayToVector(Object a[]) {
        Vector v = new Vector();
        for (int i = 0; i < a.length; i++)
            v.addElement(a[i]);
        return v;
    }

    static String capitalize(String s) {
        if (s.length() == 0 || Character.isUpperCase(s.charAt(0)))
            return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    public static void copyProperties(String list, Object src, Object dest) {
        String l[] = listToArray(list);
        for (int i = 0; i < l.length; i++) {
            try {
                String pn = l[i];
                Object pv = getPropertyValue(src, pn);
                setPropertyValue(dest, pn, pv);
            } catch (Exception e) {
                throw new IllegalArgumentException("copyProperties failed");
            }
        }
    }

    public static boolean equalProperties(String list, Object o1, Object o2) {
        String l[] = listToArray(list);
        for (int i = 0; i < l.length; i++) {
            try {
                String pn = l[i];
                Object p1 = getPropertyValue(o1, pn);
                Object p2 = getPropertyValue(o2, pn);
                if (p1 == null) {
                    if (p2 != null)
                        return false;
                    continue;
                }
                if (p2 == null)
                    return false;
                if (!p1.equals(p2))
                    return false;
            } catch (Exception e) {
                throw new IllegalArgumentException("equalProperties failed");
            }
        }
        return true;
    }

    public static boolean equals(Object o1, Object o2) {
        if (o1 == o2)
            return true;
        if (o1 == null || o2 == null)
            return false;
        return o1.equals(o2);
    }

    public static Class getCommonClass(Object v[]) {
        Class c = null;
        for (int i = 0; i < v.length; i++) {
            Class d = v[i].getClass();
            if (c == null) {
                c = d;
            } else {
                if (c != d)
                    return null;
            }
        }
        return c;
    }

    public static Class getCommonClass(Vector v) {
        Class c = null;
        for (int i = 0; i < v.size(); i++) {
            Class d = v.elementAt(i).getClass();
            if (c == null) {
                c = d;
            } else {
                if (c != d)
                    return null;
            }
        }
        return c;
    }

    public static Class getPropertyType(Object o, String name)
            throws NoSuchFieldException {
        Class c = o.getClass();
        try {
            Method m = c.getMethod("get" + capitalize(name), new Class[0]);
            return m.getReturnType();
        } catch (NoSuchMethodException e) {
        }
        try {
            Field f = c.getField(name);
            return f.getType();
        } catch (NoSuchFieldException e) {
        }
        String mn = "set" + capitalize(name);
        Method ms[] = c.getMethods();
        for (int i = 0; i < ms.length; i++) {
            Method m = ms[i];
            if (!m.getName().equals(mn))
                continue;
            if (m.getParameterTypes().length != 1)
                continue;
            return m.getParameterTypes()[0];
        }
        try {
            Class ca[] = new Class[1];
            ca[0] = String.class;
            Method m = c.getMethod("getProperty", ca);
            Object oa[] = new Object[1];
            oa[0] = name;
            return m.invoke(o, oa).getClass();
        } catch (NoSuchMethodException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }
        throw new NoSuchFieldException(name);
    }

    public static Class getPropertyType(Class c, String name)
            throws NoSuchFieldException {
        try {
            Method m = c.getMethod("get" + capitalize(name), new Class[0]);
            return m.getReturnType();
        } catch (NoSuchMethodException e) {
        }
        try {
            Field f = c.getField(name);
            return f.getType();
        } catch (NoSuchFieldException e) {
        }
        String mn = "set" + capitalize(name);
        Method ms[] = c.getMethods();
        for (int i = 0; i < ms.length; i++) {
            Method m = ms[i];
            if (!m.getName().equals(mn))
                continue;
            if (m.getParameterTypes().length != 1)
                continue;
            return m.getParameterTypes()[0];
        }
        throw new NoSuchFieldException(name);
    }

    public static Object getPropertyValue(Object o, String name)
            throws NoSuchFieldException {
        Class c = o.getClass();
        try {
            Method m = c.getMethod("get" + capitalize(name), new Class[0]);
            return m.invoke(o, new Object[0]);
        } catch (NoSuchMethodException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }
        try {
            Method m = c.getMethod("is" + capitalize(name), new Class[0]);
            if (m.getReturnType() == Boolean.TYPE)
                return m.invoke(o, new Object[0]);
        } catch (NoSuchMethodException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }
        try {
            Field f = c.getField(name);
            return f.get(o);
        } catch (NoSuchFieldException e) {
        } catch (IllegalAccessException e) {
        }
        try {
            Class ca[] = new Class[1];
            ca[0] = String.class;
            Method m = c.getMethod("getProperty", ca);
            Object oa[] = new Object[1];
            oa[0] = name;
            return m.invoke(o, oa);
        } catch (NoSuchMethodException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }
        throw new NoSuchFieldException(name);
    }

    public static boolean listContains(String list, String item) {
        int i = 0;
        int il = item.length();
        int ll = list.length();
        while (true) {
            i = list.indexOf(item, i);
            if (i < 0)
                return false;
            if (i > 0 && list.charAt(i - 1) != ',') {
                i++;
                continue;
            }
            if (i + il < ll && list.charAt(i + il) != ',') {
                i++;
                continue;
            }
            return true;
        }
    }

    public static String[] listToArray(String list) {
        Vector v = new Vector();
        int i = 0;
        while (true) {
            int j = list.indexOf(",", i);
            if (j < 0) {
                v.addElement(list.substring(i));
                break;
            }
            v.addElement(list.substring(i, j));
            i = j + 1;
        }
        String a[] = new String[v.size()];
        v.copyInto(a);
        return a;
    }

    public static void setPropertyValue(Object o, String name, Object v)
            throws NoSuchFieldException {
        Class c = o.getClass();
        try {
            String mn = "set" + capitalize(name);
            Method ms[] = c.getMethods();
            for (int i = 0; i < ms.length; i++) {
                Method m = ms[i];
                if (!m.getName().equals(mn))
                    continue;
                if (m.getParameterTypes().length != 1)
                    continue;
                Object p[] = new Object[1];
                p[0] = v;
                m.invoke(o, p);
                return;
            }
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            e.getTargetException().printStackTrace();
        }
        try {
            Field f = c.getField(name);
            f.set(o, v);
            return;
        } catch (NoSuchFieldException e) {
        } catch (IllegalAccessException e) {
        }
        try {
            Class ca[] = new Class[2];
            ca[0] = String.class;
            ca[1] = Object.class;
            Method m = c.getMethod("setProperty", ca);
            Object oa[] = new Object[2];
            oa[0] = name;
            oa[1] = v;
            m.invoke(o, oa);
            return;
        } catch (IllegalAccessException e) {
        } catch (NoSuchMethodException e) {
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            e.getTargetException().printStackTrace();
        }
        throw new NoSuchFieldException(name);
    }

    public static Class type2class(Class c) {
        if (c == Integer.TYPE)
            return Integer.class;
        if (c == Long.TYPE)
            return Long.class;
        if (c == Byte.TYPE)
            return Byte.class;
        if (c == Character.TYPE)
            return Character.class;
        if (c == Float.TYPE)
            return Float.class;
        if (c == Double.TYPE)
            return Double.class;
        if (c == Boolean.TYPE)
            return Boolean.class;
        return c;
    }
}