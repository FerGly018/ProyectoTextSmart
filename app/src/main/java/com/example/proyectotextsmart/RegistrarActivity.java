package com.example.proyectotextsmart;

import static android.app.ProgressDialog.show;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegistrarActivity extends AppCompatActivity {

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

        txt_terminos.setOnClickListener(v ->{
            Toast.makeText(RegistrarActivity.this, "Click detectado", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RegistrarActivity.this, TerminosActivity.class);
            startActivity(intent);
        });

    }
}