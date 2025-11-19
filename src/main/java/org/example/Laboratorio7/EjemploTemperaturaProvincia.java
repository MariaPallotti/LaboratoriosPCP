import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

class EjemploTemperaturaProvincia {
  public static void main(String[] args) {
    int                numHebras, codProvincia, desp;
    String             nombreFichero = "";
    long               t1, t2, tt[];
    double             ts, tp;
    PuebloMaximaMinimaSec MaxMinSec; // Objeto sin sincronizacion
    PuebloMaximaMinimaPar MaxMinPar; // Objeto con sincronizacion
    
    // Comprobacion y extraccion de los argumentos de entrada.
    if( args.length != 3 ) {
      System.out.println( "ERROR: numero de argumentos incorrecto.");
      System.out.println( "Uso: java programa <numHebras> <provincia> <desplazamiento>" );
      System.exit( -1 );
    }
    try {
      numHebras    = Integer.parseInt( args[ 0 ] );
      codProvincia = Integer.parseInt( args[ 1 ] );
      desp         = Integer.parseInt( args[ 2 ] );
      if( ( numHebras <= 0 ) || (codProvincia < 1) || (codProvincia > 50) || 
          (desp < 0) || (desp >= 7) ) {
          System.out.println( "Uso: java programa <numHebras> <provincia> <desplazamiento>" );
          System.err.print( "  donde ( numHebras > 0 ) , codProvincia in [ 1 , 50 ] ," );
          System.err.println( "( desplazamiento in [ 0 , 6 ]" );
          System.exit( -1 );
      }
    } catch( NumberFormatException ex ) {
      numHebras    = -1;
      codProvincia = -1;
      desp         = -1;
      System.out.println( "ERROR: Numero de entrada incorrecto." );
      System.exit( -1 );
    }

    // Mensaje inicial
    System.out.println();
    System.out.println( "Obtiene el pueblo de una provincia con mayor diferencia " +
                        "de temperatura." );

    // Nombre del fichero de codigos
    if (codProvincia < 10) {
      nombreFichero = "codPueblos_0" + codProvincia + ".txt";
    } else {
      nombreFichero = "codPueblos_"  + codProvincia + ".txt";
    }
    
    // Seleccion del dia elegido
    String fecha;
    Calendar c = Calendar.getInstance();
    Integer dia, mes, anyo;
    
    c.add(Calendar.DAY_OF_MONTH, desp);
    dia = c.get(Calendar.DATE);
    mes = c.get(Calendar.MONTH) + 1;
    anyo = c.get(Calendar.YEAR);
    fecha = String.format("%02d", anyo) + "-" + String.format("%02d", mes) + "-" +
             String.format("%02d", dia);
    System.out.println("Fecha de busqueda: " + fecha);
    
    //
    // Implementacion secuencial sin temporizar.
    //
    MaxMinSec = new PuebloMaximaMinimaSec();
    MaxMinPar = new PuebloMaximaMinimaPar();
    File f = new File(nombreFichero);
    if (f.exists()) {
      obtenMayorDiferenciaDeFichero (nombreFichero, fecha, codProvincia, MaxMinSec, MaxMinPar, 
                                      0, numHebras);
    } else {
      obtenMayorDiferenciaAFichero_Secuencial (nombreFichero, fecha, codProvincia, MaxMinSec);
    }
    MaxMinSec = new PuebloMaximaMinimaSec();
    obtenMayorDiferenciaDeFichero (nombreFichero, fecha, codProvincia, MaxMinSec, MaxMinPar, 
                                    0, numHebras);
    System.out.println( "  Pueblo: " + MaxMinSec.damePueblo() + " , Maxima = " +
                       MaxMinSec.dameTemperaturaMaxima() + " , Minima = " +
                       MaxMinSec.dameTemperaturaMinima() );
    
    //
    // Implementacion secuencial.
    //
    System.out.println();
    t1 = System.nanoTime();
    MaxMinSec = new PuebloMaximaMinimaSec();
    MaxMinPar = new PuebloMaximaMinimaPar();
    obtenMayorDiferenciaDeFichero (nombreFichero, fecha, codProvincia, MaxMinSec, MaxMinPar, 
                                      0, numHebras);
    t2 = System.nanoTime();
    ts = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.print( "Implementacion secuencial.                           " );
    System.out.println( " Tiempo(s): " + ts );
    System.out.println( "  Pueblo: " + MaxMinSec.damePueblo() + " , Maxima = " +
                       MaxMinSec.dameTemperaturaMaxima() + " , Minima = " +
                       MaxMinSec.dameTemperaturaMinima() );

    //
    // Implementacion paralela: Gestion Propia.
    //
    System.out.println();
    MaxMinSec = new PuebloMaximaMinimaSec();
    MaxMinPar = new PuebloMaximaMinimaPar();
    t1 = System.nanoTime();
    obtenMayorDiferenciaDeFichero (nombreFichero, fecha, codProvincia, MaxMinSec, MaxMinPar,
            1, numHebras);
    t2 = System.nanoTime();
    tp = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.print( "Implementacion paralela: Gestion Propia.     " );
    System.out.println( " Tiempo(s): " + tp + " , Incremento: " + ts/tp );
    System.out.println( "  Pueblo: " + MaxMinPar.damePueblo() + " , Maxima = " + MaxMinPar.dameTemperaturaMaxima() + " , Minima = " + MaxMinPar.dameTemperaturaMinima() );

    //
    // Implementacion paralela: Thread Pool isTerminated.
    //
    // ...
      System.out.println();
      t1 = System.nanoTime();
      MaxMinSec = new PuebloMaximaMinimaSec();
      MaxMinPar = new PuebloMaximaMinimaPar();
      obtenMayorDiferenciaDeFichero (nombreFichero, fecha, codProvincia, MaxMinSec, MaxMinPar,
              2, numHebras);
      t2 = System.nanoTime();
      tp = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
      System.out.print( "Implementacion paralela: ThreadPools isTerminated().     " );
      System.out.println( " Tiempo(s): " + tp + " , Incremento: " + ts/tp );
      System.out.println( "  Pueblo: " + MaxMinPar.damePueblo() + " , Maxima = " + MaxMinPar.dameTemperaturaMaxima() + " , Minima = " + MaxMinPar.dameTemperaturaMinima() );
    //
    // Implementacion paralela: Thread Pool con awaitTermination.
    //
    // ...
      System.out.println();
      t1 = System.nanoTime();
      MaxMinSec = new PuebloMaximaMinimaSec();
      MaxMinPar = new PuebloMaximaMinimaPar();
      obtenMayorDiferenciaDeFichero (nombreFichero, fecha, codProvincia, MaxMinSec, MaxMinPar,
              3, numHebras);
      t2 = System.nanoTime();
      tp = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
      System.out.print( "Implementacion paralela: ThreadPools con awaitTermination.     " );
      System.out.println( " Tiempo(s): " + tp + " , Incremento: " + ts/tp );
      System.out.println( "  Pueblo: " + MaxMinPar.damePueblo() + " , Maxima = " + MaxMinPar.dameTemperaturaMaxima() + " , Minima = " + MaxMinPar.dameTemperaturaMinima() );
    
    //
    // Implementacion paralela: Thread Pool con Future.
    //
    // ...
      System.out.println();
      t1 = System.nanoTime();
      MaxMinSec = new PuebloMaximaMinimaSec();
      MaxMinPar = new PuebloMaximaMinimaPar();
      obtenMayorDiferenciaDeFichero (nombreFichero, fecha, codProvincia, MaxMinSec, MaxMinPar,
              4, numHebras);
      t2 = System.nanoTime();
      tp = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
      System.out.print( "Implementacion paralela: ThreadPools con Future.     " );
      System.out.println( " Tiempo(s): " + tp + " , Incremento: " + ts/tp );
      System.out.println( "  Pueblo: " + MaxMinSec.damePueblo() + " , Maxima = " + MaxMinSec.dameTemperaturaMaxima() + " , Minima = " + MaxMinSec.dameTemperaturaMinima() );
  }
  
