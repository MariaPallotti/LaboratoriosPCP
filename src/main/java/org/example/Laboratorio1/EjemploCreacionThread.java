package org.example.Laboratorio1;

class MiHebra extends Thread {
  int miId;
  public MiHebra( int miId ) {
    this.miId = miId;
  }
  public void run() {
    for( int i = 0; i < 1000; i++ ) {
      System.out.println( "Hebra: " + miId );
    }
  }
}
class EjemploCreacionThread {
  public static void main( String args[] ) {
    MiHebra h1 = new MiHebra(0);
    MiHebra h2 = new MiHebra(1);

    h1.start();
    h2.start();
  }
}
