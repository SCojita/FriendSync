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
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    // Limpiamos las listas principales y notificamos al adaptador.
                    listaEventos.clear();
                    listaIds.clear();
                    listaAutores.clear();
                    adapter.notifyDataSetChanged();

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()); // Formato de fecha.
                    Date fechaActual = new Date(); // Obtenemos la fecha actual.

                    // Declaramos listas temporales para almacenar los datos mientras se resuelven los alias.
                    List<Evento> eventosTemporales = new ArrayList<>();
                    List<String> idsTemporales = new ArrayList<>();
                    List<String> uidsTemporales = new ArrayList<>();

                    // Verificamos que la respuesta no sea nula.
                    if (queryDocumentSnapshots == null) return;

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
                                eventosTemporales.add(evento); // Agregamos el evento a la lista temporal.
                                idsTemporales.add(doc.getId()); // Agregamos el ID del evento a la lista temporal.
                                uidsTemporales.add(uidCreador); // Agregamos el UID del creador a la lista temporal.
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    // Si no hay eventos que mostrar, salimos.
                    if (uidsTemporales.isEmpty()) return;

                    // Preparamos una lista de autores temporales del mismo tamaño.
                    List<String> autoresTemporales = new ArrayList<>();
                    for (int i = 0; i < uidsTemporales.size(); i++) {
                        autoresTemporales.add(""); // Inicializamos con cadenas vacías.
                    }

                    // Contador para saber cuándo hemos terminado de cargar todos los alias.
                    final int total = uidsTemporales.size();
                    final int[] contador = {0};

                    // Por cada UID de creador, buscamos su alias y lo colocamos en la posición correcta.
                    for (int i = 0; i < total; i++) {
                        final int index = i;
                        db.collection("users").document(uidsTemporales.get(i))
                                .get()
                                .addOnSuccessListener(userDoc -> {
                                    String alias = userDoc.getString("alias"); // Obtenemos el alias del usuario.
                                    if (alias == null) alias = "Usuario"; // Si es nulo, lo reemplazamos.
                                    autoresTemporales.set(index, alias); // Colocamos en su posición correcta.
                                    contador[0]++;

                                    // Cuando hayamos completado todos los alias, los asignamos a las listas principales.
                                    if (contador[0] == total) {
                                        listaEventos.addAll(eventosTemporales);
                                        listaIds.addAll(idsTemporales);
                                        listaAutores.addAll(autoresTemporales);
                                        adapter.notifyDataSetChanged(); // Notificamos al adaptador.
                                    }
                                });
                    }
                });
    }

    // Creamos un método para filtrar los eventos según el texto ingresado en el SearchView.
    private void filtrarEventos(String texto) {
        // Declaramos las listas filtradas.
        List<Evento> listaFiltrada = new ArrayList<>();
        List<String> listaIdsFiltrada = new ArrayList<>();
        List<String> listaAutoresFiltrada = new ArrayList<>();

        // Nos aseguramos de que todas las listas tengan el mismo tamaño antes de filtrar.
        int total = Math.min(Math.min(listaEventos.size(), listaIds.size()), listaAutores.size());

        // Recorremos con seguridad todas las listas.
        for (int i = 0; i < total; i++) {
            Evento evento = listaEventos.get(i);
            String id = listaIds.get(i);
            String autor = listaAutores.get(i);

            // Verificamos si el título o el lugar del evento contienen el texto ingresado.
            if (evento.getTitulo().toLowerCase().contains(texto.toLowerCase()) ||
                    evento.getLugar().toLowerCase().contains(texto.toLowerCase())) {
                listaFiltrada.add(evento);
                listaIdsFiltrada.add(id);
                listaAutoresFiltrada.add(autor);
            }
        }

        // Creamos un nuevo adaptador y lo asignamos al RecyclerView.
        adapter = new EventoAdapter(listaFiltrada, listaIdsFiltrada, listaAutoresFiltrada);
        recyclerEventos.setAdapter(adapter);
    }

}
