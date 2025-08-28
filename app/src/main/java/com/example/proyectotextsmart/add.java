package com.example.proyectotextsmart;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class add extends AppCompatActivity {

    EditText txt_ape_nom, txt_tel, txt_imei, txt_mar, txt_deta, txt_tra, txt_pre;
    Button btn_re;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        txt_ape_nom = findViewById(R.id.txt_ape_nom);
        txt_tel = findViewById(R.id.txt_tel);
        txt_imei = findViewById(R.id.txt_imei);
        txt_mar = findViewById(R.id.txt_mar);
        txt_deta = findViewById(R.id.txt_deta);
        txt_tra = findViewById(R.id.txt_tra);
        txt_pre = findViewById(R.id.txt_pre);
        btn_re = findViewById(R.id.btn_re);

        btn_re.setOnClickListener(v -> {
            String ape_nom = txt_ape_nom.getText().toString().trim();
            String telefono = txt_tel.getText().toString().trim();
            String imei = txt_imei.getText().toString().trim();
            String marca = txt_mar.getText().toString().trim();
            String detalle = txt_deta.getText().toString().trim();
            String trabajo = txt_tra.getText().toString().trim();
            String presu = txt_pre.getText().toString().trim();

            if (ape_nom.isEmpty() || telefono.isEmpty() || imei.isEmpty() || marca.isEmpty()
                    || detalle.isEmpty() || trabajo.isEmpty() || presu.isEmpty()) {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            } else {
                new RegistroOrdenTask().execute(ape_nom, telefono, imei, marca, detalle, trabajo, presu);
            }
        });
    }

    private class RegistroOrdenTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL("http://192.168.0.22/conexion_mysql/insertcli.php"); // Cambia por tu ruta
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                String data = URLEncoder.encode("ape_nom", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8") + "&" +
                        URLEncoder.encode("telefono", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8") + "&" +
                        URLEncoder.encode("imei", "UTF-8") + "=" + URLEncoder.encode(params[2], "UTF-8") + "&" +
                        URLEncoder.encode("marca", "UTF-8") + "=" + URLEncoder.encode(params[3], "UTF-8") + "&" +
                        URLEncoder.encode("detalle", "UTF-8") + "=" + URLEncoder.encode(params[4], "UTF-8") + "&" +
                        URLEncoder.encode("trabajo", "UTF-8") + "=" + URLEncoder.encode(params[5], "UTF-8") + "&" +
                        URLEncoder.encode("presu", "UTF-8") + "=" + URLEncoder.encode(params[6], "UTF-8");

                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(data);
                writer.flush();
                writer.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                return result.toString();

            } catch (Exception e) {
                e.printStackTrace();
                return "Error de conexión";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(add.this, result, Toast.LENGTH_LONG).show();

            // Si fue exitosa la operación, limpiar campos
            if (result.contains("exitosa")) {
                txt_ape_nom.setText("");
                txt_tel.setText("");
                txt_imei.setText("");
                txt_mar.setText("");
                txt_deta.setText("");
                txt_tra.setText("");
                txt_pre.setText("");
            }
        }
    }
}