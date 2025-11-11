package org.example.Laboratorio6;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

// import java.util.concurrent.*;
// import java.util.concurrent.atomic.*;
// import java.util.Map;
// import java.util.stream.*;
// import java.util.function.*;
// import static java.util.stream.Collectors.*;
// import java.util.Comparator.*;

// ============================================================================
class EjemploPalabraMasUsada {
// ============================================================================

  // -------------------------------------------------------------------------
  public static void main(String args[]) {
    long t1, t2;
    double ts, tp;
    int numHebras;
    String nombreFichero, palabraActual;
    Vector<String> vectorLineas;
    HashMap<String, Integer> hmCuentaPalabras;
    Map<String, Integer> maCuentaPalabras;
    Hashtable<String, Integer> htCuentaPalabras;
    ConcurrentHashMap<String, Integer> chCuentaPalabras;

    // Comprobacion y extraccion de los argumentos de entrada.
    if (args.length != 2) {
      System.err.println("Uso: java programa <numHebras> <fichero>");
      System.exit(-1);
    }
    try {
      numHebras = Integer.parseInt(args[0]);
      nombreFichero = args[1];
      if (numHebras <= 0) {
        System.err.print("Uso: [ java programa <numHebras> <fichero> ] ");
        System.err.println("donde ( numHebras > 0 )");
        System.exit(-1);
      }
    } catch (NumberFormatException ex) {
      numHebras = -1;
      nombreFichero = "";
      System.out.println("ERROR: Argumento numerico incorrectos.");
      System.exit(-1);
    }

    // Lectura y carga de lineas en "vectorLineas".
    vectorLineas = leeFichero(nombreFichero);
    System.out.println("Numero de lineas leidas: " + vectorLineas.size());
    System.out.println();

    //
    // Implementacion secuencial sin temporizar.
    //
    hmCuentaPalabras = new HashMap<String, Integer>(1000, 0.75F);
    for (int i = 0; i < vectorLineas.size(); i++) {
      // Procesa la linea "i".
      String[] palabras = vectorLineas.get(i).split("\\W+");
      for (int j = 0; j < palabras.length; j++) {
        // Procesa cada palabra de la linea "i", si es distinta de blanco.
        palabraActual = palabras[j].trim();
        if (palabraActual.length() > 0) {
          contabilizaPalabra(hmCuentaPalabras, palabraActual);
        }
      }
    }

    //
    // Implementacion secuencial.
    //
    t1 = System.nanoTime();
    hmCuentaPalabras = new HashMap<String, Integer>(1000, 0.75F);
    for (int i = 0; i < vectorLineas.size(); i++) {
      // Procesa la linea "i".
      String[] palabras = vectorLineas.get(i).split("\\W+");
      for (int j = 0; j < palabras.length; j++) {
        // Procesa cada palabra de la linea "i", si es distinta de blanco.
        palabraActual = palabras[j].trim();
        if (palabraActual.length() > 0) {
          contabilizaPalabra(hmCuentaPalabras, palabraActual);
        }
      }
    }
    t2 = System.nanoTime();
    ts = ((double) (t2 - t1)) / 1.0e9;
    System.out.print("Implementacion secuencial: ");
    imprimePalabraMasUsadaYVeces(hmCuentaPalabras);
    System.out.println(" Tiempo(s): " + ts);
    System.out.println("Num. elems. tabla hash: " + hmCuentaPalabras.size());
    System.out.println();


    //
    // Implementacion paralela 1: Uso de synchronizedMap y cerrojo
    //
    t1 = System.nanoTime();

    maCuentaPalabras = new HashMap<>(1000, 0.75F);
    maCuentaPalabras = Collections.synchronizedMap(maCuentaPalabras);

    MiHebra_1[] vh1 = new MiHebra_1[numHebras];

    for (int i = 0; i < numHebras; i++) {
      vh1[i] = new MiHebra_1(i, numHebras, vectorLineas, maCuentaPalabras);
      vh1[i].start();
    }

    for (int i = 0; i < numHebras; i++) {
      try {
        vh1[i].join();
      } catch (InterruptedException ex) {
        ex.printStackTrace();
      }
    }

    t2 = System.nanoTime();
    tp = ((double) (t2 - t1)) / 1.0e9;
    System.out.print("Implementacion paralela 1: ");
    imprimePalabraMasUsadaYVeces(maCuentaPalabras);
    System.out.println(" Tiempo(s): " + tp + " , Incremento " + ts / tp);
    System.out.println("Num. elems. tabla hash: " + maCuentaPalabras.size());
    System.out.println();

    //
    // Implementacion paralela 2: Uso de Hashtable y cerrojo
    // ...
    t1 = System.nanoTime();

    htCuentaPalabras = new Hashtable<>(1000, 0.75F);

    MiHebra_1[] vh2 = new MiHebra_1[numHebras];

    for (int i = 0; i < numHebras; i++) {
      vh2[i] = new MiHebra_1(i, numHebras, vectorLineas, htCuentaPalabras);
      vh2[i].start();
    }

    for (int i = 0; i < numHebras; i++) {
      try {
        vh2[i].join();
      } catch (InterruptedException ex) {
        ex.printStackTrace();
      }
    }

    t2 = System.nanoTime();
    tp = ((double) (t2 - t1)) / 1.0e9;
    System.out.print("Implementacion paralela 2: ");
    imprimePalabraMasUsadaYVeces(htCuentaPalabras);
    System.out.println(" Tiempo(s): " + tp + " , Incremento " + ts / tp);
    System.out.println("Num. elems. tabla hash: " + htCuentaPalabras.size());
    System.out.println();


    //
    // Implementacion paralela 3: Uso de ConcurrentHashMap y cerrojo
    // ...
    t1 = System.nanoTime();

    chCuentaPalabras = new ConcurrentHashMap<>(1000, 0.75F);

    MiHebra_1[] vh3 = new MiHebra_1[numHebras];

    for (int i = 0; i < numHebras; i++) {
      vh3[i] = new MiHebra_1(i, numHebras, vectorLineas, chCuentaPalabras);
      vh3[i].start();
    }

    for (int i = 0; i < numHebras; i++) {
      try {
        vh3[i].join();
      } catch (InterruptedException ex) {
        ex.printStackTrace();
      }
    }

    t2 = System.nanoTime();
    tp = ((double) (t2 - t1)) / 1.0e9;
    System.out.print("Implementacion paralela 3: ");
    imprimePalabraMasUsadaYVeces(chCuentaPalabras);
    System.out.println(" Tiempo(s): " + tp + " , Incremento " + ts / tp);
    System.out.println("Num. elems. tabla hash: " + chCuentaPalabras.size());
    System.out.println();


    //
    // Implementacion paralela 4: Uso de ConcurrentHashMap con merge
    // ...
    t1 = System.nanoTime();

    chCuentaPalabras = new ConcurrentHashMap<>(1000, 0.75F);

    MiHebra_4[] vh4 = new MiHebra_4[numHebras];

    for (int i = 0; i < numHebras; i++) {
      vh4[i] = new MiHebra_4(i, numHebras, vectorLineas, chCuentaPalabras);
      vh4[i].start();
    }

    for (int i = 0; i < numHebras; i++) {
      try {
        vh4[i].join();
      } catch (InterruptedException ex) {
        ex.printStackTrace();
      }
    }

    t2 = System.nanoTime();
    tp = ((double) (t2 - t1)) / 1.0e9;
    System.out.print("Implementacion paralela 4: ");
    imprimePalabraMasUsadaYVeces(chCuentaPalabras);
    System.out.println(" Tiempo(s): " + tp + " , Incremento " + ts / tp);
    System.out.println("Num. elems. tabla hash: " + chCuentaPalabras.size());
    System.out.println();

    //
    // Implementacion paralela 5: Uso de ConcurrentHashMap escalable
    // ...

    t1 = System.nanoTime();

    chCuentaPalabras = new ConcurrentHashMap<>(1000, 0.75F);

    MiHebra_5[] vh5 = new MiHebra_5[numHebras];

    for (int i = 0; i < numHebras; i++) {
      vh5[i] = new MiHebra_5(i, numHebras, vectorLineas, chCuentaPalabras);
      vh5[i].start();
    }

    for (int i = 0; i < numHebras; i++) {
      try {
        vh5[i].join();
      } catch (InterruptedException ex) {
        ex.printStackTrace();
      }
    }

    t2 = System.nanoTime();
    tp = ((double) (t2 - t1)) / 1.0e9;
    System.out.print("Implementacion paralela 5: ");
    imprimePalabraMasUsadaYVeces(chCuentaPalabras);
    System.out.println(" Tiempo(s): " + tp + " , Incremento " + ts / tp);
    System.out.println("Num. elems. tabla hash: " + chCuentaPalabras.size());
    System.out.println();

    //
    // Implementacion paralela 6: Uso de CHM escalable con AtomicInteger
    // ...

    t1 = System.nanoTime();

    ConcurrentHashMap<String, AtomicInteger> saCuentaPalabras = new ConcurrentHashMap<>(1000, 0.75F);

    MiHebra_6[] vh6 = new MiHebra_6[numHebras];

    for (int i = 0; i < numHebras; i++) {
      vh6[i] = new MiHebra_6(i, numHebras, vectorLineas, saCuentaPalabras);
      vh6[i].start();
    }

    for (int i = 0; i < numHebras; i++) {
      try {
        vh6[i].join();
      } catch (InterruptedException ex) {
        ex.printStackTrace();
      }
    }

    t2 = System.nanoTime();
    tp = ((double) (t2 - t1)) / 1.0e9;
    System.out.print("Implementacion paralela 6: ");
    imprimePalabraMasUsadaYVeces6(saCuentaPalabras);
    System.out.println(" Tiempo(s): " + tp + " , Incremento " + ts / tp);
    System.out.println("Num. elems. tabla hash: " + saCuentaPalabras.size());
    System.out.println();

    //
    // Implementacion paralela 7: Uso de CHM escalable con AtomicInteger y 256 niv.
    // ...

    t1 = System.nanoTime();

    saCuentaPalabras = new ConcurrentHashMap<>(1000, 256);

    MiHebra_6[] vh7 = new MiHebra_6[numHebras];

    for (int i = 0; i < numHebras; i++) {
      vh7[i] = new MiHebra_6(i, numHebras, vectorLineas, saCuentaPalabras);
      vh7[i].start();
    }

    for (int i = 0; i < numHebras; i++) {
      try {
        vh7[i].join();
      } catch (InterruptedException ex) {
        ex.printStackTrace();
      }
    }

    t2 = System.nanoTime();
    tp = ((double) (t2 - t1)) / 1.0e9;
    System.out.print("Implementacion paralela 7: ");
    imprimePalabraMasUsadaYVeces6(saCuentaPalabras);
    System.out.println(" Tiempo(s): " + tp + " , Incremento " + ts / tp);
    System.out.println("Num. elems. tabla hash: " + saCuentaPalabras.size());
    System.out.println();

    //
    // Implementacion paralela 8: Uso de Streams
    t1 = System.nanoTime();
    Map<String, Long> stCuentaPalabras = vectorLineas.parallelStream().filter(s -> s != null)
            .map(s -> s.split("\\W+"))
            .flatMap(Arrays::stream)
            .map(String::trim)
            .filter(s -> (s.length() > 0))
            .collect(groupingBy(s -> s, counting()));
    t2 = System.nanoTime();
    // ...
    tp = ((double) (t2 - t1)) / 1.0e9;
    System.out.print("Implementacion paralela 8: ");
    imprimePalabraMasUsadaYVeces8(stCuentaPalabras);
    System.out.println(" Tiempo(s): " + tp + " , Incremento " + ts / tp);
    System.out.println("Num. elems. tabla hash: " + stCuentaPalabras.size());
    System.out.println();

    System.out.println("Fin de programa.");
  }

