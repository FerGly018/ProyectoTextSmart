package com.example.proyectotextsmart;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class InicioActivity extends AppCompatActivity {

    ImageButton btn_add, btn_search, btn_dispo;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inicio);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // boton para direccionar a la view agregar cliente
        btn_add = findViewById(R.id.btn_add);
        btn_add.setOnClickListener(v ->{
            Intent i = new Intent(InicioActivity.this, add.class);
            startActivity(i);
        });
        // boton para direccionar a la view  buscar clientes.
        btn_search = findViewById(R.id.btn_search);
        btn_search.setOnClickListener(v ->{
            Intent i = new Intent(InicioActivity.this, clientes.class);
            startActivity(i);
        });
        // boton para direccionar a la view estado del dispositivo.
        btn_dispo = findViewById(R.id.btn_dispo);
        btn_dispo.setOnClickListener(v -> {
            Intent i = new Intent(InicioActivity.this, estado.class);
            startActivity(i);
        });
    }
}