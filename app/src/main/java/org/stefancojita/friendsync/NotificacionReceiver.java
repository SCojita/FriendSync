package org.stefancojita.friendsync;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificacionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Declaramos las variables del titulo y el id del evento.
        String tituloEvento = intent.getStringExtra("tituloEvento");
        String eventoId = intent.getStringExtra("eventoId");

        Intent i = new Intent(context, DetalleEventoActivity.class);
        i.putExtra("eventoId", eventoId);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Nos aseguramos de que la actividad se abra en una nueva tarea y borre la anterior.

        // Creamos un PendingIntent para abrir la actividad DetalleEventoActivity al hacer clic en la notificación.
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE); // Obtenemos el NotificationManager.

        String canalId = "canal_eventos_v2"; // ID del canal de notificación.

        // Creamos el canal de notificación si la versión de Android es Oreo o superior.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Verificamos si el canal ya existe.
            NotificationChannel canal = new NotificationChannel(
                    canalId,
                    "Notificaciones de eventos",
                    NotificationManager.IMPORTANCE_HIGH // Importancia alta para mostrar la notificación como un pop-up.
            );
            canal.setDescription("Recordatorios de eventos programados"); // Descripción del canal.

            // Configuramos el sonido de la notificación.
            canal.setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                    // Configuramos los atributos de audio.
                    new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
            );

            canal.enableLights(true); // Habilitamos las luces LED.
            canal.enableVibration(true); // Habilitamos la vibración.

            notificationManager.createNotificationChannel(canal); // Creamos el canal de notificación.
        }

        // Creamos la notificación.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, canalId)
                .setSmallIcon(R.drawable.ic_notificacion)
                .setContentTitle("¡Ya es la hora del evento!")
                .setContentText("El evento \"" + tituloEvento + "\" comienza ahora.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(eventoId.hashCode(), builder.build()); // Mostramos la notificación con un ID único basado en el hash del eventoId.
    }
}