  // -------------------------------------------------------------------------
  public static Vector<String> leeFichero(String fileName) {
    BufferedReader br;
    String linea;
    Vector<String> data = new Vector<String>();

    try {
      br = new BufferedReader(new FileReader(fileName));
      while ((linea = br.readLine()) != null) {
        //// System.out.println( "Leida linea: " + linea );
        data.add(linea);
      }
      br.close();
    } catch (FileNotFoundException ex) {
      ex.printStackTrace();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    return data;
  }

  // -------------------------------------------------------------------------
  public static void contabilizaPalabra(
          HashMap<String, Integer> cuentaPalabras,
          String palabra) {
    Integer numVeces = cuentaPalabras.get(palabra);
    if (numVeces != null) {
      cuentaPalabras.put(palabra, numVeces + 1);
    } else {
      cuentaPalabras.put(palabra, 1);
    }
  }

  // --------------------------------------------------------------------------
  static void imprimePalabraMasUsadaYVeces(
          Map<String, Integer> cuentaPalabras) {
    Vector<Map.Entry> lista =
            new Vector<Map.Entry>(cuentaPalabras.entrySet());

    String palabraMasUsada = "";
    int numVecesPalabraMasUsada = 0;
    // Calcula la palabra mas usada.
    for (int i = 0; i < lista.size(); i++) {
      String palabra = (String) lista.get(i).getKey();
      int numVeces = (Integer) lista.get(i).getValue();
      if (i == 0) {
        palabraMasUsada = palabra;
        numVecesPalabraMasUsada = numVeces;
      } else if (numVecesPalabraMasUsada < numVeces) {
        palabraMasUsada = palabra;
        numVecesPalabraMasUsada = numVeces;
      }
    }
    // Imprime resultado.
    System.out.print("( Palabra: '" + palabraMasUsada + "' " +
            "veces: " + numVecesPalabraMasUsada + " )");
  }

  // --------------------------------------------------------------------------
  static void imprimePalabraMasUsadaYVeces6(
          Map<String, AtomicInteger> cuentaPalabras) {
    Vector<Map.Entry> lista =
            new Vector<Map.Entry>(cuentaPalabras.entrySet());

    String palabraMasUsada = "";
    AtomicInteger numVecesPalabraMasUsada = new AtomicInteger(0);
    // Calcula la palabra mas usada.
    for (int i = 0; i < lista.size(); i++) {
      String palabra = (String) lista.get(i).getKey();
      AtomicInteger numVeces = (AtomicInteger) lista.get(i).getValue();
      if (i == 0) {
        palabraMasUsada = palabra;
        numVecesPalabraMasUsada.set(numVeces.get());
      } else if (numVecesPalabraMasUsada.get() < numVeces.get()) {
        palabraMasUsada = palabra;
        numVecesPalabraMasUsada = numVeces;
      }
    }
    // Imprime resultado.
    System.out.print("( Palabra: '" + palabraMasUsada + "' " +
            "veces: " + numVecesPalabraMasUsada + " )");
  }


  // --------------------------------------------------------------------------
  static void imprimePalabraMasUsadaYVeces8(
          Map<String,Long> cuentaPalabras ) {
    Vector<Map.Entry> lista =
            new Vector<Map.Entry>( cuentaPalabras.entrySet() );

    String palabraMasUsada = "";
    long    numVecesPalabraMasUsada = 0;
    // Calcula la palabra mas usada.
    for( int i = 0; i < lista.size(); i++ ) {
      String palabra = ( String ) lista.get( i ).getKey();
      Long numVeces = ( Long ) lista.get( i ).getValue();
      if( i == 0 ) {
        palabraMasUsada = palabra;
        numVecesPalabraMasUsada = numVeces;
      } else if( numVecesPalabraMasUsada < numVeces ) {
        palabraMasUsada = palabra;
        numVecesPalabraMasUsada = numVeces;
      }
    }
    // Imprime resultado.
    System.out.print( "( Palabra: '" + palabraMasUsada + "' " +
            "veces: " + numVecesPalabraMasUsada + " )" );
  }



  // --------------------------------------------------------------------------
  static void printCuentaPalabrasOrdenadas(
                  HashMap<String,Integer> cuentaPalabras ) {
    int             i, numVeces;
    List<Map.Entry> list = new Vector<Map.Entry>( cuentaPalabras.entrySet() );

    // Ordena por valor.
    Collections.sort( 
        list,
        new Comparator<Map.Entry>() {
            public int compare( Map.Entry e1, Map.Entry e2 ) {
              Integer i1 = ( Integer ) e1.getValue();
              Integer i2 = ( Integer ) e2.getValue();
              return i2.compareTo( i1 );
            }
        }
    );
    // Muestra contenido.
    i = 1;
    System.out.println( "Veces Palabra" );
    System.out.println( "-----------------" );
    for( Map.Entry e : list ) {
      numVeces = ( ( Integer ) e.getValue () ).intValue();
      System.out.println( i + " " + e.getKey() + " " + numVeces );
      i++;
    }
    System.out.println( "-----------------" );
  }
}

class MiHebra_1 extends Thread {
  int miId;
  int numHebras;
  Vector<String> vectorLineas;
  String palabraActual;
  Map<String, Integer> hmCuentaPalabras;
  public MiHebra_1(int miId, int numHebras,
                   Vector<String> vectorLineas,
                   Map<String, Integer> hmCuentaPalabras){
    this.miId = miId;
    this.numHebras = numHebras;
    this.vectorLineas = vectorLineas;
    this.hmCuentaPalabras = hmCuentaPalabras;
  }
  public void run(){
    for( int i = miId; i < vectorLineas.size(); i += numHebras ) {
      // Procesa la linea "i".
      String[] palabras = vectorLineas.get( i ).split( "\\W+" );
      for( int j = 0; j < palabras.length; j++ ) {
        // Procesa cada palabra de la linea "i", si es distinta de blanco.
        palabraActual = palabras[ j ].trim();
        if( palabraActual.length() > 0 ) {
          contabilizaPalabraSync( hmCuentaPalabras, palabraActual );
        }
      }
    }
  }

