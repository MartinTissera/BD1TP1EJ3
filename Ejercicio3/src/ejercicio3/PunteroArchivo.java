package ejercicio3;
import java.io.*;
import java.util.Arrays;

class PunteroArchivo {
  /* -------------------- */
  /* --- Aclaraciones --- */
  /* -------------------- */
  // Estructura del archivo de metadatos:
    // * El primer byte indica la cantidad de campos, como entero sin signo.
    // * Luego, habrá tantos registros lógicos de 61 bytes como
    // cantidad de campos, indicado en el primer byte, compuestos por:
    // 1 byte que indique la longitud del campo, como entero sin signo y 60 bytes
    // que indiquen el nombre del campo, como caracteres UTF-8.

  // Estructura del archivo de datos:
    // Cada registro lógico estará compuesto por tantos campos como lo indique el
    // primer byte del archivo de metadatos, y cada uno tendrá 1 bytes de habilitado
    // más una porción de bytes con una longitud igual a la indicada por el primer
    // byte del correspondiente RL del archivo de metadatos.

  /* ----------------- */
  /* --- Variables --- */
  /* ----------------- */
  private File archivoDatos, archivoMetadatos;
  private String rutaDeAcceso;

  /* --------------------- */
  /* --- Constructores --- */
  /* --------------------- */
  public PunteroArchivo() {
    inicializarPunteroArchivo();
  }

  public PunteroArchivo(String rutaDeAcceso) {
    this.rutaDeAcceso = rutaDeAcceso;
    archivoDatos = new File(rutaDeAcceso + DatosGenerales.DATOS_EXTENSION);
    archivoMetadatos = new File(rutaDeAcceso + DatosGenerales.METADATOS_EXTENSION);
  }

  /* ------------------------ */
  /* --- Métodos privados --- */
  /* ------------------------ */

  // Condiciones previas: la posición debe ser válida; el archivo de datos
  // debe estar abierto.
  // Comportamiento: devuelve un arreglo de dos dimensiones con la información
  // del registro con la posición especificada (la posición comienza en cero), de
  // forma tal que el arreglo tendrá tantos elementos como campos tenga el registro
  // lógico del archivo de datos, y en cada elemento habrá un arreglo con los bytes
  // de cada campo.
  private byte[][] obtenerRegistroPorPosicion(long posicionLogica, long longitudRL, byte[][] infoMetadatos, RandomAccessFile archivo) throws IOException {
    byte retorno[][];
    
    // Posiciona el cursor en el registro a obtener.
    archivo.seek((posicionLogica* longitudRL)+1);

    // Inicializa el arreglo que retorna.
    retorno = new byte[infoMetadatos.length][];

    /* Obtiene el registro lógico del archivo de datos */
    for(int i = 1; i <= infoMetadatos.length; i++) {
    // Por cada campo en los metadatos...
      // Reserva el espacio suficiente en el arreglo de retorno para
      // albergar la cantidad de bytes correspondientes al campo.
      retorno[i - 1] = new byte[infoMetadatos[i - 1][0]];
            
      for(int j = 1; j <= retorno[i - 1].length; j++)
      // Se repite tantas veces como bytes tenga el campo.
    	// Lee los bytes del registro lógico del archivo y los
    	// almacena en el arreglo de retorno.
        retorno[i - 1][j - 1] = archivo.readByte();
    }
    /* */

    return retorno;
  }
  
  // Condiciones previas: el puntero debe apuntar a un archivo existente.
  private String nombreArchivoMetadatos() {
    return archivoMetadatos.getName();
  }

  // Condiciones previas: el archivo de metadatos no tiene que estar
  // abierto.
  // Comportamiento: obtiene los metadatos en un arreglo de dos dimensiones,
  // de forma tal que en cada elemento de la primera dimensión habrá un arreglo
  // de 61 bytes que contendrá en el 1er byte la información de la longitud del
  // campo, y en los 60 restantes el nombre del campo.
  private byte[][] obtenerMetadatos() throws FileNotFoundException, IOException {
    FileInputStream fInStream;
    byte cantidadCampos[], infoMetadatos[][];

    // Lee, del archivo de metadatos, la cantidad de campos que tiene.
    fInStream = new FileInputStream(rutaDeAcceso + DatosGenerales.METADATOS_EXTENSION);
    cantidadCampos = new byte[1];
    fInStream.read(cantidadCampos);

    // Guarda los datos de los campos.
    infoMetadatos = new byte[cantidadCampos[0]][DatosGenerales.METADATOS_MAX_BYTES_RL];
    for(int i = 0; i < cantidadCampos[0]; i++)
      fInStream.read(infoMetadatos[i]);

    // Cierra el archivo de metadatos.
    fInStream.close();

    return infoMetadatos;
  }

  /* ------------------------ */
  /* --- Métodos públicos --- */
  /* ------------------------ */
  // Inicializa los valores de las variables, desvinculando
  // los puntero de los archivos.
  public void inicializarPunteroArchivo() {
    rutaDeAcceso = "";
    archivoDatos = null;
    archivoMetadatos = null;
  }

