package com.tw.auth.web.rest;

import lombok.Cleanup;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;

public abstract class AbstractSdbResource {

    protected final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @SneakyThrows({UnsupportedEncodingException.class, IOException.class})
    protected void exportExcelFile(String fileName, InputStream is, HttpServletResponse response) {
        String encodeFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
        String fileExtension = FilenameUtils.getExtension(encodeFileName).toLowerCase();

        response.setHeader("Content-Disposition", "attachment; filename=\"" + encodeFileName + "\"");
        response.setHeader("X-Suggested-Filename", encodeFileName);
        response.setContentType(fileExtension.equals("xlsx") ?
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" :
            "application/vnd.ms-excel");
        @Cleanup OutputStream os = response.getOutputStream();
        IOUtils.copy(is, os);
    }

    @SneakyThrows({UnsupportedEncodingException.class, IOException.class})
    protected void exportExcelFile(String fileName, byte[] ibs, HttpServletResponse response) {
        String encodeFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
        String fileExtension = FilenameUtils.getExtension(encodeFileName).toLowerCase();

        response.setHeader("Content-Disposition", "attachment; filename=\"" + encodeFileName + "\"");
        response.setHeader("X-Suggested-Filename", encodeFileName);
        response.setContentType(fileExtension.equals("xlsx") ?
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" :
            "application/vnd.ms-excel");
        @Cleanup OutputStream os = response.getOutputStream();
        os.write(ibs);
        os.flush();
    }
}
