package org.stefancojita.friendsync;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificacionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String tituloEvento = intent.getStringExtra("tituloEvento");
        String eventoId = intent.getStringExtra("eventoId");

        Intent i = new Intent(context, DetalleEventoActivity.class);
        i.putExtra("eventoId", eventoId);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String canalId = "canal_eventos";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(
                    canalId,
                    "Notificaciones de eventos",
                    NotificationManager.IMPORTANCE_HIGH
            );
            canal.setDescription("Recordatorios de eventos programados");
            notificationManager.createNotificationChannel(canal);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, canalId)
                .setSmallIcon(R.drawable.ic_notificacion)
                .setContentTitle("¡Ya es la hora del evento!")
                .setContentText("El evento \"" + tituloEvento + "\" comienza ahora.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(eventoId.hashCode(), builder.build());
    }
}