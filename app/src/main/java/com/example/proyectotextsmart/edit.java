package com.example.proyectotextsmart;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class edit extends AppCompatActivity {

    EditText editNombre, editTelefono, editIMEI, editMarcaModelo, editDetalleFalla, editTrabajoRealizar, editPresupuesto;
    Button btnGuardarCambios, btn_cancelar, btn_delete;
    int idCliente;
    private static final String TAG = "EDIT_CLIENTE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // Referencias UI
        editNombre = findViewById(R.id.txt_ape_nom);
        editTelefono = findViewById(R.id.txt_tel);
        editIMEI = findViewById(R.id.txt_imei);
        editMarcaModelo = findViewById(R.id.txt_mar);
        editDetalleFalla = findViewById(R.id.txt_deta);
        editTrabajoRealizar = findViewById(R.id.txt_tra);
        editPresupuesto = findViewById(R.id.txt_pre);
        btnGuardarCambios = findViewById(R.id.btn_edit);
        btn_cancelar = findViewById(R.id.btn_cancelar);
        btn_delete = findViewById(R.id.btn_delete);


        btn_delete.setOnClickListener(v -> {

            eliminarClientePorId(idCliente); // eliminar de BD
        });

        btn_cancelar.setOnClickListener(v ->{
            Intent i = new Intent(edit.this, clientes.class);
            startActivity(i);
        });

        // Obtener ID del cliente desde el Intent
        idCliente = getIntent().getIntExtra("id", -1);

        if (idCliente != -1) {
            cargarClienteDesdeServidor(idCliente);
        } else {
            Toast.makeText(this, "ID de cliente no recibido", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnGuardarCambios.setOnClickListener(v -> modificarCliente());
    }

    private void eliminarClientePorId(int idCliente) {
        Log.d("EliminarCliente", "Eliminando cliente con id_clientes: " + idCliente);
        new Thread(() -> {
            try {
                URL url = new URL("http://192.168.0.24/conexion_mysql/deletecliente.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);

                String data = URLEncoder.encode("id_clientes", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(idCliente), "UTF-8");

                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
                writer.write(data);
                writer.flush();
                writer.close();

                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();
                inputStream.close();

                String respuestaServidor = result.toString();
                Log.d("EliminarCliente", "Respuesta del servidor: " + respuestaServidor);

                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(edit.this, respuestaServidor, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(edit.this, clientes.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish(); // cierra la actividad actual
                });


            } catch (Exception e) {
                e.printStackTrace();
                Log.e("EliminarCliente", "Error al eliminar cliente: " + e.getMessage());
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(edit.this, "Error de conexión al eliminar cliente", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();

    }

    private void cargarClienteDesdeServidor(int idCliente) {
        new Thread(() -> {
            try {
                URL url = new URL("http://192.168.0.22/conexion_mysql/datoscliente.php?id_clientes=" + idCliente);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();

                JSONObject cliente = new JSONObject(result.toString());

                if (cliente.has("error")) {
                    runOnUiThread(() -> Toast.makeText(edit.this, "Cliente no encontrado", Toast.LENGTH_SHORT).show());
                } else {
                    runOnUiThread(() -> {
                        try {
                            editNombre.setText(cliente.getString("ape_nom"));
                            editTelefono.setText(cliente.getString("telefono"));
                            editIMEI.setText(cliente.getString("imei"));
                            editMarcaModelo.setText(cliente.getString("marca_modelo"));
                            editDetalleFalla.setText(cliente.getString("detalle_falla"));
                            editTrabajoRealizar.setText(cliente.getString("trabajo_realizar"));
                            editPresupuesto.setText(cliente.getString("presupuesto"));
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(edit.this, "Error al cargar los datos", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(edit.this, "Error de conexión", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void modificarCliente() {
        String apeNom = editNombre.getText().toString().trim();
        String telefono = editTelefono.getText().toString().trim();
        String imei = editIMEI.getText().toString().trim();
        String marcaModelo = editMarcaModelo.getText().toString().trim();
        String detalleFalla = editDetalleFalla.getText().toString().trim();
        String trabajoRealizar = editTrabajoRealizar.getText().toString().trim();
        String presupuesto = editPresupuesto.getText().toString().trim();

        new Thread(() -> {
            try {
                URL url = new URL("http://192.168.0.22/conexion_mysql/modificarcliente.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(idCliente), "UTF-8")
                        + "&" + URLEncoder.encode("ape_nom", "UTF-8") + "=" + URLEncoder.encode(apeNom, "UTF-8")
                        + "&" + URLEncoder.encode("telefono", "UTF-8") + "=" + URLEncoder.encode(telefono, "UTF-8")
                        + "&" + URLEncoder.encode("imei", "UTF-8") + "=" + URLEncoder.encode(imei, "UTF-8")
                        + "&" + URLEncoder.encode("marca_modelo", "UTF-8") + "=" + URLEncoder.encode(marcaModelo, "UTF-8")
                        + "&" + URLEncoder.encode("detalle_falla", "UTF-8") + "=" + URLEncoder.encode(detalleFalla, "UTF-8")
                        + "&" + URLEncoder.encode("trabajo_realizar", "UTF-8") + "=" + URLEncoder.encode(trabajoRealizar, "UTF-8")
                        + "&" + URLEncoder.encode("presupuesto", "UTF-8") + "=" + URLEncoder.encode(presupuesto, "UTF-8");

                Writer writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
                writer.write(data);
                writer.flush();
                writer.close();

                InputStream inputStream = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder resultado = new StringBuilder();
                String linea;
                while ((linea = reader.readLine()) != null) {
                    resultado.append(linea);
                }
                reader.close();

                String respuestaServidor = resultado.toString();
                Log.d(TAG, "Respuesta: " + respuestaServidor);

                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(edit.this, respuestaServidor, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(edit.this, clientes.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish(); // cierra la actividad actual
                });

            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Error: " + e.getMessage());
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(edit.this, "Error al modificar", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}

