#include <stdio.h>
#include <stdlib.h>
#include <mpi.h>

// ============================================================================
int main( int argc, char * argv[] ) {
  int  numProcs, miId;

  // Inicializa MPI.
  MPI_Init( &argc, &argv );
  MPI_Comm_size( MPI_COMM_WORLD, &numProcs );
  MPI_Comm_rank( MPI_COMM_WORLD, &miId );
  
  // --------------------------------------------------------------------------
  int prc, suma, aux, dato, i;
  MPI_Status st ;
  // Cada proceso dispone de un dato cualquiera.
  dato = numProcs - miId + 1;
  printf( "Proc %d Dato vale: %d \n", miId, dato );
 
  // ... Incluir codigo asociado a los ejercicios 1 y 2
  if (miId == 0){
    suma = dato;
    for( i = 2; i < numProcs; i+=2){
      MPI_Recv( &aux, 1, MPI_INT, i, 22, MPI_COMM_WORLD, &st);
      suma += aux;
    }
    printf("La suma total = %d \n", suma);

  } else if(miId % 2 == 0){
    printf("Sumo el proceso %d \n", miId);
    MPI_Send( &dato, 1, MPI_INT, 0, 22, MPI_COMM_WORLD);
  }


  // --------------------------------------------------------------------------

  // Finalizacion de MPI.
  MPI_Finalize();

  // Fin de programa.
  printf( "Final de programa (%d) \n", miId );
  return 0;
}

