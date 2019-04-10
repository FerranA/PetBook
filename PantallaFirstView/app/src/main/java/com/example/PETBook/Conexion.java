package com.example.PETBook;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Conexion extends AsyncTask<JSONObject,Void,JSONObject> {


    private URL url;
    private String Metodo;
    private JSONObject body;
    HttpURLConnection urlConnection;

    public Conexion (String URL, String tipoMetodo, JSONObject body){
        try {
            //url = new URL("http://localhost:9999/ServerRESTAPI/ConfirmLogin?email=" + user + "&password=" + pass);
            // EL hijo de puta del Android studio usa el localhost como su direccion, asi que hay que usar otra
            // Hay que usar la de tu red local. En mi caso es 192.168.1.12, de todos modos creo que basta con una ip local dentro de tu red privada que te assigina el router
            url = new URL(URL);
            Metodo = tipoMetodo;
            this.body = body;
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected JSONObject doInBackground(JSONObject... jsonObjects) {
        HttpURLConnection urlConnection = null;
        JSONObject result = new JSONObject();
        try {

            urlConnection = (HttpURLConnection) url
                    .openConnection();
            urlConnection.setRequestMethod(Metodo);



            urlConnection.setRequestProperty("Content-Type", "application/json");


          //  System.out.println("Arriba--------------------------------------------------------------------------------------------------------------------------------------------");
            URL u = urlConnection.getURL();
            System.out.println(u.toString());
            urlConnection.connect();    //
            //System.out.println("Aqui llego 0");


            if (this.body != null) {
                OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
                wr.write(body.toString());
                wr.flush();
            }

            //System.out.println("Aqui llego 1");

            Integer nume = urlConnection.getResponseCode();

            InputStream in = null;
            StringBuffer bf = new StringBuffer();
            in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader read = new BufferedReader(new InputStreamReader(in));
            String input = "";
            while ((input = read.readLine()) != null){
                bf.append(input);
            }
           /* System.out.println("Aqui llego 2");

            System.out.println(bf.toString());
            System.out.println("Length = "+bf.length());*/
            if(bf.length() != 0) {
                JSONObject inter = new JSONObject(bf.toString());
                result = inter;
            }
            result.put("code",nume);

        } catch (Exception e) {
            e.printStackTrace();
            result = new JSONObject();
            try {
                result.put("code", "404");
            } catch (Exception e1){
                e1.printStackTrace();
            }
            return result;

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return result;
    }



}