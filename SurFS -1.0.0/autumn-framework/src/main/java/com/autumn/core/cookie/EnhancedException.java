package com.autumn.core.cookie;

/**
 * Convenience implementation of <code>IEnhancedException</code>
 * interface, and subclass of <code>Exception</code>.<p>
 * Setter methods for the error code, causal Exception, class and
 * method of origin, and data Object are provided. However, once these
 * fields have been set, any subsequent calls to the respective setters
 * throws an <code>UnsupportedOperationException</code>.
 *
 * @author	Sonal Bansal
 * @see		java.lang.Exception
 */
public final class EnhancedException extends Exception implements IEnhancedException {

    private static final long serialVersionUID = 20120701000010L;
    private Exception causalException;
    private String exceptionCode;
    private String originClass;
    private String originMethod;
    private Object data;
    private boolean bDataSet = false;
    private boolean bClassSet = false;
    private boolean bMethodSet = false;
    private boolean bCodeSet = false;
    private boolean bCausalSet = false;

    /**
     * Constructs a "plain vanilla" EnhancedException.
     */
    public EnhancedException() {
        super();
    }

    /**
     * Constructs an EnhancedException with a short detail message.
     *
     * @param s	the detail message.
     */
    public EnhancedException(String s) {
        super(s);
    }

    /**
     * Constructs an EnhancedException with the specified causal Exception.
     *
     * @param under	the causal Exception.
     * @see 	java.lang.Exception
     */
    public EnhancedException(Exception under) {
        super();
        setCausalException(under);
    }

    /**
     * Constructs an EnhancedException with the specified detail message and causal Exception.
     *
     * @param under	the causal Exception.
     * @param s 	the detail message.
     * @see 	java.lang.Exception
     */
    public EnhancedException(String s, Exception under) {
        super(s);
        setCausalException(under);
    }

    /**
     * Constructs an EnhancedException with a detail message, causal Exception, error code,
     * class and method of origin. For example,<br>
     * <pre>
     * ...
     * throw new EnhancedException("Failed to connect to server", excp, "SVR_0090", this, "connect");
     * ...</pre>
     *
     * @param under	the causal Exception.
     * @param s 	the detail message.
     * @param code	the error-code.
     * @param o	the Object from which the class of origin is determined.
     * @param method	the method of origin.
     * @see 	java.lang.Exception
     */
    public EnhancedException(String s, Exception under, String code, Object o, String method) {
        super(s);
        setCausalException(under);
        setCode(code);
        setOriginClass(o);
        setOriginMethod(method);
    }

    /**
     * Constructs an EnhancedException with an error code and, class and method of origin. For example,<br>
     * <pre>
     * ...
     * throw new EnhancedException("SVR_0090", this, "connect");
     * ...</pre>
     *
     * @param code	the error-code.
     * @param o	the Object from which the class of origin is determined.
     * @param method	the method of origin.
     */
    public EnhancedException(String code, Object o, String method) {
        super();
        setCode(code);
        setOriginClass(o);
        setOriginMethod(method);
    }

    /**
     * Constructs an EnhancedException with a detail message, error code,
     * class and method of origin. For example,<br>
     * <pre>
     * ...
     * throw new EnhancedException("Failed to connect to server", "SVR_0090", this, "connect");
     * ...</pre>
     *
     * @param s 	the detail message.
     * @param code	the error-code.
     * @param o 	the Object from which the class of origin is determined.
     * @param method	the method of origin.
     * @see 	java.lang.Exception
     */
    public EnhancedException(String s, String code, Object o, String method) {
        super(s);
        setCode(code);
        setOriginClass(o);
        setOriginMethod(method);
    }

    @Override
    public Exception getCausalException() {
        return (causalException);
    }

    @Override
    public String getOriginClass() {
        return ((originClass == null) ? "UNKNOWN" : originClass);
    }

    @Override
    public String getOriginMethod() {
        return ((originMethod == null) ? "UNKNOWN" : originMethod);
    }

    @Override
    public String getCode() {
        return ((exceptionCode == null) ? "UNSPECIFIED" : exceptionCode);
    }

    /**
     * Sets the data Object which will be passed up the call stack.
     *
     * @param o	the data Object.
     * @throws UnsupportedOperationException	Thrown if the data Object has already been set.
     */
    public void setDataObject(Object o) throws UnsupportedOperationException {
        if (bDataSet) {
            throw new UnsupportedOperationException("Data Object has already been set.");
        }

        internalSetDataObject(o);
        bDataSet = true;
    }

    @Override
    public Object getDataObject() {
        return (data);
    }

    @Override
    public void removeDataObject() {
        data = null;
    }

    /**
     * Sets the error-code which identifies the particular error condition that
     * triggered this exception.
     *
     * @param c	the error code.
     * @throws UnsupportedOperationException	Thrown if the error code has already been set.
     */
    public void setCode(String c) throws UnsupportedOperationException {
        if (bCodeSet) {
            throw new UnsupportedOperationException("Error-code has already been set.");
        }

        internalSetCode(c);
        bCodeSet = true;
    }

