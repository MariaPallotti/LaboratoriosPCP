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
class MiHebra extends Thread {
// ============================================================================
  // Declaracion de variables
  // ... 

  // --------------------------------------------------------------------------
  // Definicion del constructor, si es necesario
  // ... 

  // --------------------------------------------------------------------------
  public void run() {
    System.out.println( "Hebra: " + miId + " Comenzando incrementos" );
    // Bucle de 1000000 incrementos del objeto compartido
    // ... 
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
  }
}

