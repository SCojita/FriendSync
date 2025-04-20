package org.stefancojita.friendsync;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class DetalleEventoActivity extends AppCompatActivity {

    private TextView tvTitulo, tvFecha, tvLugar, tvDescripcion;
    private Button btnUnirse;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private String eventoId;
    private List<String> asistentes = new ArrayList<>();
    private String creatorUid;
    private RecyclerView recyclerAsistentes;
    private AsistenteAdapter asistenteAdapter;
    private List<Asistente> listaAsistentes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_evento);

        tvTitulo = findViewById(R.id.tvTitulo);
        tvFecha = findViewById(R.id.tvFecha);
        tvLugar = findViewById(R.id.tvLugar);
        tvDescripcion = findViewById(R.id.tvDescripcion);
        btnUnirse = findViewById(R.id.btnUnirse);
        recyclerAsistentes = findViewById(R.id.recyclerAsistentes);
        recyclerAsistentes.setLayoutManager(new LinearLayoutManager(this));
        asistenteAdapter = new AsistenteAdapter(listaAsistentes);
        recyclerAsistentes.setAdapter(asistenteAdapter);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Obtener ID del evento desde el intent
        eventoId = getIntent().getStringExtra("eventoId");

        if (eventoId != null) {
            cargarDetallesEvento();
        }

        btnUnirse.setOnClickListener(v -> unirseAlEvento());
    }

    private void cargarDetallesEvento() {
        db.collection("eventos").document(eventoId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        tvTitulo.setText(document.getString("titulo"));
                        tvFecha.setText("Fecha: " + document.getString("fecha"));
                        tvLugar.setText("Lugar: " + document.getString("lugar"));
                        tvDescripcion.setText("Descripción: " + document.getString("descripcion"));

                        creatorUid = document.getString("uid_usuario");
                        asistentes = (List<String>) document.get("asistentes");

                        if (creatorUid != null && creatorUid.equals(currentUser.getUid())) {
                            btnUnirse.setVisibility(View.GONE); // no mostrar botón si eres el creador
                        } else if (asistentes != null && asistentes.contains(currentUser.getUid())) {
                            btnUnirse.setEnabled(false);
                            btnUnirse.setText("Ya estás unido");
                        }
                    }
                    asistentes = (List<String>) document.get("asistentes");

                    if (asistentes != null && !asistentes.isEmpty()) {
                        listaAsistentes.clear();
                        for (String uid : asistentes) {
                            db.collection("users") // tu colección de usuarios
                                    .document(uid)
                                    .get()
                                    .addOnSuccessListener(userDoc -> {
                                        String email = userDoc.getString("correo"); // o "email", depende de tu estructura
                                        listaAsistentes.add(new Asistente(uid, email != null ? email : uid));
                                        asistenteAdapter.notifyDataSetChanged();
                                    });
                        }
                    }
                });
    }

    private void unirseAlEvento() {
        if (asistentes == null) asistentes = new ArrayList<>();
        if (!asistentes.contains(currentUser.getUid())) {
            asistentes.add(currentUser.getUid());

            db.collection("eventos").document(eventoId)
                    .update("asistentes", asistentes)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Te has unido al evento", Toast.LENGTH_SHORT).show();
                        btnUnirse.setEnabled(false);
                        btnUnirse.setText("Ya estás unido");
                    });
        }
    }
}
