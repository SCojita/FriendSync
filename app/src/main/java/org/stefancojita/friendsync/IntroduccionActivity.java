package org.stefancojita.friendsync;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class IntroduccionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduccion);

        TextView tvIntro = findViewById(R.id.txtTextoIntroduccion);
        // Añadimos el texto.
        tvIntro.setText(
                "FriendSync es una plataforma moderna pensada para conectar amigos y grupos sociales a través de la organización de eventos. " +
                        "Nuestra aplicación nació con una idea muy simple: facilitar el proceso de planificar, comunicar y disfrutar de actividades compartidas entre personas.\n\n" +
                        "Con FriendSync, puedes crear eventos en segundos, invitar a tus amigos, establecer una hora, lugar y una descripción personalizada. " +
                        "Cada usuario puede unirse, salir o consultar la información de un evento con total libertad y transparencia. Además, los creadores pueden gestionar los asistentes " +
                        "y realizar modificaciones si algo cambia de última hora.\n\n" +
                        "Una de nuestras funciones más valoradas es la calculadora de gastos integrada. Si organizas un evento que conlleva costes, puedes activar esta opción " +
                        "y la aplicación te permitirá dividir el importe equitativamente entre todos los asistentes. Todo queda gestionado de forma sencilla, clara y automática.\n\n" +
                        "La aplicación incluye un sistema de notificaciones para recordarte la hora exacta de tus eventos y te mantiene informado a través de un carrusel de noticias dinámico. " +
                        "También puedes personalizar tu perfil, añadir un alias y gestionar la privacidad de tus eventos.\n\n" +
                        "FriendSync está diseñada para ser intuitiva, rápida y ligera. Ya sea una comida, un partido, una escapada de fin de semana o una fiesta improvisada, puedes confiar " +
                        "en FriendSync para que todo fluya sin complicaciones.\n\n" +
                        "Nuestro objetivo es hacerte la vida más fácil a la hora de compartir momentos. Gracias por usar FriendSync."
        );
    }
}
