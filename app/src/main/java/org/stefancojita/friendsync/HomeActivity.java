package org.stefancojita.friendsync;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    // Declaración de variables.
    private ImageView btnAjustes, btnCerrarSesion;
    private TextView textBienvenido;
    private List<Noticia> listaNoticias;
    private NoticiasAdapter noticiasAdapter;
    private TextView textoSinNoticias;
    private LinearLayout botonesLayout;
    private RecyclerView recyclerNovedades;
    private ArticuloAdapter articuloAdapter;
    private List<Articulo> listaArticulos;
    private TextView tvSinNovedades;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Inicialización de variables.
        btnAjustes = findViewById(R.id.btnSettings);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        textBienvenido = findViewById(R.id.txtBienvenido);
        textoSinNoticias = findViewById(R.id.tvSinNoticias);
        botonesLayout = findViewById(R.id.botones);

        btnAjustes.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, PreferencesActivity.class))); // Abrimos la actividad de preferencias.

        // Configuración del botón de cerrar sesión.
        btnCerrarSesion.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Limpiamos la pila de actividades.
            startActivity(intent);
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance(); // Inicializamos Firestore.
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // Obtenemos el usuario actual.

        // Comprobamos si el usuario está autenticado.
        if (user != null) {
            // Obtenemos el alias del usuario desde Firestore.
            db.collection("users").document(user.getUid())
                    .get()
                    // Si la consulta es exitosa, actualizamos el saludo.
                    .addOnSuccessListener(document -> {
                        // Comprobamos si el documento existe.
                        if (document.exists()) {
                            String alias = document.getString("alias"); // Obtenemos el alias.
                            textBienvenido.setText("¡Hola, " + alias + "!"); // Actualizamos el saludo.
                        }
                    });
        }

        RecyclerView recyclerNoticias = findViewById(R.id.rcyNoticias); // Inicializamos el RecyclerView.
        recyclerNoticias.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)); // Configuramos el LayoutManager.

        listaNoticias = new ArrayList<>(); // Inicializamos la lista de noticias.
        noticiasAdapter = new NoticiasAdapter(listaNoticias); // Inicializamos el adaptador de noticias.
        recyclerNoticias.setAdapter(noticiasAdapter); // Asignamos el adaptador al RecyclerView.

        recyclerNoticias.setAlpha(0f); // Establecemos la opacidad inicial del RecyclerView.
        recyclerNoticias.animate().alpha(1f).setDuration(800).setStartDelay(300).start(); // Animamos la aparición del RecyclerView.

        cargarNoticiasDesdeFirestore(); // Cargamos las noticias desde Firestore.

        botonesLayout.setTranslationY(100f); // Establecemos la posición inicial del layout de botones.
        botonesLayout.setAlpha(0f); // Establecemos la opacidad inicial del layout de botones.
        botonesLayout.animate().translationY(0f).alpha(1f).setDuration(800).setStartDelay(400).start(); // Animamos la aparición del layout de botones.

        MaterialButton btnCrearEvento = findViewById(R.id.btnCrearEvento); // Inicializamos el botón de crear evento.
        MaterialButton btnVerEventos = findViewById(R.id.btnVerEventos); // Inicializamos el botón de ver eventos.
        MaterialButton btnMisEventos = findViewById(R.id.btnMisEventos); // Inicializamos el botón de mis eventos.

        // Configuramos los listeners de los botones.
        btnCrearEvento.setOnClickListener(v -> startActivity(new Intent(this, CrearEventoActivity.class)));
        btnVerEventos.setOnClickListener(v -> startActivity(new Intent(this, ListaEventosActivity.class)));
        btnMisEventos.setOnClickListener(v -> startActivity(new Intent(this, MisEventosActivity.class)));
        
        recyclerNovedades = findViewById(R.id.rcyNovedades); // Inicializamos el RecyclerView de novedades.
        recyclerNovedades.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)); // Configuramos el LayoutManager.

        listaArticulos = new ArrayList<>(); // Inicializamos la lista de artículos.

        // Agregamos artículos a la lista.
        listaArticulos.add(new Articulo("Introducción a FriendSync", "Una visión general de la aplicación", 0));
        listaArticulos.add(new Articulo("Sobre nosotros", "Descubre cómo nació FriendSync", 1));
        listaArticulos.add(new Articulo("Forma parte del equipo", "¿Quieres unirte al proyecto?", 2));
        listaArticulos.add(new Articulo("Demo lanzada", "Explora las funcionalidades clave", 3));
        listaArticulos.add(new Articulo("Errores comunes", "Posibles errores y soluciones", 4));

        Collections.reverse(listaArticulos); // Invertimos el orden de la lista para mostrar los artículos más recientes primero.
        articuloAdapter = new ArticuloAdapter(this, listaArticulos); // Inicializamos el adaptador de artículos.
        recyclerNovedades.setAdapter(articuloAdapter); // Asignamos el adaptador al RecyclerView.

        tvSinNovedades = findViewById(R.id.tvSinNovedades); // Inicializamos el TextView de "sin novedades".

        // Configuramos la visibilidad del TextView según si hay artículos en la lista.
        if (listaArticulos.isEmpty()) {
            tvSinNovedades.setVisibility(View.VISIBLE); // Si no hay artículos, mostramos el mensaje.
        } else {
            tvSinNovedades.setVisibility(View.GONE); // Si hay artículos, ocultamos el mensaje.
        }

        TextView tvSugerencias = findViewById(R.id.txtSugerencias); // Inicializamos el TextView de sugerencias.
        tvSugerencias.setPaintFlags(tvSugerencias.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG); // Subrayamos el texto.
        // Configuramos el listener del TextView de sugerencias.
        tvSugerencias.setOnClickListener(v -> {
            String url = "https://docs.google.com/forms/d/e/1FAIpQLSdWfQZjKgqY6JbevQ9R_1HpIy3mB7SAhnl3XnoQwcfPu-8x1w/viewform?usp=preview"; // URL del formulario de sugerencias.
            Intent intent = new Intent(Intent.ACTION_VIEW); // Creamos un intent para abrir la URL.
            intent.setData(android.net.Uri.parse(url)); // Establecemos la URL en el intent.
            startActivity(intent);
        });

    }

    // Creamos un método para cargar las noticias desde Firestore.
    private void cargarNoticiasDesdeFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance(); // Inicializamos Firestore.

        // Configuramos el listener para obtener las noticias.
        db.collection("eventos")
                // Ordenamos las noticias por fecha de creación.
                .addSnapshotListener((querySnapshot, error) -> {
                    listaNoticias.clear(); // Limpiamos la lista de noticias.
                    textoSinNoticias.setVisibility(View.VISIBLE); // Mostramos el mensaje de "sin noticias".

                    if (error != null || querySnapshot == null) return; // Si hay un error o la consulta es nula, salimos del método.

                    // Iteramos sobre los documentos obtenidos.
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        // Obtenemos los datos del documento en variables.
                        String tituloEvento = doc.getString("titulo");
                        String fechaEvento = doc.getString("fecha");
                        String lugarEvento = doc.getString("lugar");
                        String uidCreador = doc.getString("uid_usuario");
                        String horaEvento = doc.getString("hora");
                        Boolean esPublico = doc.getBoolean("publico");

                        // Comprobamos si los datos son válidos.
                        if (tituloEvento != null && fechaEvento != null && lugarEvento != null && uidCreador != null && Boolean.TRUE.equals(esPublico)) {
                            // Obtenemos el documento del creador del evento.
                            db.collection("users").document(uidCreador)
                                    .get()
                                    // Si la consulta es exitosa, obtenemos el alias del creador.
                                    .addOnSuccessListener(userDoc -> {
                                        // Comprobamos si el documento del creador existe.
                                        if (userDoc.exists()) {
                                            String aliasCreador = userDoc.getString("alias"); // Obtenemos el alias del creador.
                                            if (aliasCreador == null) aliasCreador = "Usuario"; // Si el alias es nulo, lo establecemos como "Usuario".

                                            // Comprobamos si la hora del evento es válida con ternarios.
                                            String fechaLugar = (horaEvento != null && !horaEvento.isEmpty())
                                                    ? fechaEvento + " (" + horaEvento + ") - " + lugarEvento
                                                    : fechaEvento + " - " + lugarEvento;

                                            listaNoticias.add(new Noticia(aliasCreador, tituloEvento, fechaLugar, doc.getId())); // Agregamos la noticia a la lista.
                                            noticiasAdapter.notifyDataSetChanged(); // Notificamos al adaptador que los datos han cambiado.
                                            textoSinNoticias.setVisibility(View.GONE); // Ocultamos el mensaje de "sin noticias".
                                        }
                                    });
                        }
                    }

                    // Si la lista de noticias está vacía, mostramos el mensaje de "sin noticias".
                    if (listaNoticias.isEmpty()) {
                        textoSinNoticias.setVisibility(View.VISIBLE);
                    }
                });
    }
}