  // --------------------------------------------------------------------------
  public static void obtenMayorDiferenciaAFichero_Secuencial (String nombreFichero, 
                       String fecha, int codProvincia, PuebloMaximaMinimaSec MaxMin) {
    FileWriter fichero = null;
    PrintWriter pw = null;
    
    // Verifica todas los codigos de pueblos y escribe el fichero
    try
    {
      // Apertura del fichero y creacion de FileWriter para poder
      // hacer una lectura comoda (disponer del metodo readLine()).
      
      fichero = new FileWriter(nombreFichero);
      pw = new PrintWriter(fichero);
      
      for (int i=codProvincia*1000; i<(codProvincia+1)*1000; i++){
        if (ProcesaPueblo(fecha, i, MaxMin, false) == true) {
          pw.println(i);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        // Nuevamente aprovechamos el finally para
        // asegurarnos que se cierra el fichero.
        if (null != fichero)
          fichero.close();
      } catch (Exception e2) {
        e2.printStackTrace();
      }
    }
  }
  
  // --------------------------------------------------------------------------
  
  public static void obtenMayorDiferenciaDeFichero (String nombreFichero, String fecha, 
                       int codProvincia, PuebloMaximaMinimaSec MaxMinSec, 
                       PuebloMaximaMinimaPar MaxMinPar, int opcion, int numHebras) {
    File fichero = null;
    FileReader fr = null;
    BufferedReader br = null;
    
    // Procesa el fichero
    try
    {
      // Apertura del fichero y creacion de BufferedReader para poder
      // hacer una lectura comoda (disponer del metodo readLine()).
      fichero = new File (nombreFichero);
      fr = new FileReader (fichero);
      br = new BufferedReader(fr);
      
      String           linea;
      ExecutorService  exec;
      switch (opcion) {
        case 0:  // Caso secuencial
          while( ( linea = br.readLine() ) != null ) {
            int codPueblo = Integer.parseInt(linea);
            ProcesaPueblo(fecha, codPueblo, MaxMinSec, false);
          }
          break;
        case 1:  // Gestion Propia

          MiHebraConsumidora[] v_HebrasConsumidoras1 = new MiHebraConsumidora[numHebras];
          LinkedBlockingQueue<TareaEnColaGestionPropia> cola = new LinkedBlockingQueue<>();
          for(int i = 0; i < numHebras; i++){
            v_HebrasConsumidoras1[i] = new MiHebraConsumidora(cola, MaxMinPar, fecha);
            v_HebrasConsumidoras1[i].start();
          }


          while( ( linea = br.readLine() ) != null ) {
            int codPueblo = Integer.parseInt(linea);
            TareaEnColaGestionPropia tarea = new TareaEnColaGestionPropia(false, codPueblo);
            cola.put(tarea);

          }
          for (int i = 0; i < numHebras; i++){
            TareaEnColaGestionPropia tarea = new TareaEnColaGestionPropia(true, -1);
            cola.put(tarea);
          }
          for (int i = 0; i < numHebras; i++){
            v_HebrasConsumidoras1[i].join();
          }
          break;
        case 2: // ThreadPools con isTerminated
          exec = Executors.newFixedThreadPool(numHebras);
          while( ( linea = br.readLine() ) != null ) {
              int codPueblo = Integer.parseInt(linea);
              TareaRunnable t = new TareaRunnable(fecha, codPueblo, MaxMinPar);
              exec.execute(t);
          }
          exec.shutdown();
          while(!exec.isTerminated()){

          }
          break;
        case 3: // ThreadPools con awaitTermination
            exec = Executors.newFixedThreadPool(numHebras);
            while( ( linea = br.readLine() ) != null ) {
                int codPueblo = Integer.parseInt(linea);
                TareaRunnable t = new TareaRunnable(fecha, codPueblo, MaxMinPar);
                exec.execute(t);
            }
            exec.shutdown();
            try{
                while(!exec.awaitTermination(2L, TimeUnit.MILLISECONDS)){}
            }catch (InterruptedException e){
                e.printStackTrace();
            }
          break;
        case 4: // ThreadPools + con Future
          Future<PuebloMaximaMinimaSec> f;
          exec = Executors.newFixedThreadPool(numHebras);

          ArrayList<Future<PuebloMaximaMinimaSec>> alf = new ArrayList<Future<PuebloMaximaMinimaSec>>(numHebras);

            while( ( linea = br.readLine() ) != null ) {
                int codPueblo = Integer.parseInt(linea);
                TareaCallable t = new TareaCallable(fecha, codPueblo);
                f = exec.submit(t);
                alf.add(f);
            }
            exec.shutdown();
            for (int i = 0; i < alf.size(); i++){
                try{
                    f = alf.get(i);
                    PuebloMaximaMinimaSec resultado = f.get();
                    MaxMinSec.actualizaMaxMin(resultado.poblacion, resultado.dameCodigo(), resultado.dameTemperaturaMaxima(), resultado.dameTemperaturaMinima());
                }catch(ExecutionException e){
                    e.printStackTrace();
                } catch (InterruptedException e){
                    e.printStackTrace();
                }

            }
          break;

        default:
          break;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }finally{
      // En el finally se cierra el fichero, para asegurar
      // que el cierre se completa tanto si todo va bien 
      // como si activa una excepcion.
      try{
        if( null != fr ){
          fr.close();
        }
      }catch (Exception e2){
        e2.printStackTrace();
      }
    }
  }
  
  // --------------------------------------------------------------------------
  public static boolean ProcesaPueblo (String fecha, int codPueblo, 
                                       PuebloMaximaMinimaSec MaxMin, boolean imprime) {
    URL            url;
    InputStream    is = null;
    BufferedReader br;
    String         line, poblacion = new String (), provincia = new String ();
    int            state, num[]=new int[2];
    boolean        res = false;
    
    // Procesamiento de la informacion XML asociada a codPueblo
    // Actualizacion de MaxMin de acuerdo a los valores obtenidos
    try {
      String urlStr = "https://www.aemet.es/xml/municipios/localidad_" +
                         String.format("%05d",codPueblo)+ ".xml";
      //url = new URL(urlStr);
      url = URI.create(urlStr).toURL();
      is  = url.openStream();  // throws an IOException
      br  = new BufferedReader(new InputStreamReader(is));
      if (imprime) System.out.println(urlStr);
      
      state = 0;
      while (((line = br.readLine()) != null) && (state < 6)) {
        //        System.out.println (line);
        if ((state == 0) && (line.contains ("nombre"))) {
          poblacion=line.split(">")[1].split("<")[0].split("/")[0];
          state++;
        } else if ((state == 1) && (line.contains ("provincia"))) {
          provincia=line.split(">")[1].split("<")[0].split("/")[0];
          state++;
        } else if ((state == 2) && (line.contains (fecha))) {
          state++;
        } else if ((state == 3) && (line.contains ("temperatura"))) {
          state++;
        } else if ((state > 3) && ((line.contains ("maxima")) || (line.contains ("minima")))) {
          num[state-4] = Integer.parseInt (line.split(">")[1].split("<")[0]);
          state++;
        }
      }
      // System.out.println("(" + codPueblo + ") " + poblacion + "(" + provincia + ") => " +
      //                    "(" + num[0] + " , " + num[1] + ")");
      if (codPueblo == 24116)
        System.out.println (poblacion + " , " + codPueblo + " , " + num[0] + " , " + num[1]);
      MaxMin.actualizaMaxMin (poblacion, codPueblo, num[0], num[1]);
      res = true;
    } catch (MalformedURLException mue) {
      mue.printStackTrace();
    } catch (IOException ioe) {
      //      ioe.printStackTrace();
    } finally {
      try {
        if (is != null) is.close();
      } catch (IOException ioe) {
        // nothing to see here
      }
    }
    return res;
  }

  public static boolean ProcesaPuebloPar (String fecha, int codPueblo,
                                       PuebloMaximaMinimaPar MaxMin, boolean imprime) {
    URL            url;
    InputStream    is = null;
    BufferedReader br;
    String         line, poblacion = new String (), provincia = new String ();
    int            state, num[]=new int[2];
    boolean        res = false;

    // Procesamiento de la informacion XML asociada a codPueblo
    // Actualizacion de MaxMin de acuerdo a los valores obtenidos
    try {
      String urlStr = "https://www.aemet.es/xml/municipios/localidad_" +
              String.format("%05d",codPueblo)+ ".xml";
      //url = new URL(urlStr);
      url = URI.create(urlStr).toURL();
      is  = url.openStream();  // throws an IOException
      br  = new BufferedReader(new InputStreamReader(is));
      if (imprime) System.out.println(urlStr);

      state = 0;
      while (((line = br.readLine()) != null) && (state < 6)) {
        //        System.out.println (line);
        if ((state == 0) && (line.contains ("nombre"))) {
          poblacion=line.split(">")[1].split("<")[0].split("/")[0];
          state++;
        } else if ((state == 1) && (line.contains ("provincia"))) {
          provincia=line.split(">")[1].split("<")[0].split("/")[0];
          state++;
        } else if ((state == 2) && (line.contains (fecha))) {
          state++;
        } else if ((state == 3) && (line.contains ("temperatura"))) {
          state++;
        } else if ((state > 3) && ((line.contains ("maxima")) || (line.contains ("minima")))) {
          num[state-4] = Integer.parseInt (line.split(">")[1].split("<")[0]);
          state++;
        }
      }
      // System.out.println("(" + codPueblo + ") " + poblacion + "(" + provincia + ") => " +
      //                    "(" + num[0] + " , " + num[1] + ")");
      if (codPueblo == 24116)
        System.out.println (poblacion + " , " + codPueblo + " , " + num[0] + " , " + num[1]);
      MaxMin.actualizaMaxMin (poblacion, codPueblo, num[0], num[1]);
      res = true;
    } catch (MalformedURLException mue) {
      mue.printStackTrace();
    } catch (IOException ioe) {
      //      ioe.printStackTrace();
    } finally {
      try {
        if (is != null) is.close();
      } catch (IOException ioe) {
        // nothing to see here
      }
    }
    return res;
  }

}



// ============================================================================
class PuebloMaximaMinimaSec {
  // ============================================================================
  String poblacion;
  int    codigo, max, min;
  
  
  // --------------------------------------------------------------------------
  public PuebloMaximaMinimaSec() {
    poblacion = null;
    codigo    = -1;
    max       = -1;
    min       = -1;
  }
  
  // --------------------------------------------------------------------------
  public void actualizaMaxMin( String poblacion, int codigo, int max, int min ) {
    if ((this.poblacion == null) || ((this.max-this.min) < (max-min)) ||
        (((this.max-this.min) == (max-min)) && (this.min > min)) ||
        (((this.max-this.min) == (max-min)) && (this.min == min) && (this.codigo < codigo))
        ) {
      this.poblacion = poblacion;
      this.codigo = codigo;
      this.max = max;
      this.min = min;
    }
  }
  
  // --------------------------------------------------------------------------
  public String damePueblo() {
    return this.poblacion + "(" + this.codigo + ")";
  }
  
  // --------------------------------------------------------------------------
  public int dameCodigo() {
    return this.codigo;
  }
  
  // --------------------------------------------------------------------------
  public int dameTemperaturaMaxima() {
    return this.max;
  }
  
  // --------------------------------------------------------------------------
  public int dameTemperaturaMinima() {
    return this.min;
  }
}

// ============================================================================
class PuebloMaximaMinimaPar {
  // ============================================================================
  String poblacion;
  int    codigo, max, min;
  
  
  // --------------------------------------------------------------------------
  public PuebloMaximaMinimaPar() {
    poblacion = null;
    codigo    = -1;
    max       = -1;
    min       = -1;
  }
  
  // --------------------------------------------------------------------------
  public synchronized void actualizaMaxMin( String poblacion, int codigo, int max, int min ) {
    if ((this.poblacion == null) || ((this.max-this.min) < (max-min)) ||
        (((this.max-this.min) == (max-min)) && (this.min > min)) ||
        (((this.max-this.min) == (max-min)) && (this.min == min) && (this.codigo < codigo))
        ) {
      this.poblacion = poblacion;
      this.codigo = codigo;
      this.max = max;
      this.min = min;
    }
  }
  
  // --------------------------------------------------------------------------
  public String damePueblo() {
    return this.poblacion + "(" + this.codigo + ")";
  }
  
  // --------------------------------------------------------------------------
  public int dameCodigo() {
    return this.codigo;
  }
  
  // --------------------------------------------------------------------------
  public int dameTemperaturaMaxima() {
    return this.max;
  }
  
  // --------------------------------------------------------------------------
  public int dameTemperaturaMinima() {
    return this.min;
  }
}

class TareaEnColaGestionPropia {
  boolean esVeneno;
  int     codPueblo;

  public TareaEnColaGestionPropia(boolean esVeneno, int codPueblo) {
    this.esVeneno = esVeneno;
    this.codPueblo = codPueblo;
  }
}

class MiHebraConsumidora extends Thread {
  LinkedBlockingQueue<TareaEnColaGestionPropia> cola;
  PuebloMaximaMinimaPar pueblo;
  String fecha;

  public MiHebraConsumidora(LinkedBlockingQueue<TareaEnColaGestionPropia> cola, PuebloMaximaMinimaPar pueblo, String fecha) {
    this.cola = cola;
    this.pueblo = pueblo;
    this.fecha = fecha;
  }
  public void run() {
      try {
          TareaEnColaGestionPropia tarea = cola.take();
          while (!tarea.esVeneno) {
            int codpueblo = tarea.codPueblo;
            ProcesaPuebloPar(fecha, codpueblo, pueblo, false);
            tarea = cola.take();
          }
      } catch (InterruptedException e) {
          e.printStackTrace();
      }

  }


  public static boolean ProcesaPuebloPar (String fecha, int codPueblo,
                                          PuebloMaximaMinimaPar MaxMin, boolean imprime) {
    URL            url;
    InputStream    is = null;
    BufferedReader br;
    String         line, poblacion = new String (), provincia = new String ();
    int            state, num[]=new int[2];
    boolean        res = false;

    // Procesamiento de la informacion XML asociada a codPueblo
    // Actualizacion de MaxMin de acuerdo a los valores obtenidos
    try {
      String urlStr = "https://www.aemet.es/xml/municipios/localidad_" +
              String.format("%05d",codPueblo)+ ".xml";
      //url = new URL(urlStr);
      url = URI.create(urlStr).toURL();
      is  = url.openStream();  // throws an IOException
      br  = new BufferedReader(new InputStreamReader(is));
      if (imprime) System.out.println(urlStr);

      state = 0;
      while (((line = br.readLine()) != null) && (state < 6)) {
        //        System.out.println (line);
        if ((state == 0) && (line.contains ("nombre"))) {
          poblacion=line.split(">")[1].split("<")[0].split("/")[0];
          state++;
        } else if ((state == 1) && (line.contains ("provincia"))) {
          provincia=line.split(">")[1].split("<")[0].split("/")[0];
          state++;
        } else if ((state == 2) && (line.contains (fecha))) {
          state++;
        } else if ((state == 3) && (line.contains ("temperatura"))) {
          state++;
        } else if ((state > 3) && ((line.contains ("maxima")) || (line.contains ("minima")))) {
          num[state-4] = Integer.parseInt (line.split(">")[1].split("<")[0]);
          state++;
        }
      }
      // System.out.println("(" + codPueblo + ") " + poblacion + "(" + provincia + ") => " +
      //                    "(" + num[0] + " , " + num[1] + ")");
      if (codPueblo == 24116)
        System.out.println (poblacion + " , " + codPueblo + " , " + num[0] + " , " + num[1]);

      MaxMin.actualizaMaxMin (poblacion, codPueblo, num[0], num[1]);
      res = true;
    } catch (MalformedURLException mue) {
      mue.printStackTrace();
    } catch (IOException ioe) {
      //      ioe.printStackTrace();
    } finally {
      try {
        if (is != null) is.close();
      } catch (IOException ioe) {
        // nothing to see here
      }
    }
    return res;
  }
}

class TareaRunnable implements Runnable {
    String fecha;
    int codPueblo;
    PuebloMaximaMinimaPar pueblo;
    public TareaRunnable(String fecha, int codPueblo, PuebloMaximaMinimaPar pueblo) {
        this.fecha = fecha;
        this.codPueblo = codPueblo;
        this.pueblo = pueblo;
    }
    public void run(){
        ProcesaPuebloPar(fecha, codPueblo, pueblo, false);
    }
    public static boolean ProcesaPuebloPar (String fecha, int codPueblo,
                                            PuebloMaximaMinimaPar MaxMin, boolean imprime) {
        URL            url;
        InputStream    is = null;
        BufferedReader br;
        String         line, poblacion = new String (), provincia = new String ();
        int            state, num[]=new int[2];
        boolean        res = false;

        // Procesamiento de la informacion XML asociada a codPueblo
        // Actualizacion de MaxMin de acuerdo a los valores obtenidos
        try {
            String urlStr = "https://www.aemet.es/xml/municipios/localidad_" +
                    String.format("%05d",codPueblo)+ ".xml";
            //url = new URL(urlStr);
            url = URI.create(urlStr).toURL();
            is  = url.openStream();  // throws an IOException
            br  = new BufferedReader(new InputStreamReader(is));
            if (imprime) System.out.println(urlStr);

            state = 0;
            while (((line = br.readLine()) != null) && (state < 6)) {
                //        System.out.println (line);
                if ((state == 0) && (line.contains ("nombre"))) {
                    poblacion=line.split(">")[1].split("<")[0].split("/")[0];
                    state++;
                } else if ((state == 1) && (line.contains ("provincia"))) {
                    provincia=line.split(">")[1].split("<")[0].split("/")[0];
                    state++;
                } else if ((state == 2) && (line.contains (fecha))) {
                    state++;
                } else if ((state == 3) && (line.contains ("temperatura"))) {
                    state++;
                } else if ((state > 3) && ((line.contains ("maxima")) || (line.contains ("minima")))) {
                    num[state-4] = Integer.parseInt (line.split(">")[1].split("<")[0]);
                    state++;
                }
            }
            // System.out.println("(" + codPueblo + ") " + poblacion + "(" + provincia + ") => " +
            //                    "(" + num[0] + " , " + num[1] + ")");
            // if (codPueblo == 24116)
            //    System.out.println (poblacion + " , " + codPueblo + " , " + num[0] + " , " + num[1]);

            MaxMin.actualizaMaxMin (poblacion, codPueblo, num[0], num[1]);
            res = true;
        } catch (MalformedURLException mue) {
            mue.printStackTrace();
        } catch (IOException ioe) {
            //      ioe.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException ioe) {
                // nothing to see here
            }
        }
        return res;
    }
}

class TareaCallable implements Callable<PuebloMaximaMinimaSec> {
    String fecha;
    int codPueblo;

    public TareaCallable(String fecha, int codPueblo) {
        this.fecha = fecha;
        this.codPueblo = codPueblo;
    }

    public PuebloMaximaMinimaSec call() {
        PuebloMaximaMinimaSec MaxMin = new PuebloMaximaMinimaSec ();
        EjemploTemperaturaProvincia.ProcesaPueblo(fecha,codPueblo, MaxMin, false);
        return MaxMin;
    }

}