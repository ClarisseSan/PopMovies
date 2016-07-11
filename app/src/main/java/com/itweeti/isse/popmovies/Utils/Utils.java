package com.itweeti.isse.popmovies.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.util.Base64;
import android.view.View;

import com.itweeti.isse.popmovies.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;


public class Utils {

    private static final String FAVORITES = "favorites" ;


    // Suppress default constructor for noninstantiability
    private Utils() {
        throw new AssertionError();
    }

    public static AlertDialog showSuccessDialog(final Context context, int title, int message) {
        // Creates a popup dialog
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(context);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            // Method below is the click handler for the YES button on the popup
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        return downloadDialog.show();
    }

    public static void saveFavoriteMovies(Context context, JSONObject item, View view) throws JSONException {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        JSONArray jsonArray = new JSONArray();
        String json = preferences.getString("favorites", null);

        if (json != null) {
            jsonArray = new JSONArray(json);
        }
        jsonArray.put(item);

        preferences.edit().putString(FAVORITES, jsonArray.toString()).commit();

        Snackbar.make(view, R.string.fav_add, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();


    }

    public static JSONArray getFavoriteMovies(Context context) throws JSONException {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String items = preferences.getString(FAVORITES, "");
        return new JSONArray(items);
    }


    public static void removeFromFavorites(Context context, String movieId, View view) throws JSONException {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        // Prepare the blank array
        JSONArray blank = new JSONArray();

        // Get the favorites from preferences
        String json = preferences.getString(FAVORITES, null);

        JSONArray lines = new JSONArray(json);
        for (int i = 0; i < lines.length(); i++) {
            // This is one favorites on the preferences
            JSONObject line = lines.getJSONObject(i);

            System.out.println("THIS IS A LINE---> " + line);

            String id = line.getString("movie_id");
            if (!id.equals(movieId)) {
                blank.put(line);
            }
        }


        // At this point, the blank array only contains the favorite movies lines we wanted.
        // Save it again on the preferences
        preferences.edit().putString("favorites", blank.toString()).apply();

        Snackbar.make(view, R.string.fav_remove, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    public static String convertImageToBase64(String poster) throws MalformedURLException {
        //convert image url into bitmap
        //then encode bitmap image to base64 string so we can save to sharedpreferences
        String encodedString = "";
        URL imageurl = new URL(poster);
        try {
            Bitmap bm = BitmapFactory.decodeStream(imageurl.openConnection().getInputStream());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
            byte[] bytes = baos.toByteArray();

            //encode to bitmap to base64
            encodedString = Base64.encodeToString(bytes, Base64.DEFAULT);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return encodedString;
    }


    public static Bitmap decodeBase64Image(String base64String) {
        byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        return decodedByte;
    }



}
