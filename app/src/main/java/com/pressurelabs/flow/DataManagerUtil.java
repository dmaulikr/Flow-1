package com.pressurelabs.flow;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
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

/**
 * Flow_V2
 *
 * @author Robert Simoes, 2016-07-09
 *         Copyright (c) 2016, Robert Simoes All rights reserved.
 *
 * DataManagerUtil is a utility that saves and all Flows to a compact ArrayList which is
 * converted to JSON and saved to plain text in internal storage.
 */
public class DataManagerUtil implements Parcelable{

    private static final String COMPLEX_PREFS = "COMPLEX_PREFS";
    private static final String USER_FLOWS = "USER_FLOWS";

    private static final String flowListFileName = "userflows.json";
    private static File file = new File(flowListFileName);
//
//    private static final String idMapFileName = "userflowsidmap.txt";
//    private static File idFile = new File(idMapFileName);

    /* MUST BE STATIC & GLOBAL Creates file directory for the data files please don't delete again :))) */

    private static ArrayList<Flow> flowList;
        /* Acts as an outer encasing List Object which wraps all the
           Flow objects inside. This allows the whole ArrayList to be
           instantiated rather than individual objects :)
         */

//    private static Map<String,Flow> idMap = new HashMap<>();

    /** Reads JSON data from file and builds it's flowList
     *  with the return data
      */
    public DataManagerUtil(Context context){

        flowList = buildFlowArrayList(context);
    } // End of Constructor

    public static ArrayList<Flow> getFlowList() {
        return flowList;
    }


    /** Updates the flowList with the updated list containing the
     *  most recently saved flow objects. Converts this Wrapper object containing
     *  the list of Flows to a JSON string and saves to internal storage by overwritting
     *  flows.json
     *
     * @param context the context in which the method is being called
     * @param updatedList the most recent ArrayList containing the past Flows and recent Flow to be saved
     */
    public static void saveToFile(Context context, ArrayList<Flow> updatedList) {
            // Save an Array inside an ArrayList to string:


        flowList =null;
        flowList = updatedList;
            // Creates reference to the updated LV Content ArrayList
           // Then is used as an outer JSON Object Wrapper

        Gson gson = new Gson();

        String jsonData = gson.toJson(flowList);
            // Converts the inputted ArrayList Objection (Containing new Flow)
            // to JSON format in String form

        FileOutputStream outputStream;

        /* Saving ArrayList to file */
        try {
            outputStream= context.openFileOutput(flowListFileName, Context.MODE_PRIVATE);
                    // Overwrites the data present in the File, MODE_PRIVATE
            outputStream.write(jsonData.getBytes());
            outputStream.close();

            ComplexPreferences cPreferences = ComplexPreferences.getComplexPreferences(context, COMPLEX_PREFS, Context.MODE_PRIVATE);
            cPreferences.putObject(USER_FLOWS, flowList);

        } catch (Exception e) {
            Toast.makeText(context, "Could not save Flow to file", Toast.LENGTH_LONG).show();
        }

        /* Saving Id Map to file */

    }


    /** Reads the flows.json file and converts the JSON to a String object
     *  which is passed as a return value.
     *
     * @param context the context in which the method is being called
     * @return jsonData the json String data read from file
     */
    private static String loadFlowListFromFile(Context context) {
        try {

            FileInputStream fis = context.openFileInput(flowListFileName);
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
        } catch (IOException e){
            return "";
            // Returns blank if no flow file exists
        }


    }

//    private static String loadIdMapFromFile(Context context) {
//        try {
//
//            FileInputStream fis = context.openFileInput(idMapFileName);
//            InputStreamReader isr = new InputStreamReader(fis);
//            BufferedReader bufferedReader = new BufferedReader(isr);
//            StringBuilder sb = new StringBuilder();
//            String line;
//
//            while ((line = bufferedReader.readLine()) != null) {
//                sb.append(line);
//            }
//
//            String json = sb.toString();
//            fis.close();
//            isr.close();
//            bufferedReader.close();
//            return json;
//        } catch (IOException e){
//            return "";
//            // Returns blank if no flow file exists
//        }

    /** Deletes all the current Flows in the flows.json file by overwriting with a blank string
     *
     * @param context the context which the method is being called
     * @return boolean determines whether method was successful
     */
    public static void eraseFileData(Context context) {
        try {
            FileOutputStream os = context.openFileOutput(flowListFileName, Context.MODE_PRIVATE);
            PrintWriter writer = new PrintWriter(os);
            writer.print("");
            writer.close();
            flowList = new ArrayList<>();
                // Consider changing to removeAll?
        } catch (Exception e) {
            Toast.makeText(context, "Flows could not be deleted!", Toast.LENGTH_LONG);
        }
    }

    public static void delete(Flow target, Context ctx) {
        flowList.remove(target);
        saveToFile(ctx,flowList);

    }

    /** Using the json String data obtained from the loadFlowListFromFile() method,
     *  instantiates a new ArrayList containing all the Flows via GSON to pass back
     *  to the context in which it is being called.
     *
     * @param context the context in which the method is being called
     * @return flowsFromFile returns the ArrayList which contains the flows saved in file
     */
    private static ArrayList<Flow> buildFlowArrayList(Context context) {
        Type FLOW_TYPE = new TypeToken<ArrayList<Flow>>() {
        }.getType();
        try {
            Gson gson = new Gson();

            String json = loadFlowListFromFile(context);

            if (json.equals("")) {
                // if return data is blank, no file was found (exists)
                return new ArrayList<>();
            }

            JsonReader reader = new JsonReader(new StringReader(json));
            reader.setLenient(true);
            //GSON throws that particular error when there's extra characters after the end of the object
            // that aren't whitespace, and it defines whitespace very narrowly

            //Expected BEGIN_OBJECT but was BEGIN_ARRAY

            return gson.fromJson(reader, FLOW_TYPE);
            // Returns flows to use for lvContent
        } catch (Exception e) {
            // Flow File not regenerated that sucks..
            return  new ArrayList<>();
        }

    }

    /** Receives the index value of the Flow to be updated in the
     *  master wrapper object. Overwrites this object in the FlowWrapper
     *  and saves the FlowWrapper as the object... acting as the "updatedList"
     *
     *
     * @param updatedFlow the Flow object that has been updated with New Elements
     * @param ctx context being called from
     */
    public static void overwriteFlow(int indexToUpdate, Flow updatedFlow, Context ctx) {
            //Overwrites Flow at index in file
        flowList.set(
                indexToUpdate,
                updatedFlow);

        //TODO Because the flowList locations can now change dynamically, the flowManagerIndex from the Flow's becomes outdated when a single Flow is deleted

        saveToFile(ctx, flowList);
    }

    // Parcel Implementation to pass data from the Stream to the Sandbox about
    // the current flow object.
    // Still need to make method to calculate the total time for the flow based on elements
    // The order of READING and WRITING is important (Read and write in same order)
    public DataManagerUtil(Parcel in) {

        flowList = in.readArrayList(getClass().getClassLoader());

    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(flowList);
    }


    public static final Parcelable.Creator<DataManagerUtil> CREATOR =
            new Parcelable.Creator<DataManagerUtil>() {

                @Override
                public DataManagerUtil createFromParcel(Parcel source) {
                    return new DataManagerUtil(source);
                    //Using Parcelable constructor
                }

                @Override
                public DataManagerUtil[] newArray(int size) {
                    return new DataManagerUtil[size];
                }
            };

}
