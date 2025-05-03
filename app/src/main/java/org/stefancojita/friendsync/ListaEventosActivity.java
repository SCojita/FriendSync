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

    // Declaración de variables
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

        // Inicialización de variables.
        recyclerEventos = findViewById(R.id.rcyEventos);
        recyclerEventos.setLayoutManager(new LinearLayoutManager(this));
        listaEventos = new ArrayList<>();
        listaIds = new ArrayList<>();
        listaAutores = new ArrayList<>();
        adapter = new EventoAdapter(listaEventos, listaIds, listaAutores);
        recyclerEventos.setAdapter(adapter);

        searchView = findViewById(R.id.srchView); // Asegúrate de que el ID coincida con el de tu layout.
        // Configuración del SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // Sobreescribimos los métodos onQueryTextSubmit y onQueryTextChange para manejar la búsqueda.
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filtrarEventos(newText); // Llamamos al método para filtrar eventos según el texto ingresado.
                return true;
            }
        });

        db = FirebaseFirestore.getInstance(); // Inicializa Firestore

        cargarEventosDesdeFirestore(); // Llamamos al método para cargar eventos desde Firestore.
    }

    // Creamos un método para cargar eventos desde Firestore.
    private void cargarEventosDesdeFirestore() {
        // Usamos un listener para escuchar los cambios en la colección "eventos".
        db.collection("eventos")
                // ordenamos los eventos por fecha.
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    listaEventos.clear(); // Limpiamos la lista de eventos.
                    listaIds.clear(); // Limpiamos la lista de IDs.
                    listaAutores.clear(); // Limpiamos la lista de autores.

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()); // Formato de fecha.
                    Date fechaActual = new Date(); // Obtenemos la fecha actual.

                    // Recorremos los documentos obtenidos de Firestore.
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Evento evento = doc.toObject(Evento.class); // Convertimos el documento a un objeto Evento.
                        String fechaStr = evento.getFecha(); // Obtenemos la fecha del evento.
                        String uidCreador = doc.getString("uid_usuario"); // Obtenemos el UID del creador del evento.
                        Boolean esPublico = doc.getBoolean("publico"); // Obtenemos si el evento es público.

                        try {
                            Date fechaEvento = sdf.parse(fechaStr); // Convertimos la fecha del evento a un objeto Date.

                            // Comprobamos si la fecha del evento es válida y si el evento es público.
                            if (fechaEvento != null && !fechaEvento.before(fechaActual) && Boolean.TRUE.equals(esPublico)) {
                                listaEventos.add(evento); // Agregamos el evento a la lista.
                                listaIds.add(doc.getId()); // Agregamos el ID del evento a la lista.

                                // Obtenemos el alias del creador del evento.
                                db.collection("users").document(uidCreador)
                                        .get()
                                        // Obtenemos el documento del usuario.
                                        .addOnSuccessListener(userDoc -> {
                                            String alias = userDoc.getString("alias"); // Obtenemos el alias del usuario.

                                            // Si el alias es nulo, asignamos un valor por defecto.
                                            if (alias == null) alias = "Usuario";
                                            listaAutores.add(alias); // Agregamos el alias a la lista de autores.
                                            adapter.notifyDataSetChanged(); // Notificamos al adaptador que los datos han cambiado.
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