  public synchronized static void contabilizaPalabraSync(
          Map<String,Integer> cuentaPalabras,
          String palabra ) {
    Integer numVeces = cuentaPalabras.get( palabra );
    if( numVeces != null ) {
      cuentaPalabras.put( palabra, numVeces+1 );
    } else {
      cuentaPalabras.put( palabra, 1 );
    }
  }
}


class MiHebra_4 extends Thread {
  int miId;
  int numHebras;
  Vector<String> vectorLineas;
  String palabraActual;
  ConcurrentHashMap<String, Integer> hmCuentaPalabras;
  public MiHebra_4(int miId, int numHebras,
                   Vector<String> vectorLineas,
                   ConcurrentHashMap<String, Integer> hmCuentaPalabras){
    this.miId = miId;
    this.numHebras = numHebras;
    this.vectorLineas = vectorLineas;
    this.hmCuentaPalabras = hmCuentaPalabras;
  }
  public void run(){
    for( int i = miId; i < vectorLineas.size(); i += numHebras ) {
      String[] palabras = vectorLineas.get(i).split("\\W+");
      for (String palabra : palabras) {
        palabra = palabra.trim();
        if (!palabra.isEmpty()) {
          contabilizaPalabra(hmCuentaPalabras, palabra);
        }
      }
    }
  }

