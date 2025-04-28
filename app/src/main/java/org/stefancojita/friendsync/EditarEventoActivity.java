package org.stefancojita.friendsync;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditarEventoActivity extends AppCompatActivity {

    private EditText etTitulo, etFecha, etLugar, etDescripcion;
    private Button btnGuardarCambios;
    private FirebaseFirestore db;
    private String eventoId;
    private MaterialCheckBox checkboxPublico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_evento);

        etTitulo = findViewById(R.id.etTitulo);
        etFecha = findViewById(R.id.etFecha);
        etFecha.setOnClickListener(v -> mostrarDatePicker());
        etLugar = findViewById(R.id.etLugar);
        etDescripcion = findViewById(R.id.etDescripcion);
        checkboxPublico = findViewById(R.id.checkboxPublico);
        btnGuardarCambios = findViewById(R.id.btnGuardarEvento);

        db = FirebaseFirestore.getInstance();
        eventoId = getIntent().getStringExtra("eventoId");

        cargarDatosEvento();

        btnGuardarCambios.setText("Guardar cambios");
        btnGuardarCambios.setOnClickListener(v -> guardarCambios());
    }

    private void cargarDatosEvento() {
        db.collection("eventos").document(eventoId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        etTitulo.setText(document.getString("titulo"));
                        etFecha.setText(document.getString("fecha"));
                        etLugar.setText(document.getString("lugar"));
                        etDescripcion.setText(document.getString("descripcion"));

                        Boolean esPublico = document.getBoolean("publico");
                        checkboxPublico.setChecked(Boolean.TRUE.equals(esPublico));
                    }
                });
    }


    private void mostrarDatePicker() {
        final Calendar calendario = Calendar.getInstance();
        int anio = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    String fechaSeleccionada = String.format("%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year);
                    etFecha.setText(fechaSeleccionada);
                }, anio, mes, dia);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void guardarCambios() {
        String titulo = etTitulo.getText().toString().trim();
        String fecha = etFecha.getText().toString().trim();
        String lugar = etLugar.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        boolean esPublico = checkboxPublico.isChecked();

        if (titulo.isEmpty()) {
            etTitulo.setError("El título es obligatorio");
            etTitulo.requestFocus();
            return;
        }

        if (fecha.isEmpty()) {
            etFecha.setError("La fecha es obligatoria");
            etFecha.requestFocus();
            return;
        }

        if (lugar.isEmpty()) {
            etLugar.setError("El lugar es obligatorio");
            etLugar.requestFocus();
            return;
        }

        Map<String, Object> actualizacion = new HashMap<>();
        actualizacion.put("titulo", titulo);
        actualizacion.put("fecha", fecha);
        actualizacion.put("lugar", lugar);
        actualizacion.put("descripcion", descripcion);
        actualizacion.put("publico", esPublico);

        db.collection("eventos").document(eventoId)
                .update(actualizacion)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Evento actualizado", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }
}

