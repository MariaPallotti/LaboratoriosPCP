package org.example.Laboratorio1;

class MiRun implements Runnable {
  int miId;
  public MiRun( int miId ) {
    this.miId = miId;
  }
  public void run() {
    for( int i = 0; i < 1000; i++ ) {
      System.out.println( "Hebra: " + miId );
    }
  }
}
class EjemploCreacionRunnable {
  public static void main( String args[] ) {
    Thread h1 = new Thread(new MiRun(0));
    Thread h2 = new Thread(new MiRun(1));

    h1.run();
    h2.run();
  }
}
