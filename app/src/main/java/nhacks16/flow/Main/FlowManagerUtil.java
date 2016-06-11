package nhacks16.flow.Main;

import android.content.Context;
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
public class FlowManagerUtil {

    private static final String TAG = FlowManagerUtil.class.getName();
    private static final String fileName = "flows.json";
             // File name that data will be saved to.

    private static ArrayList<Flow> JSONFlowWrapper;
        /* Acts as an outer encasing List Object which wraps all the
           Flow objects inside. This allows the whole ArrayList to be
           instantiated rather than individual objects :)
         */

    /** Basic Constructor
     *
      */
    public FlowManagerUtil(){
        this.JSONFlowWrapper = new ArrayList<Flow>();
    } // End of Constructor

    /** Updates the JSONFlowWrapper with the updated list containing the
     *  most recently saved flow objects. Converts this Wrapper object containing
     *  the list of Flows to a JSON string and saves to internal storage by overwritting
     *  flows.json
     *
     * @param context the context in which the method is being called
     * @param updatedList the most recent ArrayList containing the past Flows and recent Flow to be saved
     */
    public void saveFlowDataInternal(Context context, ArrayList<Flow> updatedList) {

        JSONFlowWrapper =null;
        JSONFlowWrapper = updatedList;
            // Creates reference to the updated LV Content ArrayList
           // Then is used as an outer JSON Object Wrapper

        Gson gson = new Gson();

        String jsonData = gson.toJson(JSONFlowWrapper);
            // Converts the inputted ArrayList Objection (Containing new Flow)
            // to JSON format in String form

        FileOutputStream outputStream;

        try {
            Log.d(TAG, "Writing Data to file....");
            outputStream= context.openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(jsonData.getBytes());
            outputStream.close();
            Log.d(TAG, "SUCCESS! \n ~ " + loadFlowDataInternal(context));

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
    public static String loadFlowDataInternal(Context context) {
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
            return null;
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
            JSONFlowWrapper =null;
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Could not delete data.. \n ~ "+e.getMessage());
            return false;
        }
    }

    /** Using the json String data obtained from the loadFlowDataInternal() method,
     *  instantiates a new ArrayList containing all the Flows via GSON to pass back
     *  to the context in which it is being called.
     *
     * @param context the context in which the method is being called
     * @return flowsFromFile returns the ArrayList which contains the flows saved in file
     */
    public static ArrayList<Flow> rebuildFlowArray(Context context) {
        Type FLOW_TYPE = new TypeToken<ArrayList<Flow>>() {}.getType();
        try {
            Log.d(TAG, "Attempting to regenerate Flows...");
            Gson gson = new Gson();

            String json = loadFlowDataInternal(context);

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
            Log.e(TAG, "Could not regenerate Flows... \n" +e.getMessage());
            return null;
        }

    }
}
