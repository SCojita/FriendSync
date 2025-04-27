package org.stefancojita.friendsync;

import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ListaEventosActivity extends AppCompatActivity {

    private RecyclerView recyclerEventos;
    private EventoAdapter adapter;
    private List<Evento> listaEventos;
    List<String> listaIds = new ArrayList<>();
    private FirebaseFirestore db;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_eventos);

        recyclerEventos = findViewById(R.id.recyclerEventos);
        recyclerEventos.setLayoutManager(new LinearLayoutManager(this));

        listaEventos = new ArrayList<>();
        adapter = new EventoAdapter(listaEventos, listaIds);
        recyclerEventos.setAdapter(adapter);
        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // No hacemos nada al pulsar "Enter"
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filtrarEventos(newText);
                return true;
            }
        });

        db = FirebaseFirestore.getInstance();

        cargarEventosDesdeFirestore();
    }

    private void cargarEventosDesdeFirestore() {
        db.collection("eventos")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaEventos.clear();
                    listaIds.clear();

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    Date fechaActual = soloFechaActual();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Evento evento = doc.toObject(Evento.class);
                        String fechaStr = evento.getFecha();

                        Boolean esPublico = doc.getBoolean("publico");
                        if (!Boolean.TRUE.equals(esPublico)) {
                            continue;
                        }

                        try {
                            Date fechaEvento = sdf.parse(fechaStr);

                            if (fechaEvento != null && !fechaEvento.before(fechaActual)) {
                                listaEventos.add(evento);
                                listaIds.add(doc.getId());
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    Collections.sort(listaEventos, (e1, e2) -> {
                        try {
                            Date fecha1 = sdf.parse(e1.getFecha());
                            Date fecha2 = sdf.parse(e2.getFecha());
                            return fecha1.compareTo(fecha2);
                        } catch (ParseException e) {
                            e.printStackTrace();
                            return 0;
                        }
                    });

                    adapter = new EventoAdapter(listaEventos, listaIds);
                    recyclerEventos.setAdapter(adapter);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cargar eventos", Toast.LENGTH_SHORT).show()
                );
    }


    private void filtrarEventos(String texto) {
        List<Evento> listaFiltrada = new ArrayList<>();
        List<String> listaIdsFiltrada = new ArrayList<>();

        for (int i = 0; i < listaEventos.size(); i++) {
            Evento evento = listaEventos.get(i);
            String id = listaIds.get(i);

            if (evento.getTitulo().toLowerCase().contains(texto.toLowerCase()) ||
                    evento.getLugar().toLowerCase().contains(texto.toLowerCase())) {
                listaFiltrada.add(evento);
                listaIdsFiltrada.add(id);
            }
        }

        // Actualizamos el adaptador con la lista filtrada
        adapter = new EventoAdapter(listaFiltrada, listaIdsFiltrada);
        recyclerEventos.setAdapter(adapter);
    }

    private Date soloFechaActual() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }



}
