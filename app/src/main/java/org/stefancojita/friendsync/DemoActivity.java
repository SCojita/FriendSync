package org.stefancojita.friendsync;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class DemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        TextView txtDemo = findViewById(R.id.txtTextoDemo);
        // Añadimos el texto de la actividad.
        txtDemo.setText("¡La demo de FriendSync ya está disponible! Esta versión incluye las funciones principales para organizar tus eventos: registro, creación, unión a eventos, edición, " +
                "gestión de gastos, alias, notificaciones automáticas y más.\n\n" +
                "Durante este periodo de pruebas podrás explorar la aplicación, probar la interfaz, descubrir las noticias, gestionar tus propios eventos e incluso repartir los gastos " +
                "de manera sencilla.\n\n" +
                "Seguiremos actualizando la demo con mejoras visuales, pequeñas optimizaciones, y nuevas funcionalidades según los comentarios de nuestros usuarios y testers.\n\n" +
                "Gracias por acompañarnos en este camino y ser parte del inicio de FriendSync.\n\n" +
                "Esperamos que la experiencia sea intuitiva, rápida y útil. ¡Nos encantaría saber tu opinión!");
    }
}