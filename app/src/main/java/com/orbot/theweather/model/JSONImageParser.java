
package com.orbot.theweather.model;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class JSONImageParser {
    public final static int FLICKERIMAGEREQUESTDATA = 0;
    final protected static int FLICKERIMAGE = 1;
    final protected static int WEATHERREQUEST = 1;
    private String flickerURLBase = "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=";
    private String flickerImageData0 = "&text=";
    private String flickerImageData1 = "&accuracy=&safe_search=&per_page=1&format=json&nojsoncallback=1";
    private String flickerDataImageSearchTerm;
    //Enter Flickr API here
    private String flickerAPIK = "";

    private String flickerImageURL = "https://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}.jpg";

    private String getFlickerDataImageSearchTerm() {
        return flickerDataImageSearchTerm;
    }

    private void setFlickerDataImageSearchTerm(String flickerDataImageSearchTerm) {
        this.flickerDataImageSearchTerm = flickerDataImageSearchTerm;
    }



    public static FlickrImage getFlickrImageData(String data) throws JSONException {
        FlickrImage flickrImage = new FlickrImage();

        if (data != null) {
            //  create out JSONObject from the data
            JSONObject jObj = new JSONObject(data);

            //  start extracting the info
            // We get flickrImage info (This is an array)
            JSONObject JSONWeather = jObj.getJSONObject("photos");
            JSONArray jArr = JSONWeather.getJSONArray("photo");

            JSONObject JSONImage = jArr.getJSONObject(0);
            // We use only the first value

            flickrImage.setID(getString("id", JSONImage));
            flickrImage.setServerID(getInt("server", JSONImage));
            flickrImage.setFarmID(getInt("farm", JSONImage));

            flickrImage.setOwner(getString("owner", JSONImage));
            flickrImage.setSecret(getString("secret", JSONImage));
            flickrImage.setTitle(getString("title", JSONImage));
        }
        return flickrImage;
    }


    private static JSONObject getObject(String tagName, JSONObject jObj) throws JSONException {
        JSONObject subObj = jObj.getJSONObject(tagName);
        return subObj;
    }

    private static String getString(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getString(tagName);
    }

    private static double getDouble(String tagName, JSONObject jObj) throws JSONException {
        return (double) jObj.getDouble(tagName);
    }

    private static boolean getBoolean(String tagName, JSONObject jObj) throws JSONException {
        return (boolean) jObj.getBoolean(tagName);
    }

    private static int getInt(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getInt(tagName);
    }

    public byte[] getImage(String url) {
        HttpURLConnection con = null;
        InputStream is = null;
        try {
            con = (HttpURLConnection) (new URL(url)).openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            // Let's read the response
            is = con.getInputStream();
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            while (is.read(buffer) != -1)
                baos.write(buffer);

            return baos.toByteArray();
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (Throwable t) {
            }
            try {
                con.disconnect();
            } catch (Throwable t) {
            }
        }

        return null;

    }

    public String getImageDataFor(int operationType, String cityName, String currentMainWeather) {
        HttpURLConnection con = null;
        InputStream is = null;
        this.setFlickerDataImageSearchTerm(cityName + " " + currentMainWeather);
        String tempUrl = null;
        String data = null;

        try {
            tempUrl = getUrl(operationType);
            con = (HttpURLConnection) (new URL(tempUrl)).openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();
            //will throw exception so handle it on finish - not connected to internet.

            // Let's read the response
            StringBuffer buffer = new StringBuffer();
            is = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ((line = br.readLine()) != null)
                buffer.append(line + "\r\n");

            is.close();
            con.disconnect();
            data = buffer.toString();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return data;
    }

    private String getUrl(int operationType) throws UnsupportedEncodingException {
        String s = null;
        switch (operationType) {
            case FLICKERIMAGEREQUESTDATA:
                s = flickerURLBase + flickerAPIK + flickerImageData0 + URLEncoder.encode(this.getFlickerDataImageSearchTerm(), "UTF-8") + flickerImageData1;
                break;

        }
        return s;
    }

    public String getFlickrImageURL(int farmID, int serverID, String ID, String secret) {
        //Log.d("reguestImage", "https://farm" + farmID + ".staticflickr.com/" + serverID + "/" + ID + "_" + secret + ".jpg");
        return "https://farm" + farmID + ".staticflickr.com/" + serverID + "/" + ID + "_" + secret + ".jpg";
    }

    public Bitmap DownloadImage(String URL) {
        Bitmap bitmap = null;
        InputStream in = null;
        try {
            in = OpenHttpConnection(URL);
            bitmap = BitmapFactory.decodeStream(in);
            in.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return bitmap;
    }

    private InputStream OpenHttpConnection(String urlString) throws IOException {
        InputStream in = null;
        int response = -1;

        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();

        if (!(conn instanceof HttpURLConnection))
            throw new IOException("Not an HTTP connection");

        try {
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();

            response = httpConn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();
            }
        } catch (Exception ex) {
            throw new IOException("Error connecting");
        }
        return in;
    }
}
