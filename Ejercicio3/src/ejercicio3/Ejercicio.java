package ejercicio3;

import java.io.*;
import java.util.Scanner;

class Ejercicio {
  /* ------------------------ */
  /* --- Métodos privados --- */
  /* ------------------------ */
  static private int seleccionarArchivo() throws IOException {
    PunteroArchivo punteroArchivo;
    String texto, nombreArchivo;
    boolean respuesta;
    int retorno;
    
    // Se pide la ruta de acceso del archivo al usuario, sin la extensión.
    texto = "Ingrese el nombre del archivo que desea crear o del archivo que desea seleccionar, sin la extensión: ";
    nombreArchivo = MetodosGenerales.entradaString(texto, DatosGenerales.DATOS_NOMBRE_MAX_BYTES);

    // Se crea el puntero a archivo con la ruta de acceso obtenida.
    punteroArchivo = new PunteroArchivo(nombreArchivo);
    
    if(!punteroArchivo.isArchivo()) {
    // El puntero no apunta a un archivo.
      // Pregunta si se crea el archivo.
      texto = "El archivo \"" + nombreArchivo + DatosGenerales.DATOS_EXTENSION + "\" no existe.\r\nDesea crearlo? [S/N]: ";
      respuesta = MetodosGenerales.entradaBinaria(texto);

      if(respuesta == true) {
      // El usuario quiere crear el archivo.
        punteroArchivo.crearArchivo();
        
        retorno = manipularArchivo(nombreArchivo, punteroArchivo);
      } else retorno = -1;
    } else {
    // El puntero apunta a un archivo
      retorno = manipularArchivo(nombreArchivo, punteroArchivo);
    }
    
    return retorno;
  }

  // Comportamiento: devuelve la opción elegida.
  static private int manipularArchivo(String nombreArchivo, PunteroArchivo punteroArchivo) throws FileNotFoundException, IOException {
    boolean repite;
    int opcion, retorno;
    String texto;
    
    do {
      repite = true;
      
      // Pregunta al usuario qué desea hacer con el archivo.
      texto = "Archivo seleccionado: " + nombreArchivo + DatosGenerales.DATOS_EXTENSION + "\r\n";
      texto += "\r\n";
      texto += "1) Mostrar estructura de los metadatos\r\n";
      texto += "2) Mostrar registros habilitados\r\n";
      texto += "3) Mostrar registros inhabilitados\r\n";
      texto += "4) Mostrar todos los registros\r\n";
      texto += "5) Mostrar registro específico por posición\r\n";
      texto += "6) ABM de datos\r\n";
      texto += "\r\n";
      texto += "7) Seleccionar otro archivo\r\n";
      texto += "\r\n";
      texto += "0) Salir\r\n";
      texto += "\r\n";
      texto += "Indique su opción: ";
      opcion = MetodosGenerales.entradaNumerica(texto, 0, 7);

      // Determina qué hacer en base a la respuesta del usuario.
      switch(opcion) {
        case 1:
          punteroArchivo.mostrarMetadatos();
          retorno = 1;
        break;
        case 2:
          punteroArchivo.mostrarRegistrosHabilitados();
          retorno = 2;
        break;
        case 3:
          punteroArchivo.mostrarRegistrosInhabilitados();
          retorno = 3;
        break;
        case 4:
          punteroArchivo.mostrarTodosLosRegistros();
          retorno = 4;
        break;
        case 5:
          punteroArchivo.mostrarRegistro();
          retorno = 5;
        break;
        case 6:
          menuABM(punteroArchivo);
          retorno = 6;
        break;
        case 7:
          repite = false;
          retorno = 7;          
        break;
        case 0:
          repite = false;
          retorno = 0;
        break;
        default:
          repite = false;
          retorno = -1;          
      }        
    } while(repite);
    
    return retorno;
  }
  
  static private void menuABM(PunteroArchivo punteroArchivo) throws FileNotFoundException, IOException {
    String texto;
    int opcion;
    boolean repite;

    // Pregunta al usuario qué operación desea realizar sobre el archivo de datos.
    do {
      repite = true;
      // Realiza la pregunta al usuario y obtiene una respuesta.
      texto = "Indique a continuación qué desea hacer.\r\n\r\n1) Alta de datos\r\n2) Modificación de datos\r\n3) Baja de datos\r\n\r\n0) Volver\r\n\r\nIngrese la opción: ";
      opcion = MetodosGenerales.entradaNumerica(texto, 0, 3);

      // Determina qué acción se toma, en base a la respuesta del usuario.
      switch(opcion) {
        case 1: 
          punteroArchivo.altaRegistro();
        break;
        case 2:
          punteroArchivo.modificarRegistro();
        break;
        case 3:
          punteroArchivo.eliminarRegistro();
        break;
        case 0:
          repite = false;
      }
    } while(repite);
  }

  /* ------------------------ */
  /* --- Método principal --- */
  /* ------------------------ */
  public static void main(String args[]) throws IOException {
    boolean repite;
    int retorno;
    
    DatosGenerales.scanConsola = new Scanner(System.in, "UTF-8");
    
    do {
      repite = true;
      retorno = seleccionarArchivo();
      
      if(retorno == 0) repite = false;
      else if(retorno == -1)
        if(!MetodosGenerales.entradaBinaria("Desea crear/seleccionar otro archivo [S/N]?: ")) repite = false;      
    } while(repite);
    
    DatosGenerales.scanConsola.close();
    
    MetodosGenerales.limpiarPantalla();
  }  
}