  // Condiciones previas: el puntero a archivo debe apuntar a un archivo existente.
  // Pregunta al usuario los datos del nuevo registro y lo guarda en el archivo
  // de datos.
  public void altaRegistro() throws FileNotFoundException, IOException {
    RandomAccessFile archivoAccesoBinario;
    byte[] stringABytes; 
    byte[][] infoMetadatos, rlArchivoDatos;
    String nombreDelCampo, stringConsola;
    boolean valido;
    int cantidadCampos;

    // Obtiene los metadatos y la cantidad de campos.
    infoMetadatos = obtenerMetadatos();
    cantidadCampos = infoMetadatos.length;

    // Crea un arreglo irregular de bytes para almacenar los datos del
    // registro nuevo.
    rlArchivoDatos = new byte[cantidadCampos + 1][];
    rlArchivoDatos[0] = new byte[1]; // El byte de habilitación.
    rlArchivoDatos[0][0] = 1; // Registro habilitado.

    // Pregunta y valida los datos del registro nuevo.
    for(byte i = 0; i < cantidadCampos; i++) {
      do {
        MetodosGenerales.limpiarPantalla();

        // Imprimimos los datos del campo.
        nombreDelCampo = new String(infoMetadatos[i], 1, 60, "UTF-8");
        System.out.println("Nombre del campo " + (i + 1) + ": " + nombreDelCampo);
        System.out.println("Longitud máxima del campo, en bytes: " + infoMetadatos[i][0]);
        System.out.println("Tipo de dato: texto");

        // Obtiene datos del usuario.
        System.out.println();
        System.out.print("Ingrese el dato del campo: ");
        stringConsola = DatosGenerales.scanConsola.nextLine();

        // Valida el dato.
        stringABytes = stringConsola.getBytes("UTF-8");
        if(stringABytes.length == 0) {
          System.out.println("Debe ingresar al menos un carácter.");
          System.out.print("Presione Enter para volver a intentarlo.");
          DatosGenerales.scanConsola.nextLine();
          valido = false;
        } else if (stringABytes.length > infoMetadatos[i][0]) {
          System.out.println();
          System.out.println("La cantidad de bytes ingresados no puede superar los " + infoMetadatos[i][0] + ".");
          System.out.println("Usted ingresó " + stringABytes.length + " bytes.");
          System.out.println();
          System.out.print("Presione Enter para volver a intentarlo.");
          DatosGenerales.scanConsola.nextLine();
          valido = false;
        } else valido = true;
      } while(!valido);
      
      // Guarda el dato en el arreglo de bytes de memoria.
      rlArchivoDatos[i + 1] = new byte[infoMetadatos[i][0]];
      for(int j = 0; j < stringABytes.length; j++)
        rlArchivoDatos[i + 1][j] = stringABytes[j];
    }

    // Mostrar mensaje de OK.
    archivoAccesoBinario = new RandomAccessFile(this.archivoDatos, "rw");
    archivoAccesoBinario.seek(archivoAccesoBinario.length());
    for(int i = 0; i <= cantidadCampos; i++) {
      archivoAccesoBinario.write(rlArchivoDatos[i]);
    }
    archivoAccesoBinario.close();
    MetodosGenerales.limpiarPantalla();
    System.out.println("El registro nuevo ha sido guardado exitosamente.");
    System.out.println("Presione Enter para continuar.");
    DatosGenerales.scanConsola.nextLine();
  }

