package org.sunchezz.webdav;

import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;

public class WebdavConnector {

    private Sardine client;

    //http://files.schlitt.info/work/ez/public_webdav.html#using-the-webdav-test-server
    private static String rawDomain = "webdav.schlitt.info";
    private static String URL = rawDomain;
    private static String webDavURL = "http://" + URL;
    private static String pw = "";
    private static String user = "";

    //https://www.dlp-test.com/WebDAV-Intro/
//    private static String rawDomain = "www.dlp-test.com";
//    private static String URL = rawDomain + "/webdav";
//    private static String webDavURL = "https://" + URL;
//    private static String pw = "WebDAV";
//    private static String user = "www.dlp-test.com\\WebDAV";


    private WebdavConnector() {
        client = SardineFactory.begin();
        client.setCredentials(user, pw);
        client.enablePreemptiveAuthentication(rawDomain);
    }

    public boolean addFolder(String path) {
        try {
            path = webDavURL + path;
            if (!client.exists(path)) {
                client.createDirectory(path);
                return client.exists(path);
            } else return true;
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

    public static void main(String[] args) throws IOException, InterruptedException {
        WebdavConnector connector = new WebdavConnector();
        URL templateUrl = connector.getClass().getClassLoader().getResource("test.xml");
        File templateFile = new File(URLDecoder.decode(templateUrl.getFile(), "UTF-8"));

        System.out.println(connector.client.list(webDavURL + "/"));
        connector.addFolder("/dir");

        System.out.println("Success: " + connector.uploadFile("/dir/", templateFile));
    }
}
