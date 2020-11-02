package ejercicio3;

import java.util.Scanner;

class DatosGenerales {
  /* ------------------ */
  /* --- Constantes --- */
  /* ------------------ */
  static public final int METADATOS_RL_BYTES_LONGITUD = 1;
  static public final int METADATOS_RL_BYTES_NOMBRE = 60;
  static public final int METADATOS_MAX_BYTES_RL = METADATOS_RL_BYTES_LONGITUD + METADATOS_RL_BYTES_NOMBRE;
  static public final int METADATOS_MAX_CAMPOS = 10;
  static public final int METADATOS_MAX_BYTES_CAMPO_NOMBRE = 100;
  static public final int DATOS_NOMBRE_MAX_BYTES = 60;
  static public final String METADATOS_EXTENSION = ".mdat";
  static public final String DATOS_EXTENSION = ".dat";

  /* ----------------- */
  /* --- Variables --- */
  /* ----------------- */
  static public Scanner scanConsola = new Scanner(System.in, "UTF-8");
  
  
  /* ------------------ */
  /* --- Enumerados --- */
  /* ------------------ */
  static public enum tipoRetorno {
    OK, PUNTERO_NULO, ELEMENTO_EXISTENTE, ELEMENTO_INEXISTENTE, ES_ARCHIVO, NO_ES_ARCHIVO, ERROR
  }
}
