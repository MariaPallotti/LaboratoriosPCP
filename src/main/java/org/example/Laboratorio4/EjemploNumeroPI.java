package org.example.Laboratorio4;

import javax.management.remote.rmi.RMIConnectionImpl;
import java.util.concurrent.atomic.DoubleAdder;

// ===========================================================================
class Acumula {
// ===========================================================================
  double  suma;

  // -------------------------------------------------------------------------
  Acumula() {
    this.suma = 0.0;
  }

  // -------------------------------------------------------------------------
  synchronized void acumulaDato( double dato ) {
    suma += dato;
  }

  // -------------------------------------------------------------------------
  synchronized double dameDato() {
    return suma;
  }
}

// ===========================================================================
class MiHebraMultAcumulaciones extends Thread {
// ===========================================================================
  int      miId, numHebras;
  long     numRectangulos;
  Acumula  a;

  // -------------------------------------------------------------------------
  MiHebraMultAcumulaciones( int miId, int numHebras, long numRectangulos,
                              Acumula a ) {
    // ...
    this.miId = miId;
    this.numHebras = numHebras;
    this.numRectangulos = numRectangulos;
    this.a = a;
  }

  // -------------------------------------------------------------------------
  public void run() {
      double baseRectangulo = 1.0 / ((double) numRectangulos);
      for(long i = miId; i < numRectangulos; i += numHebras) {
          double x = baseRectangulo * ( ( ( double ) i ) + 0.5 );
          a.acumulaDato(EjemploNumeroPI.f( x ));
      }
  }
}

// ===========================================================================
class MiHebraUnaAcumulacion extends Thread {
// ===========================================================================
// ...
  int      miId, numHebras;
  long     numRectangulos;
  Acumula  a;

  MiHebraUnaAcumulacion( int miId, int numHebras, long numRectangulos,
                            Acumula a ) {
    // ...
    this.miId = miId;
    this.numHebras = numHebras;
    this.numRectangulos = numRectangulos;
    this.a = a;
  }

  public void run() {
    double baseRectangulo = 1.0 / ((double) numRectangulos);
    double sumaL = 0.0;

    for(long i = miId; i < numRectangulos; i += numHebras) {
      double x = baseRectangulo * ( ( ( double ) i ) + 0.5 );
      sumaL += EjemploNumeroPI.f( x );
    }

    a.acumulaDato(sumaL);
  }
}

// ===========================================================================
class MiHebraMultAcumulacionAtomica extends Thread {
  // ===========================================================================
// ...
  int miId, numHebras;
  long numRectangulos;
  DoubleAdder a;

  MiHebraMultAcumulacionAtomica(int miId, int numHebras, long numRectangulos,
                                DoubleAdder a) {
    // ...
    this.miId = miId;
    this.numHebras = numHebras;
    this.numRectangulos = numRectangulos;
    this.a = a;
  }

