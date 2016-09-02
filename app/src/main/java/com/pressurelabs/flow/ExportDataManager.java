package com.pressurelabs.flow;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.supercsv.io.CsvListReader;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Flow_app
 *
 * @author Robert Simoes, 2016-09-01
 *         Copyright (c) 2016, Robert Simoes All rights reserved.
 */
public class ExportDataManager {


    private static final String statsFileName = "stats_export.csv";
    private static final String[] header = AppConstants.EXPORT_DATA_CSV_HEADER;
    private static File statsExportFile;
    private Context mContext;

    public ExportDataManager(Context ctx) {
        this.mContext = ctx;
        statsExportFile = new File(mContext.getFilesDir(), statsFileName);
    }

    public void saveStatistics(String[] runDataToWrite) {
        //TODO Clean up this File header stuff
        FileWriter mFileWriter;
        CsvListWriter listWriter = null;
        try {
            if(statsExportFile.exists() && !statsExportFile.isDirectory()){
                mFileWriter = new FileWriter(statsExportFile, true);
                listWriter = new CsvListWriter(mFileWriter,
                        CsvPreference.STANDARD_PREFERENCE);

            }
            else {
                listWriter = new CsvListWriter(new FileWriter(statsExportFile, true), CsvPreference.STANDARD_PREFERENCE);
            }
            // write the header
            listWriter.writeHeader(header);

            // write the customer lists
            listWriter.write(Arrays.asList(runDataToWrite));

        } catch (Exception e) {
            Log.e("ERROR", "Could not write data to file: " + e.getMessage());
        } finally {
            try {
                if( listWriter != null ) {
                    listWriter.close();
                }
            } catch (Exception e) {
                Log.e("ER:", "Couldn't close writer");
            }

        }
    }

    private List<List<String>> readCSV(File statsExportFile) {
        CsvListReader listReader = null;
        List<List<String>> data = new LinkedList<>();
        try {
            listReader = new CsvListReader(new FileReader(statsExportFile), CsvPreference.STANDARD_PREFERENCE);

            listReader.getHeader(true); // skip the header (can't be used with CsvListReader)

            List<String> line;
            while( (line = listReader.read()) != null ) {
                Log.d("READING", line.toString());
                data.add(line);
            }

        } catch (Exception e) {
            Log.e("ER", e.getMessage());
        }
        finally {
                try {
                    if( listReader != null ) {
                        listReader.close();
                    }
                } catch (Exception e) {
                    Log.e("ER", "COULDNT CLOSE READER" + e.getMessage());
                }

        }
        return data;
    }

    public String readPureFile() {
        try {

            FileInputStream fis = mContext.openFileInput(statsFileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            String loadedData = sb.toString();
            fis.close();
            isr.close();
            bufferedReader.close();
            return loadedData;
        } catch (IOException e) {
            Log.e("ERROR", e.getMessage());
            return "";
            // Returns blank if no flow file exists
        }
    }

    /**
     * Overwrites all data present in file with a blank file.
     *
     */
    private void eraseData() {
        try {
            FileOutputStream os = mContext.openFileOutput(statsFileName, Context.MODE_PRIVATE);
            PrintWriter writer = new PrintWriter(os);
            writer.print("");
            writer.close();

        } catch (Exception e) {
            Toast.makeText(mContext, "Could not delete stats deleted!", Toast.LENGTH_LONG);
        }
    }

    public void deleteAllStats() {
        eraseData();
    }

    @Override
    public String toString() {
        return "\n~~~ CSV STATS EXPORT ~~~" + this.readCSV(statsExportFile);
    }
}

