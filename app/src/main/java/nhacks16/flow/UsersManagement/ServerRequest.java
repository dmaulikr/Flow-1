package nhacks16.flow.UsersManagement;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import nhacks16.flow.R;


/**
 * Created by Owner on 2016-04-01.
 */

/*
public class ServerRequest {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout);
    }


}

public class JSONTask extends  AsyncTask<URL, String, String> {

    @Override
    protected String doInBackground(URL... params) {
        HttpURLConnection connection;
        BufferedReader reader;

        try

        {
            URL myURL = new URL('http://127.0.0.1:8080');
            connection = (HttpURLConnection) myURL.openConnection();
            connection.connect(); //Connects to server recieves input stream

            InputStream stream = connection.getInputStream();
            //Holds stream in stream object

            reader = new BufferedReader(new InputStreamReader((stream)));
            //Helps to read input stream line by line in text format

            String line = "";
            StringBuffer buffer = new StringBuffer();

            while ((line = reader.readLine()) != null){
                buffer.append(line);
            }

        }

        catch(
                MalformedURLException e
                )

        {
            e.printStackTrace();
        }

        catch(
                IOException e
                )

        {
            e.printStackTrace();
        }

        finally {
            if (connection != null) {
                connection.disconnect();
                //Closes connection
                //Otherwise nullpoint error
            }

            try {
                if (reader != null) {
                    reader.close();
                    //closes reader
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        myData.setText(result);
    }
} */

