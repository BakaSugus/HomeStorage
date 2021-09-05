package cn.j.netstorage.filter;

import net.sf.webdav.WebdavStatus;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class ErrorFilter extends OncePerRequestFilter {
    private static final String errorPage = readErrorPage();

    private static String readErrorPage() {
        try {
            ClassPathResource classPathResource = new ClassPathResource("error.xml");
            InputStream inputStream = classPathResource.getInputStream();
            byte[] buffer = new byte[(int) classPathResource.contentLength()];
            IOUtils.readFully(inputStream, buffer);
            return new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "";
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        ErrorWrapperResponse wrapperResponse = new ErrorWrapperResponse(httpServletResponse);

        try {
            filterChain.doFilter(httpServletRequest, wrapperResponse);
            if (wrapperResponse.hasErrorToSend()) {
                int status = wrapperResponse.getStatus();
                if (status == 401) {
//                    httpServletResponse.addHeader("WWW-Authenticate", "Digest realm=\"iptel.org\", qop=\"auth,auth-int\",\n" +
//                            "nonce=\"dcd98b7102dd2f0e8b11d0f600bfb0c093\", opaque=\"\", algorithm=MD5");
//
                }
                httpServletResponse.setStatus(status);
                String message = wrapperResponse.getMessage();
                if (message == null) {
                    message = WebdavStatus.getStatusText(status);
                }
                String errorXml = errorPage.replace("{{code}}", status + "").replace("{{message}}", message);
                httpServletResponse.getWriter().write(errorXml);
            }
            httpServletResponse.flushBuffer();
        } catch (Throwable t) {
            httpServletResponse.setStatus(500);
            httpServletResponse.getWriter().write(t.getMessage());
            httpServletResponse.flushBuffer();
        }
    }

    private static class ErrorWrapperResponse extends HttpServletResponseWrapper {
        private int status;
        private String message;
        private boolean hasErrorToSend = false;

        ErrorWrapperResponse(HttpServletResponse response) {
            super(response);
        }

        public void sendError(int status) throws IOException {
            this.sendError(status, (String) null);
        }

        public void sendError(int status, String message) throws IOException {
            this.status = status;
            this.message = message;
            this.hasErrorToSend = true;
        }

        public int getStatus() {
            return this.hasErrorToSend ? this.status : super.getStatus();
        }

        public void flushBuffer() throws IOException {
            super.flushBuffer();
        }


        String getMessage() {
            return this.message;
        }

        boolean hasErrorToSend() {
            return this.hasErrorToSend;
        }

        public PrintWriter getWriter() throws IOException {
            return super.getWriter();
        }

        public ServletOutputStream getOutputStream() throws IOException {
            return super.getOutputStream();
        }
    }
}