    /**
     * Sets the class of origin for this instance. The class of origin is taken as
     * the value returned by o.getClass().getName(). However, if the parameter
     * is itself an instance of <code>Class</code>, then the class of origin is taken as
     * o.getName().
     *
     * @param o	the Object representing the class of origin.
     * @throws UnsupportedOperationException	Thrown if the class of origin has already been set.
     * @see java.lang.Class
     */
    public void setOriginClass(Object o) throws UnsupportedOperationException {
        if (bClassSet) {
            throw new UnsupportedOperationException("Class of origin has already been set.");
        }

        internalSetOriginClass(o);
        bClassSet = true;
    }

    /**
     * Sets the method of origin for this instance.
     *
     * @param meth 	the String representing the method of origin.
     * @throws UnsupportedOperationException	Thrown if the method of origin has already been set.
     */
    public void setOriginMethod(String meth) throws UnsupportedOperationException {
        if (bMethodSet) {
            throw new UnsupportedOperationException("Method of origin has already been set.");
        }

        internalSetOriginMethod(meth);
        bMethodSet = true;
    }

    /**
     * Sets the underlying (causal) Exception for this instance.
     *
     * @param e 	the Exception representing the causal Exception.
     * @throws UnsupportedOperationException	Thrown if the causal Exception has already been set.
     */
    public void setCausalException(Exception e) throws UnsupportedOperationException {
        if (bCausalSet) {
            throw new UnsupportedOperationException("Causal Exception has already been set.");
        }

        internalSetCausalException(e);
        bCausalSet = true;
    }

    /**
     * Returns a short description of this instance. If this instance
     * does not contain the error code or the origin info, the returned String
     * is same as would be returned by <code>Exception.toString()</code>.
     * Otherwise, the returned String is formed by concatenating the following :-<br>
     * <ul>
     * <li>The name of the actual class of this object </li>
     * <li>": " (a colon and a space) </li>
     * <li>The result of the {@link #getMessage} method for this object</li>
     * <li>" : Code="</li>
     * <li>The error-code</li>
     * <li>" : OriginClass="</li>
     * <li>The class of origin</li>
     * <li>" : OriginMethod="</li>
     * <li>The method of origin</li>
     * <li>" : CausalException="</li>
     * <li>The name of the class of the causal Exception (if any)</li>
     * </ul>
     *
     * @return	the <code>String</code> representation of this <code>EnhancedException</code>.
     * @see		java.lang.Exception#toString()
     */
    @Override
    public String toString() {
        if (exceptionCode == null && originClass == null && originMethod == null) {
            return (super.toString());
        }

        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getName());
        sb.append(": ");
        sb.append((getLocalizedMessage() == null) ? "" : getLocalizedMessage());
        sb.append(" : Code=");
        sb.append(getCode());
        sb.append(" : OriginClass=");
        sb.append(getOriginClass());
        sb.append(" : OriginMethod=");
        sb.append(getOriginMethod());
        sb.append(" : CausalException=");
        sb.append((getCausalException() == null) ? "" : getCausalException().getClass().getName());

        if (getCausalException() != null) {
            try {
                java.io.StringWriter sw = new java.io.StringWriter();
                getCausalException().printStackTrace(new java.io.PrintWriter(sw));
                sb.append("\n");
                sb.append(sw.toString());
            } catch (Exception e) {
            }
        }

        return (sb.toString());
    }

    /**
     * Sets the underlying (causal) Exception for this instance. Allows subclasses to have
     * unrestricted access to "causal exception" field.
     *
     * @param e 	the Exception representing the causal Exception.
     */
    protected void internalSetCausalException(Exception e) {
        causalException = e;
    }

    /**
     * Sets the method of origin for this instance. Allows subclasses to have
     * unrestricted access to "method of origin" field.
     *
     * @param meth 	the String representing the method of origin.
     */
    protected void internalSetOriginMethod(String meth) {
        originMethod = meth;
    }

    /**
     * Sets the class of origin for this instance. The class of origin is taken as
     * the value returned by o.getClass().getName(). However, if the parameter
     * is itself an instance of <code>Class</code>, then the class of origin is taken as
     * o.getName(). Allows subclasses to have unrestricted access to "class of origin" field.
     *
     * @param o	the Object representing the class of origin.
     * @see java.lang.Class
     */
    protected void internalSetOriginClass(Object o) {
        if (o != null) {
            if (o instanceof Class) {
                originClass = ((Class) o).getName();
            } else {
                originClass = o.getClass().getName();
            }
        } else {
            originClass = null;
        }
    }

    /**
     * Sets the error-code which identifies the particular error condition that
     * triggered this exception. Allows subclasses to have unrestricted access
     * to "error code" field.
     *
     * @param c	the error code.
     */
    protected void internalSetCode(String c) {
        exceptionCode = c;
    }

    /**
     * Sets the data Object which will be passed up the call stack. Allows subclasses to have
     * unrestricted access to "data Object" field.
     *
     * @param o	the data Object.
     */
    protected void internalSetDataObject(Object o) {
        data = o;
    }
}
