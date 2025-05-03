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

    private EditText etTitulo, etFecha, etHora, etLugar, etDescripcion;
    private CheckBox checkboxPublico;
    private CheckBox checkboxGastos;
    private Button btnGuardar;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private String eventoId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_evento);

        etTitulo = findViewById(R.id.edtTitulo);
        etFecha = findViewById(R.id.edtFecha);
        etHora = findViewById(R.id.edtHora);
        etLugar = findViewById(R.id.edtLugar);
        etDescripcion = findViewById(R.id.edtDescripcion);
        checkboxPublico = findViewById(R.id.ckboxPublico);
        checkboxGastos = findViewById(R.id.ckbokGastos);
        btnGuardar = findViewById(R.id.btnGuardarEvento);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        eventoId = getIntent().getStringExtra("eventoId");

        cargarDatosEvento();

        etFecha.setOnClickListener(v -> mostrarSelectorFecha());

        etHora.setOnClickListener(v -> mostrarSelectorHora());

        btnGuardar.setOnClickListener(v -> actualizarEvento());
    }

    private void mostrarSelectorFecha() {
        final Calendar calendario = Calendar.getInstance();
        int anio = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    String fechaSeleccionada = String.format(Locale.getDefault(), "%02d/%02d/%04d",
                            dayOfMonth, monthOfYear + 1, year);
                    etFecha.setText(fechaSeleccionada);
                }, anio, mes, dia);

        datePickerDialog.show();
    }


    private void mostrarSelectorHora() {
        final Calendar calendar = Calendar.getInstance();
        int hora = calendar.get(Calendar.HOUR_OF_DAY);
        int minuto = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (TimePicker view, int hourOfDay, int minute1) -> {
                    String horaFormateada = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute1);
                    etHora.setText(horaFormateada);
                }, hora, minuto, true);

        timePickerDialog.show();
    }

    private void cargarDatosEvento() {
        db.collection("eventos").document(eventoId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        etTitulo.setText(document.getString("titulo"));
                        etFecha.setText(document.getString("fecha"));
                        etHora.setText(document.getString("hora"));
                        etLugar.setText(document.getString("lugar"));
                        etDescripcion.setText(document.getString("descripcion"));
                        checkboxPublico.setChecked(Boolean.TRUE.equals(document.getBoolean("publico")));
                        checkboxGastos.setChecked(Boolean.TRUE.equals(document.getBoolean("gastos")));
                    }
                });
    }

    private void actualizarEvento() {
        String titulo = etTitulo.getText().toString().trim();
        String fecha = etFecha.getText().toString().trim();
        String hora = etHora.getText().toString().trim();
        String lugar = etLugar.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        boolean publico = checkboxPublico.isChecked();
        boolean gastos = checkboxGastos.isChecked();

        if (titulo.isEmpty() || fecha.isEmpty() || hora.isEmpty() || lugar.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> datosEvento = new HashMap<>();
        datosEvento.put("titulo", titulo);
        datosEvento.put("fecha", fecha);
        datosEvento.put("hora", hora);
        datosEvento.put("lugar", lugar);
        datosEvento.put("descripcion", descripcion);
        datosEvento.put("publico", publico);
        datosEvento.put("gastos", gastos);
        datosEvento.put("uid_usuario", currentUser.getUid());

        DocumentReference ref = db.collection("eventos").document(eventoId);

        ref.update(datosEvento)
                .addOnSuccessListener(unused -> {
                    programarNotificacion(eventoId, titulo, fecha, hora);
                    Toast.makeText(this, "Evento actualizado correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al actualizar evento: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private void programarNotificacion(String eventoId, String tituloEvento, String fecha, String hora) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        try {
            Date fechaHora = sdf.parse(fecha + " " + hora);
            if (fechaHora == null) return;

            long tiempoEnMillis = fechaHora.getTime();
            if (tiempoEnMillis < System.currentTimeMillis()) return;

            Intent intent = new Intent(this, NotificacionReceiver.class);
            intent.putExtra("tituloEvento", tituloEvento);
            intent.putExtra("eventoId", eventoId);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    eventoId.hashCode(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, tiempoEnMillis, pendingIntent);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
