package nhacks16.flow.Main;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Robert on 2016-06-05.
 */
public class FlowManagerUtil implements Parcelable{

    static final String COMPLEX_PREFS = "COMPLEX_PREFS";
    static final String USER_FLOWS = "USER_FLOWS";

    private static final String TAG = FlowManagerUtil.class.getName();
    private static final String fileName = "flows.json";
             // File name that data will be saved to.

    public static ArrayList<Flow> getFlowList() {
        return flowList;
    }

    private static ArrayList<Flow> flowList = new ArrayList<Flow>();
        /* Acts as an outer encasing List Object which wraps all the
           Flow objects inside. This allows the whole ArrayList to be
           instantiated rather than individual objects :)
         */

    /** Reads JSON data from file and builds it's flowList
     *  with the return data
      */
    public FlowManagerUtil(Context context){
        this.flowList = rebuildFlowArrayList(context);
    } // End of Constructor

    /** Updates the flowList with the updated list containing the
     *  most recently saved flow objects. Converts this Wrapper object containing
     *  the list of Flows to a JSON string and saves to internal storage by overwritting
     *  flows.json
     *
     * @param context the context in which the method is being called
     * @param updatedList the most recent ArrayList containing the past Flows and recent Flow to be saved
     */
    public static void saveFlowToFile(Context context, ArrayList<Flow> updatedList) {
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

        try {
            Log.d(TAG, "Writing Data to file....");
            outputStream= context.openFileOutput(fileName, Context.MODE_PRIVATE);
                    // Overwrites the data present in the File, MODE_PRIVATE
            outputStream.write(jsonData.getBytes());
            outputStream.close();
            Log.d(TAG, "SUCCESS! \n ~ " + loadJSONFlowListFromFile(context));

            ComplexPreferences cPreferences = ComplexPreferences.getComplexPreferences(context, COMPLEX_PREFS, context.MODE_PRIVATE);
            cPreferences.putObject(USER_FLOWS, flowList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Reads the flows.json file and converts the JSON to a String object
     *  which is passed as a return value.
     *
     * @param context the context in which the method is being called
     * @return jsonData the json String data read from file
     */
    public static String loadJSONFlowListFromFile(Context context) {
        try {
            FileInputStream fis = context.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            String json = sb.toString();
            return json;
        } catch (IOException e) {
            Log.e("TAG", "Error while reading: " + fileName + "\n" + e.getLocalizedMessage());
            return "";
        }
    }

    /** Deletes all the current Flows in the flows.json file by overwriting with a blank string
     *
     * @param context the context which the method is being called
     * @return boolean determines whether method was successful
     */
    public static boolean deleteFileData(Context context) {
        try {
            Log.d(TAG, "Delete all file data....");
            FileOutputStream os = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            PrintWriter writer = new PrintWriter(os);
            writer.print("");
            writer.close();
            flowList =null;
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Could not delete data.. \n ~ "+e.getMessage());
            return false;
        }
    }

    /** Using the json String data obtained from the loadJSONFlowListFromFile() method,
     *  instantiates a new ArrayList containing all the Flows via GSON to pass back
     *  to the context in which it is being called.
     *
     * @param context the context in which the method is being called
     * @return flowsFromFile returns the ArrayList which contains the flows saved in file
     */
    public static ArrayList<Flow> rebuildFlowArrayList(Context context) {
        Type FLOW_TYPE = new TypeToken<ArrayList<Flow>>() {
        }.getType();
        try {
            Log.d(TAG, "Attempting to regenerate Flows...");
            Gson gson = new Gson();

            String json = loadJSONFlowListFromFile(context);

            JsonReader reader = new JsonReader(new StringReader(json));
            reader.setLenient(true);
            //GSON throws that particular error when there's extra characters after the end of the object
            // that aren't whitespace, and it defines whitespace very narrowly

            ArrayList<Flow> flowsFromFile = gson.fromJson(reader, FLOW_TYPE);
            //Expected BEGIN_OBJECT but was BEGIN_ARRAY

            Log.d(TAG, "SUCCESS! \n~ " + flowsFromFile);
            return flowsFromFile;
            // Returns flows to use for lvContent
        } catch (Exception e) {
            Log.e(TAG, "Could not regenerate Flows... \n" + e.getMessage());
            return null;
        }

    }

    /** Receives the index value of the Flow to be updated in the
     *  master wrapper object. Overwrites this object in the FlowWrapper
     *  and saves the FlowWrapper as the object... acting as the "updatedList"
     *
     *
     * @param indexToUpdate index value of the Flow being overwrote
     * @param updatedFlow the Flow object that has been updated with New Elements
     * @param ctx context being called from
     */
    public static void overwriteFlow(int indexToUpdate, Flow updatedFlow, Context ctx) {
        try{
            Log.d(TAG, "Overwriting Flow...");
            flowList.set(indexToUpdate, updatedFlow);
        } catch (Exception e) {
            Log.e(TAG, "Error in overwriting Flow: " + e.getMessage());
        }

        saveFlowToFile(ctx, flowList);
    }

    // Parcel Implementation to pass data from the Stream to the Sandbox about
    // the current flow object.
    // Still need to make method to calculate the total time for the flow based on elements
    // The order of READING and WRITING is important (Read and write in same order)
    public FlowManagerUtil(Parcel in) {

        this.flowList = in.readArrayList(getClass().getClassLoader());

    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.flowList);
    }


    public static final Parcelable.Creator<FlowManagerUtil> CREATOR =
            new Parcelable.Creator<FlowManagerUtil>() {

                @Override
                public FlowManagerUtil createFromParcel(Parcel source) {
                    return new FlowManagerUtil(source);
                    //Using Parcelable constructor
                }

                @Override
                public FlowManagerUtil[] newArray(int size) {
                    return new FlowManagerUtil[size];
                }
            };


}
