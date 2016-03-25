package com.autumn.core.cookie;

/**
 * Indicates some problem caused by a bad or malformed cookie. For constructor descriptions, see the
 * documentation for superclass.
 * @author	Sonal Bansal
 */
public class MalformedCookieException extends EnhancedIOException {

    private static final long serialVersionUID = 20120701000007L;
    public MalformedCookieException() {
        super();
    }

    public MalformedCookieException(String s) {
        super(s);
    }

    public MalformedCookieException(Exception under) {
        super(under);
    }

    public MalformedCookieException(String s, Exception under) {
        super(s, under);
    }

    public MalformedCookieException(String s, Exception under, String code, Object o, String method) {
        super(s, under, code, o, method);
    }

    public MalformedCookieException(String code, Object o, String method) {
        super(code, o, method);
    }

    public MalformedCookieException(String s, String code, Object o, String method) {
        super(s, code, o, method);
    }

    @Override
    public String getCode() {
        return (super.getCode().equals("UNSPECIFIED") ? "SBCL_0000" : super.getCode());
    }
}