  // Condiciones previas: el puntero a archivo debe apuntar a un archivo existente.
  // Comportamiento: si el archivo está vacío, muestra el mensaje; si no, pregunta
  // la posición del registro a modificar. Si la posición seleccionada no es válida, 
  // pide otra posición; si no, procede a la modificación. Para la modificación, va
  // mostrando campo por campo y va preguntando si se quiere modificar. Si el usuario
  // no quiere modificar el campo actual, muestra el siguiente o finaliza, aplicando
  // las modificaciones, si las hubo; si no, si el usuario quiere modificar el campo
  // actual, pide al usuario los nuevos datos, validando.
  public void modificarRegistro() throws IOException {
	boolean seguirModificando, huboModificaciones;
	RandomAccessFile archivoAccesoDirecto;
	int posicion, cantidadCampos, indiceDeCampo, longitudRLDatos, registroHabilitado;
	long cantidadRL_archivoDatos;
	byte infoMetadatos[][], infoDatos[][];
	String bytesAString, dato;
	
	// Inicializaciones.
    archivoAccesoDirecto = new RandomAccessFile(this.archivoDatos, "rw");

    if(archivoAccesoDirecto.length() != 0) {
      // Obtiene los metadatos y calcula la cantidad de campos que tendrá
      // cada registro lógico del archivo de datos.
      infoMetadatos = obtenerMetadatos();
      cantidadCampos = infoMetadatos.length + 1; // Suma 1 por el byte de habilitación.
      
      // Obtiene la longitud del registro lógico del archivo de datos y la
      // cantidad de registros lógicos que tiene el archivo de datos.
      longitudRLDatos = 1; // El primer byte es el del byte de habilitación.
      for(int i = 1; i < cantidadCampos; i++) {
        longitudRLDatos += infoMetadatos[i - 1][0];
      }
      cantidadRL_archivoDatos = archivoAccesoDirecto.length() / longitudRLDatos;
      
      do {
      // Mientras se quiera, se siguen modificando registros.        
        // Inicialización.
        seguirModificando = false;
        huboModificaciones = false;
        
        // Pide la posición.
        posicion = MetodosGenerales.entradaNumerica("Ingrese la posición del registro a modificar: ", 0);
        System.out.println();
  	
        if(posicion >= cantidadRL_archivoDatos) {
        // Posición no es válida: muestra mensaje.
          System.out.println("La posición lógica " + posicion + " es inválida.");
          System.out.println();
          System.out.println("Presione Enter para volver a intentarlo.");
          DatosGenerales.scanConsola.nextLine();
        } else {
        // La posición es válida.
          // Lee el byte de habilitación.
          archivoAccesoDirecto.seek(posicion * longitudRLDatos);
          registroHabilitado = archivoAccesoDirecto.read();
          
          if(registroHabilitado == 0)
          // El registro no está habilitado.
            System.out.println("El registro seleccionado no está habilitado.");
          else {
          // El registro está habilitado.
            // Obtiene los datos del registro del archivo de datos,
            // con la posición lógica especificada.
            infoDatos = obtenerRegistroPorPosicion(posicion, longitudRLDatos, infoMetadatos, archivoAccesoDirecto);
            
            // Inicializo el índice de campo.
            indiceDeCampo = 1;
            
            // Mientras haya siguiente campo.
            while(indiceDeCampo <= (cantidadCampos - 1)) {
              // Muestra el campo.
              MetodosGenerales.limpiarPantalla();
              bytesAString = new String(infoMetadatos[indiceDeCampo - 1], 1, 60, "UTF-8");
              
              System.out.print(bytesAString + ": ");
              bytesAString = new String(infoDatos[indiceDeCampo - 1], "UTF-8");
                           
              System.out.println(bytesAString);
              System.out.println();
              
              // Pregunta si se quiere modificar.
             
              if(MetodosGenerales.entradaBinariaSinLimpiarPantalla("Desea modificar este dato [S/N]?: ")) {
              // El usuario desea modificar el dato.              
    		      // Pide los datos y valida.
                bytesAString = new String(infoMetadatos[indiceDeCampo - 1], 1, 60, "UTF-8");
                dato = MetodosGenerales.entradaString("Ingrese los datos del campo " + bytesAString + " (máx. " + infoMetadatos[indiceDeCampo - 1][0] + " bytes): ", infoMetadatos[indiceDeCampo - 1][0]);
                
                // Guarda el dato nuevo en el arreglo en memoria.              
                infoDatos[indiceDeCampo - 1] = Arrays.copyOf(dato.getBytes("UTF-8"), infoMetadatos[indiceDeCampo - 1][0]);              
                
                huboModificaciones = true;
              }
              
              indiceDeCampo++;
            }
            
            if(huboModificaciones) {
            // Si hubo modificaciones
    	  	    // Se guardan en el archivo.
              archivoAccesoDirecto.seek(posicion * longitudRLDatos + 1);
              for(int i = 1; i <= infoDatos.length; i++)
                archivoAccesoDirecto.write(infoDatos[i - 1]);
              MetodosGenerales.limpiarPantalla();
              System.out.println("El registro ha sido modificado exitosamente.");
            } else {            
              MetodosGenerales.limpiarPantalla();
              System.out.println("No se realizó ninguna modificación.");
            }
          }
        }
        
  	  // Pregunta al usuario si quiere seguir modificando registros.
      System.out.println();
  	  seguirModificando = MetodosGenerales.entradaBinariaSinLimpiarPantalla("Desea modificar otro registro [S/N]?: ");
  	} while(seguirModificando);
  } else {
    MetodosGenerales.limpiarPantalla();
    System.out.println("El archivo de datos está vacío.");
    System.out.println();
    System.out.println("Presione Enter para volver al menú anterior.");
    DatosGenerales.scanConsola.nextLine();
  }
    
  // Cerramos el archivo de acceso directo.
  archivoAccesoDirecto.close();
}
  
