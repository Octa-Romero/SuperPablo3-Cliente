# Changelog

Todos los cambios importantes del módulo cliente de Super Pablo 3 serán documentados en este archivo.

Las primeras versiones reflejan el desarrollo del juego original, utilizado como base para el módulo cliente creado el 4 de octubre de 2026. Desde entonces, el historial distingue la incorporación de contenido del juego de las funcionalidades propias del cliente: envío de entradas, recepción y representación de estados, interfaz cooperativa y recuperación de la conexión.

[Volver al repositorio del cliente](https://github.com/Octa-Romero/SuperPablo3-Cliente)

## [0.6.0] - 2026-11-17

### Agregado
- Gestión de desconexiones y reconexiones, con avisos al jugador y recuperación de la pantalla y el nivel al regresar a una partida.
- Solicitud y recepción de snapshots para reconstruir el estado visual de jugadores, enemigos, proyectiles y jefe tras una reconexión.
- Integración de las funcionalidades de muerte y regreso al Hub en el flujo del cliente.
- Animaciones de flotación y rotación de las reliquias en el cliente.
- Actualización del fondo del nivel final.

### Cambiado
- Separación de responsabilidades entre la comunicación UDP y la lógica de las pantallas del cliente durante la reconexión.

### Corregido
- Corrección de las rutas de los niveles para cargarlos correctamente.
- Ajustes en la actualización del juego durante la pausa.

## [0.5.1] - 2026-11-15

### Agregado
- Recepción y representación de los estados de Retumbar y sus ataques: manos, fuego del suelo, rayos, láseres, meteoritos y fueguitos.
- Incorporación de barras de vida del jefe en el HUD.
- Búsqueda automática del servidor y notificaciones del estado de conexión.
- Incorporación del logo del juego y actualización del spritesheet de Pablo.

### Cambiado
- Unificación del tratamiento visual de los fueguitos de lluvia y radiales, diferenciándolos por tipo.
- Ajustes en la eliminación visual del jefe al morir y en la presentación de la ventana del juego.

### Corregido
- Problemas de sincronización visual de Retumbar y de sus ataques.
- Errores de textura de los fueguitos, animaciones y parpadeo de invencibilidad.

## [0.5.0] - 2026-11-12

### Agregado
- Procesamiento de los estados recibidos de ambos jugadores, enemigos y orbes de Walter durante la partida cooperativa.
- Gestión de creación, actualización y eliminación de proyectiles y enemigos a partir de los mensajes del servidor.
- Sistema visual de espera y votación cooperativa para iniciar niveles, con colores en los portales según la selección.

### Cambiado
- Adaptación de las funcionalidades del juego principal al funcionamiento del cliente conectado por red.
- Reorganización de entidades en paquetes y centralización del uso de la dirección para su representación y actualización.

### Corregido
- Ajustes en el comportamiento de enemigos sobre pendientes.

## [0.4.1] - 2026-11-06

### Agregado
- Incorporación de sprites, sonidos y animaciones de Retumbar, junto con la representación de ataques de meteoritos, rayos y enemigos.
- Animaciones y ataques de la segunda fase del jefe.
- Animaciones para enemigos y pantalla final con créditos y relato.
- Gestión mediante listas de las creaciones, actualizaciones y eliminaciones de enemigos recibidas por red.

### Cambiado
- Ampliación de los efectos y animaciones de las fases de combate de Retumbar.

### Corregido
- Ajustes en la representación de la vida de las manos y los ataques adicionales de la primera fase del jefe.
- Correcciones de pendientes en los niveles 2 y 4 y del fondo del nivel 3.

## [0.4.0] - 2026-10-31

### Agregado
- Implementación de nuevos niveles, ampliando la progresión del juego hasta el nivel 7.
- Incorporación del NPC Pancrasio en el Hub, junto con su sistema de diálogo.
- Implementación del sistema de reliquias y su integración en los niveles.
- Incorporación de nuevos recursos y elementos para ampliar los escenarios del juego.
- Desarrollo de nuevos escenarios y mapas mediante Tiled.

### Cambiado
- Ampliación y reorganización de los niveles, incorporando nuevas áreas y mecánicas.
- Ajustes en las colisiones y el comportamiento de la cámara para mejorar la experiencia de juego.
- Integración de los nuevos niveles con el sistema de navegación y selección desde el Hub.
- Actualización de recursos gráficos y elementos visuales de los escenarios.

### Corregido
- Ajustes en las mecánicas de los niveles para mejorar su funcionamiento.
- Correcciones en la integración de recursos y escenarios.

## [0.3.4] - 2026-10-28

### Agregado
- Envío de acciones de movimiento y ataque al servidor y recepción de coordenadas para actualizar la posición de los jugadores.
- Animaciones de salto y mejoras en las animaciones de Walter.
- Primera incorporación de Retumbar y enemigos a distancia a la base visual del cliente.

### Cambiado
- Ajustes en el formato de los mensajes enviados al servidor.
- Separación de la lógica de animación y reorganización de los paquetes de entidades.

### Corregido
- Errores de funcionamiento, textos y presentación del Hub.

## [0.3.3] - 2026-10-12

### Agregado
- Creación del módulo cliente a partir de la estructura del juego original.
- Primera comunicación UDP con el servidor y mecanismos iniciales de conexión y reconexión.
- Integración del cliente de red con las distintas pantallas del juego.
- Sistema de espera y sincronización de ambos jugadores en la selección de personajes.
- Integración inicial del Hub y de la selección de niveles en el módulo cliente.

## [0.3.2] - 2026-09-22

### Agregado
- Incorporación del nivel 2 y nuevos recursos al juego original, posteriormente utilizado como base del cliente.
- Integración de niveles, cinemáticas y nuevas interfaces de menú en la base del proyecto.

## [0.3.1] - 2026-09-19

### Cambiado
- Desacoplada la navegación de pantallas mediante la interfaz `Navegable`, eliminando la dependencia directa con `Principal`.
- Unificación del uso de `GestorRecursos` para la carga de texturas reutilizables.
- Optimización del HUD para evitar reconstrucción innecesaria de corazones en cada frame.
- Reorganización del manejo de entrada eliminando asignaciones redundantes de `InputProcessor`.
- Limpieza del repositorio eliminando archivos generados (`build/`) y ajuste de `.gitignore`.
- Unificación del versionado entre `CHANGELOG` y `gradle.properties`.

### Corregido
- Corrección en el ciclo de vida de pantallas: gestión adecuada de `dispose()` en Screens.
- Ajuste en `InputMultiplexer` para evitar conflictos entre el juego y el menú de pausa.
- Corrección del respawn del jugador desacoplándolo de la resolución (`Gdx.graphics.getHeight()`).
- Actualización del HUD en `resize()` mediante `actualizarTamano()`.
- Liberación completa de entidades en `Nivel.dispose()`.
- Corrección en la gestión de recursos compartidos (ownership de texturas).

## [0.3.0] - 2026-09-04

### Agregado
- Pantalla de Game Over para finalización de partida.
- Pantalla de selección de personajes.
- Sistema de menú de opciones con configuración de resolución y fullscreen.
- Integración del menú de opciones dentro del menú de pausa.
- Sistema de ataque para Walter con animaciones mediante spritesheets.
- Ajustes visuales en botones y estilo general del menú.
- Sistema de pausa completo en PantallaJuego.

### Cambiado
- Refactor del sistema de configuración de resolución y fullscreen.
- Mejora en la arquitectura del menú principal usando ExtendViewport.
- Reorganización de la lógica de PantallaMenu para mejor inicialización.
- Optimización de carga de texturas de entidades.
- Refactor general del sistema de UI (HUD, menús y pantallas).
- Centralización de lógica de gameplay en Nivel.

### Corregido
- Fix de error al spawnear al jugador (Pablo).
- Correcciones en conflictos de merge en múltiples pantallas y sistemas.
- Liberación de texturas para evitar memory leaks.
- Eliminación de imports innecesarios.
- Ajustes en variables locales y limpieza de código.

## [0.2.4] - 2026-09-01

### Agregado
- Sistema de pausa en la pantalla de juego (PantallaJuego), permitiendo detener la lógica del gameplay.

### Cambiado
- Refactor de la creación y estructura de PantallaMenu para mejorar la organización del código y su inicialización.

## [0.2.3] - 2026-09-01

### Agregado
- Sistema de audio con música de fondo y efectos de sonido.
- Sistema de ataque del jugador (Pablo).
- Sprites placeholder para prototipado visual.
- Clase reutilizable para manejo de imágenes/sprites.

### Cambiado
- Mejora en la cámara del menú con renderizado independiente.
- Reorganización de estructura de carpetas del proyecto(texturas y niveles).

### Corregido
- Liberación de texturas para evitar memory leaks.
- Eliminación de imports innecesarios en HUD.
- Correcciones generales de gestión de recursos.

## [0.2.2] - 2026-08-31

### Agregado
- Sistema de ataque para el jugador (Pablo), incluyendo animaciones y lógica de combate.
- Implementación de HUD para visualización de estado del jugador.

### Cambiado
- Refactor general del sistema de entidades aplicando herencia para unificar comportamiento base.

## [0.2.1] - 2026-08-30

### Agregado
- Sistema de enemigos con lógica de persecución hacia el jugador.
- Implementación de ataques básicos y patrones de comportamiento enemigo.
- Sistema de vida para el jugador con gestión de daño.

### Cambiado
- Refactor de la clase Nivel para centralizar la lógica de update: movimiento del jugador, colisiones, enemigos y cámara del nivel.

## [0.2.0] - 2026-08-29

### Agregado
- Se integraron dos ramas principales del proyecto, una orientada al sistema de mapa (TiledMap, camara, viewport y menu de inicio) y otra orientada al jugador (movimiento y colisiones).
- Implementación de colisiones básicas del jugador con el mapa utilizando el layer de TiledMap.
- Mejoras en el sistema de movimiento del jugador, incluyendo la mecánica de salto.
- Incorporación del sistema de cámara y viewport para el seguimiento del jugador.
- Clases como Camara, Colisiones y Entrada.
- Estructura básica para sonidos y musicas.

## [0.1.4] - 2026-08-16

### Agregado
- Se modifico la version de Java a 21.
- build.gradle y gradle.properties fueron configurados correctamente para correr esta version.
- Se realizó un merge minimo para modificar lo ya establecido en el repositorio remoto.

## [0.1.3] - 2026-08-04

### Agregado
- Paquetes como entidades, recursos, utilidades y pantallas.
- Pantallas para el juego en general, el menu y pausa pero sin una lógica significativamente importante aún.
- Clase Render para manejar el batch de forma static.
- Lógica básica de movimiento para Jugador.

## [0.1.2] - 2026-07-15

### Agregado
- Creación del archivo `CHANGELOG.md`.

## [0.1.1] - 2026-07-14

### Agregado
- Activación de la Wiki del proyecto en GitHub.
- Incorporación de la propuesta inicial del videojuego en la Wiki.

## [0.1.0] - 2026-07-13

### Agregado
- Inicialización del proyecto con LibGDX.
- Creación del repositorio en GitHub.
- Configuración del archivo `.gitignore`.
- Creación del archivo `README.md`.
- Estructura base del proyecto preparada para el desarrollo del videojuego.
