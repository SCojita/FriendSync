package org.stefancojita.friendsync;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ArticuloErroresActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articulo_errores);

        // Definimos los TextViews para el título y el contenido.
        TextView txtTitulo = findViewById(R.id.txtTituloErrores);
        TextView txtContenido = findViewById(R.id.txtContenidoErrores);

        txtTitulo.setText("Posibles errores que puedes tener en FriendSync y cómo solucionarlos"); // Título de la actividad.

        // Creamos un String largo que contiene la información sobre los errores y sus soluciones.
        String textoLargo = "En FriendSync nos esforzamos por ofrecerte la mejor experiencia, pero sabemos que pueden surgir algunos problemas. Aquí te explicamos cómo solucionarlos:\n\n" +
                "1. No recibes notificaciones:\n" +
                "   - Asegúrate de haber otorgado el permiso para mostrar notificaciones.\n" +
                "   - Comprueba que no tengas el modo “No molestar” activado.\n" +
                "   - En Android 12 o superior, también debes tener habilitado el permiso SCHEDULE_EXACT_ALARM.\n" +
                "   - Verifica que la hora del evento sea futura y no pasada.\n\n" +
                "2. La app no funciona correctamente en tu dispositivo:\n" +
                "   - FriendSync requiere al menos Android 8.0 (Oreo). Asegúrate de que tu sistema sea compatible.\n" +
                "   - Revisa que tengas conexión a Internet estable.\n\n" +
                "3. Al editar un evento no se actualiza:\n" +
                "   - La información de los eventos puede tardar unos segundos en sincronizar. Prueba cerrando y volviendo a abrir la app.\n" +
                "   - También puedes ir hacia atrás y volver a entrar en el evento para forzar la recarga.\n\n" +
                "4. El modo oscuro no se aplica completamente:\n" +
                "   - Actualmente, la funcionalidad está en desarrollo. Puedes seleccionar el tema claro como alternativa temporal.\n\n" +
                "Si el problema persiste, contáctanos a través del formulario de sugerencias en la página principal.\n\n" +
                "Gracias por utilizar FriendSync.";

        txtContenido.setText(textoLargo); // Establecemos el contenido del TextView con el texto largo.
    }
}
