package org.stefancojita.friendsync;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditarEventoActivity extends AppCompatActivity {

    // Declaración de variables.
    private EditText editTitulo, editFecha, editText, editLugar, editDescripcion;
    private CheckBox checkboxPublico;
    private CheckBox checkboxGastos;
    private Button btnGuardar;
    private FirebaseFirestore db;
    private FirebaseUser usuarioActual;
    private String evento_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_evento);

        // Inicialización de vistas.
        editTitulo = findViewById(R.id.edtTitulo);
        editFecha = findViewById(R.id.edtFecha);
        editText = findViewById(R.id.edtHora);
        editLugar = findViewById(R.id.edtLugar);
        editDescripcion = findViewById(R.id.edtDescripcion);
        checkboxPublico = findViewById(R.id.ckboxPublico);
        checkboxGastos = findViewById(R.id.ckbokGastos);
        btnGuardar = findViewById(R.id.btnGuardarEvento);

        db = FirebaseFirestore.getInstance(); // Inicializa Firestore
        usuarioActual = FirebaseAuth.getInstance().getCurrentUser(); // Obtiene el usuario actual.

        evento_id = getIntent().getStringExtra("eventoId"); // Obtiene el ID del evento a editar.

        cargarDatosEvento(); // Cargamos los datos del evento.

        editFecha.setOnClickListener(v -> mostrarSelectorFecha()); // Mostramos el selector de fecha al pulsar.

        editText.setOnClickListener(v -> mostrarSelectorHora()); // Mostramos el selector de hora al pulsar.

        btnGuardar.setOnClickListener(v -> actualizarEvento()); // Guardamos los cambios al pulsar el botón.
    }

    // Creamos un método para mostrar el selector de fecha.
    private void mostrarSelectorFecha() {
        final Calendar calendario = Calendar.getInstance(); // Obtiene la fecha actual.
        int anio = calendario.get(Calendar.YEAR); // Obtenemos el año actual.
        int mes = calendario.get(Calendar.MONTH); // Obtenemos el mes actual.
        int dia = calendario.get(Calendar.DAY_OF_MONTH); // Obtenemos el día actual.

        // Creamos un DatePickerDialog para seleccionar la fecha.
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    // Formateamos la fecha seleccionada y la mostramos en el EditText.
                    String fechaSeleccionada = String.format(Locale.getDefault(), "%02d/%02d/%04d",
                            dayOfMonth, monthOfYear + 1, year);
                    editFecha.setText(fechaSeleccionada); // Mostramos la fecha seleccionada.
                }, anio, mes, dia);

        datePickerDialog.show(); // Mostramos el selector de fecha.
    }

    // Creamos un método para mostrar el selector de hora.
    private void mostrarSelectorHora() {
        final Calendar calendar = Calendar.getInstance(); // Obtenemos la hora actual.
        int hora = calendar.get(Calendar.HOUR_OF_DAY); // Obtenemos la hora actual.
        int minuto = calendar.get(Calendar.MINUTE); // Obtenemos el minuto actual.

        // Creamos un TimePickerDialog para seleccionar la hora.
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (TimePicker view, int hourOfDay, int minute1) -> {
                    // Formateamos la hora seleccionada y la mostramos en el EditText.
                    String horaFormateada = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute1);
                    editText.setText(horaFormateada); // Mostramos la hora seleccionada.
                }, hora, minuto, true);

        timePickerDialog.show(); // Mostramos el selector de hora.
    }

    // Creamos un método para cargar los datos del evento en los EditText.
    private void cargarDatosEvento() {
        // Obtenemos el ID del evento de la actividad anterior.
        db.collection("eventos").document(evento_id)
                .get()
                // Si el documento existe, cargamos los datos en los EditText.
                .addOnSuccessListener(document -> {
                    // Verificamos si el documento existe.
                    if (document.exists()) {
                        // Cargamos los datos del evento en los EditText.
                        editTitulo.setText(document.getString("titulo"));
                        editFecha.setText(document.getString("fecha"));
                        editText.setText(document.getString("hora"));
                        editLugar.setText(document.getString("lugar"));
                        editDescripcion.setText(document.getString("descripcion"));
                        checkboxPublico.setChecked(Boolean.TRUE.equals(document.getBoolean("publico")));
                        checkboxGastos.setChecked(Boolean.TRUE.equals(document.getBoolean("gastos")));
                    }
                });
    }

    // Creamos un método para actualizar el evento en Firestore.
    private void actualizarEvento() {
        // Obtenemos los datos de los EditText.
        String titulo = editTitulo.getText().toString().trim();
        String fecha = editFecha.getText().toString().trim();
        String hora = editText.getText().toString().trim();
        String lugar = editLugar.getText().toString().trim();
        String descripcion = editDescripcion.getText().toString().trim();
        boolean publico = checkboxPublico.isChecked();
        boolean gastos = checkboxGastos.isChecked();

        // Verificamos que los campos no estén vacíos.
        if (titulo.isEmpty() || fecha.isEmpty() || hora.isEmpty() || lugar.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> datosEvento = new HashMap<>(); // Creamos un mapa para almacenar los datos del evento.

        // Agregamos los datos al mapa.
        datosEvento.put("titulo", titulo);
        datosEvento.put("fecha", fecha);
        datosEvento.put("hora", hora);
        datosEvento.put("lugar", lugar);
        datosEvento.put("descripcion", descripcion);
        datosEvento.put("publico", publico);
        datosEvento.put("gastos", gastos);
        datosEvento.put("uid_usuario", usuarioActual.getUid());

        DocumentReference ref = db.collection("eventos").document(evento_id); // Obtenemos la referencia al documento del evento.

        // Actualizamos el evento en Firestore.
        ref.update(datosEvento)
                // Si la actualización es exitosa, programamos la notificación y mostramos un mensaje.
                .addOnSuccessListener(unused -> {
                    programarNotificacion(evento_id, titulo, fecha, hora); // Programamos la notificación.
                    Toast.makeText(this, "Evento actualizado correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al actualizar evento: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    // Creamos un método para programar la notificación.
    private void programarNotificacion(String eventoId, String tituloEvento, String fecha, String hora) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()); // Formato de fecha y hora.
        try {
            Date fechaHora = sdf.parse(fecha + " " + hora); // Convertimos la fecha y hora a un objeto Date.
            if (fechaHora == null) return; // Verificamos que la fecha y hora no sean nulos.

            long tiempoEnMillis = fechaHora.getTime(); // Obtenemos el tiempo en milisegundos.
            if (tiempoEnMillis < System.currentTimeMillis()) return; // Verificamos que la fecha y hora no sean pasadas.

            Intent intent = new Intent(this, NotificacionReceiver.class); // Creamos un Intent para la notificación.
            intent.putExtra("tituloEvento", tituloEvento);
            intent.putExtra("eventoId", eventoId);

            // Creamos un PendingIntent para la notificación.
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    eventoId.hashCode(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarma = (AlarmManager) getSystemService(ALARM_SERVICE); // Obtenemos el AlarmManager.
            alarma.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, tiempoEnMillis, pendingIntent); // Programamos la alarma.

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
