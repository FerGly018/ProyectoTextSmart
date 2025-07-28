package com.example.proyectotextsmart;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class clientes extends AppCompatActivity {

    LinearLayout contenedorClientes;
    SearchView buscador;
    JSONArray listaClientes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_clientes);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        buscador = findViewById(R.id.search_view);
        contenedorClientes = findViewById(R.id.main).findViewById(R.id.contenedor_clientes);

        new ObtenerClientes().execute();

        buscador.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filtrarClientes(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filtrarClientes(newText);
                return true;
            }
        });
    }

    private void filtrarClientes(String texto) {
        if (listaClientes == null) return;

        contenedorClientes.removeAllViews();

        for (int i = 0; i < listaClientes.length(); i++) {
            try {
                JSONObject cliente = listaClientes.getJSONObject(i);
                String nombre = cliente.getString("ape_nom");

                if (nombre.toLowerCase().contains(texto.toLowerCase())) {
                    agregarClienteVista(cliente);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class ObtenerClientes extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            String urlWebService = "http://192.168.0.17/conexion_mysql/obtenercliente.php";

            try {
                URL url = new URL(urlWebService);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String linea;
                while ((linea = bufferedReader.readLine()) != null) {
                    sb.append(linea);
                }
                return sb.toString();
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                Toast.makeText(clientes.this, "Error al conectar con el servidor", Toast.LENGTH_LONG).show();
                return;
            }

            try {
                listaClientes = new JSONArray(result);
                contenedorClientes.removeAllViews();

                for (int i = 0; i < listaClientes.length(); i++) {
                    JSONObject cliente = listaClientes.getJSONObject(i);
                    agregarClienteVista(cliente);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(clientes.this, "Error al procesar datos", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void agregarClienteVista(JSONObject cliente) {
        try {
            LayoutInflater inflater = LayoutInflater.from(this);
            View itemView = inflater.inflate(R.layout.datos_clientes, contenedorClientes, false);

            TextView txtCliente = itemView.findViewById(R.id.txt_cliente);
            TextView txtMarca = itemView.findViewById(R.id.txt_marca);
            TextView txtTrabajo = itemView.findViewById(R.id.txt_trabajo);
            TextView txtPresupuesto = itemView.findViewById(R.id.txt_presupuesto);

            ImageButton btnDelete = itemView.findViewById(R.id.btn_delete);
            ImageButton btnEdit = itemView.findViewById(R.id.btn_edit);


            txtCliente.setText(cliente.getString("ape_nom"));
            txtMarca.setText(cliente.getString("marca_modelo"));
            txtTrabajo.setText(cliente.getString("trabajo_realizar"));
            txtPresupuesto.setText("$ " + cliente.getString("presupuesto"));

            // Obtener el ID del cliente
            int idCliente = cliente.getInt("id_clientes");

            btnDelete.setOnClickListener(v -> {
                contenedorClientes.removeView(itemView); // quitar visualmente
                eliminarClientePorId(idCliente); // eliminar de BD
            });

            btnEdit.setOnClickListener(v -> {
                Intent intent = new Intent(clientes.this, edit.class);
                intent.putExtra("id", idCliente);  // Pasamos solo el ID
                startActivity(intent);
            });

            contenedorClientes.addView(itemView);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void eliminarClientePorId(int idCliente) {
        Log.d("EliminarCliente", "Eliminando cliente con id_clientes: " + idCliente);
        new Thread(() -> {
            try {
                URL url = new URL("http://192.168.0.17/conexion_mysql/deletecliente.php");
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

                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(clientes.this, respuestaServidor, Toast.LENGTH_SHORT).show()
                );

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("EliminarCliente", "Error al eliminar cliente: " + e.getMessage());
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(clientes.this, "Error de conexión al eliminar cliente", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }
}