  public void run() {
    double baseRectangulo = 1.0 / ((double) numRectangulos);

    for (long i = miId; i < numRectangulos; i += numHebras) {
      double x = baseRectangulo * (((double) i) + 0.5);
      a.add(EjemploNumeroPI.f(x));
    }
  }
}

  // ===========================================================================
  class MiHebraUnaAcumulacionAtomica extends Thread {
    // ===========================================================================
// ...
    int miId, numHebras;
    long numRectangulos;
    DoubleAdder a;

    MiHebraUnaAcumulacionAtomica(int miId, int numHebras, long numRectangulos,
                                 DoubleAdder a) {
      // ...
      this.miId = miId;
      this.numHebras = numHebras;
      this.numRectangulos = numRectangulos;
      this.a = a;
    }

    public void run() {
      double baseRectangulo = 1.0 / ((double) numRectangulos);
      double sumaL = 0.0;

      for (long i = miId; i < numRectangulos; i += numHebras) {
        double x = baseRectangulo * (((double) i) + 0.5);
        sumaL += EjemploNumeroPI.f(x);
      }
      a.add(sumaL);
    }
  }


  // ===========================================================================
  class EjemploNumeroPI {
// ===========================================================================

    // -------------------------------------------------------------------------
    public static void main(String args[]) {
      long numRectangulos;
      double baseRectangulo, x, suma, pi;
      int numHebras;
      long t1, t2;
      double tSec, tPar;
      Acumula a;
      MiHebraMultAcumulaciones vt[];
      MiHebraUnaAcumulacion [] vm;
      MiHebraMultAcumulacionAtomica[] vma;
      MiHebraUnaAcumulacionAtomica [] vua;
      DoubleAdder b;

      // Comprobacion de los argumentos de entrada.
      if (args.length != 2) {
        System.out.println("ERROR: numero de argumentos incorrecto.");
        System.out.println("Uso: java programa <numHebras> <numRectangulos>");
        System.exit(-1);
      }
      try {
        numHebras = Integer.parseInt(args[0]);
        numRectangulos = Long.parseLong(args[1]);
        if ((numHebras <= 0) || (numRectangulos <= 0)) {
          System.err.print("Uso: [ java programa <numHebras> <n> ] ");
          System.err.println("donde ( numHebras > 0 ) y ( numRectangulos > 0 )");
          System.exit(-1);
        }
      } catch (NumberFormatException ex) {
        numHebras = -1;
        numRectangulos = -1;
        System.out.println("ERROR: Numeros de entrada incorrectos.");
        System.exit(-1);
      }

      System.out.println();
      System.out.println("Calculo del numero PI mediante integracion.");

      //
      // Calculo del numero PI de forma secuencial.
      //
      System.out.println();
      System.out.println("Inicio del calculo secuencial.");
      t1 = System.nanoTime();
      baseRectangulo = 1.0 / ((double) numRectangulos);
      suma = 0.0;
      for (long i = 0; i < numRectangulos; i++) {
        x = baseRectangulo * (((double) i) + 0.5);
        suma += f(x);
      }
      pi = baseRectangulo * suma;
      t2 = System.nanoTime();
      tSec = ((double) (t2 - t1)) / 1.0e9;
      System.out.println("Version secuencial. Numero PI: " + pi);
      System.out.println("Tiempo secuencial (s.):        " + tSec);

      //
      // Calculo del numero PI de forma paralela:
      // Multiples acumulaciones por hebra.
      //
      System.out.println();
      System.out.print("Inicio del calculo paralelo: ");
      System.out.println("Multiples acumulaciones por hebra.");
      t1 = System.nanoTime();
      // ...
      vt = new MiHebraMultAcumulaciones[numHebras];
      a = new Acumula();

      for (int i = 0; i < numHebras; i++) {
        vt[i] = new MiHebraMultAcumulaciones(i, numHebras, numRectangulos, a);
        vt[i].start();
      }

      for (int i = 0; i < numHebras; i++) {
        try {
          vt[i].join();
        } catch (InterruptedException ex) {
          ex.printStackTrace();
        }
      }

      pi = a.dameDato() * baseRectangulo;

      t2 = System.nanoTime();
      tPar = ((double) (t2 - t1)) / 1.0e9;
      System.out.println("Calculo del numero PI:   " + pi);
      System.out.println("Tiempo ejecucion (s.):   " + tPar);
      System.out.println("Incremento velocidad :   " + tSec / tPar);

      //
      // Calculo del numero PI de forma paralela:
      // Una acumulacion por hebra.
      //
      System.out.println();
      System.out.print("Inicio del calculo paralelo: ");
      System.out.println("Una acumulacion por hebra.");
      vm = new MiHebraUnaAcumulacion[numHebras];
      a = new Acumula();
      t1 = System.nanoTime();
      // ...
      for (int i = 0; i < numHebras; i++) {
        vm[i] = new MiHebraUnaAcumulacion(i, numHebras, numRectangulos, a);
        vm[i].start();
      }

      for (int i = 0; i < numHebras; i++) {
        try {
          vm[i].join();
        } catch (InterruptedException ex) {
          ex.printStackTrace();
        }
      }

      pi = a.dameDato() * baseRectangulo;

      t2 = System.nanoTime();
      tPar = ((double) (t2 - t1)) / 1.0e9;
      System.out.println("Calculo del numero PI:   " + pi);
      System.out.println("Tiempo ejecucion (s.):   " + tPar);
      System.out.println("Incremento velocidad :   " + tSec / tPar);

      //
      // Calculo del numero PI de forma paralela:
      // Multiples acumulaciones por hebra (Atomica)
      //
      System.out.println();
      System.out.print("Inicio del calculo paralelo: ");
      System.out.println("Multiples acumulaciones por hebra (At).");
      t1 = System.nanoTime();
      // ...

      vma = new MiHebraMultAcumulacionAtomica[numHebras];
      b = new DoubleAdder();

      for (int i = 0; i < numHebras; i++) {
        vma[i] = new MiHebraMultAcumulacionAtomica(i, numHebras, numRectangulos, b);
        vma[i].start();
      }

      for (int i = 0; i < numHebras; i++) {
        try {
          vma[i].join();
        } catch (InterruptedException ex) {
          ex.printStackTrace();
        }
      }

      pi = b.doubleValue() * baseRectangulo;

      t2 = System.nanoTime();
      tPar = ((double) (t2 - t1)) / 1.0e9;
      System.out.println("Calculo del numero PI:   " + pi);
      System.out.println("Tiempo ejecucion (s.):   " + tPar);
      System.out.println("Incremento velocidad :   " + tSec / tPar);

      //
      // Calculo del numero PI de forma paralela:
      // Una acumulacion por hebra (Atomica).
      //
      System.out.println();
      System.out.print("Inicio del calculo paralelo: ");
      System.out.println("Una acumulacion por hebra (At).");
      t1 = System.nanoTime();
      // ...

      vua = new MiHebraUnaAcumulacionAtomica[numHebras];
      b = new DoubleAdder();

      for (int i = 0; i < numHebras; i++) {
        vua[i] = new MiHebraUnaAcumulacionAtomica(i, numHebras, numRectangulos, b);
        vua[i].start();
      }

      for (int i = 0; i < numHebras; i++) {
        try {
          vua[i].join();
        } catch (InterruptedException ex) {
          ex.printStackTrace();
        }
      }

      pi = b.doubleValue() * baseRectangulo;

      t2 = System.nanoTime();
      tPar = ((double) (t2 - t1)) / 1.0e9;
      System.out.println("Calculo del numero PI:   " + pi);
      System.out.println("Tiempo ejecucion (s.):   " + tPar);
      System.out.println("Incremento velocidad :   " + tSec / tPar);

      System.out.println();
      System.out.println("Fin de programa.");
    }

    // -------------------------------------------------------------------------
    static double f(double x) {
      return (4.0 / (1.0 + x * x));
    }
}

