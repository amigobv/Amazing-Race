package moc5.amazingrace;

import android.net.Uri;
import android.support.annotation.BoolRes;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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

    public boolean checkCredentials(String username, String password) throws ServiceCallException {
        // execute get request
        StringBuffer result = new StringBuffer();
        executeGetRequest("/CheckCredentials?" +
                "userName=" + Uri.encode(username) +
                "&password=" + Uri.encode(password),
                result);

        Boolean b = result.toString().equals("true") ? true : false;
        Log.i("Console", String.format("Result: '%s",result));

        return b;
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
            Log.i("Console", String.format("Url '%s", url.toString()));
            try {
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    Log.i("Console", "create reader");
                    String line;
                    boolean isFirst = true;
                    while ((line = reader.readLine()) != null) {
                        if (isFirst) {
                            isFirst = false;
                        } else {
                            result.append(System.getProperty("line.separator"));
                        }
                        Log.i("Console", String.format("%s", line));
                        result.append(line);
                    }

                } finally {
                    if (reader != null) {
                        reader.close();
                    }
                }
            } finally {
                Log.i("Console", "close connection");
                urlConnection.disconnect();
            }

        } catch(IOException ex) {
            Log.e("Console", "IOException");
            throw new ServiceCallException(ex);
        }
    }

    private void executePostRequest(String urlString, Request req, StringBuffer result) throws ServiceCallException {
        try {
            URL url = new URL("https://demo.nexperts.com/MOC5/AmazingRaceService/AmazingRaceService.svc" + urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                urlConnection.setDoOutput(true);
                urlConnection.setChunkedStreamingMode(0);

                OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                Gson gson = new GsonBuilder().create();
                String params = gson.toJson(req);
                out.write(params.getBytes());
                out.flush();

                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
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
            } finally {
                urlConnection.disconnect();
            }
        } catch( IOException ex) {
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