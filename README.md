# Envio_de_ficheros_mediante_Sockets

# Sistema de Transferencia de Archivos

Este proyecto implementa un sistema de transferencia de archivos cliente-servidor utilizando sockets en Java. Permite a los usuarios subir, descargar y listar archivos en un servidor a través de una conexión TCP.

## Descripción

El sistema consta de dos componentes principales:

1. **Servidor (FileServer)**: Escucha en un puerto específico y gestiona las solicitudes de los clientes.
2. **Cliente (FileClient)**: Permite a los usuarios interactuar con el servidor para subir, descargar y listar archivos.

## Funcionalidades

- **UPLOAD**: Permite subir un archivo desde el cliente al servidor.
- **DOWNLOAD**: Permite descargar un archivo desde el servidor al cliente.
- **LIST**: Muestra los archivos disponibles en el servidor.
- **EXIT**: Cierra la conexión con el servidor.

## Instrucciones de Uso

### 1. Ejecutar el servidor

1. Abre el proyecto en IntelliJ IDEA.
2. Ejecuta la clase `FileServer` para iniciar el servidor. El servidor escuchará en el puerto `12345`.

### 2. Ejecutar el cliente

1. Abre otra instancia del proyecto en IntelliJ IDEA.
2. Ejecuta la clase `FileClient`.
3. Se te mostrará un menú de comandos disponibles: `UPLOAD`, `DOWNLOAD`, `LIST`, `EXIT`.

    - **UPLOAD**: Ingresa la ruta del archivo en tu sistema local que deseas subir al servidor.
    - **DOWNLOAD**: Ingresa el nombre del archivo en el servidor que deseas descargar.
    - **LIST**: Muestra los archivos disponibles en el servidor.
    - **EXIT**: Finaliza la conexión con el servidor.

### Ejemplo de uso

1. Ejecuta el servidor.
2. Ejecuta el cliente y selecciona el comando `UPLOAD` para subir un archivo.(ruta completa)
3. Luego, puedes usar el comando `LIST` para ver los archivos disponibles en el servidor.
4. Usa `DOWNLOAD` para descargar archivos desde el servidor. (solo el nombre del archivo)

## Notas

- Los archivos subidos se almacenarán en la carpeta `server_files/` en el directorio del servidor.
- Los archivos descargados se guardarán en una carpeta llamada `downloads/` en el directorio del cliente.
