package com.example.proyectotextsmart;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class estado extends AppCompatActivity {


    Spinner spinner;
    CheckBox checkBoxListo, checkBoxNoSePudo;
    Button btnNotificar;

    ArrayList<String> listaSpinner = new ArrayList<>();
    ArrayList<ClienteItem> clientesList = new ArrayList<>();

    String telefonoSeleccionado = "";
    int idSeleccionado = -1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estado);

        spinner = findViewById(R.id.mi_spinner);
        checkBoxListo = findViewById(R.id.checkBox);
        checkBoxNoSePudo = findViewById(R.id.checkBox2);
        btnNotificar = findViewById(R.id.btn_notificar); // Asegurate que el botón tenga este ID

        // Solo se puede seleccionar un checkbox
        checkBoxListo.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) checkBoxNoSePudo.setChecked(false);
        });

        checkBoxNoSePudo.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) checkBoxListo.setChecked(false);
        });

        btnNotificar.setOnClickListener(v -> notificarCliente());

        cargarClientesDesdeServidor();


    }

    private void cargarClientesDesdeServidor() {
        new Thread(() -> {
            try {
                URL url = new URL("http://192.168.0.24/conexion_mysql/obtenerdispo.php"); // Ajustá esta URL
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();

                JSONArray jsonArray = new JSONArray(result.toString());

                listaSpinner.add("Selecciona una opción");
                clientesList.add(null);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject cliente = jsonArray.getJSONObject(i);
                    int id = cliente.getInt("id_clientes");
                    String modelo = cliente.getString("marca_modelo");
                    String imei = cliente.getString("imei");
                    String telefono = cliente.getString("telefono");

                    String displayText = modelo + " - " + imei;
                    listaSpinner.add(displayText);
                    clientesList.add(new ClienteItem(id, modelo, imei, telefono));
                }

                runOnUiThread(() -> {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            estado.this,
                            R.layout.spinner_item, // Layout personalizado
                            listaSpinner
                    );
                    adapter.setDropDownViewResource(R.layout.spinner_item); // También para el dropdown
                    spinner.setAdapter(adapter);


                    spinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                            if (position == 0) {
                                telefonoSeleccionado = "";
                                idSeleccionado = -1;
                            } else {
                                ClienteItem seleccionado = clientesList.get(position);
                                telefonoSeleccionado = seleccionado.telefono;
                                idSeleccionado = seleccionado.id;
                            }
                        }

                        @Override
                        public void onNothingSelected(android.widget.AdapterView<?> parent) {
                            telefonoSeleccionado = "";
                            idSeleccionado = -1;
                        }
                    });
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(estado.this, "Error al obtener los datos", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void notificarCliente() {
        if (idSeleccionado == -1 || telefonoSeleccionado.isEmpty()) {
            Toast.makeText(this, "Selecciona un cliente válido", Toast.LENGTH_SHORT).show();
            return;
        }

        String mensaje;

        if (checkBoxListo.isChecked()) {
            mensaje = "Su dispositivo ya está listo para ser retirado.";
        } else if (checkBoxNoSePudo.isChecked()) {
            mensaje = "No se pudo reparar su dispositivo. Por favor, pase a retirarlo.";
        } else {
            Toast.makeText(this, "Selecciona un estado del dispositivo", Toast.LENGTH_SHORT).show();
            return;
        }

        // Simulación de envío
        Toast.makeText(this, "Mensaje enviado a " + telefonoSeleccionado + ":\n" + mensaje, Toast.LENGTH_LONG).show();

        // Aquí podrías agregar el código real de envío por API SMS o WhatsApp si lo tenés implementado.

        // Redirigir después de 3 segundos
        new android.os.Handler().postDelayed(() -> {
            Intent intent = new Intent(estado.this, InicioActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Opcional: cerrar esta pantalla
        }, 3000); // 3000 milisegundos = 3 segundos
    }

    // Clase interna para representar al cliente
    static class ClienteItem {
        int id;
        String modelo;
        String imei;
        String telefono;

        public ClienteItem(int id, String modelo, String imei, String telefono) {
            this.id = id;
            this.modelo = modelo;
            this.imei = imei;
            this.telefono = telefono;
        }
    }
}
