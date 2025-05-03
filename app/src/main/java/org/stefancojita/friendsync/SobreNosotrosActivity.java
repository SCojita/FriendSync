package org.stefancojita.friendsync;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SobreNosotrosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sobre_nosotros);

        TextView tvSobreNosotros = findViewById(R.id.txtTextoSobreNosotros);
        // Añadimos el texto.
        tvSobreNosotros.setText("FriendSync nació como un proyecto de final de grado, impulsado por la pasión por la tecnología y la necesidad de resolver un problema real: " +
                "hacer más fácil quedar con amigos y organizar eventos de forma digital. Lo que empezó siendo una pequeña idea entre estudiantes fue creciendo poco a poco, " +
                "sumando funcionalidades, ilusión y apoyo por parte de compañeros, profesores y usuarios que creyeron en la utilidad de nuestra herramienta.\n\n" +
                "Hoy, FriendSync se consolida como una aplicación sencilla, eficiente y potente. Nuestro equipo se compone de personas comprometidas con el desarrollo ágil, " +
                "la experiencia de usuario y la mejora constante. Creemos en el trabajo bien hecho, en escuchar a los usuarios, y en mantener la esencia del proyecto: ayudar " +
                "a conectar personas y facilitar la gestión de sus momentos compartidos.\n\n" +
                "Cada línea de código, cada pantalla, y cada nueva función ha sido pensada con el objetivo de aportar valor y de ser útiles en la vida real. Agradecemos tu interés " +
                "y confianza en FriendSync. Seguimos aprendiendo, creciendo y evolucionando junto a ti.");
    }
}