  public synchronized static void contabilizaPalabra(
          Map<String,Integer> cuentaPalabras,
          String palabra ) {
    cuentaPalabras.merge(palabra, 1, (oldV, newV) -> newV + oldV);
  }
}


class MiHebra_5 extends Thread {
  int miId;
  int numHebras;
  Vector<String> vectorLineas;
  String palabraActual;
  ConcurrentHashMap<String, Integer> hmCuentaPalabras;
  public MiHebra_5(int miId, int numHebras,
                   Vector<String> vectorLineas,
                   ConcurrentHashMap<String, Integer> hmCuentaPalabras){
    this.miId = miId;
    this.numHebras = numHebras;
    this.vectorLineas = vectorLineas;
    this.hmCuentaPalabras = hmCuentaPalabras;
  }
  public void run(){
    for( int i = miId; i < vectorLineas.size(); i += numHebras ) {
      // Procesa la linea "i".
      String[] palabras = vectorLineas.get( i ).split( "\\W+" );
      for( int j = 0; j < palabras.length; j++ ) {
        // Procesa cada palabra de la linea "i", si es distinta de blanco.
        palabraActual = palabras[ j ].trim();
        if( palabraActual.length() > 0 ) {
          contabilizaPalabra( hmCuentaPalabras, palabraActual );
        }
      }
    }
  }

