package ejercicio3;
import java.io.*;

class MetodosGenerales {
  static public void limpiarPantalla() {
    /* Limpieza de terminal en linux 
	System.out.print("\033[H\033[2J");  
    System.out.flush();  
    /* */
	for(int i = 1; i <= 50; i++)
		System.out.println();
  }

  static public String entradaString(String texto, int maxBytes) throws UnsupportedEncodingException {
    String stringConsola;
    byte[] stringABytes;
    boolean repite;

    do {
      // Obtiene respuesta del usuario.
      limpiarPantalla();
      System.out.print(texto);
      stringConsola = new String(DatosGenerales.scanConsola.nextLine());

      // Convierte el string a la cantidad equivalente de bytes 
      // en UTF-8.
      stringABytes = stringConsola.getBytes("UTF-8");

      if(stringABytes.length == 0) {
        System.out.println();
        System.out.println("Debe ingresar al menos un carácter.");
        System.out.println("Presione Enter para volver a intentarlo.");
        DatosGenerales.scanConsola.nextLine();
        repite = true;
      } else if(stringABytes.length > maxBytes) {
      // Ingreso inválido.
        System.out.println();
        System.out.println("La cantidad de bytes es " + stringABytes.length + " y supera el rango máximo de bytes permitido (" + maxBytes + ")");
        System.out.println("Presione Enter para volver a intentarlo.");
        DatosGenerales.scanConsola.nextLine();
        repite = true;
      } else {
      // Ingreso válido.
        repite = false;
      }
    } while(repite);

    return stringConsola;
  }

  static public boolean entradaBinaria(String texto) {
    String stringConsola;
    boolean repite, retorno;

    do {
      // Obtiene respuesta del usuario.
      limpiarPantalla();
      System.out.print(texto);
      stringConsola = new String(DatosGenerales.scanConsola.nextLine());

      // Determina el tipo de respuesta.
      switch(stringConsola) {
        case "n":
        case "N":
          retorno = false;
          repite = false;
        break;
        case "s": 
        case "S":
          retorno = true;
          repite = false;
        break;
        default: 
          // Esta línea de código evita el error por la
          // posible no inicialización de "retorno".
          retorno = false;

          repite = true;
      }

      // Explica que hubo un error, en tal caso.
      if(repite) {
        System.out.println();
        System.out.println("Ingrese una opción correcta.");
        System.out.println("Presione Enter para volver a intentarlo.");
        DatosGenerales.scanConsola.nextLine();
      }
    } while(repite);

    return retorno;
  }
  
  static public boolean entradaBinariaSinLimpiarPantalla(String texto) {
    String stringConsola;
    boolean repite, retorno;

    do {
      // Obtiene respuesta del usuario.
      System.out.print(texto);
      stringConsola = new String(DatosGenerales.scanConsola.nextLine());

      // Determina el tipo de respuesta.
      switch(stringConsola) {
        case "n":
        case "N":
          retorno = false;
          repite = false;
        break;
        case "s": 
        case "S":
          retorno = true;
          repite = false;
        break;
        default: 
          // Esta línea de código evita el error por la
          // posible no inicialización de "retorno".
          retorno = false;

          repite = true;
      }

      // Explica que hubo un error, en tal caso.
      if(repite) {
        System.out.println();
        System.out.println("Ingrese una opción correcta.");
        System.out.println("Presione Enter para volver a intentarlo.");
        DatosGenerales.scanConsola.nextLine();
      }
    } while(repite);

    return retorno;
  }

  static public int entradaNumerica(String texto, int valorMinimo, int valorMaximo) {
    int intConsola;
    String stringConsola;
    boolean repite;

    // Inicializaciones.
    intConsola = 0;

    do {
      // Obtiene respuesta del usuario.
      limpiarPantalla();
      System.out.print(texto);
      stringConsola = DatosGenerales.scanConsola.nextLine();
      try {
        intConsola = Integer.parseInt(stringConsola);
        repite = false;
      } catch(NumberFormatException e) {
        System.out.println();
        System.out.println("Debe ingresar un valor numérico.");
        System.out.println("Presione Enter para volver a intentarlo.");
        DatosGenerales.scanConsola.nextLine();
        repite = true;
      }

      // Determina si el ingreso fue válido o no.
      if(!repite) {
        if(intConsola < valorMinimo || intConsola > valorMaximo) {
        // Ingreso inválido.
          System.out.println();
          System.out.println("El valor numérico debe estar entre " + valorMinimo + " y " + valorMaximo + ".");
          System.out.println("Presione Enter para volver a intentarlo.");
          DatosGenerales.scanConsola.nextLine();
          repite = true;
        }
      }
    } while(repite);

    return intConsola;
  }

  static public int entradaNumerica(String texto, int valorMinimo) {
    int intConsola;
    String stringConsola;
    boolean repite;

    // Inicializaciones.
    intConsola = 0;

    do {
      // Obtiene respuesta del usuario.
      limpiarPantalla();
      System.out.print(texto);
      stringConsola = DatosGenerales.scanConsola.nextLine();
      try {
        intConsola = Integer.parseInt(stringConsola);
        repite = false;
      } catch(NumberFormatException e) {
        System.out.println();
        System.out.println("Debe ingresar un valor numérico.");
        System.out.println("Presione Enter para volver a intentarlo.");
        DatosGenerales.scanConsola.nextLine();
        repite = true;
      }

      // Determina si el ingreso fue válido o no.
      if(!repite) {
        if(intConsola < valorMinimo) {
        // Ingreso inválido.
          System.out.println();
          System.out.println("El valor numérico debe ser mayor o igual a " + valorMinimo + ".");
          System.out.println("Presione Enter para volver a intentarlo.");
          DatosGenerales.scanConsola.nextLine();
          repite = true;
        }
      }
    } while(repite);

    return intConsola;
  }
}
