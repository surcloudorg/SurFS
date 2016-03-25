package com.autumn.core.cookie;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/*
 * According to RFC2965, if Set-Cookie and set-cookie2 both describe the same
 * cookie, then sc2 should be used. This distinction has not been incorporated.
 *
 * ADD function to getcookies by version
 */
/**
 * Container for
 * <code>Cookie</code> objects. Each CookieJar is independent of any request.
 * This means that a single CookieJar can hold all the cookies for a number of
 * requests and servers.
 *
 * @author	Sonal Bansal
 */
@SuppressWarnings("unchecked")
public class CookieJar implements java.util.Collection, java.io.Serializable {

    private static final long serialVersionUID = 20110930140924L;
    private List theJar;
    private int iNumCookies;

    /**
     * Creates an empty CookieJar.
     */
    public CookieJar() {
        theJar = new ArrayList();
        iNumCookies = 0;
    }

    public void addCookieJar(CookieJar jar) {
        this.addAll(jar.theJar);
    }

    
    public String getCookieHead() {
        StringBuilder sbb = null;
        Iterator it = this.iterator();
        while (it.hasNext()) {
            Cookie c = (Cookie) it.next();
            if (sbb == null) {
                sbb = new StringBuilder();
            } else {
                sbb.append(";");
            }
            sbb.append(c.getName()).append("=").append(c.getValue());
        }
        if (sbb == null) {
            return null;
        } else {
            return sbb.toString();
        }
    }

    /**
     * Creates a CookieJar, and populates it with Cookies from input Collection.
     * All the objects in the input Collection NEED NOT be Cookie objects.
     *
     * @param c the input Collection
     */
    public CookieJar(Collection c) {
        theJar = new ArrayList();
        iNumCookies = 0;
        addAll(c);
    }

    protected CookieJar(int initialCapacity, int growthStep) {
        theJar = new ArrayList(initialCapacity);
        iNumCookies = 0;
    }

    @Override
    public boolean add(Object o) {
        if (o == null) {
            throw new IllegalArgumentException("Null cookie.");
        } else if (!(o instanceof Cookie)) {
            throw new ClassCastException("Not a Cookie.");
        }

        Cookie cookie;

        try {
            cookie = (Cookie) ((Cookie) o).clone();
        } catch (CloneNotSupportedException cnse) {
            throw new IllegalArgumentException("Could not add. Object does not support Cloning.");
        }

        if (!cookie.isValid()) {
            throw new IllegalArgumentException("Invalid cookie.");
        }

        int ind = getCookieIndex(cookie);

        if (ind == -1) {
            theJar.add(cookie);
            iNumCookies++;
        } else {
            theJar.set(ind, cookie);
        }

        return (true);
    }

    @Override
    public final boolean addAll(Collection c) {
        if (c == null) {
            throw new IllegalArgumentException("Null Collection");
        }

        if (!c.isEmpty()) {
            Iterator iter = c.iterator();
            while (iter.hasNext()) {
                try {
                    add(iter.next());
                } catch (Exception e) {
                }
            }
        } else {
            return (false);
        }

        return (true);
    }

    @Override
    public Iterator iterator() {
        return (theJar.iterator());
    }

    @Override
    public boolean contains(Object o) {
        if (o == null) {
            throw new IllegalArgumentException("Null cookie");
        } else if (!(o instanceof Cookie)) {
            throw new ClassCastException("Not a cookie");
        }

        Cookie c = (Cookie) o;

        if (!c.isValid()) {
            throw new IllegalArgumentException("Invalid cookie.");
        }

        return (theJar.contains(c));
    }

    @Override
    public boolean containsAll(Collection c) {
        if (c != null) {
            Iterator iter = c.iterator();
            while (iter.hasNext()) {
                if (!contains(iter.next())) {
                    return (false);
                }
            }
        } else {
            throw new IllegalArgumentException("Null collection");
        }

        return (true);
    }

    @Override
    public Object[] toArray() {
        return (theJar.toArray());
    }

    @Override
    public Object[] toArray(Object[] array) {
        if (array == null) {
            throw new IllegalArgumentException("Null array.");
        }

        Cookie[] cookieArray = new Cookie[array.length];

        try {
            for (int i = 0; i < array.length; i++) {
                cookieArray[i] = (Cookie) array[i];
            }
        } catch (ClassCastException cce) {
            throw new ArrayStoreException("ClassCastException occurred.");
        }

        return (theJar.toArray(cookieArray));
    }

