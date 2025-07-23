package com.example.proyectotextsmart;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    // declaracion de los txt
    TextView txt_registrar, txt_ol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Ajuste de insets para bordes seguros (EdgeToEdge)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Referencia al botón con id txt_registrar (que está definido como <Button> en XML)
        txt_registrar = findViewById(R.id.txt_registrar);
        // al hacer click en "Registrar" ir a RegistrarActivity
        txt_registrar.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Click detectado", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, RegistrarActivity.class);
            startActivity(intent);
        });

        // referencia al txt de olvid clave
        txt_ol = findViewById(R.id.txt_ol);
        // al hacer click en "olvide clave" ir a OlcontraActivity
        txt_ol.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this,"click detectado", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, OlcontraActivity.class);
            startActivity(intent);
        });
    }
}