package ro.pub.cs.systems.pdsd.practicaltest02.network;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;
import ro.pub.cs.systems.pdsd.practicaltest02.general.Constants;
import ro.pub.cs.systems.pdsd.practicaltest02.general.Utilities;
import ro.pub.cs.systems.pdsd.practicaltest02.model.Result;

public class CommunicationThread extends Thread {

    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            BufferedReader bufferedReader = null;
            try {
                bufferedReader = Utilities.getReader(socket);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            PrintWriter printWriter = null;
            try {
                printWriter = Utilities.getWriter(socket);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            //Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (word!");
            String word = null;
            try {
                word = bufferedReader.readLine();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            String informationType = null;
            try {
                informationType = bufferedReader.readLine();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            if (word == null || word.isEmpty() || informationType == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (word");
                return;
            }
            HashMap<String, Result> data = serverThread.getData();
            Result WordInformation = null;
            if (data.containsKey(word)) {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the cache...");
                WordInformation = data.get(word);
            } else {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");
                HttpClient httpClient = new DefaultHttpClient();
                String pageSourceCode = "";
                HttpGet httpGet = new HttpGet(Constants.WEB_SERVICE_API_KEY + word);
                HttpResponse httpGetResponse = httpClient.execute(httpGet);
                HttpEntity httpGetEntity = httpGetResponse.getEntity();
                if (httpGetEntity != null) {
                    try {
                        pageSourceCode = EntityUtils.toString(httpGetEntity);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    try {
                        JSONObject content = new JSONObject(pageSourceCode);
                    } catch (JSONException jsonException) {
                        jsonException.printStackTrace();
                    }
                }
                if (pageSourceCode == null) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                    return;
                } else
                    Log.i(Constants.TAG, pageSourceCode);
                    WordInformation = new Result(
                            pageSourceCode
                         );
                    serverThread.setData(word, WordInformation);
                    //break;
                }} catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }}}