    @Override
    public void clear() {
        theJar.clear();
        iNumCookies = 0;
    }

    @Override
    public boolean removeAll(Collection c) {
        if (c == null) {
            throw new IllegalArgumentException("Null collection");
        }

        if (!c.isEmpty()) {
            Iterator iter = c.iterator();
            while (iter.hasNext()) {
                remove(iter.next());
            }
        } else {
            return (false);
        }

        return (true);
    }

    @Override
    public boolean retainAll(Collection c) {
        if (c == null) {
            throw new IllegalArgumentException("Null collection");
        }

        if (!c.isEmpty()) {
            Iterator iter = c.iterator();
            Object o;

            while (iter.hasNext()) {
                o = iter.next();
                if (!contains(o)) {
                    remove(o);
                }
            }
        } else {
            return (false);
        }

        return (true);
    }

    @Override
    public boolean remove(Object o) {
        if (o == null) {
            throw new IllegalArgumentException("Null cookie.");
        } else if (!(o instanceof Cookie)) {
            throw new ClassCastException("Not a cookie.");
        }

        Cookie cookie = (Cookie) o;

        if (!cookie.isValid()) {
            throw new IllegalArgumentException("Invalid cookie.");
        }

        return (theJar.remove(cookie));
    }

    /**
     * Removes all cookies that match the given CookieMatcher.
     *
     * @param cm the CookieMatcher
     */
    public void removeCookies(CookieMatcher cm) {
        if (cm == null) {
            throw new IllegalArgumentException("Null CookieMatcher");
        }

        Cookie c;

        for (int i = 0; i < iNumCookies; i++) {
            c = (Cookie) theJar.get(i);
            if (cm.doMatch(c)) {
                theJar.remove(i);
                iNumCookies--;
            }
        }
    }

    protected int getCookieIndex(Cookie c) {
        int retVal = -1;

        for (int i = 0; i < iNumCookies; i++) {
            if (c.equals(theJar.get(i))) {
                retVal = i;
                break;
            }
        }

        return (retVal);
    }

    @Override
    public int size() {
        if (iNumCookies > Integer.MAX_VALUE) {
            return (Integer.MAX_VALUE);
        }

        return (iNumCookies);
    }

    @Override
    public boolean isEmpty() {
        return (iNumCookies == 0);
    }

    /**
     * Gets all Cookies that match the given CookieMatcher.
     *
     * @param cm the CookieMatcher
     * @return the CookieJar with matching cookies; always non-null
     */
    public CookieJar getCookies(CookieMatcher cm) {
        if (cm == null) {
            throw new IllegalArgumentException("Invalid CookieMatcher");
        }

        CookieJar cj = new CookieJar();
        Cookie c;

        for (int i = 0; i < iNumCookies; i++) {
            c = (Cookie) theJar.get(i);
            if (cm.doMatch(c)) {
                cj.add(c);
            }
        }

        return (cj);
    }

    /**
     * Gets all Cookies with the given name.
     *
     * @param cookieName the cookie name
     * @return the CookieJar with matching cookies; always non-null
     */
    public CookieJar getCookies(String cookieName) {
        if (Utils.isNullOrWhiteSpace(cookieName)) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        CookieJar cj = new CookieJar();
        Cookie c;

        for (int i = 0; i < iNumCookies; i++) {
            c = (Cookie) theJar.get(i);
            if (cookieName.equalsIgnoreCase(c.getName())) {
                cj.add(c);
            }
        }

        return (cj);
    }

    /**
     * Gets all Cookies having given version.
     *
     * @param ver the version
     * @return the CookieJar with Cookies; always non-null
     */
    public CookieJar getVersionCookies(String ver) {
        CookieJar cj = new CookieJar();
        Cookie c;

        for (int i = 0; i < iNumCookies; i++) {
            c = (Cookie) theJar.get(i);
            if (c.getVersion().equals(ver)) {
                cj.add(c);
            }
        }

        return (cj);
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return ("{}");
        }

        StringBuilder sb = new StringBuilder();

        sb.append("{");

        for (int i = 0; i < iNumCookies; i++) {
            sb.append(theJar.get(i).toString());
            sb.append(",");
        }

        sb.deleteCharAt(sb.length() - 1);
        sb.append("}");

        return (sb.toString());
    }
}