  // Condiciones previas: el puntero a archivo debe apuntar a un archivo existente.
  // Comportamiento: pregunta al usuario la posición lógica del registro; si es una
  // posición válida y el registro está habilitado, se muestran los datos y se
  // pregunta si se quiere eliminar; si no, muestra el debido mensaje. Si el usuario
  // desea eliminar el registro, se modifica el bit de habilitación del registro.
  public void eliminarRegistro() throws FileNotFoundException, IOException {
    RandomAccessFile archivoAccesoDirecto;
    byte longitudCampo, infoMetadatos[][], infoDatos[][], habilitado[][];
    String nombreCampo, contenidoCampo;
    boolean respuesta, repite;
    int cantidadCampos;
    long longitudRLDatos, posicion, cantidadRL_archivoDatos;

    // Inicializaciones.
    archivoAccesoDirecto = new RandomAccessFile(this.archivoDatos, "rw");

    if(archivoAccesoDirecto.length() != 0) {
	    // Obtiene los metadatos y calcula la cantidad de campos que tendrá
	    // cada registro lógico del archivo de datos.
	    infoMetadatos = obtenerMetadatos();
	    cantidadCampos = infoMetadatos.length + 1; // Suma 1 por el byte de habilitación.
	    
	    // Obtiene la longitud del registro lógico del archivo de datos y la
	    // cantidad de registros lógicos que tiene el archivo de datos.
	    longitudRLDatos = 1; // El primer byte es el del byte de habilitación.
	    for(int i = 1; i < cantidadCampos; i++)
	      longitudRLDatos += infoMetadatos[i - 1][0];
	    cantidadRL_archivoDatos = archivoAccesoDirecto.length() / longitudRLDatos;
	
	    do {
	      repite = false;
	      // Pregunta al usuario la posición lógica del registro a eliminar.
	      MetodosGenerales.limpiarPantalla();
	      posicion = MetodosGenerales.entradaNumerica("Ingrese la posición lógica del registro, a partir del cero: ", 0);
	
	      if(posicion >= cantidadRL_archivoDatos) {
	      // Es una posición inválida: muestra mensaje.	      
	        System.out.println();
	    	System.out.println("La posición lógica " + posicion + " es inválida.");
	        System.out.println("Presione Enter para volver a intentarlo.");
	        DatosGenerales.scanConsola.nextLine();
	      } else {
	      // Es una posición válida.
	        infoDatos = obtenerRegistroPorPosicion(posicion, longitudRLDatos, infoMetadatos, archivoAccesoDirecto);
	        
	        archivoAccesoDirecto.seek(posicion * longitudRLDatos);
	          int registroHabilitado = archivoAccesoDirecto.read();
	               
	        
	        if(registroHabilitado == 0) {
	        // El registro no está habilitado.	
	          // Pregunta si quiere intentar con otro registro.
	          System.out.println();
	          System.out.println("El registro seleccionado no está habilitado.");
	          System.out.println();
	          respuesta = MetodosGenerales.entradaBinariaSinLimpiarPantalla("Desea intentar con otro registro [S/N]?: ");
	          if(respuesta == true) repite = true;
	          else repite = false;
	        } else {
	        // El registro está habilitado.
	          // Muestra los datos del registro
	          MetodosGenerales.limpiarPantalla();
	          for(int i = 1; i <= infoMetadatos.length; i++) {
	            // Convierte a String el nombre del campo.
	            longitudCampo = infoMetadatos[i - 1][0];
	            nombreCampo = new String(infoMetadatos[i - 1], 1, 60, "UTF-8");
	            contenidoCampo = new String(infoDatos[i - 1], 0, longitudCampo, "UTF-8");
	            System.out.println(nombreCampo + ": " + contenidoCampo);
	          }
	
	          // Pregunta si quiere eliminar
	          System.out.println();
	          respuesta = MetodosGenerales.entradaBinariaSinLimpiarPantalla("Desea eliminar el registro [S/N]?: ");
	          if(respuesta == true) {
	            archivoAccesoDirecto.seek(posicion * longitudRLDatos);
	            archivoAccesoDirecto.write(0);
	            System.out.println();
	            System.out.println("El registro ha sido eliminado");
	            System.out.println();
	          } else MetodosGenerales.limpiarPantalla();	          
	        }
	      }
	      
	      // Pregunta si quiere eliminar otro registro.
          respuesta = MetodosGenerales.entradaBinariaSinLimpiarPantalla("Desea eliminar otro registro [S/N]?: ");
          if(respuesta == true) repite = true;
          else repite = false;
	    } while(repite);
    } else {
    	MetodosGenerales.limpiarPantalla();
    	System.out.println("El archivo de datos está vacío.");
    	System.out.println();
    	System.out.println("Presione Enter para volver al menú anterior.");
    	DatosGenerales.scanConsola.nextLine();
    }
    
    archivoAccesoDirecto.close();
  }

  // Condiciones previas: el puntero debe apuntar a un archivo existente.
  // Muestra la estructura de los metadatos.
  public void mostrarMetadatos() throws FileNotFoundException, IOException {
    int cantidadCampos, longitudCampo;
    byte[] bytesNombreCampo;
    String nombreCampo;
    RandomAccessFile archivoAccesoDirecto;

    // Inicializaciones.
    bytesNombreCampo = new byte[DatosGenerales.METADATOS_RL_BYTES_NOMBRE];

    // Abre el archivo de metadatos para lectura.
    archivoAccesoDirecto = new RandomAccessFile(archivoMetadatos, "rw");

    // Muestra la estructura del archivo de metadatos.
    MetodosGenerales.limpiarPantalla();
      
    // Leo el valor que determina la cantidad de campos del archivo
    // de metadatos.
    cantidadCampos = archivoAccesoDirecto.read();

    // Imprime la cantidad de campos.
    System.out.print("Cantidad de campos: ");
    System.out.println(cantidadCampos);

    // Obtiene e imprime los datos de los registros.
    for(int i = 1; i <= cantidadCampos; i++) {  
      // Obtiene los datos del registro.
      longitudCampo = archivoAccesoDirecto.read();
      archivoAccesoDirecto.read(bytesNombreCampo);
      nombreCampo = new String(bytesNombreCampo, "UTF-8");

      // Imprime los datos del registro.
      System.out.println("\tNúmero de campo: " + i);
      System.out.println("\tLongitud del campo: " + longitudCampo + ((longitudCampo == 1) ? " byte" : " bytes"));
      System.out.println("\tNombre del campo: " + nombreCampo);
      System.out.println();
    }

    // Mostramos el tamaño en bytes del archivo de metadatos.
    System.out.println("Tamaño de archivo: " + archivoAccesoDirecto.length() + " bytes.");

    // Cerramos el archivo.
    archivoAccesoDirecto.close();

    // Paramos la ejecución para que el usuario vea los datos.
    System.out.println();
    System.out.println("Presione Enter para volver al menú anterior.");
    DatosGenerales.scanConsola.nextLine();

    return;
  }
  
