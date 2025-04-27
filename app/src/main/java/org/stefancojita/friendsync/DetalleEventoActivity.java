package org.stefancojita.friendsync;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
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
    private Button btnEliminarEvento;
    private Button btnEditarEvento;
    private TextView tvCreadorEvento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_evento);

        tvCreadorEvento = findViewById(R.id.tvCreadorEvento);
        tvTitulo = findViewById(R.id.tvTitulo);
        tvFecha = findViewById(R.id.tvFecha);
        tvLugar = findViewById(R.id.tvLugar);
        tvDescripcion = findViewById(R.id.tvDescripcion);
        btnUnirse = findViewById(R.id.btnUnirse);
        recyclerAsistentes = findViewById(R.id.recyclerAsistentes);
        recyclerAsistentes.setLayoutManager(new LinearLayoutManager(this));
        asistenteAdapter = new AsistenteAdapter(listaAsistentes);
        recyclerAsistentes.setAdapter(asistenteAdapter);
        btnEliminarEvento = findViewById(R.id.btnEliminarEvento);
        btnEliminarEvento.setOnClickListener(v -> eliminarEvento());
        btnEditarEvento = findViewById(R.id.btnEditarEvento);
        btnEditarEvento.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditarEventoActivity.class);
            intent.putExtra("eventoId", eventoId);
            startActivity(intent);
        });

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Obtener ID del evento desde el intent
        eventoId = getIntent().getStringExtra("eventoId");

        if (eventoId != null) {
            cargarDetallesEvento();
        }

        btnUnirse.setOnClickListener(v -> unirseAlEvento());

        crearCanalNotificaciones();
    }

    private void cargarDetallesEvento() {
        btnEliminarEvento.setVisibility(View.GONE);
        btnEditarEvento.setVisibility(View.GONE);
        db.collection("eventos").document(eventoId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        tvTitulo.setText(document.getString("titulo"));
                        tvFecha.setText("Fecha: " + document.getString("fecha"));
                        tvLugar.setText("Lugar: " + document.getString("lugar"));
                        tvDescripcion.setText("Descripción: " + document.getString("descripcion"));

                        creatorUid = document.getString("uid_usuario");

                        db.collection("users").document(creatorUid)
                                .get()
                                .addOnSuccessListener(userDoc -> {
                                    if (userDoc.exists()) {
                                        String alias = userDoc.getString("alias");
                                        tvCreadorEvento.setText("Autor: " + alias);
                                    }
                                });

                        asistentes = (List<String>) document.get("asistentes");

                        if (creatorUid != null && creatorUid.equals(currentUser.getUid())) {
                            btnUnirse.setVisibility(View.GONE);
                            btnEliminarEvento.setVisibility(View.VISIBLE);
                            btnEditarEvento.setVisibility(View.VISIBLE);
                        } else if (asistentes != null && asistentes.contains(currentUser.getUid())) {
                            btnUnirse.setText("Salir del evento");
                            btnUnirse.setOnClickListener(v -> salirDelEvento());
                        } else {
                            btnUnirse.setText("Unirse al evento");
                            btnUnirse.setOnClickListener(v -> unirseAlEvento());
                        }

                    }
                    asistentes = (List<String>) document.get("asistentes");

                    if (asistentes != null && !asistentes.isEmpty()) {
                        listaAsistentes.clear();
                        for (String uid : asistentes) {
                            db.collection("users")
                                    .document(uid)
                                    .get()
                                    .addOnSuccessListener(userDoc -> {
                                        String alias = userDoc.getString("alias");
                                        String email = userDoc.getString("email");
                                        listaAsistentes.add(new Asistente(uid, alias, email));
                                        asistenteAdapter.notifyDataSetChanged();
                                    });
                        }
                    }
                });
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

                                        enviarNotificacionUnido(tvTitulo.getText().toString());

                                        cargarDetallesEvento();
                                    });
                        } else {
                            Toast.makeText(this, "Ya estás unido a este evento", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void eliminarEvento() {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar evento")
                .setMessage("¿Estás seguro de que quieres eliminar este evento?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    db.collection("eventos").document(eventoId)
                            .delete()
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "Evento eliminado", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Error al eliminar el evento", Toast.LENGTH_SHORT).show()
                            );
                })
                .setNegativeButton("Cancelar", null)
                .show();
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


    private void crearCanalNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence nombre = "CanalEventos";
            String descripcion = "Canal para notificaciones de eventos";
            int importancia = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel canal = new NotificationChannel("canal_eventos", nombre, importancia);
            canal.setDescription(descripcion);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(canal);
        }
    }

    private void enviarNotificacionUnido(String tituloEvento) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "canal_eventos")
                .setSmallIcon(R.drawable.ic_event) // Icono de la notificación
                .setContentTitle("¡Te has unido a un evento!")
                .setContentText("Evento: " + tituloEvento)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(1, builder.build());
        }
    }

}
