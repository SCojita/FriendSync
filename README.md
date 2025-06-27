📱 FriendSync es una aplicación móvil para organizar eventos entre amigos de forma rápida y eficiente.

---

## 🧭 Introducción

**FriendSync** nace como una solución a los problemas cotidianos que surgen al intentar coordinar planes con amigos: mensajes perdidos, falta de confirmaciones, horarios confusos... 
La aplicación permite la gestión de eventos, calculadora de gastos básica, recibir notificaciones puntuales, mantener un historial de actividades pasadas, entre otras...

El objetivo principal es centralizar la organización de quedadas en una sola app intuitiva y colaborativa, eliminando el caos de los grupos de chat y mejorando la experiencia social digital.

---

## 🚀 Cómo integrar y ejecutar FriendSync en Android Studio

1. Descargar el .zip del proyecto y extraerlo en la carpeta de AndroidStudioProjects/

2. Ir a Android Studio y abrirlo desde la jerarquía de carpetas.

3. Entrar dentro del proyecto y esperar a que el Gradle prepare todo al IDE.

4. Seleccionar la ">" en la parte superior lateral derecha para ejecutar la aplicación. Es recomendable tener una versión de emulador más moderna. Si se quiere usar para móvil, basta con conectarlo por USB.

---

## 🛠️ Especificaciones técnicas

- **Lenguaje**: Java
- **IDE**: Android Studio (Flamingo o superior recomendado)
- **Base de datos**: Firebase Firestore
- **Autenticación**: Firebase Authentication
- **Notificaciones**: AlarmManager + BroadcastReceiver
- **Almacenamiento**: Firestore.
- **Compatibilidad mínima**: Android 7.0 (API 24)
- **Permisos requeridos**:
  - `POST_NOTIFICATIONS`
  - `SCHEDULE_EXACT_ALARM`
  - `USE_EXACT_ALARM`
  - `FOREGROUND_SERVICE`