  // Condiciones previas: el puntero debe apuntar a un archivo existente.
  public void mostrarRegistrosHabilitados() throws FileNotFoundException, IOException, UnsupportedEncodingException {
    RandomAccessFile archivoAccesoDirecto;
    int cantidadCampos, contadorPosicion;
    byte[][] bytesRLDatos, bytesRLMetadatos;
    String bytesAString;
    boolean hayHabilitados;

    // Obtenemos los metadatos del archivo.
    archivoAccesoDirecto = new RandomAccessFile(archivoMetadatos, "rw");
    cantidadCampos = archivoAccesoDirecto.read();
    bytesRLMetadatos = new byte[cantidadCampos][DatosGenerales.METADATOS_MAX_BYTES_RL];
    for(int i = 1; i <= cantidadCampos; i++)
      archivoAccesoDirecto.read(bytesRLMetadatos[i - 1]);
    
    // Reservamos espacio en memoria para dichas cantidades de bytes.
    bytesRLDatos = new byte[cantidadCampos + 1][];
    for(int i = 1; i <= cantidadCampos; i++) {      
      // Reservamos espacio en memoria para dicha cantidad de bytes.
      bytesRLDatos[i] = new byte[bytesRLMetadatos[i - 1][0]];
    }

    // Sumamos un campo más que corresponde al campo de habilitación,
    // y que se encuentra en el archivo de datos.
    cantidadCampos++;
    bytesRLDatos[0] = new byte[1];

    // Cerramos el archivo de metadatos.
    archivoAccesoDirecto.close();

    // Abrimos el archivo de datos.
    archivoAccesoDirecto = new RandomAccessFile(archivoDatos, "rw");

    MetodosGenerales.limpiarPantalla();
    if(archivoAccesoDirecto.length() == 0) {
    // El archivo de datos está vacío.
      System.out.println("El archivo de datos está vacío.");
      System.out.println();
    } else {
    // El archivo de datos no está vacío.
      // Obtenemos e imprimimos los registros lógicos del archivo de datos.
      System.out.println("--- Registros habilitados ---");
      System.out.println();
      contadorPosicion = -1;
      hayHabilitados = false;
      do {
    	++contadorPosicion;
    	  
        // Obtenemos el siguiente registro lógico.
        for(int i = 1; i <= cantidadCampos; i++) {
          archivoAccesoDirecto.read(bytesRLDatos[i - 1]);
        }

        // Si está habilitado, mostramos sus datos.
        if(bytesRLDatos[0][0] == 1) {
          hayHabilitados = true;
          System.out.println("Registro con posición lógica: " + contadorPosicion);
          for(int i = 1; i < cantidadCampos; i++) {
            bytesAString = new String(bytesRLMetadatos[i - 1], 1, bytesRLMetadatos[i - 1].length - 1, "UTF-8");
            System.out.print(bytesAString + ": ");
            bytesAString = new String(bytesRLDatos[i], "UTF-8");
            System.out.println(bytesAString);
          }
          System.out.println();
        }
      } while(archivoAccesoDirecto.getFilePointer() != archivoAccesoDirecto.length());
      
      if(!hayHabilitados) {
        System.out.println("No hay registros habilitados.");
        System.out.println();
      }
    }

    archivoAccesoDirecto.close();
    
    // Para la ejecución para que el usuario vea el resultado.
    System.out.println("Presione Enter para volver al menú anterior.");
    DatosGenerales.scanConsola.nextLine();
  }
  
