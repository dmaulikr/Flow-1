package com.pressurelabs.flow;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Flow_V2
 *
 * @author Robert Simoes, 2016-07-09
 *         Copyright (c) 2016, Robert Simoes All rights reserved.
 *
 * AppDataManager is a utility that saves and all Flows to a compact ArrayList which is
 * converted to JSON and saved to plain text in internal storage.
 */
public class AppDataManager implements Parcelable{


    private static final String mapFileName = "appdata.json";
    private static File mapFile = new File(mapFileName);

    /* MUST BE STATIC & GLOBAL Creates file directory for the data files please don't delete again :))) */
    private static HashMap<String,Flow> dataMap;
    private static Context mContext;

    /** Reads JSON data from file and builds it's flowList
     *  with the return data
      */
    public AppDataManager(Context context){
        this.mContext = context;
        dataMap = buildMap(mContext);
    } // End of Constructor

    public void save(String keyToSave, Flow objectToSave) {
            dataMap.put(keyToSave,objectToSave);
            saveFile();
    }

    public Flow load(String keyToLoad) {
        return dataMap.get(keyToLoad);
    }

    public void overwrite(String keyToOverwrite, Flow valueToWrite) {
        dataMap.put(keyToOverwrite, valueToWrite);
        saveFile();
    }

    public void delete(String keyToDelete) {
        dataMap.remove(keyToDelete);
        saveFile();
    }

    public void deleteAll() {
        dataMap.clear();
        eraseFileData();
    }

    public ArrayList<Flow> generateArrayList(){
        return new ArrayList<>(dataMap.values());
    }

    public boolean hasData() {
        return !dataMap.isEmpty();
    }

    private void saveFile() {

        Gson gson = new Gson();

        String jsonData = gson.toJson(dataMap);

        FileOutputStream outputStream;

        try {
            outputStream= mContext.openFileOutput(mapFileName, Context.MODE_PRIVATE);
            outputStream.write(jsonData.getBytes());
            outputStream.close();

        } catch (Exception e) {
            Log.e("ERROR:", "\n " +e.getMessage());

            // Toast.makeText(context, "Could not save file", Toast.LENGTH_LONG).show();
        }


    }


    private String loadFile() {
        try {

            FileInputStream fis = mContext.openFileInput(mapFileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            String json = sb.toString();
            fis.close();
            isr.close();
            bufferedReader.close();
            return json;
        } catch (IOException e) {
            Log.e("ERROR", e.getMessage());
            return "";
            // Returns blank if no flow file exists
        }
    }


    private void eraseFileData() {
        try {
            FileOutputStream os = mContext.openFileOutput(mapFileName, Context.MODE_PRIVATE);
            PrintWriter writer = new PrintWriter(os);
            writer.print("");
            writer.close();

        } catch (Exception e) {
            Toast.makeText(mContext, "Flows could not be deleted!", Toast.LENGTH_LONG);
        }
    }

    /**
     *
     * @param context the context in which the method is being called
     */
    private HashMap<String,Flow> buildMap(Context context) {
        Type type = new TypeToken<HashMap<String, Flow>>() {
        }.getType();

        try {
            Gson gson = new Gson();

            String json = loadFile();

            if (json.equals("")) {
                // if return data is blank, no file was found (exists)
                return new HashMap<>();
            }

            JsonReader reader = new JsonReader(new StringReader(json));
            reader.setLenient(true);
            //GSON throws that particular error when there's extra characters after the end of the object
            // that aren't whitespace, and it defines whitespace very narrowly

            //Expected BEGIN_OBJECT but was BEGIN_ARRAY

            return  gson.fromJson(reader, type);
            // Returns flows to use for Map Format
        } catch (Exception e) {
            // Flow loadFile not regenerated that sucks..
            return  new HashMap<>();
        }

    }


    // Parcel Implementation to pass data from the Stream to the Sandbox about
    // the current flow object.
    // Still need to make method to calculate the total time for the flow based on elements
    // The order of READING and WRITING is important (Read and write in same order)

    public AppDataManager(Parcel in) {
        dataMap = in.readHashMap(getClass().getClassLoader());

    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeMap(dataMap);
    }


    public static final Parcelable.Creator<AppDataManager> CREATOR =
            new Parcelable.Creator<AppDataManager>() {

                @Override
                public AppDataManager createFromParcel(Parcel source) {
                    return new AppDataManager(source);
                    //Using Parcelable constructor
                }

                @Override
                public AppDataManager[] newArray(int size) {
                    return new AppDataManager[size];
                }
            };

}
