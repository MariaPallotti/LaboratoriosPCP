package org.example.Laboratorio2;

import static java.lang.Math.min;

// ============================================================================
class EjemploMuestraNumeros {
// ============================================================================

  // --------------------------------------------------------------------------
  public static void main(String[] args) {
    int  n, numHebras;

    // Comprobacion y extraccion de los argumentos de entrada.
    if( args.length != 2 ) {
      System.err.println( "Uso: java programa <numHebras> <n>" );
      System.exit( -1 );
    }
    try {
      numHebras = Integer.parseInt( args[ 0 ] );
      n         = Integer.parseInt( args[ 1 ] );
      if( ( numHebras <= 0 ) || ( n <= 0 ) ) {
        System.err.print( "Uso: [ java programa <numHebras> <n> ] " );
        System.err.println( "donde ( numHebras > 0 )  y ( n > 0 )" );
        System.exit( -1 );
      }
    } catch( NumberFormatException ex ) {
      numHebras = -1;
      n         = -1;
      System.out.println( "ERROR: Argumentos numericos incorrectos." );
      System.exit( -1 );
    }
    //
    // Implementacion paralela con distribucion ciclica o por bloques.
    //
    // Crea un vector de hebras. Crea y arranca las hebras
    // (A) ... 
    
    System.out.println("Comienzo implementación cíclica");
    
    miHebraCiclica[] vC = new miHebraCiclica[numHebras];
    
    for( int i = 0; i < numHebras; i++){
        
       vC[i] = new miHebraCiclica(i, n, numHebras);
       vC[i].start();
       
    }
    
    
    // Espera a que terminen todas las hebras.
    // (B) ... 
    //
    
    for(int i = 0; i < numHebras; i++){
      try{
        vC[i].join();
        }
      catch (InterruptedException ex){
          ex.printStackTrace();
    }

  
  System.out.println("Fin implementación cíclica");
  
    System.out.println("Comienzo implementación por bloques");

  
  miHebraBloques[] vB = new miHebraBloques[numHebras];
    
    for(i = 0; i < numHebras; i++){
        
       vB[i] = new miHebraBloques(i, n, numHebras);
       vB[i].start();
       
    }
    
    
    // Espera a que terminen todas las hebras.
    // (B) ... 
    //
    
    for(i = 0; i < numHebras; i++){
      try{
        vB[i].join();
        }
      catch (InterruptedException ex){
        ex.printStackTrace();
      }
  }
  
  System.out.println("Fin implementación por bloques");
}
}


// Crea las clases adicionales que sean necesarias
// (C) ... 
// 

private static class miHebraCiclica extends Thread{
    
    int miId;
    int numero;
    int numHebras;
    
    public miHebraCiclica(int miId, int numero, int numHebras){
        this.miId = miId;
        this.numero = numero;
        this.numHebras = numHebras;
    }
    
    public void run(){
        for( int i = miId ; i < numero; i += numHebras){
            System.out.println("h"+ miId + "n" + i);
        }
    }
}

private static class miHebraBloques extends Thread{
    
    int miId;
    int numero;
    int numHebras;
    
    public miHebraBloques(int miId, int numero, int numHebras){
        this.miId = miId;
        this.numero = numero;
        this.numHebras = numHebras;
    }
    
    public void run(){
        int tam = (numero + numHebras -1)/ numHebras ;
        int inicio = tam*miId;
        int fin = min(inicio + tam, numero);
        
        for( int i = inicio ; i < fin ; i++){
            System.out.println("h"+ miId + "n" + i);
        }
    }
}
}