   // Condiciones previas: el puntero debe apuntar a un archivo existente.
   public void mostrarRegistrosInhabilitados() throws FileNotFoundException, IOException, UnsupportedEncodingException {
     RandomAccessFile archivoAccesoDirecto;
     int cantidadCampos, contadorPosicion;
     byte[][] bytesRLDatos, bytesRLMetadatos;
     String bytesAString;
     boolean hayInhabilitados;
  
     // Obtenemos los metadatos del archivo.
     archivoAccesoDirecto = new RandomAccessFile(archivoMetadatos, "rw");
     cantidadCampos = archivoAccesoDirecto.read();
     bytesRLMetadatos = new byte[cantidadCampos][DatosGenerales.METADATOS_MAX_BYTES_RL];
     for(int i = 1; i <= cantidadCampos; i++)
       archivoAccesoDirecto.read(bytesRLMetadatos[i - 1]);
     
     // Reservamos espacio en memoria para dichas cantidades de bytes.
     bytesRLDatos = new byte[cantidadCampos + 1][];
     for(int i = 1; i <= cantidadCampos; i++) {      
       // Reservamos espacio en memoria para dicha cantidad de bytes.
       bytesRLDatos[i] = new byte[bytesRLMetadatos[i - 1][0]];
     }
  
     // Sumamos un campo más que corresponde al campo de habilitación,
     // y que se encuentra en el archivo de datos.
     cantidadCampos++;
     bytesRLDatos[0] = new byte[1];
  
     // Cerramos el archivo de metadatos.
     archivoAccesoDirecto.close();
  
     // Abrimos el archivo de datos.
     archivoAccesoDirecto = new RandomAccessFile(archivoDatos, "rw");
  
     MetodosGenerales.limpiarPantalla();
     if(archivoAccesoDirecto.length() == 0) {
     // El archivo de datos está vacío.
       System.out.println("El archivo de datos está vacío.");
       System.out.println();
     } else {
     // El archivo de datos no está vacío.
       // Obtenemos e imprimimos los registros lógicos del archivo de datos.
       System.out.println("--- Registros inhabilitados ---");
       System.out.println();
       contadorPosicion = -1;
       hayInhabilitados = false;
       do {
         ++contadorPosicion;
           
         // Obtenemos el siguiente registro lógico.
         for(int i = 1; i <= cantidadCampos; i++) {
           archivoAccesoDirecto.read(bytesRLDatos[i - 1]);
         }
  
         // Si está inhabilitado, mostramos sus datos.
         if(bytesRLDatos[0][0] == 0) {
           hayInhabilitados = true;
           System.out.println("Registro con posición lógica: " + contadorPosicion);
           for(int i = 1; i < cantidadCampos; i++) {
             bytesAString = new String(bytesRLMetadatos[i - 1], 1, bytesRLMetadatos[i - 1].length - 1, "UTF-8");
             System.out.print(bytesAString + ": ");
             bytesAString = new String(bytesRLDatos[i], "UTF-8");
             System.out.println(bytesAString);
           }
           System.out.println();
         }
       } while(archivoAccesoDirecto.getFilePointer() != archivoAccesoDirecto.length());
       
       if(!hayInhabilitados) {
         System.out.println("No hay registros inhabilitados.");
         System.out.println();
       }
     }
  
     archivoAccesoDirecto.close();
     
     // Para la ejecución para que el usuario vea el resultado.
     System.out.println("Presione Enter para volver al menú anterior.");
    DatosGenerales.scanConsola.nextLine();
  }
 
  // Condiciones previas: el puntero debe apuntar a un archivo existente.
  public void mostrarTodosLosRegistros() throws FileNotFoundException, IOException, UnsupportedEncodingException {
   RandomAccessFile archivoAccesoDirecto;
   int cantidadCampos, contadorPosicion;
   byte[][] bytesRLDatos, bytesRLMetadatos;
   String bytesAString;
  
   // Obtenemos los metadatos del archivo.
   archivoAccesoDirecto = new RandomAccessFile(archivoMetadatos, "rw");
   cantidadCampos = archivoAccesoDirecto.read();
   bytesRLMetadatos = new byte[cantidadCampos][DatosGenerales.METADATOS_MAX_BYTES_RL];
   for(int i = 1; i <= cantidadCampos; i++)
     archivoAccesoDirecto.read(bytesRLMetadatos[i - 1]);
   
   // Reservamos espacio en memoria para dichas cantidades de bytes.
   bytesRLDatos = new byte[cantidadCampos + 1][];
   for(int i = 1; i <= cantidadCampos; i++) {      
     // Reservamos espacio en memoria para dicha cantidad de bytes.
     bytesRLDatos[i] = new byte[bytesRLMetadatos[i - 1][0]];
   }
  
   // Sumamos un campo más que corresponde al campo de habilitación,
   // y que se encuentra en el archivo de datos.
   cantidadCampos++;
   bytesRLDatos[0] = new byte[1];
  
   // Cerramos el archivo de metadatos.
   archivoAccesoDirecto.close();
  
   // Abrimos el archivo de datos.
   archivoAccesoDirecto = new RandomAccessFile(archivoDatos, "rw");
  
   MetodosGenerales.limpiarPantalla();
   if(archivoAccesoDirecto.length() == 0) {
   // El archivo de datos está vacío.
     System.out.println("El archivo de datos está vacío.");
     System.out.println();
   } else {
   // El archivo de datos no está vacío.
     // Obtenemos e imprimimos los registros lógicos del archivo de datos.
     System.out.println("--- Lista de registros ---");
     System.out.println();
     contadorPosicion = -1;
     do {
       ++contadorPosicion;
         
       // Obtenemos el siguiente registro lógico.
       for(int i = 1; i <= cantidadCampos; i++)
         archivoAccesoDirecto.read(bytesRLDatos[i - 1]);
  
       // Mostramos los datos.
       if(bytesRLDatos[0][0] == 0) System.out.println("Estado: inhabilitado.");
       else System.out.println("Estado: habilitado.");
       System.out.println("Registro con posición lógica: " + contadorPosicion);
       for(int i = 1; i < cantidadCampos; i++) {
         bytesAString = new String(bytesRLMetadatos[i - 1], 1, bytesRLMetadatos[i - 1].length - 1, "UTF-8");
         System.out.print(bytesAString + ": ");
         bytesAString = new String(bytesRLDatos[i], "UTF-8");
         System.out.println(bytesAString);
       }
       System.out.println();
     } while(archivoAccesoDirecto.getFilePointer() != archivoAccesoDirecto.length());
   }
  
   archivoAccesoDirecto.close();
   
   // Para la ejecución para que el usuario vea el resultado.
   System.out.println("Presione Enter para volver al menú anterior.");
   DatosGenerales.scanConsola.nextLine();
  }

