package org.sunchezz.webdav;

import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
import com.github.sardine.impl.SardineException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.List;

public class WebdavConnector {
    private static WebdavConnector instance;

    private Sardine client;
    //    private static String webDavURL = "http://webdav.schlitt.info";
    private static String rawDomain = "webdav.schlitt.info";
    private static String URL = rawDomain ;
    private static String webDavURL = "http://" + URL;

//    private static String rawDomain = "www.dlp-test.com";
//    private static String URL = rawDomain + "/webdav";
//    private static String webDavURL = "https://" + URL;


    private WebdavConnector() {
        client = SardineFactory.begin();
//        client.setCredentials("www.dlp-test.com\\WebDAV", "WebDAV");
        client.enablePreemptiveAuthentication(rawDomain);
        instance = this;
    }

    public boolean addFolder(String path) {
        try {
            path = webDavURL + path;
            if (!client.exists(path)) {
                client.createDirectory(path);
            }
            return client.exists(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean uploadFile(String targetDir, File file) throws IOException {
        if (file.exists()) {
            targetDir = webDavURL + targetDir + file.getName();

            FileInputStream stream = new FileInputStream(file);

            // Produces 0b File
            client.put(targetDir, stream);

            // Works
//            client.put(targetDir, stream, null, false, file.length());
        }

        return client.exists(targetDir);
    }

    private List<DavResource> listFiles(String path) {
        try {
            return client.list(webDavURL + path);
        } catch (SardineException e) {
            e.getResponsePhrase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        WebdavConnector connector = new WebdavConnector();
        URL templateUrl = connector.getClass().getClassLoader().getResource("test.xml");
        File templateFile = new File(URLDecoder.decode(templateUrl.getFile(), "UTF-8"));

        System.out.println(connector.listFiles("/"));
//        connector.client.delete(webDavURL + "/sunchezz");
        connector.addFolder("/dir");

        System.out.println(connector.uploadFile("/dir/", templateFile));
    }
}
