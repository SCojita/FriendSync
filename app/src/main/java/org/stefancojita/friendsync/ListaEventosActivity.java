package org.stefancojita.friendsync;

import android.os.Bundle;
import androidx.appcompat.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ListaEventosActivity extends AppCompatActivity {

    private RecyclerView recyclerEventos;
    private EventoAdapter adapter;
    private List<Evento> listaEventos;
    private List<String> listaIds;
    private List<String> listaAutores;
    private FirebaseFirestore db;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_eventos);

        recyclerEventos = findViewById(R.id.rcyEventos);
        recyclerEventos.setLayoutManager(new LinearLayoutManager(this));

        listaEventos = new ArrayList<>();
        listaIds = new ArrayList<>();
        listaAutores = new ArrayList<>();
        adapter = new EventoAdapter(listaEventos, listaIds, listaAutores);
        recyclerEventos.setAdapter(adapter);

        searchView = findViewById(R.id.srchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
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
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    listaEventos.clear();
                    listaIds.clear();
                    listaAutores.clear();

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    Date fechaActual = new Date();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Evento evento = doc.toObject(Evento.class);
                        String fechaStr = evento.getFecha();
                        String uidCreador = doc.getString("uid_usuario");
                        Boolean esPublico = doc.getBoolean("publico");

                        try {
                            Date fechaEvento = sdf.parse(fechaStr);
                            if (fechaEvento != null && !fechaEvento.before(fechaActual) && Boolean.TRUE.equals(esPublico)) {
                                listaEventos.add(evento);
                                listaIds.add(doc.getId());

                                db.collection("users").document(uidCreador)
                                        .get()
                                        .addOnSuccessListener(userDoc -> {
                                            String alias = userDoc.getString("alias");
                                            if (alias == null) alias = "Usuario";
                                            listaAutores.add(alias);
                                            adapter.notifyDataSetChanged();
                                        });
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void filtrarEventos(String texto) {
        List<Evento> listaFiltrada = new ArrayList<>();
        List<String> listaIdsFiltrada = new ArrayList<>();
        List<String> listaAutoresFiltrada = new ArrayList<>();

        for (int i = 0; i < listaEventos.size(); i++) {
            Evento evento = listaEventos.get(i);
            String id = listaIds.get(i);
            String autor = listaAutores.get(i);

            if (evento.getTitulo().toLowerCase().contains(texto.toLowerCase()) ||
                    evento.getLugar().toLowerCase().contains(texto.toLowerCase())) {
                listaFiltrada.add(evento);
                listaIdsFiltrada.add(id);
                listaAutoresFiltrada.add(autor);
            }
        }

        adapter = new EventoAdapter(listaFiltrada, listaIdsFiltrada, listaAutoresFiltrada);
        recyclerEventos.setAdapter(adapter);
    }
}
