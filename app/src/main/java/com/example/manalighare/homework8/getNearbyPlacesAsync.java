package com.example.manalighare.homework8;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class getNearbyPlacesAsync extends AsyncTask<String,Void,ArrayList<place_attibutes>> {

    putnearbyMarkers putnearbyMarkers;
    String type;

    public getNearbyPlacesAsync(putnearbyMarkers context,String type) {
        putnearbyMarkers=context;
        this.type=type;
    }


    @Override

    protected ArrayList<place_attibutes> doInBackground(String... params) {


        StringBuilder stringBuilder = new StringBuilder();
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        String result = null;
        ArrayList<place_attibutes> places_nearby = new ArrayList<>();



        try {
            URL url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = "";
                places_nearby.clear();
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                result = stringBuilder.toString();

                Log.d("result from async",""+result.toString());

                JSONObject root = new JSONObject(result.toString());
                JSONArray results = root.getJSONArray("results");


                for (int i=0;i<results.length();i++) {
                    JSONObject record = results.getJSONObject(i);
                    JSONObject geometry = record.getJSONObject("geometry");
                    JSONObject location = geometry.getJSONObject("location");
                    place_attibutes individual_place = new place_attibutes();


                        individual_place.latitude=location.getString("lat");
                        individual_place.longitude=location.getString("lng");
                        individual_place.name_of_place=record.getString("name");

                        if(!record.isNull("vicinity")){
                            individual_place.vicinity=record.getString("vicinity");
                        }


                        if(!record.isNull("rating")) {
                            individual_place.rating = record.getString("rating");
                        }

                        individual_place.type_of_place=type;



                    if(i<15) {
                        places_nearby.add(individual_place);
                    }

                }



            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        return places_nearby;

    }


    @Override
    protected void onPostExecute(ArrayList<place_attibutes> points) {
        super.onPostExecute(points);

        putnearbyMarkers.putmarkers(points);
    }

}
