package org.isoron.uhabits.core.io;

import org.isoron.uhabits.core.utils.DateUtils;
import org.json.simple.JSONObject;
import org.openprovenance.prov.interop.InteropFramework;
import org.openprovenance.prov.model.Document;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.ws.http.HTTPException;

/**
 * Created by muel_s12 on 17.01.2018.
 */

public class ProvUploader {
    private final static String API_KEY = "a5ad3b0ed57d1825b3f524fd016f6da1002b066c";
    private final static String USERNAME = "AgenoDrei";
    private final static String CONTENT_TYPE = "application/json";
    private final static String PROV_STORE_URL = "https://provenance.ecs.soton.ac.uk/store/api/v0/documents/";

    public ProvUploader() {

    }

    public int uploadDocument(Document doc) throws HTTPException, IOException {
        URL endpoint = new URL(PROV_STORE_URL);
        HttpsURLConnection connection = (HttpsURLConnection) endpoint.openConnection();

        connection.setRequestProperty("Authorization", getAuthorizationHeader());
        connection.setRequestProperty("Content-Type", CONTENT_TYPE);
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        connection.getOutputStream().write(getRequestBody(doc).toString().getBytes());

        int resCode = connection.getResponseCode();
        return resCode;
    }

    private String getAuthorizationHeader() {
        return "ApiKey " + USERNAME + ":" + API_KEY;
    }

    private JSONObject getRequestBody(Document doc) {
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(DateUtils.getStartOfToday());
        String docName = String.format("Loop Habits PROVN %s", date);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        InteropFramework fw = new InteropFramework(InteropFramework.newXMLProvFactory());
        fw.writeDocument(outputStream, InteropFramework.ProvFormat.JSON, doc);
        String documentString = outputStream.toString();

        JSONObject requestBody = new JSONObject();
        requestBody.put("rec_id", docName);
        requestBody.put("public", true);
        requestBody.put("content", documentString);

        return requestBody;
    }
}
