package org.stefancojita.friendsync;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class UneteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unete);

        TextView tvUnete = findViewById(R.id.tvTextoUnete);
        tvUnete.setText("FriendSync no solo es una aplicación, es una idea viva. Creemos en el poder de la colaboración y en construir soluciones desde la comunidad para la comunidad. " +
                "¿Te gusta la tecnología? ¿Te apasiona el diseño, la programación o simplemente tienes buenas ideas? Entonces tal vez este sea tu sitio.\n\n" +
                "Buscamos personas con ilusión, que quieran crecer profesionalmente en un entorno joven, motivado y comprometido con una visión: mejorar cómo las personas se organizan " +
                "y se conectan en el día a día.\n\n" +
                "Tanto si eres estudiante como profesional, en FriendSync valoramos tu energía, tus propuestas y tus ganas de aprender. Juntos podemos hacer que esta plataforma llegue más lejos, " +
                "se refine y evolucione con nuevas funciones y mejoras constantes.\n\n" +
                "Si quieres formar parte del equipo, no dudes en ponerte en contacto con nosotros. Escríbenos, comparte tus ideas o simplemente saluda. ¡Te estamos esperando!");
    }
}