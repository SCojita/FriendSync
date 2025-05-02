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

    private EditText etTitulo, etFecha, etHora, etLugar, etDescripcion;
    private CheckBox checkboxPublico;
    private CheckBox checkboxGastos;
    private Button btnGuardar;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_evento);

        etTitulo = findViewById(R.id.etTitulo);
        etFecha = findViewById(R.id.etFecha);
        etHora = findViewById(R.id.etHora);
        etLugar = findViewById(R.id.etLugar);
        etDescripcion = findViewById(R.id.etDescripcion);
        checkboxPublico = findViewById(R.id.checkboxPublico);
        checkboxGastos = findViewById(R.id.checkboxGastos);
        btnGuardar = findViewById(R.id.btnGuardarEvento);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        etFecha.setOnClickListener(v -> mostrarSelectorFecha());

        etHora.setOnClickListener(v -> mostrarSelectorHora());

        btnGuardar.setOnClickListener(v -> guardarEvento());
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

    private void guardarEvento() {
        String titulo = etTitulo.getText().toString().trim();
        String fecha = etFecha.getText().toString().trim();
        String hora = etHora.getText().toString().trim();
        String lugar = etLugar.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        boolean publico = checkboxPublico.isChecked();

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
        datosEvento.put("gastos", checkboxGastos.isChecked());
        datosEvento.put("uid_usuario", currentUser.getUid());

        db.collection("eventos")
                .add(datosEvento)
                .addOnSuccessListener(documentReference -> {
                    programarNotificacion(documentReference.getId(), titulo, fecha, hora);
                    Toast.makeText(this, "Evento creado correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al crear evento: " + e.getMessage(), Toast.LENGTH_LONG).show()
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