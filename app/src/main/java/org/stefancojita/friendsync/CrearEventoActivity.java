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
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CrearEventoActivity extends AppCompatActivity {

    private EditText editTitulo, editFecha, editHora, editLugar, editDescripcion;
    private CheckBox checkboxPublico;
    private CheckBox checkboxGastos;
    private Button btnGuardar;
    private FirebaseFirestore db;
    private FirebaseUser usuarioActual;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_evento);

        // Inicializamos las variables.
        editTitulo = findViewById(R.id.edtTitulo);
        editFecha = findViewById(R.id.edtFecha);
        editHora = findViewById(R.id.edtHora);
        editLugar = findViewById(R.id.edtLugar);
        editDescripcion = findViewById(R.id.edtDescripcion);
        checkboxPublico = findViewById(R.id.ckboxPublico);
        checkboxGastos = findViewById(R.id.ckbokGastos);
        btnGuardar = findViewById(R.id.btnGuardarEvento);

        db = FirebaseFirestore.getInstance(); // Inicializamos Firestore.
        usuarioActual = FirebaseAuth.getInstance().getCurrentUser(); // Obtenemos el usuario actual.

        editFecha.setOnClickListener(v -> mostrarSelectorFecha()); // Mostramos el selector de fecha al pulsar.

        editHora.setOnClickListener(v -> mostrarSelectorHora()); // Mostramos el selector de hora al pulsar.

        btnGuardar.setOnClickListener(v -> guardarEvento()); // Guardamos el evento al pulsar.
    }

    // Creamos un método para mostrar el selector de fecha.
    private void mostrarSelectorFecha() {
        final Calendar calendario = Calendar.getInstance(); // Obtenemos la fecha actual.
        int any = calendario.get(Calendar.YEAR); // Obtenemos el año actual.
        int mes = calendario.get(Calendar.MONTH); // Obtenemos el mes actual.
        int dia = calendario.get(Calendar.DAY_OF_MONTH); // Obtenemos el día actual.

        // Creamos el selector de fecha.
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                // Creamos el listener para el selector de fecha.
                (view, year, monthOfYear, dayOfMonth) -> {
                    String fechaSeleccionada = String.format(Locale.getDefault(), "%02d/%02d/%04d",
                            dayOfMonth, monthOfYear + 1, year);
                    editFecha.setText(fechaSeleccionada);
                }, any, mes, dia);

        datePickerDialog.show(); // Mostramos el selector de fecha.
    }

    // Creamos un método para mostrar el selector de hora.
    private void mostrarSelectorHora() {
        final Calendar calendar = Calendar.getInstance(); // Obtenemos la hora actual.
        int hora = calendar.get(Calendar.HOUR_OF_DAY); // Obtenemos la hora actual.
        int minuto = calendar.get(Calendar.MINUTE); // Obtenemos el minuto actual.

        // Creamos el selector de hora.
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                // Creamos el listener para el selector de hora.
                (TimePicker view, int hourOfDay, int minute1) -> {
                    String horaFormateada = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute1);
                    editHora.setText(horaFormateada);
                }, hora, minuto, true);

        timePickerDialog.show(); // Mostramos el selector de hora.
    }

    // Creamos un método para guardar el evento.
    private void guardarEvento() {
        // Obtenemos los datos del evento.
        String titulo = editTitulo.getText().toString().trim();
        String fecha = editFecha.getText().toString().trim();
        String hora = editHora.getText().toString().trim();
        String lugar = editLugar.getText().toString().trim();
        String descripcion = editDescripcion.getText().toString().trim();
        boolean publico = checkboxPublico.isChecked();

        // Verificamos que los campos estén completos.
        if (titulo.isEmpty() || fecha.isEmpty() || hora.isEmpty() || lugar.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> datosEvento = new HashMap<>(); // Creamos un mapa para almacenar los datos del evento.

        // Añadimos los datos al mapa.
        datosEvento.put("titulo", titulo);
        datosEvento.put("fecha", fecha);
        datosEvento.put("hora", hora);
        datosEvento.put("lugar", lugar);
        datosEvento.put("descripcion", descripcion);
        datosEvento.put("publico", publico);
        datosEvento.put("gastos", checkboxGastos.isChecked());
        datosEvento.put("uid_usuario", usuarioActual.getUid());

        // Guardamos el evento en Firestore.
        db.collection("eventos")
                .add(datosEvento) // Añadimos el evento a la colección "eventos".

                .addOnSuccessListener(documentReference -> {
                    programarNotificacion(documentReference.getId(), titulo, fecha, hora); // Programamos la notificación.
                    Toast.makeText(this, "Evento creado correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al crear evento: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    // Creamos un método para programar la notificación.
    private void programarNotificacion(String eventoId, String tituloEvento, String fecha, String hora) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()); // Formato de fecha y hora.

        try {
            Date fechaHora = sdf.parse(fecha + " " + hora); // Parseamos la fecha y hora.
            if (fechaHora == null) return; // Si no se pudo parsear, salimos.

            long tiempoEnMillis = fechaHora.getTime(); // Obtenemos el tiempo en milisegundos.
            if (tiempoEnMillis < System.currentTimeMillis()) return; // Si la fecha y hora ya pasaron, salimos.

            Intent intent = new Intent(this, NotificacionReceiver.class); // Creamos un intent para la notificación.
            intent.putExtra("tituloEvento", tituloEvento); // Añadimos el título del evento al intent.
            intent.putExtra("eventoId", eventoId); // Añadimos el ID del evento al intent.

            // Creamos un PendingIntent para la notificación.
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    // Usamos el ID del evento como requestCode para el PendingIntent.
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