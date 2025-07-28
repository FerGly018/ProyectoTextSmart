package com.example.proyectotextsmart;

import static android.app.ProgressDialog.show;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class RegistrarActivity extends AppCompatActivity {

    EditText txt_ape, txt_dni, txt_email, txt_clave, txt_repclave;
    Button btn_crear;
    TextView txt_terminos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txt_terminos = findViewById(R.id.txt_terminos);

        txt_terminos.setOnClickListener(v -> {
            Toast.makeText(RegistrarActivity.this, "Click detectado", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RegistrarActivity.this, TerminosActivity.class);
            startActivity(intent);
        });

        txt_ape = findViewById(R.id.txt_ape);
        txt_dni = findViewById(R.id.txt_dni);
        txt_email = findViewById(R.id.txt_email);
        txt_clave = findViewById(R.id.txt_clave);
        txt_repclave = findViewById(R.id.txt_repclave);
        btn_crear = findViewById(R.id.btn_crear);


        btn_crear.setOnClickListener(v -> {
            String nom_ape = txt_ape.getText().toString().trim();
            String dni = txt_dni.getText().toString().trim();
            String email = txt_email.getText().toString().trim();
            String clave = txt_clave.getText().toString().trim();
            String repclave = txt_repclave.getText().toString().trim();

            if (nom_ape.isEmpty() || dni.isEmpty() || email.isEmpty() || clave.isEmpty() || repclave.isEmpty()) {
                Toast.makeText(this, "Completa todo lo campos", Toast.LENGTH_SHORT).show();
            } else if (!clave.equals(repclave)) {
                Toast.makeText(this, "Las claves no coinciden", Toast.LENGTH_SHORT).show();
            } else {
                new RegistroTask().execute(nom_ape, dni, email, clave);
            }
        });
    }

    private class RegistroTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String nom_ape = params[0];
            String dni = params[1];
            String email = params[2];
            String clave = params[3];

            try {
                URL url = new URL("http://192.168.0.17/conexion_mysql/insertuser.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                String data = URLEncoder.encode("nom_ape", "UTF-8") + "=" + URLEncoder.encode(nom_ape, "UTF-8") + "&" +
                        URLEncoder.encode("dni", "UTF-8") + "=" + URLEncoder.encode(dni, "UTF-8") + "&" +
                        URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") + "&" +
                        URLEncoder.encode("clave", "UTF-8") + "=" + URLEncoder.encode(clave, "UTF-8");

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

            } catch (IOException e) {
                e.printStackTrace();
                return "Error de conexión";
            }
        }
        @Override
        protected void onPostExecute(String result) {
            if (result.contains("exitosa")) {
                Toast.makeText(RegistrarActivity.this, result, Toast.LENGTH_LONG).show();


                startActivity(new Intent(RegistrarActivity.this, InicioActivity.class));
                finish();
            } else {
                Toast.makeText(RegistrarActivity.this, result, Toast.LENGTH_LONG).show();
            }
        }
    }
}