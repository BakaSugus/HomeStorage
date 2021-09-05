package net.sf.webdav;
/*
 * Copyright 1999,2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import net.sf.webdav.IWebdavStore;
import net.sf.webdav.WebDavServletBean;

import javax.servlet.ServletException;
import java.lang.reflect.Constructor;

/**
 * Servlet which provides support for WebDAV level 2.
 * <p>
 * the original class is org.apache.catalina.servlets.WebdavServlet by Remy
 * Maucherat, which was heavily changed
 *
 * @author Remy Maucherat
 */

public class WebdavServlet extends WebDavServletBean {


    public void init() throws ServletException {

        boolean lazyFolderCreationOnPut = getInitParameter("lazyFolderCreationOnPut") != null
                && getInitParameter("lazyFolderCreationOnPut").equals("1");

        String dftIndexFile = getInitParameter("default-index-file");
        String insteadOf404 = getInitParameter("instead-of-404");

        int noContentLengthHeader = getIntInitParameter("no-content-length-headers");

        super.init(dftIndexFile, insteadOf404,
                noContentLengthHeader, lazyFolderCreationOnPut);
    }

    private int getIntInitParameter(String key) {
        return getInitParameter(key) == null ? -1 : Integer
                .parseInt(getInitParameter(key));
    }

    protected IWebdavStore constructStore(String clazzName) {
        IWebdavStore webdavStore;
        try {
            Class<?> clazz = WebdavServlet.class.getClassLoader().loadClass(
                    clazzName);

            Constructor<?> ctor = clazz
                    .getConstructor();

            webdavStore = (IWebdavStore) ctor
                    .newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("some problem making store component", e);
        }
        return webdavStore;
    }

}
