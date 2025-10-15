package org.example.Laboratorio2;

import static java.lang.Math.min;
import static org.example.Laboratorio2.EjemploFuncionSencilla.evaluaFuncion;

// ============================================================================
class EjemploFuncionSencilla {
// ============================================================================

  // --------------------------------------------------------------------------
  public static void main( String args[] ) {
    int n, numHebras;
    long t1, t2;
    double sumaX, sumaY, ts, tc, tb;

    // Comprobacion y extraccion de los argumentos de entrada.
    if (args.length != 2) {
      System.err.println("Uso: java programa <numHebras> <tamanyo>");
      System.exit(-1);
    }
    try {
      numHebras = Integer.parseInt(args[0]);
      n = Integer.parseInt(args[1]);
      if ((numHebras <= 0) || (n <= 0)) {
        System.err.print("Uso: [ java programa <numHebras> <n> ] ");
        System.err.println("donde ( numHebras > 0 )  y ( n > 0 )");
        System.exit(-1);
      }
    } catch (NumberFormatException ex) {
      numHebras = -1;
      n = -1;
      System.out.println("ERROR: Argumentos numericos incorrectos.");
      System.exit(-1);
    }

    // Crea los vectores.
    double vectorX[] = new double[n];
    double vectorY[] = new double[n];

    //
    // Implementacion secuencial (sin temporizar).
    //
    inicializaVectorX(vectorX);
    inicializaVectorY(vectorY);
    for (int i = 0; i < n; i++) {
      vectorY[i] = evaluaFuncion(vectorX[i]);
    }

    //
    // Implementacion secuencial.
    //
    inicializaVectorX(vectorX);
    inicializaVectorY(vectorY);
    t1 = System.nanoTime();
    for (int i = 0; i < n; i++) {
      vectorY[i] = evaluaFuncion(vectorX[i]);
    }
    t2 = System.nanoTime();
    ts = ((double) (t2 - t1)) / 1.0e9;
    System.out.println("Tiempo secuencial (seg.):                    " + ts);
    //// imprimeResultado( vectorX, vectorY );
    // Comprueba el resultado. 
    sumaX = sumaVector(vectorX);
    sumaY = sumaVector(vectorY);
    System.out.println("Suma del vector X:          " + sumaX);
    System.out.println("Suma del vector Y:          " + sumaY);

    //
    // Implementacion paralela ciclica.
    //
    inicializaVectorX(vectorX);
    inicializaVectorY(vectorY);
    t1 = System.nanoTime();
    // Gestion de hebras para la implementacion paralela ciclica
    // (A) ....

    miHebraCiclica2[] vC = new miHebraCiclica2[numHebras];

    for (int i = 0; i < numHebras; i++) {
      vC[i] = new miHebraCiclica2(i, numHebras, n, vectorX, vectorY);
      vC[i].start();
    }

    for (int i = 0; i < numHebras; i++) {
      try {
        vC[i].join();
      } catch (InterruptedException ex) {
        ex.printStackTrace();
      }
    }

    t2 = System.nanoTime();
    tc = ((double) (t2 - t1)) / 1.0e9;
    System.out.println("Tiempo paralela ciclica (seg.):              " + tc);
    System.out.println("Incremento paralela ciclica:                 " + ts / tc); // (B)
    //// imprimeResultado( vectorX, vectorY );
    // Comprueba el resultado. 
    sumaX = sumaVector(vectorX);
    sumaY = sumaVector(vectorY);
    System.out.println("Suma del vector X:          " + sumaX);
    System.out.println("Suma del vector Y:          " + sumaY);


    //
    // Implementacion paralela por bloques.
    //
    // (C) ....
    //

    inicializaVectorX(vectorX);
    inicializaVectorY(vectorY);
    t1 = System.nanoTime();

    miHebraBloques2[] vB = new miHebraBloques2[numHebras];

    for (int i = 0; i < numHebras; i++) {
      vB[i] = new miHebraBloques2(i, numHebras, n, vectorX, vectorY);
      vB[i].start();
    }

    for (int i = 0; i < numHebras; i++) {
      try {
        vB[i].join();
      } catch (InterruptedException ex) {
        ex.printStackTrace();
      }
    }

    System.out.println("Fin del programa.");


    t2 = System.nanoTime();
    tb = ((double) (t2 - t1)) / 1.0e9;
    System.out.println("Tiempo paralela ciclica (seg.):              " + tb);
    System.out.println("Incremento paralela ciclica:                 " + ts / tb); // (B)
    //// imprimeResultado( vectorX, vectorY );
    // Comprueba el resultado. 
    sumaX = sumaVector(vectorX);
    sumaY = sumaVector(vectorY);
    System.out.println("Suma del vector X:          " + sumaX);
    System.out.println("Suma del vector Y:          " + sumaY);
  }

  // --------------------------------------------------------------------------
  static void inicializaVectorX( double vectorX[] ) {
    if( vectorX.length == 1 ) {
      vectorX[ 0 ] = 0.0;
    } else {
      for( int i = 0; i < vectorX.length; i++ ) {
        vectorX[ i ] = 10.0 * ( double ) i / ( ( double ) vectorX.length - 1 );
      }
    }
  }

  // --------------------------------------------------------------------------
  static void inicializaVectorY( double vectorY[] ) {
    for( int i = 0; i < vectorY.length; i++ ) {
      vectorY[ i ] = 0.0;
    }
  }

  // --------------------------------------------------------------------------
  static double sumaVector( double vector[] ) {
    double  suma = 0.0;
    for( int i = 0; i < vector.length; i++ ) {
      suma += vector[ i ];
    }
    return suma;
  }

  // --------------------------------------------------------------------------
  static double evaluaFuncion( double x ) {
    return 5.4 * x;
  }

  // --------------------------------------------------------------------------
  static void imprimeVector( double vector[] ) {
    for( int i = 0; i < vector.length; i++ ) {
      System.out.println( " vector[ " + i + " ] = " + vector[ i ] );
    }
  }

  // --------------------------------------------------------------------------
  static void imprimeResultado( double vectorX[], double vectorY[] ) {
    for( int i = 0; i < Math.min( vectorX.length, vectorY.length ); i++ ) {
      System.out.println( "  i: " + i + 
                          "  x: " + vectorX[ i ] +
                          "  y: " + vectorY[ i ] );
    }
  }

}

// Crea las clases adicionales que sean necesarias
// (D) ... 
// 

class miHebraCiclica2 extends Thread{
    int miId, numHebras, numero;
    double[] vectorX, vectorY;

    public miHebraCiclica2 (int miId, int numHebras, int numero, double[] vectorX, double[] vectorY){
      this.miId = miId;
      this.numHebras = numHebras;
      this.numero = numero;
      this.vectorX = vectorX;
      this.vectorY = vectorY;
    }

    public void run(){
      for(int i = miId; i < numero; i += numHebras) {
        vectorY[i] = evaluaFuncion(vectorX[i]);
      }
    }
}

class miHebraBloques2 extends Thread{
  int miId, numHebras, numero;
  double[] vectorX, vectorY;

  public miHebraBloques2 (int miId, int numHebras, int numero, double[] vectorX, double[] vectorY){
    this.miId = miId;
    this.numHebras = numHebras;
    this.numero = numero;
    this.vectorX = vectorX;
    this.vectorY = vectorY;
  }

  public void run(){
    int tam = (numero + numHebras -1)/ numHebras ;
    int inicio = tam*miId;
    int fin = min(inicio + tam, numero);

    for(int i = inicio; i < fin; i++) {
      vectorY[i] = evaluaFuncion(vectorX[i]);
    }
  }
}

