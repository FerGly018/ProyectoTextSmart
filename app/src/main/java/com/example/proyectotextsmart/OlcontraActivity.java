package com.example.proyectotextsmart;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class OlcontraActivity extends AppCompatActivity {

    EditText txtEmail, txtClave, txtRepetirClave;
    Button btnActualizar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_olcontra);

        txtEmail = findViewById(R.id.txt_em);
        txtClave = findViewById(R.id.txt_con);
        txtRepetirClave = findViewById(R.id.txt_rcon);
        btnActualizar = findViewById(R.id.btn_act);

        btnActualizar.setOnClickListener(v -> verificarYActualizarClave());
    }

    private void verificarYActualizarClave() {
        String email = txtEmail.getText().toString().trim();
        String clave = txtClave.getText().toString().trim();
        String repetirClave = txtRepetirClave.getText().toString().trim();

        if (email.isEmpty() || clave.isEmpty() || repetirClave.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!clave.equals(repetirClave)) {
            Toast.makeText(this, "Las claves no coinciden.", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                URL url = new URL("http://192.168.0.20/conexion_mysql/verificar_email.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                String postData = "email=" + email;
                OutputStream os = connection.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();

                Scanner in = new Scanner(connection.getInputStream());
                StringBuilder result = new StringBuilder();
                while (in.hasNextLine()) result.append(in.nextLine());
                in.close();

                JSONObject response = new JSONObject(result.toString());

                if (response.has("success")) {

                    runOnUiThread(() -> actualizarClave(email, clave));
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(this, "El correo no está registrado.", Toast.LENGTH_LONG).show()
                    );
                }

            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Error al verificar el correo.", Toast.LENGTH_SHORT).show()
                );
                e.printStackTrace();
            }
        }).start();
    }

    private void actualizarClave(String email, String clave) {
        new Thread(() -> {
            try {
                URL url = new URL("http://192.168.0.20/conexion_mysql/restablecer_clave.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                String data = "email=" + email + "& clave=" + clave;
                OutputStream os = conn.getOutputStream();
                os.write(data.getBytes());
                os.flush();
                os.close();

                Scanner sc = new Scanner(conn.getInputStream());
                StringBuilder response = new StringBuilder();
                while (sc.hasNextLine()) response.append(sc.nextLine());
                sc.close();

                JSONObject jsonResponse = new JSONObject(response.toString());

                if (jsonResponse.has("success")) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Clave actualizada con éxito.", Toast.LENGTH_LONG).show();
                        finish(); // cerrar esta Activity
                    });
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(this, "Error al actualizar la clave.", Toast.LENGTH_SHORT).show()
                    );
                }

            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Error de red al actualizar.", Toast.LENGTH_SHORT).show()
                );
                e.printStackTrace();
            }
        }).start();
    }
}
