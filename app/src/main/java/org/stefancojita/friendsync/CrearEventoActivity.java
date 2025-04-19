package org.stefancojita.friendsync;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CrearEventoActivity extends AppCompatActivity {

    private EditText etTitulo, etFecha, etLugar, etDescripcion;
    private Button btnGuardarEvento;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_evento);

        etTitulo = findViewById(R.id.etTitulo);
        etFecha = findViewById(R.id.etFecha);
        etLugar = findViewById(R.id.etLugar);
        etDescripcion = findViewById(R.id.etDescripcion);
        btnGuardarEvento = findViewById(R.id.btnGuardarEvento);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        btnGuardarEvento.setOnClickListener(v -> guardarEvento());
    }

    private void guardarEvento() {
        String titulo = etTitulo.getText().toString().trim();
        String fecha = etFecha.getText().toString().trim();
        String lugar = etLugar.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();

        if (titulo.isEmpty() || fecha.isEmpty() || lugar.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> evento = new HashMap<>();
        evento.put("titulo", titulo);
        evento.put("fecha", fecha);
        evento.put("lugar", lugar);
        evento.put("descripcion", descripcion);
        evento.put("uid_usuario", currentUser.getUid()); // para asociar evento al usuario

        db.collection("eventos")
                .add(evento)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Evento guardado con éxito", Toast.LENGTH_SHORT).show();
                    finish(); // volver a la pantalla anterior
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al guardar el evento", Toast.LENGTH_SHORT).show()
                );
    }
}
