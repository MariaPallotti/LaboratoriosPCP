package org.example.Laboratorio1;

// ============================================================================
class CuentaIncrementos {
// ============================================================================
  long contador = 0;

  // --------------------------------------------------------------------------
  void incrementaContador() {
    contador++;
  }

  // --------------------------------------------------------------------------
  long dameContador() {
    return( contador );
  }
}


// ============================================================================
class MiHebra4 extends Thread {
// ============================================================================
  // Declaracion de variables
  // ...
  int miId;
  CuentaIncrementos c;

  // --------------------------------------------------------------------------
  // Definicion del constructor, si es necesario
  // ...
    public MiHebra4(int miId, CuentaIncrementos c){
      this.miId = miId;
      this.c = c;
    }

  // --------------------------------------------------------------------------
  public void run() {
    System.out.println( "Hebra: " + miId + " Comenzando incrementos" );
    // Bucle de 1000000 incrementos del objeto compartido
    // ...

    for(int i = 0; i < 1000000; i++){
      c.incrementaContador();
    }

    System.out.println( "Hebra: " + miId + " Terminando incrementos" );
  }
}

// ============================================================================
class EjemploIncrementos {
// ============================================================================

  // --------------------------------------------------------------------------
  public static void main( String args[] ) {
    int  numHebras;

    // Comprobacion y extraccion de los argumentos de entrada.
    if( args.length != 1 ) {
      System.err.println( "Uso: java programa <numHebras>" );
      System.exit( -1 );
    }
    try {
      numHebras = Integer.parseInt( args[ 0 ] );
      if( numHebras <= 0 ) {
        System.err.println( "Uso: [ java programa <numHebras> ] donde numHebras > 0" );
        System.exit( -1 );
      }
    } catch( NumberFormatException ex ) {
      numHebras = -1;
      System.out.println( "ERROR: Argumentos numericos incorrectos." );
      System.exit( -1 );
    }
    System.out.println( "numHebras: " + numHebras );

    // --------  INCLUIR NUEVO CODIGO A CONTINUACION --------------------------
    // ...
    //(4.2)
    CuentaIncrementos contador = new CuentaIncrementos();

    //(4.3)
    System.out.println("Valor inicial del contador: " +  contador.dameContador());

    //(4.4)
    MiHebra4 [] v = new MiHebra4 [numHebras];

    for( int i = 0; i < numHebras; i++){
      v[i] = new MiHebra4(i, contador);
      v[i].start();
    }

    //(4.5)
    try{
      for(int i = 0; i < numHebras; i++){
        v[i].join();
      }
    }catch (InterruptedException ex){
      ex.printStackTrace();
    }

    //(4.6)
    System.out.println("Valor final del contador z" + contador.dameContador());
  }
}

