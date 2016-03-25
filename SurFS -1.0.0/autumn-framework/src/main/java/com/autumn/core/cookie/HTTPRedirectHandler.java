package com.autumn.core.cookie;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Convenience class that combines cookie-handling and redirect-handling logic. When the connect()
 * method is invoked, the handler cyclically processes HTTP Redirects (if any), and also takes care of
 * cookie-handling while doing so. The maximum number of redirects defaults to 10. The handler determines
 * a successful run, when the HTTP response code is equal to a specified success code. This code defaults
 * to 200 (OK).
 * @author Sonal Bansal
 */
public class HTTPRedirectHandler {

    private HttpURLConnection huc;
    private CookieJar cj = new CookieJar();
    private Client client = new Client();
    private int successCode = 200;
    private int maxRedirects = 10;
    private boolean bConnected = false;
    private boolean bConnectMethodAlreadyCalled = false;
    private InputStream is;

    /**
     * Creates a handler for the input HttpURLConnection. The HttpURLConnection must NOT be
     * connected yet.
     */
    public HTTPRedirectHandler(HttpURLConnection huc) {
        if (huc == null) {
            throw new NullPointerException();
        }

        this.huc = huc;
    }

    /**
     * Sets the Client to be used for this handler.
     */
    public void setClient(Client cl) {
        if (cl == null) {
            throw new IllegalArgumentException("Null argument.");
        }
        synchronized (client) {
            client = cl;
        }
    }


    /**
     * Sets the HTTP response code designating a successful run.
     * @param i the code; non-positive values ignored
     */
    public void setSuccessCode(int i) {
        if (i > 0) {
            successCode = i;
        }
    }

    /**
     * Sets the CookieJar containing Cookies to be used during cookie-handling.
     */
    public void setCookieJar(CookieJar cj) {
        if (cj == null || cj.isEmpty()) {
            return;
        }

        this.cj = cj;
    }

    /**
     * Adds some Cookies to the existing Cookies in an HTTPRedirectHandler.
     * @param cj the Cookies to be added
     */
    public void addCookies(CookieJar cj) {
        if (cj == null || cj.isEmpty()) {
            return;
        }

        this.cj.addAll(cj);
    }

    /**
     * Gets the CookieJar containing any pre-existing Cookies, as well as new ones extracted
     * during processing.
     * @return the CookieJar; always non-null
     */
    public CookieJar getCookieJar() {
        return (cj);
    }

    /**
     * Gets the InputStream for the final successful response.
     * @throws IllegalStateException when called before successful connection.
     */
    public InputStream getInputStream() {
        if (!bConnected) {
            throw new IllegalStateException("Not Connected");
        }

        return (is);
    }

    /**
     * Gets the HttpURLConnection for the final successful response.
     */
    public HttpURLConnection getConnection() {
        if (!bConnected) {
            throw new IllegalStateException("Not Connected");
        }

        return (huc);
    }

    /**
     * Sets the maximum number of redirects that will be followed.
     * @param i the max number; non-positive values are ignored
     */
    public void setMaxRedirects(int i) {
        if (i > 0) {
            maxRedirects = i;
        }
    }

    /**
     * Connects to initial HttpURLConnection (specified during construction), and initiates
     * cookie-handling and redirect-handling. It can only be called once per instance.
     * @throws IOException if there is an I/O problem
     * @throws MalformedCookieException if there was a problem with cookie-handling
     * @throws IllegalStateException if this method has already been called
     */
    public void connect() throws IOException, MalformedCookieException {
        if (bConnectMethodAlreadyCalled) {
            throw new IllegalStateException("No can do.");
        }

        bConnectMethodAlreadyCalled = true;

        int code;
        URL url;

        HttpURLConnection.setFollowRedirects(false);
        if (!cj.isEmpty()) {
            client.setCookies(huc, cj);
        }
        is = huc.getInputStream();
        cj.addAll(client.getCookies(huc));

        while ((code = huc.getResponseCode()) != successCode
                && maxRedirects > 0) {
            if (code < 300 || code > 399) {
                throw new IOException("Can't deal with this response code ("
                        + code + ").");
            }

            is.close();
            is = null;

            url = new URL(huc.getHeaderField("location"));

            huc.disconnect();
            huc = null;

            huc = (HttpURLConnection) url.openConnection();
            client.setCookies(huc, cj);
            HttpURLConnection.setFollowRedirects(false);
            huc.connect();

            is = huc.getInputStream();
            cj.addAll(client.getCookies(huc));
            maxRedirects--;
        }

        if (maxRedirects <= 0 && code != successCode) {
            throw new IOException("Max redirects exhausted.");
        }

        bConnected = true;
    }

    /**
     * Checks whether this handler has successfully connected.
     */
    public boolean isConnected() {
        return (bConnected);
    }
}