  //Condiciones previas: el puntero debe apuntar a un archivo existente.
  public void mostrarRegistro() throws FileNotFoundException, IOException, UnsupportedEncodingException {
    RandomAccessFile archivoAccesoDirecto;
    int cantidadCampos, longitudRLDatos, posicion, byteHabilitacion;
    long cantidadRL_archivoDatos;
    byte infoMetadatos[][], infoDatos[][];
    String bytesAString;
    boolean repite;
    
    // Abrimos el archivo de datos.
    archivoAccesoDirecto = new RandomAccessFile(archivoDatos, "rw");
    
    MetodosGenerales.limpiarPantalla();
    if(archivoAccesoDirecto.length() == 0) {
    // El archivo de datos está vacío.
      MetodosGenerales.limpiarPantalla();
      System.out.println("El archivo de datos está vacío.");
      System.out.println();
      System.out.println("Presione Enter para volver al menú anterior.");
      DatosGenerales.scanConsola.nextLine();
    } else {
    // El archivo de datos no está vacío.
      
      // Obtenemos los metadatos del archivo.
      infoMetadatos = obtenerMetadatos();
      
      // Calculamos la cantidad de campos que tendrán los registros
      // del archivo de datos (tendrá uno más que la cantidad de campos
      // del archivo de metadatos, porque se encuentra el byte de
      // habilitación en el archivo de datos).
      cantidadCampos = infoMetadatos.length + 1;
      
      // Reservamos espacio en memoria para el registro del archivo
      // de datos.
      infoDatos = new byte[cantidadCampos][];
      for(int i = 0; i < cantidadCampos; i++) {      
        if(i == 0) infoDatos[i] = new byte[1];
        else infoDatos[i] = new byte[infoMetadatos[i - 1][0]];
      }
      
      // Obtenemos la longitud del registro lógico del archivo de datos.
      longitudRLDatos = 1; // El primer byte es el del byte de habilitación.
      for(int i = 1; i < cantidadCampos; i++)
        longitudRLDatos += infoMetadatos[i - 1][0];
      
      // Calculamos la cantidad de registros lógicos que tiene el archivo
      // de datos.
      cantidadRL_archivoDatos = archivoAccesoDirecto.length() / longitudRLDatos;
      
      do {
        repite = false;
        
        // Pregunta al usuario la posición lógica del registro a eliminar.
        MetodosGenerales.limpiarPantalla();
        posicion = MetodosGenerales.entradaNumerica("Ingrese la posición lógica del registro, a partir del cero: ", 0);
  
        if(posicion >= cantidadRL_archivoDatos) {
        // Es una posición inválida: muestra mensaje.       
          System.out.println();
          System.out.println("La posición lógica " + posicion + " es inválida.");
          System.out.println("Presione Enter para volver a intentarlo.");
          DatosGenerales.scanConsola.nextLine();
          repite = true;
        } else {
        // Es una posición válida.
          // Chequeamos que el registro esté habilitado.
          archivoAccesoDirecto.seek(posicion * longitudRLDatos);
          byteHabilitacion = archivoAccesoDirecto.read();
          
          if(byteHabilitacion == 0) {
          // El registro está inhabilitado.
            System.out.println();
            System.out.println("El registro está inhabilitado.");
          } else {
          // El registro está habilitado.
            // Obtenemos los datos del registro.
            for(int i = 1; i < cantidadCampos; i++)
              archivoAccesoDirecto.read(infoDatos[i]);        
      
            // Mostramos los datos.
            System.out.println();
            System.out.println("Registro con posición lógica: " + posicion);
            for(int i = 1; i < cantidadCampos; i++) {
              bytesAString = new String(infoMetadatos[i - 1], 1, infoMetadatos[i - 1].length - 1, "UTF-8");
              System.out.print(bytesAString + ": ");
              bytesAString = new String(infoDatos[i], "UTF-8");
              System.out.println(bytesAString);
            }          
          }
          
          // Pregunta al usuario si quiere mostrar otro registro.
          System.out.println();
          if(MetodosGenerales.entradaBinariaSinLimpiarPantalla("Desea mostrar otro registro [S/N]?: ")) repite = true;
          else repite = false;
        }
      } while(repite);
    }
    
    archivoAccesoDirecto.close();
  }

  // Condiciones previas: el String no debe estar vacío.
  // Guarda la nueva ruta de acceso y crea los apuntadores a los archivos
  // de datos y de metadatos.
  public void setRutaDeAcceso(String rutaDeAcceso) {
    // Asigna la nueva ruta de acceso e inicializa los punteros a archivo.
    this.rutaDeAcceso = rutaDeAcceso;
    archivoDatos = new File(rutaDeAcceso + DatosGenerales.DATOS_EXTENSION);
    archivoMetadatos = new File(rutaDeAcceso + DatosGenerales.METADATOS_EXTENSION);
  }

