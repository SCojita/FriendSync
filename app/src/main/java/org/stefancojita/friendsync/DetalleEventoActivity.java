package org.stefancojita.friendsync;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class DetalleEventoActivity extends AppCompatActivity {

    private TextView tvTitulo, tvFecha, tvLugar, tvDescripcion, tvCreadorEvento, tvSinAsistentes;
    private Button btnUnirse, btnEliminarEvento, btnEditarEvento, btnGastos;
    private RecyclerView recyclerAsistentes;
    private AsistenteAdapter asistenteAdapter;
    private List<Asistente> listaAsistentes;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    private String eventoId;
    private String creatorUid;
    private List<String> asistentes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_evento);

        tvTitulo = findViewById(R.id.tvTitulo);
        tvFecha = findViewById(R.id.tvFecha);
        tvLugar = findViewById(R.id.tvLugar);
        tvDescripcion = findViewById(R.id.tvDescripcion);
        tvCreadorEvento = findViewById(R.id.tvCreadorEvento);
        tvSinAsistentes = findViewById(R.id.tvSinAsistentes);

        btnUnirse = findViewById(R.id.btnUnirse);
        btnEliminarEvento = findViewById(R.id.btnEliminarEvento);
        btnEditarEvento = findViewById(R.id.btnEditarEvento);
        btnGastos = findViewById(R.id.btnGastos);

        recyclerAsistentes = findViewById(R.id.recyclerAsistentes);
        recyclerAsistentes.setLayoutManager(new LinearLayoutManager(this));
        listaAsistentes = new ArrayList<>();
        asistenteAdapter = new AsistenteAdapter(listaAsistentes);
        recyclerAsistentes.setAdapter(asistenteAdapter);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        eventoId = getIntent().getStringExtra("eventoId");

        cargarDetallesEvento();

        btnEliminarEvento.setOnClickListener(v -> eliminarEvento());

        btnEditarEvento.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditarEventoActivity.class);
            intent.putExtra("eventoId", eventoId);
            startActivity(intent);
        });
    }

    private void cargarDetallesEvento() {
        btnEliminarEvento.setVisibility(View.GONE);
        btnEditarEvento.setVisibility(View.GONE);
        db.collection("eventos").document(eventoId)
                .addSnapshotListener((documentSnapshot, error) -> {
                    if (error != null || documentSnapshot == null || !documentSnapshot.exists()) return;

                    tvTitulo.setText(documentSnapshot.getString("titulo"));
                    tvFecha.setText("Fecha: " + documentSnapshot.getString("fecha"));
                    tvLugar.setText("Lugar: " + documentSnapshot.getString("lugar"));

                    String descripcion = documentSnapshot.getString("descripcion");
                    if (descripcion == null || descripcion.trim().isEmpty()) {
                        tvDescripcion.setText("Descripción: No hay descripción");
                    } else {
                        tvDescripcion.setText("Descripción: " + descripcion);
                    }

                    creatorUid = documentSnapshot.getString("uid_usuario");
                    asistentes = (List<String>) documentSnapshot.get("asistentes");
                    if (asistentes == null) asistentes = new ArrayList<>();
                    if (creatorUid != null && !asistentes.contains(creatorUid)) {
                        asistentes.add(0, creatorUid);
                    }

                    boolean gastosActivados = Boolean.TRUE.equals(documentSnapshot.getBoolean("gastos"));
                    if (gastosActivados) {
                        btnGastos.setVisibility(View.VISIBLE);
                        btnGastos.setOnClickListener(v -> {
                            Intent intent = new Intent(this, CalculadoraGastosActivity.class);
                            intent.putExtra("eventoId", eventoId);
                            startActivity(intent);
                        });
                    } else {
                        btnGastos.setVisibility(View.GONE);
                    }

                    if (creatorUid != null && creatorUid.equals(currentUser.getUid())) {
                        btnUnirse.setVisibility(View.GONE);
                        btnEliminarEvento.setVisibility(View.VISIBLE);
                        btnEditarEvento.setVisibility(View.VISIBLE);
                    } else if (asistentes.contains(currentUser.getUid())) {
                        btnUnirse.setText("Salir del evento");
                        btnUnirse.setOnClickListener(v -> salirDelEvento());
                    } else {
                        btnUnirse.setText("Unirse al evento");
                        btnUnirse.setOnClickListener(v -> unirseAlEvento());
                    }

                    if (creatorUid != null) {
                        db.collection("users").document(creatorUid)
                                .get()
                                .addOnSuccessListener(userDoc -> {
                                    if (userDoc.exists()) {
                                        String alias = userDoc.getString("alias");
                                        if (alias != null) {
                                            tvCreadorEvento.setText("Creado por: " + alias);
                                        }
                                    }
                                });
                    }

                    if (!asistentes.isEmpty()) {
                        tvSinAsistentes.setVisibility(View.GONE);
                        recyclerAsistentes.setVisibility(View.VISIBLE);
                        listaAsistentes.clear();

                        for (String uid : asistentes) {
                            db.collection("users")
                                    .document(uid)
                                    .get()
                                    .addOnSuccessListener(userDoc -> {
                                        String alias = userDoc.getString("alias");
                                        String email = userDoc.getString("email");

                                        if (alias == null) alias = "Usuario";
                                        if (email == null) email = "correo@desconocido";

                                        String correoFinal = email;
                                        if (uid.equals(creatorUid)) {
                                            correoFinal += " [creador]";
                                        }

                                        listaAsistentes.add(new Asistente(uid, alias, correoFinal));
                                        asistenteAdapter.notifyDataSetChanged();
                                    });
                        }
                    } else {
                        recyclerAsistentes.setVisibility(View.GONE);
                        tvSinAsistentes.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void eliminarEvento() {
        db.collection("eventos").document(eventoId)
                .delete()
                .addOnSuccessListener(unused -> {
                    cancelarNotificacion(eventoId);
                    Toast.makeText(this, "Evento eliminado", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al eliminar: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private void cancelarNotificacion(String eventoId) {
        Intent intent = new Intent(this, NotificacionReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                eventoId.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    private void unirseAlEvento() {
        db.collection("eventos").document(eventoId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        List<String> asistentes = (List<String>) document.get("asistentes");

                        if (asistentes == null) asistentes = new ArrayList<>();

                        if (!asistentes.contains(currentUser.getUid())) {
                            asistentes.add(currentUser.getUid());

                            db.collection("eventos").document(eventoId)
                                    .update("asistentes", asistentes)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(this, "Te has unido al evento", Toast.LENGTH_SHORT).show();
                                        btnUnirse.setEnabled(false);
                                        btnUnirse.setText("Ya estás unido");
                                        cargarDetallesEvento();
                                    });
                        } else {
                            Toast.makeText(this, "Ya estás unido a este evento", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void salirDelEvento() {
        db.collection("eventos").document(eventoId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        List<String> asistentes = (List<String>) document.get("asistentes");

                        if (asistentes != null && asistentes.contains(currentUser.getUid())) {
                            asistentes.remove(currentUser.getUid());

                            db.collection("eventos").document(eventoId)
                                    .update("asistentes", asistentes)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(this, "Has salido del evento", Toast.LENGTH_SHORT).show();
                                        cargarDetallesEvento();
                                    });
                        }
                    }
                });
    }
}
