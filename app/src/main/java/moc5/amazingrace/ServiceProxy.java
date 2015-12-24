package moc5.amazingrace;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Blade on 21.12.2015.
 */
public class ServiceProxy {
    public Route[] getRoutes(Request req) throws ServiceCallException {
        return queryDataFromService("/GetRoutes?" +
                                    "userName=" + req.getUserName() +
                                    "&password=" + req.getPassword(),
                                    Route[].class );
    }

    public boolean checkCredentials(Request req) throws ServiceCallException {
        StringBuffer result = new StringBuffer();

        executeGetRequest("/CheckCredentials?" +
                        "userName=" + Uri.encode(req.getUserName()) +
                        "&password=" + Uri.encode(req.getPassword()),
                        result);

        return result.toString().equals("true") ? true : false;
    }

    public Boolean checkVisitedCheckpoint(Request req) throws ServiceCallException {
        StringBuffer result = new StringBuffer();
        executePostRequest("/InformAboutVisitedCheckpoint", req, result);

        return result.toString().equals("true") ? true : false;
    }

    public boolean resetAllRoute(Request req) throws ServiceCallException {
        StringBuffer result = new StringBuffer();
        executePostRequest("/ResetAllRoutes", req, result);

        return result.toString().equals("true") ? true : false;
    }

    public boolean resetRoute(Request req) throws ServiceCallException {
        StringBuffer result = new StringBuffer();
        executePostRequest("/ResetRoute", req, result);

        return result.toString().equals("true") ? true : false;
    }

    private <T> T queryDataFromService(String url, Class<T> resultClass) throws ServiceCallException {
        // execute get request
        StringBuffer result = new StringBuffer();

        executeGetRequest(url, result);

        //parse JSON response
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(result.toString(), resultClass);
    }

    private void executeGetRequest(String urlString, StringBuffer result) throws ServiceCallException {
        try {
            URL url = new URL("https://demo.nexperts.com/MOC5/AmazingRaceService/AmazingRaceService.svc" + urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            result.append(readInputStream(urlConnection.getInputStream()));
        } catch(IOException ex) {
            Log.e("Console", "IOException");
            throw new ServiceCallException(ex);
        }
    }

    private void executePostRequest(String urlString, Request postReq, StringBuffer result) throws ServiceCallException {
        try {
            Request req = null;
            URL url = new URL("https://demo.nexperts.com/MOC5/AmazingRaceService/AmazingRaceService.svc" + urlString);

            if (urlString.equals("/InformAboutVisitedCheckpoint")) {
                req = (CheckpointRequest)postReq;
            } else if (urlString.equals("/ResetRoute")) {
                req = (RouteRequest)postReq;
            } else {
                req = postReq;
            }

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setChunkedStreamingMode(0);
                urlConnection.connect();

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
                Gson gson = new GsonBuilder().create();
                String params = gson.toJson(req);
                writer.write(params.toString());
                writer.flush();
                writer.close();

                if(urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                    result.append(readInputStream(urlConnection.getInputStream()));
                } else {
                    result.append(readInputStream(urlConnection.getErrorStream()));
                }
            } finally {
                urlConnection.disconnect();
            }
        } catch( IOException ex) {
            throw new ServiceCallException(ex);
        }
    }

    private String readInputStream(InputStream stream) throws ServiceCallException {
        try {
            BufferedReader reader = null;
            StringBuffer result = new StringBuffer();
            try {
                reader = new BufferedReader(new InputStreamReader(stream));
                String line;
                boolean isFirst = true;
                while ((line = reader.readLine()) != null) {
                    if (isFirst) {
                        isFirst = false;
                    } else {
                        result.append(System.getProperty("line.separator"));
                    }
                    result.append(line);
                }
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }

            return result.toString();
        } catch(IOException ex) {
            throw new ServiceCallException(ex);
        }
    }
}

class ServiceCallException extends Exception {
    public ServiceCallException() {
        super();
    }

    public ServiceCallException(String detailMessage) {
        super(detailMessage);
    }

    public ServiceCallException(Throwable throwable) {
        super(throwable);
    }

    public ServiceCallException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}