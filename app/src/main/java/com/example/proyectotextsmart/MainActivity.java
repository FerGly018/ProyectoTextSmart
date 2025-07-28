package com.example.proyectotextsmart;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    // Elementos UI con nuevos IDs
    Button btn_iniciar;
    EditText txt_email, txt_clave;
    TextView txt_ol, txt_registrar;

    ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        executorService = Executors.newSingleThreadExecutor();

        // Referencias con los nuevos IDs
        txt_email = findViewById(R.id.txt_email);
        txt_clave = findViewById(R.id.txt_clave);
        btn_iniciar = findViewById(R.id.btn_iniciar);
        txt_ol = findViewById(R.id.txt_ol);
        txt_registrar = findViewById(R.id.txt_registrar);

        // Click para registrar (asumiendo que la actividad sigue llamándose registrar.class)
        txt_registrar.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, RegistrarActivity.class);
            startActivity(i);
        });

        // Click para iniciar sesión
        btn_iniciar.setOnClickListener(v -> {
            Log.d("LOGIN_TASK", "btn_iniciar clickeado");
            iniciarSesion();
        });

        // Click en "Olvide la clave" (asumiendo que la actividad sigue siendo olvicontra.class)
        txt_ol.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, OlcontraActivity.class);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    private void iniciarSesion() {

        Log.d("LOGIN_TASK", "iniciarSesion llamado");

        String email = txt_email.getText().toString().trim();
        String clave = txt_clave.getText().toString().trim();

        if (email.isEmpty()) {
            txt_email.setError("Ingrese su correo");
            return;
        }
        if (clave.isEmpty()) {
            txt_clave.setError("Ingrese su contraseña");
            return;
        }

        executorService.execute(() -> loginTask(email, clave));
    }

    private void loginTask(String email, String clave) {

        Log.d("LOGIN_TASK", "Ejecutando loginTask con email=" + email);

        try {
            URL url = new URL("http://192.168.0.17/conexion_mysql/login.php");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);

            String data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") + "&" +
                    URLEncoder.encode("clave", "UTF-8") + "=" + URLEncoder.encode(clave, "UTF-8");

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
            new Handler(Looper.getMainLooper()).post(() ->
                    Toast.makeText(MainActivity.this, "Respuesta del servidor: " + respuestaServidor, Toast.LENGTH_SHORT).show());

            new Handler(Looper.getMainLooper()).post(() -> onPostExecute(respuestaServidor));
        } catch (Exception e) {
            Log.e("LOGIN_TASK", "Error en loginTask", e);
            e.printStackTrace();
            new Handler(Looper.getMainLooper()).post(() -> onPostExecute(null));
        }
    }

    private void onPostExecute(String result) {
        if (result != null && result.contains("Login exitoso")) {
            Toast.makeText(MainActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, InicioActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(MainActivity.this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