  // Si el elemento apuntado existe y es un archivo, devuelve
  // true; si no, devuelve false.
  public boolean isArchivo() {
    boolean retorno;

    if(this.archivoMetadatos.isFile()) retorno = true;
    else retorno = false;

    return retorno;
  }

  // Crea el archivo de metadatos, junto con su estructura,
  // y el archivo de datos vacío, si el archivo no existe.
  // Si el puntero es nulo, devuelve PUNTERO_NULO; si no, 
  // si el elemento apuntado existe, devuelve ELEMENTO_EXISTENTE;
  // si no, devuelve OK.
  public DatosGenerales.tipoRetorno crearArchivo() throws IOException {
    byte[] bytesCampo, cantidadCampos, stringABytes;
    String texto, nombreCampo;
    boolean cantCamposPredefinida, otroCampo;
    int longitudCampo, contadorDeCampos;
    FileOutputStream fOutStream;
    DatosGenerales.tipoRetorno retorno;

    if(this.archivoMetadatos != null) {
      if(this.archivoMetadatos.exists()) {
      // El archivo de metadatos que queremos crear ya existe.
        retorno = DatosGenerales.tipoRetorno.ELEMENTO_EXISTENTE;
      } else {
      // El archivo de metadatos que queremos crear no existe todavía.
        // Inicializaciones.
        bytesCampo = new byte[61];
        cantidadCampos = new byte[1];

        // Creamos el archivo de metadatos.
        this.archivoMetadatos.createNewFile();

        // Determinamos si la cantidad de campos estará definida previamente o no.
        texto = "Desea definir la cantidad de campos previamente? [S/N]: ";
        cantCamposPredefinida = MetodosGenerales.entradaBinaria(texto);
        if(cantCamposPredefinida) {
          texto = "Ingrese la cantidad de campos que tendrá el archivo: ";
          cantidadCampos[0] = (byte) MetodosGenerales.entradaNumerica(texto, 1, DatosGenerales.METADATOS_MAX_CAMPOS);
        } else {
          // Esto nos permitirá dejar reservado el primer cero para
          // luego sobreescribirlo con el valor correspondiente a
          // la cantidad de campos.
          cantidadCampos[0] = 0;
        }

        // Reservamos el primer byte para que indique la cantidad de campos.
        fOutStream = new FileOutputStream(nombreArchivoMetadatos());
        fOutStream.write(cantidadCampos);

        // Guardamos los registros lógicos de los campos.
        contadorDeCampos = 1;
        do {
          // Obtiene la longitud del campo.
          texto = "Ingrese la longitud, en bytes, del campo " + contadorDeCampos + " (máx. " + DatosGenerales.METADATOS_MAX_BYTES_CAMPO_NOMBRE + "): ";
          longitudCampo = MetodosGenerales.entradaNumerica(texto, 1, DatosGenerales.METADATOS_MAX_BYTES_CAMPO_NOMBRE);

          // Obtiene el nombre del campo.
          texto = "Ingrese el nombre del campo (máx. 60 bytes): ";
          nombreCampo = MetodosGenerales.entradaString(texto, 60);
          stringABytes = nombreCampo.getBytes("UTF-8");

          // Guardamos la información en el archivo.
          for(int i = 0; i < bytesCampo.length; i++)
            bytesCampo[i] = 0;
          bytesCampo[0] = (byte) longitudCampo;
          for(int i = 0; i < stringABytes.length; i++)
            bytesCampo[i + 1] = stringABytes[i];
          fOutStream.write(bytesCampo);

          // Determinamos si se repite el ciclo.
          if(!cantCamposPredefinida) {
            texto = "Desea agregar otro campo? [S/N]: ";
            otroCampo = MetodosGenerales.entradaBinaria(texto);

            if(otroCampo) contadorDeCampos++;
          } else {
            if(contadorDeCampos == cantidadCampos[0]) {
              otroCampo = false;
              fOutStream.close();
            } else {
              otroCampo = true;
              contadorDeCampos++;
            }
          }
        } while(otroCampo);

        // Si la cantidad de campos no estaba predeterminada, guarda
        // el valor de dicha cantidad en el byte reservado para ello.
        if(!cantCamposPredefinida) {
          RandomAccessFile archivoRandom = new RandomAccessFile(this.archivoMetadatos, "rw");
          cantidadCampos[0] = (byte) contadorDeCampos;
          archivoRandom.write(cantidadCampos[0]);
          archivoRandom.close();
        }

        // Creamos el archivo de datos vacío.
        this.archivoDatos.createNewFile();

        // Avisamos al usuario que la operación tuvo éxito.
        MetodosGenerales.limpiarPantalla();
        System.out.println("Se han creado exitosamente el archivo vacío y sus metadatos.");
        System.out.println("Presione Enter para continuar.");
        DatosGenerales.scanConsola.nextLine();

        retorno = DatosGenerales.tipoRetorno.OK;
      }
    } else {
      retorno = DatosGenerales.tipoRetorno.PUNTERO_NULO;
    }

    return retorno;
  }
}
