package crawler.util;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Reader {


    public static String readStringFromURL(String requestURL) {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(requestURL);

            CloseableHttpResponse response = httpClient.execute(httpGet);
            String result = EntityUtils.toString(response.getEntity());
            if (result != null && !result.equals("")) {
                return result;
            }
        } catch (Exception e) {
            Logger.getInstance().warning("Fail to read string from url: " + requestURL);
        }
        return readStringFromURLAssist(requestURL);
    }

    public static String readStringFromURLAssist(String requestURL) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(requestURL).openConnection();
            byte[] bytes = connection.getInputStream().readAllBytes();
            CharsetDetector detector = new CharsetDetector();
            detector.setText(bytes);
            CharsetMatch match = detector.detect();
            String content = match.getString();

            return content;

        } catch (Exception e) {
            Logger.getInstance().warning("Fail to read string from url: " + requestURL);
            return "";
        }
    }

    public static String readStringFromFile(String filePath) {
        String content = null;
        try {
            content = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            Logger.getInstance().warning("Fail to read string from file path: " + filePath);
            return "";
        }
        return content;
    }
}