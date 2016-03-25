package com.autumn.core.web;

import java.net.URLDecoder;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * <p>
 * Title: WEB框架</p>
 *
 * <p>
 * Description: 解析get请求,忽略大小写</p>
 *
 * <p>
 * Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>
 * Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
@SuppressWarnings("unchecked")
public class QueryRequestWrapper extends HttpServletRequestWrapper {

    protected HashMap<String, Object> parameters = null;
    protected HashMap<String, Object> queryparams = null;
    protected List<String> parameterNames = new ArrayList<String>();

    public QueryRequestWrapper(HttpServletRequest request, boolean onlyQueryParameter) {
        super(request);
        this.parameters = new HashMap<String, Object>();
        addQueryParameter(request);
        if (!onlyQueryParameter) {
            addPostParameter(request);
        } else {
            parameters = queryparams != null ? queryparams : parameters;
        }
    }

    public QueryRequestWrapper(HttpServletRequest request) {
        this(request, false);
    }

    /**
     * 解析querystring中的参数
     *
     * @param request
     */
    private void addQueryParameter(HttpServletRequest request) {
        String querystring = request.getQueryString();
        if (querystring == null || querystring.trim().equals("")) {
            return;
        }
        queryparams = new HashMap<String, Object>();
        String charset = request.getCharacterEncoding();
        if (charset == null || charset.isEmpty()) {
            charset = DispatchFilter.encoding;
        }
        java.util.StringTokenizer st = new java.util.StringTokenizer(querystring, "&");
        while (st.hasMoreTokens()) {
            String item = st.nextToken();
            int index = item.indexOf("=");
            if (index <= 0) {
                continue;
            }
            String name = item.substring(0, index);
            String value = item.substring(index + 1);
            try {
                value = URLDecoder.decode(value, charset);
            } catch (Exception e) {
            }
            setQueryParams(name, value);
        }
    }

    /**
     * 加参数
     *
     * @param name
     * @param value
     */
    private void setQueryParams(String name, String value) {
        if (!parameterNames.contains(name)) {
            parameterNames.add(name);
        }
        String key = name.toLowerCase();
        Object mValue = queryparams.get(key);
        if (mValue == null) {
            queryparams.put(key, value);
        } else {
            if (mValue instanceof List) {
                List l = (List) mValue;
                l.add(value);
            } else {
                List l = new ArrayList();
                l.add(mValue);
                l.add(value);
                queryparams.put(key, l);
            }
        }
    }

    /**
     * 解析post中的参数
     *
     * @param request
     */
    private void addPostParameter(HttpServletRequest request) {
        Enumeration e = request.getParameterNames();
        while (e.hasMoreElements()) {
            String name = (String) e.nextElement();
            String[] values = request.getParameterValues(name);
            if (values != null && values.length > 0) {
                if (queryparams != null) {
                    Object truevalues = queryparams.get(name.toLowerCase());
                    if (truevalues != null) {
                        if (truevalues instanceof List) {
                            String[] nvalues = (String[]) ((List) truevalues).toArray(new String[((List) truevalues).size()]);
                            int count = nvalues.length > values.length ? values.length : nvalues.length;
                            System.arraycopy(nvalues, 0, values, 0, count);
                        } else {
                            if (values.length > 0) {
                                values[0] = truevalues.toString();
                            }
                        }
                    }
                }
                setParameter(name, values);
            }
        }
    }

    /**
     * 加参数
     *
     * @param name
     * @param values
     */
    private void setParameter(String name, String[] values) {
        if (!parameterNames.contains(name)) {
            parameterNames.add(name);
        }
        String key = name.toLowerCase();
        Object mValue = parameters.get(key);
        if (mValue == null) {
            if (values.length == 1) {
                parameters.put(key, values[0]);
            } else {
                parameters.put(key, new ArrayList(Arrays.asList(values)));
            }
        } else {
            if (mValue instanceof List) {
                List l = (List) mValue;
                if (values.length == 1) {
                    l.add(values[0]);
                } else {
                    l.addAll(Arrays.asList(values));
                }
            } else {
                List l = new ArrayList();
                l.add(mValue);
                if (values.length == 1) {
                    l.add(values[0]);
                } else {
                    l.addAll(Arrays.asList(values));
                }
                parameters.put(key, l);
            }
        }
    }

    @Override
    public String getParameter(String name) {
        Object mValue = parameters.get(name.toLowerCase());
        if (mValue == null) {
            return null;
        }
        if (mValue instanceof List) {
            List l = (List) mValue;
            if (l.isEmpty()) {
                return null;
            } else {
                return l.get(0).toString();
            }
        } else {
            return mValue.toString();
        }
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(parameterNames);
    }

    @Override
    public String[] getParameterValues(String name) {
        Object mValue = parameters.get(name.toLowerCase());
        if (mValue == null) {
            return null;
        }
        if (mValue instanceof List) {
            List l = (List) mValue;
            return (String[]) l.toArray(new String[l.size()]);
        } else {
            String[] s = new String[1];
            s[0] = mValue.toString();
            return s;
        }
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map map = new HashMap<String, String[]>();
        for (String s : parameterNames) {
            map.put(s, this.getParameterValues(s));
        }
        return map;
    }
}