  public static void contabilizaPalabra(
          ConcurrentHashMap<String,Integer> cuentaPalabras,
          String palabra ) {
    Integer imp = cuentaPalabras.putIfAbsent(palabra, 1);
    if(imp != null){
      int act = imp;
      while(true){
        boolean sustit = cuentaPalabras.replace(palabra, act, act+1);
        if(sustit) break;
        act = cuentaPalabras.get(palabra);
      }
    }
  }
}

class MiHebra_6 extends Thread {
  int miId;
  int numHebras;
  Vector<String> vectorLineas;
  String palabraActual;
  ConcurrentHashMap<String, AtomicInteger> hmCuentaPalabras;
  public MiHebra_6(int miId, int numHebras,
                   Vector<String> vectorLineas,
                   ConcurrentHashMap<String, AtomicInteger> hmCuentaPalabras){
    this.miId = miId;
    this.numHebras = numHebras;
    this.vectorLineas = vectorLineas;
    this.hmCuentaPalabras = hmCuentaPalabras;
  }
  public void run(){
    for( int i = miId; i < vectorLineas.size(); i += numHebras ) {
      // Procesa la linea "i".
      String[] palabras = vectorLineas.get( i ).split( "\\W+" );
      for( int j = 0; j < palabras.length; j++ ) {
        // Procesa cada palabra de la linea "i", si es distinta de blanco.
        palabraActual = palabras[ j ].trim();
        if( palabraActual.length() > 0 ) {
          AtomicInteger a = hmCuentaPalabras.putIfAbsent(palabraActual, new AtomicInteger(1));
          if (a != null) {
            a.getAndIncrement();
          }        }
      }
    }
  }

}

