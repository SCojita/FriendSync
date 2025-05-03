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
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private ImageView btnSettings, btnCerrarSesion;
    private TextView tvBienvenido;
    private List<Noticia> listaNoticias;
    private NoticiasAdapter noticiasAdapter;
    private TextView tvSinNoticias;
    private LinearLayout botonesLayout;
    private RecyclerView recyclerNovedades;
    private ArticuloAdapter articuloAdapter;
    private List<Articulo> listaArticulos;
    private TextView tvSinNovedades;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnSettings = findViewById(R.id.btnSettings);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        tvBienvenido = findViewById(R.id.tvBienvenido);
        tvSinNoticias = findViewById(R.id.tvSinNoticias);
        botonesLayout = findViewById(R.id.botonesLayout);

        btnSettings.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, PreferencesActivity.class)));

        btnCerrarSesion.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            db.collection("users").document(user.getUid())
                    .get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            String alias = document.getString("alias");
                            tvBienvenido.setText("¡Hola, " + alias + "!");
                        }
                    });
        }

        RecyclerView recyclerNoticias = findViewById(R.id.recyclerNoticias);
        recyclerNoticias.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        listaNoticias = new ArrayList<>();
        noticiasAdapter = new NoticiasAdapter(listaNoticias);
        recyclerNoticias.setAdapter(noticiasAdapter);

        recyclerNoticias.setAlpha(0f);
        recyclerNoticias.animate().alpha(1f).setDuration(800).setStartDelay(300).start();

        cargarNoticiasDesdeFirestore();

        botonesLayout.setTranslationY(100f);
        botonesLayout.setAlpha(0f);
        botonesLayout.animate().translationY(0f).alpha(1f).setDuration(800).setStartDelay(400).start();

        MaterialButton btnCrearEvento = findViewById(R.id.btnCrearEvento);
        MaterialButton btnVerEventos = findViewById(R.id.btnVerEventos);
        MaterialButton btnMisEventos = findViewById(R.id.btnMisEventos);

        btnCrearEvento.setOnClickListener(v -> startActivity(new Intent(this, CrearEventoActivity.class)));
        btnVerEventos.setOnClickListener(v -> startActivity(new Intent(this, ListaEventosActivity.class)));
        btnMisEventos.setOnClickListener(v -> startActivity(new Intent(this, MisEventosActivity.class)));
        
        recyclerNovedades = findViewById(R.id.recyclerNovedades);
        recyclerNovedades.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        listaArticulos = new ArrayList<>();
        listaArticulos.add(new Articulo("Introducción a FriendSync", "Una visión general de la aplicación", 0));
        listaArticulos.add(new Articulo("Sobre nosotros", "Descubre cómo nació FriendSync", 1));
        listaArticulos.add(new Articulo("Forma parte del equipo", "¿Quieres unirte al proyecto?", 2));
        listaArticulos.add(new Articulo("Demo lanzada", "Explora las funcionalidades clave", 3));

        articuloAdapter = new ArticuloAdapter(this, listaArticulos);
        recyclerNovedades.setAdapter(articuloAdapter);

        tvSinNovedades = findViewById(R.id.tvSinNovedades);
        if (listaArticulos.isEmpty()) {
            tvSinNovedades.setVisibility(View.VISIBLE);
        } else {
            tvSinNovedades.setVisibility(View.GONE);
        }

        TextView tvSugerencias = findViewById(R.id.tvSugerencias);
        tvSugerencias.setPaintFlags(tvSugerencias.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvSugerencias.setOnClickListener(v -> {
            String url = "https://docs.google.com/forms/d/e/1FAIpQLSdWfQZjKgqY6JbevQ9R_1HpIy3mB7SAhnl3XnoQwcfPu-8x1w/viewform?usp=preview";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(android.net.Uri.parse(url));
            startActivity(intent);
        });

    }

    private void cargarNoticiasDesdeFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("eventos")
                .addSnapshotListener((querySnapshot, error) -> {
                    listaNoticias.clear();
                    tvSinNoticias.setVisibility(View.VISIBLE);

                    if (error != null || querySnapshot == null) return;

                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        String tituloEvento = doc.getString("titulo");
                        String fechaEvento = doc.getString("fecha");
                        String lugarEvento = doc.getString("lugar");
                        String uidCreador = doc.getString("uid_usuario");
                        String horaEvento = doc.getString("hora");
                        Boolean esPublico = doc.getBoolean("publico");

                        if (tituloEvento != null && fechaEvento != null && lugarEvento != null && uidCreador != null && Boolean.TRUE.equals(esPublico)) {
                            db.collection("users").document(uidCreador)
                                    .get()
                                    .addOnSuccessListener(userDoc -> {
                                        if (userDoc.exists()) {
                                            String aliasCreador = userDoc.getString("alias");
                                            if (aliasCreador == null) aliasCreador = "Usuario";

                                            String fechaLugar = (horaEvento != null && !horaEvento.isEmpty())
                                                    ? fechaEvento + " (" + horaEvento + ") - " + lugarEvento
                                                    : fechaEvento + " - " + lugarEvento;

                                            listaNoticias.add(new Noticia(aliasCreador, tituloEvento, fechaLugar, doc.getId()));
                                            noticiasAdapter.notifyDataSetChanged();

                                            tvSinNoticias.setVisibility(View.GONE);
                                        }
                                    });
                        }
                    }

                    if (listaNoticias.isEmpty()) {
                        tvSinNoticias.setVisibility(View.VISIBLE);
                    }
                });
    }
}
