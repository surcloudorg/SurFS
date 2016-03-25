package com.autumn.core.cookie;

import java.net.URL;
import java.net.URLConnection;

public class Client {

    private static final CookieParser defaultCookieParser = new RFC2965CookieParser();
    private final Object lock = new Object();
    private CookieParser currentCookieParser = defaultCookieParser;

    /**
     * Returns the built-in
     * <code>CookieParser</code> implementation. Current implementation conforms
     * to RFC-2965.
     *
     * @return the default
     * <code>CookieParser</code> implementation
     * @see RFC2965CookieParser
     */
    public static CookieParser getDefaultCookieParser() {
        return (defaultCookieParser);
    }

    /**
     * Resets the
     * <code>CookieParser</code> implementation to be used for this instance, to
     * the default (built-in) implementation.
     *
     * @see #setCookieParser(CookieParser)
     */
    public void resetToDefaultCookieParser() {
        synchronized (lock) {
            currentCookieParser = defaultCookieParser;
        }
    }

    /**
     * Sets the
     * <code>CookieParser</code> implementation to be used in this instance.
     *
     * @param cp	the CookieParser to be used
     */
    public void setCookieParser(CookieParser cp) {
        if (cp == null) {
            return;
        }
        synchronized (lock) {
            currentCookieParser = cp;
        }
    }

    /**
     * Gets the
     * <code>CookieParser</code> implementation being used in this instance.
     *
     * @return the CookieParser in use
     */
    public CookieParser getCookieParser() {
        return (currentCookieParser);
    }

    /**
     * Constructs an instance using the default CookieParser
     *
     * @see CookieParser
     * @see #getDefaultCookieParser()
     */
    public Client() {
    }

    /**
     * Processes cookie headers from the given URLConnection. This method
     * <em>must</em> be called <strong>after</strong> the URLConnection is
     * connected.
     *
     * @param urlConn	the URLConnection to be processed @returns the CookieJar
     * containing all the Cookies extracted
     * @throws MalformedCookieException	if there was some error during cookie
     * processing
     */
    public CookieJar getCookies(URLConnection urlConn) throws MalformedCookieException {
        return (getCookies(urlConn, urlConn.getURL()));
    }

    protected CookieJar getCookies(URLConnection urlConn, URL url) throws MalformedCookieException {
        if (urlConn == null) {
            return (getCookies((Header) null, url));
        }

        return (getCookies(HeaderUtils.extractHeaders(urlConn), url));
    }

    protected CookieJar getCookies(Header header, URL url) throws MalformedCookieException {
        return (currentCookieParser.parseCookies(header, url));
    }

    /**
     * Sets cookie headers on the given URLConnection, using Cookies in the
     * CookieJar. This method <em>must</em> be called <strong>before</strong>
     * the URLConnection is connected.
     *
     * @param urlConn	the URLConnection to be processed
     * @param cj	the CookieJar containing the Cookies to be set @returns the
     * CookieJar containing the Cookies that were actually set
     */
    public CookieJar setCookies(URLConnection urlConn, CookieJar cj) {
        if (urlConn == null || cj == null) {
            throw new IllegalArgumentException("Null URLConnection or CookieJar");
        }
        if (cj.isEmpty()) {
            return (cj);
        }
        CookieJar eligibleCookies = CookieUtils.getCookiesForURL(cj, currentCookieParser, urlConn.getURL(), true);
        Header h = currentCookieParser.getCookieHeaders(eligibleCookies);
        HeaderUtils.setHeaders(urlConn, h);
        return (eligibleCookies);
    }
}
