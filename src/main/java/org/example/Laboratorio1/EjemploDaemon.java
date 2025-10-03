class MiHebra ... Thread { // (A)
  // ... (B)

  public MiHebra( int miId, int num1, int num2 ) {
    // ... (C)
  }

  public void run() {
    long suma = 0;

    System.out.println( "Hebra Auxiliar " + miId + " , inicia calculo" );
    for( int i = num1; i <= num2 ; i++ ) {
      suma += (long) i;
    }
    System.out.println( "Hebra Auxiliar " + miId + " , suma: " + suma);
  }

}

class EjemploDaemon {
  public static void main( String args[] ) {
    System.out.println( "Hebra Principal inicia" );
    // Crea y arranca hebra t0 sumando desde 1 hasta 1000000 
    // Crea y arranca hebra t1 sumando desde 1 hasta 1000000 
    // ... (D)
    // Espera la finalizacion de las hebras t0 y t1
    // ... (E)
    System.out.println( "Hebra Principal finaliza" );
  }
}

