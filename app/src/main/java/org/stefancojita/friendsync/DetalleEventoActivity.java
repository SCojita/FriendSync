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

    // Declaración de variables.
    private TextView textTitulo, textFecha, textHora, textLugar, textDescripcion, textCreadorEvento, textSinAsistentes;
    private Button btnUnirse, btnEliminarEvento, btnEditarEvento, btnGastos;
    private RecyclerView recyclerAsistentes;
    private AsistenteAdapter asistenteAdapter;
    private List<Asistente> listaAsistentes;
    private FirebaseFirestore db;
    private FirebaseUser usuarioActual;
    private String evento_id;
    private String creador_uid;
    private List<String> asistentes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_evento);

        // Inicializamos las variables.
        textTitulo = findViewById(R.id.txtTitulo);
        textFecha = findViewById(R.id.txtFecha);
        textHora = findViewById(R.id.txtHora);
        textLugar = findViewById(R.id.txtLugar);
        textDescripcion = findViewById(R.id.txtDescripcion);
        textCreadorEvento = findViewById(R.id.txtCreadorEvento);
        textSinAsistentes = findViewById(R.id.tvSinAsistentes);
        btnUnirse = findViewById(R.id.btnUnirse);
        btnEliminarEvento = findViewById(R.id.btnEliminarEvento);
        btnEditarEvento = findViewById(R.id.btnEditarEvento);
        btnGastos = findViewById(R.id.btnGastos);
        recyclerAsistentes = findViewById(R.id.rcyAsistentes);
        recyclerAsistentes.setLayoutManager(new LinearLayoutManager(this));

        listaAsistentes = new ArrayList<>(); // Inicializamos la lista de asistentes.
        asistenteAdapter = new AsistenteAdapter(listaAsistentes); // Creamos el adaptador.
        recyclerAsistentes.setAdapter(asistenteAdapter); // Asignamos el adaptador al RecyclerView.

        db = FirebaseFirestore.getInstance(); // Inicializamos Firestore.
        usuarioActual = FirebaseAuth.getInstance().getCurrentUser(); // Obtenemos el usuario actual.

        evento_id = getIntent().getStringExtra("eventoId"); // Obtenemos el ID del evento.

        cargarDetallesEvento(); // Cargamos los detalles del evento.

        btnEliminarEvento.setOnClickListener(v -> eliminarEvento()); // Configuramos el botón de eliminar evento al pulsar.

        // Configuramos el botón de editar evento.
        btnEditarEvento.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditarEventoActivity.class);
            intent.putExtra("eventoId", evento_id);
            startActivity(intent);
        });
    }

    // Creamos un método para cargar los detalles del evento.
    private void cargarDetallesEvento() {
        btnEliminarEvento.setVisibility(View.GONE); // Ocultamos el botón de eliminar evento.
        btnEditarEvento.setVisibility(View.GONE); // Ocultamos el botón de editar evento.
        // Ocultamos el botón de gastos.
        db.collection("eventos").document(evento_id)
                // Añadimos un listener para obtener los detalles del evento.
                .addSnapshotListener((documentSnapshot, error) -> {
                    // Comprobamos si hay un error o si el documento no existe.
                    if (error != null || documentSnapshot == null || !documentSnapshot.exists()) return;

                    textTitulo.setText(documentSnapshot.getString("titulo")); // Obtenemos el título del evento.
                    textFecha.setText("Fecha: " + documentSnapshot.getString("fecha")); // Obtenemos la fecha del evento.
                    textHora.setText("Hora: " + documentSnapshot.getString("hora")); // Obtenemos la hora del evento.
                    textLugar.setText("Lugar: " + documentSnapshot.getString("lugar")); // Obtenemos el lugar del evento.

                    String descripcion = documentSnapshot.getString("descripcion"); // Obtenemos la descripción del evento en una nueva variable.

                    // Comprobamos si la descripción está vacía o nula.
                    if (descripcion == null || descripcion.trim().isEmpty()) {
                        textDescripcion.setText("Descripción: No hay descripción"); // Mostramos un mensaje por defecto.
                    } else {
                        textDescripcion.setText("Descripción: " + descripcion); // Mostramos la descripción del evento.
                    }

                    creador_uid = documentSnapshot.getString("uid_usuario"); // Obtenemos el UID del creador del evento.
                    asistentes = (List<String>) documentSnapshot.get("asistentes"); // Obtenemos la lista de asistentes.
                    if (asistentes == null) asistentes = new ArrayList<>(); // Inicializamos la lista de asistentes si es nula.

                    // Comprobamos si el creador del evento no está en la lista de asistentes.
                    if (creador_uid != null && !asistentes.contains(creador_uid)) {
                        asistentes.add(0, creador_uid); // Añadimos el creador del evento a la lista de asistentes.
                    }

                    boolean gastosActivados = Boolean.TRUE.equals(documentSnapshot.getBoolean("gastos")); // Obtenemos el estado de los gastos del evento.
                    // Comprobamos si los gastos están activados.
                    if (gastosActivados) {
                        btnGastos.setVisibility(View.VISIBLE); // Mostramos el botón de gastos.
                        // Configuramos el botón de gastos.
                        btnGastos.setOnClickListener(v -> {
                            Intent intent = new Intent(this, CalculadoraGastosActivity.class); // Creamos un nuevo Intent para la actividad de gastos.
                            intent.putExtra("eventoId", evento_id);
                            startActivity(intent);
                        });
                    } else {
                        btnGastos.setVisibility(View.GONE); // Ocultamos el botón de gastos.
                    }

                    // Comprobamos si el UID del creador del evento es igual al UID del usuario actual.
                    if (creador_uid != null && creador_uid.equals(usuarioActual.getUid())) {
                        btnUnirse.setVisibility(View.GONE); // Ocultamos el botón de unirse al evento.
                        btnEliminarEvento.setVisibility(View.VISIBLE); // Mostramos el botón de eliminar evento.
                        btnEditarEvento.setVisibility(View.VISIBLE); // Mostramos el botón de editar evento.
                    } else if (asistentes.contains(usuarioActual.getUid())) {
                        btnUnirse.setText("Salir del evento"); // Cambiamos el texto del botón a "Salir del evento".
                        btnUnirse.setOnClickListener(v -> salirDelEvento()); // Configuramos el botón para salir del evento.
                    } else {
                        btnUnirse.setText("Unirse al evento"); // Cambiamos el texto del botón a "Unirse al evento".
                        btnUnirse.setOnClickListener(v -> unirseAlEvento()); // Configuramos el botón para unirse al evento.
                    }

                    // Comprobamos si el UID del creador del evento no es nulo.
                    if (creador_uid != null) {
                        // Obtenemos el alias del creador del evento.
                        db.collection("users").document(creador_uid)
                                .get() // Obtenemos el documento del creador del evento.
                                // Añadimos un listener para obtener el alias del creador.
                                .addOnSuccessListener(userDoc -> {
                                    // Comprobamos si el documento del creador del evento existe.
                                    if (userDoc.exists()) {
                                        String alias = userDoc.getString("alias"); // Obtenemos el alias del creador del evento.
                                        // Comprobamos si el alias es nulo.
                                        if (alias != null) {
                                            textCreadorEvento.setText("Creado por: " + alias); // Mostramos el alias del creador del evento.
                                        }
                                    }
                                });
                    }

                    // Comprobamos si la lista de asistentes no está vacía.
                    if (!asistentes.isEmpty()) {
                        textSinAsistentes.setVisibility(View.GONE); // Ocultamos el mensaje de "Sin asistentes".
                        recyclerAsistentes.setVisibility(View.VISIBLE); // Mostramos el RecyclerView de asistentes.
                        listaAsistentes.clear(); // Limpiamos la lista de asistentes.

                        // Recorremos la lista de asistentes.
                        for (String uid : asistentes) {
                            // Obtenemos el documento del usuario.
                            db.collection("users")
                                    .document(uid)
                                    .get()
                                    // Añadimos un listener para obtener el documento del usuario.
                                    .addOnSuccessListener(userDoc -> {
                                        String alias = userDoc.getString("alias"); // Obtenemos el alias del usuario.
                                        String email = userDoc.getString("email"); // Obtenemos el email del usuario.

                                        // Comprobamos si el alias o el email son nulos para asignarle un texto.
                                        if (alias == null) alias = "Usuario";
                                        if (email == null) email = "correo@desconocido";

                                        String correoFinal = email; // Creamos una nueva variable para el correo.

                                        // Comprobamos si el UID del usuario es igual al UID del creador del evento.
                                        if (uid.equals(creador_uid)) {
                                            correoFinal += " [creador]"; // Añadimos un texto para indicar que es el creador del evento.
                                        }

                                        listaAsistentes.add(new Asistente(uid, alias, correoFinal)); // Añadimos el usuario a la lista de asistentes.
                                        asistenteAdapter.notifyDataSetChanged(); // Notificamos al adaptador que los datos han cambiado.
                                    });
                        }
                    } else {
                        recyclerAsistentes.setVisibility(View.GONE); // Ocultamos el RecyclerView de asistentes.
                        textSinAsistentes.setVisibility(View.VISIBLE); // Mostramos el mensaje de "Sin asistentes".
                    }
                });
    }

    // Creamos un método para eliminar el evento.
    private void eliminarEvento() {
        // Comprobamos si el UID del creador del evento es igual al UID del usuario actual.
        db.collection("eventos").document(evento_id)
                .delete()
                // Añadimos un listener para eliminar el evento.
                .addOnSuccessListener(unused -> {
                    cancelarNotificacion(evento_id); // Cancelamos la notificación del evento.
                    Toast.makeText(this, "Evento eliminado", Toast.LENGTH_SHORT).show(); // Mostramos un mensaje de éxito.
                    finish(); // Finalizamos la actividad.
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al eliminar: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    // Creamos un método para cancelar la notificación del evento.
    private void cancelarNotificacion(String eventoId) {
        Intent intent = new Intent(this, NotificacionReceiver.class); // Creamos un nuevo Intent para la clase NotificacionReceiver.
        // Añadimos el ID del evento al Intent.
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                eventoId.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarma = (AlarmManager) getSystemService(ALARM_SERVICE); // Obtenemos el servicio de AlarmManager.
        alarma.cancel(pendingIntent); // Cancelamos la alarma.
    }

    // Creamos un método para unirse al evento.
    private void unirseAlEvento() {
        // Comprobamos si el UID del creador del evento no es nulo.
        db.collection("eventos").document(evento_id)
                .get()
                // Añadimos un listener para obtener el documento del evento.
                .addOnSuccessListener(document -> {
                    // Comprobamos si el documento del evento existe.
                    if (document.exists()) {
                        List<String> asistentes = (List<String>) document.get("asistentes"); // Obtenemos la lista de asistentes.

                        // Comprobamos si la lista de asistentes es nula y la creamos.
                        if (asistentes == null) asistentes = new ArrayList<>();

                        // Comprobamos si el UID del usuario actual no está en la lista de asistentes.
                        if (!asistentes.contains(usuarioActual.getUid())) {
                            asistentes.add(usuarioActual.getUid()); // Añadimos el UID del usuario actual a la lista de asistentes.

                            // Actualizamos la lista de asistentes en Firestore.
                            db.collection("eventos").document(evento_id)
                                    .update("asistentes", asistentes)
                                    // Añadimos un listener para actualizar la lista de asistentes.
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(this, "Te has unido al evento", Toast.LENGTH_SHORT).show();
                                        btnUnirse.setEnabled(false); // Deshabilitamos el botón de unirse.
                                        btnUnirse.setText("Ya estás unido"); // Cambiamos el texto del botón a "Ya estás unido".
                                        cargarDetallesEvento(); // Recargamos los detalles del evento.
                                    });
                        } else {
                            Toast.makeText(this, "Ya estás unido a este evento", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Creamos un método para salir del evento.
    private void salirDelEvento() {
        // Comprobamos si el UID del creador del evento no es nulo.
        db.collection("eventos").document(evento_id)
                .get()
                // Añadimos un listener para obtener el documento del evento.
                .addOnSuccessListener(document -> {
                    // Comprobamos si el documento del evento existe.
                    if (document.exists()) {
                        List<String> asistentes = (List<String>) document.get("asistentes"); // Obtenemos la lista de asistentes.

                        // Comprobamos si la lista de asistentes no es nula.
                        if (asistentes != null && asistentes.contains(usuarioActual.getUid())) {
                            asistentes.remove(usuarioActual.getUid()); // Eliminamos el UID del usuario actual de la lista de asistentes.

                            // Actualizamos la lista de asistentes en Firestore.
                            db.collection("eventos").document(evento_id)
                                    .update("asistentes", asistentes)
                                    // Añadimos un listener para actualizar la lista de asistentes.
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(this, "Has salido del evento", Toast.LENGTH_SHORT).show();
                                        cargarDetallesEvento(); // Recargamos los detalles del evento.
                                    });
                        }
                    }
                });
    }
}
