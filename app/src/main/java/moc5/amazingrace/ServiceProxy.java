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
    public Route[] getRoutes(String username, String password) throws ServiceCallException {
        return queryDataFromService("/GetRoutes?" +
                                    "userName=" + username +
                                    "&password=" + password,
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

    public Boolean checkVisitedCheckpoint(CheckpointRequest req) throws ServiceCallException {
        StringBuffer result = new StringBuffer();
        executePostRequest("/InformAboutVisitedCheckpoint", req, result);

        Log.i("Console", String.format("InformAboutVisitedCheckpoint response %s", result.toString()));

        return result.toString().equals("true") ? true : false;
    }

    public boolean resetAllRoute(Request req) throws ServiceCallException {
        StringBuffer result = new StringBuffer();
        executePostRequest("/ResetAllRoutes", req, result);

        Log.i("Console", String.format("ResetAllRoutes response %s", result.toString()));

        return result.toString().equals("true") ? true : false;
    }

    public boolean resetRoute(RouteRequest req) throws ServiceCallException {
        StringBuffer result = new StringBuffer();
        executePostRequest("/ResetRoute", req, result);

        Log.i("Console", String.format("ResetRoute response %s", result.toString()));

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
//            try {
//                BufferedReader reader = null;
//                try {
//                    reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
//
//                    String line;
//                    boolean isFirst = true;
//                    while ((line = reader.readLine()) != null) {
//                        if (isFirst) {
//                            isFirst = false;
//                        } else {
//                            result.append(System.getProperty("line.separator"));
//                        }
//
//                        result.append(line);
//                    }
//
//                } finally {
//                    if (reader != null) {
//                        reader.close();
//                    }
//                }
//            } finally {
//                Log.i("Console", "close connection");
//                urlConnection.disconnect();
//            }

        } catch(IOException ex) {
            Log.e("Console", "IOException");
            throw new ServiceCallException(ex);
        }
    }

    private void executePostRequest(String urlString, Request postReq, StringBuffer result) throws ServiceCallException {
        try {
            Request req = null;
            URL url = new URL("https://demo.nexperts.com/MOC5/AmazingRaceService/AmazingRaceService.svc" + urlString);
            Log.i("Console", String.format("Url '%s", url.toString()));

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

                Log.i("Console", "execute write");

                if(urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                    result.append(readInputStream(urlConnection.getInputStream()));
                } else {
                    result.append(readInputStream(urlConnection.getErrorStream()));
                }
            } finally {
                Log.i("Console", "close connection");
                urlConnection.disconnect();
            }
        } catch( IOException ex) {
            Log.i("Console", "Post exception");
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
                    Log.i("Console", String.format("Line %s", line));
                }
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }

            return result.toString();
        } catch(IOException ex) {
            Log.i("Console", "Stream exception");
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