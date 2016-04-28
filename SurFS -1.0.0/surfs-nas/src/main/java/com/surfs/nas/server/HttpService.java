/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.surfs.nas.server;

import com.surfs.nas.StorageConfig;
import com.surfs.nas.StoragePool;
import com.surfs.nas.StorageSources;

import com.surfs.nas.util.Function;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.servlet.*;
import javax.servlet.http.*;

public class HttpService implements Filter {


    @Override
    public void init(FilterConfig fc) throws ServletException {
    }


    @Override
    public void doFilter(ServletRequest sr, ServletResponse sr1, FilterChain fc) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) sr;
        HttpServletResponse response = (HttpServletResponse) sr1;
        StoragePool pool = StorageSources.getServiceStoragePool();
        if (pool == null) {
            response.sendError(404);
            return;
        }
        if (request.getRequestURI().endsWith("scan")) {
            pool.getServerSourceMgr().getVolumeScaner().restart();
            response.getWriter().print("OK");
            response.getWriter().close();
        } else if (request.getRequestURI().endsWith("cfg")) {
            OutputStream os = response.getOutputStream();
            InputStream is = StorageConfig.getConfig();
            Function.copy(is, os);
        } else if (request.getRequestURI().endsWith("offline")) {
            String volid = request.getQueryString();
            Volume vol = pool.getServerSourceMgr().getVolumeMap().get(volid);
            if (vol != null) {
                vol.setOffline(true);
            }
            response.getWriter().print("OK");
            response.getWriter().close();
        } else if (request.getRequestURI().endsWith("online")) {
            String volid = request.getQueryString();
            Volume vol = pool.getServerSourceMgr().getVolumeMap().get(volid);
            if (vol != null) {
                vol.setOffline(false);
            }
            pool.getServerSourceMgr().getVolumeScaner().restart();
            response.getWriter().print("OK");
            response.getWriter().close();
        } else {
            response.sendError(404);
        }
    }

    @Override
    public void destroy() {
    }
}
