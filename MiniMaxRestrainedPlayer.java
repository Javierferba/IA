/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plot4;

import java.util.ArrayList;

/**
 *
 * @author Javier Fernández Basso.
 *
 * @version 1.4
 *
 * @brief Este es el algoritmo MinMax para un tablero n*m para jugar al juego conecta-k donde k, n y m vienen definidas
 * en la clase Grid. Esta clase hereda el método turno de la clase Player, el cual, devolverá la columna en la que el
 * algoritmo MiniMax habrá decidido colocar su ficha. Este algoritmo cuenta con un árbol (restringido) y una serie de
 * heurísticas para comprobar los valores de los nodos que se encuentran en el horizonte. De esta manera y, además,
 * añadiéndole otra serie de heurísticas, explicadas en el código, se consigue que este funcione muy bien aun no siendo
 * infalible.
 *
 *
 */
public class MiniMaxRestrainedPlayer extends Player {


        /**
         * @brief Arbol de n hijos.
         */
        class Arbol_N_Ario {

            /**
             * @brief Clase nodo del arbol
             */
            class Nodo {
                private byte valor = 0; /// Indica quien gana en ese caso: 1=jugador, -1=IA, 0=empate o nodo de paso.
                private byte profundidad; /// Profundidad a la que se encuentra el nodo
                private byte columna = -1;/// Indica en que columna se lanza la ficha para llegar a ese estado.
                private byte[][] _estado;/// Matriz con el estado del tablero.
                private ArrayList<Nodo> _hijos;/// Lista que contiene hasta N hijos del nodo.

                /**
                 * @param estado
                 * @brief Constructor parametrizado.
                 */
                public Nodo(byte[][] estado) {
                    this._estado = estado;
                    _hijos = new ArrayList<>();
                }

                /**
                 * @brief Constructor copia
                 * @param nodo
                 */
                public Nodo(Nodo nodo) {
                    this._estado = copiaEstado(nodo);
                    _hijos = new ArrayList<>();
                }

                /**
                 * @param hijo
                 * @return 0 si se ha introducido correctamente o -1 si ha habido algún problema.
                 * @brief Inserta un hijo suministrado.
                 */
                public int insertarHijo(Nodo hijo) {
                    if (_hijos.size() < _columnas) {
                        _hijos.add(hijo);
                        return 0;
                    }
                    return -1;
                }

                /**
                 * @brief Devuelve el valor columna.
                 */
                public int getColumna() {
                    return columna;
                }

                /**
                 * @brief Devuelve la profundidad.
                 */
                public byte getProfundidad() {
                    return profundidad;
                }
            }

            private  byte profundidadMax = 8;//Siempre debe de ser un número par.
            private byte _columnas;/// Columnas del tablero de juego.
            private byte _filas;/// Filas del tablero de juego.
            private int _conecta;/// Número de fichas a conectar.
            private Nodo _raiz;/// Caso actual.
            private byte winwin = 0;///para ganar si o si.
            private byte loselose = 0;///para evitar perder sin poder evitarse.

            /**
             * @param columnas Columnas del tablero de juego.
             * @param filas    Filas del tablero de juego.
             * @param conecta  Número de fichas a conectar.
             * @param estado   Estado actual del tablero de juego.
             * @brief constructor del arbol.
             */
            public Arbol_N_Ario(byte columnas, byte filas, int conecta, byte[][] estado) {
                _columnas = columnas;
                _filas = filas;
                _conecta = conecta;
                _raiz = new Nodo(estado);
                _raiz.profundidad = 1;
                long startTime = System.nanoTime();
                rellenaArbol();
                calculaValores();
                long endTime = System.nanoTime();
                long timeElapsed = endTime - startTime;
                System.out.println("Execution time in seconds: " + timeElapsed / 1000000000);
            }

            /**
             * @param tablero Caso a comprobar
             * @return 1 si gana el jugador, -1 si gana el agente inteligente, 0 en caso de que ninguno gane.
             * @brief Comprueba si para un cierto caso alguno de los dos competidores gana.
             */
            private int checkWin(byte[][] tablero) {


                int ganador = 0;
                int ganar1;
                int ganar2;
                boolean salir = false;
                // Comprobar horizontal
                for (int i = 0; (i < _filas) && !salir; i++) {
                    ganar1 = 0;
                    ganar2 = 0;
                    for (int j = 0; (j < _columnas) && !salir; j++) {
                        if (tablero[i][j] != Main.VACIO) {
                            if (tablero[i][j] == Main.PLAYER1) {
                                ganar1++;
                            } else {
                                ganar1 = 0;
                            }
                            // Gana el jugador 1
                            if (ganar1 >= _conecta) {
                                ganador = Main.PLAYER1;
                                // Ganador 1 en horizontal;
                                salir = true;
                            }
                            if (!salir) {
                                if (tablero[i][j] == Main.PLAYER2) {
                                    ganar2++;
                                } else {
                                    ganar2 = 0;
                                }
                                // Gana el jugador 2
                                if (ganar2 >= _conecta) {
                                    ganador = Main.PLAYER2;
                                    // Ganador 2 en horizontal;
                                    salir = true;
                                }
                            }
                        } else {
                            ganar1 = 0;
                            ganar2 = 0;
                        }
                    }
                }
                // Comprobar vertical
                for (int i = 0; (i < _columnas) && !salir; i++) {
                    ganar1 = 0;
                    ganar2 = 0;
                    for (int j = 0; (j < _filas) && !salir; j++) {
                        if (tablero[j][i] != Main.VACIO) {
                            if (tablero[j][i] == Main.PLAYER1) {
                                ganar1++;
                            } else {
                                ganar1 = 0;
                            }
                            // Gana el jugador 1
                            if (ganar1 >= _conecta) {
                                ganador = Main.PLAYER1;
                                // Ganador 1 en vertical;
                                salir = true;
                            }
                            if (!salir) {
                                if (tablero[j][i] == Main.PLAYER2) {
                                    ganar2++;
                                } else {
                                    ganar2 = 0;
                                }
                                // Gana el jugador 2
                                if (ganar2 >= _conecta) {
                                    ganador = Main.PLAYER2;
                                    // Ganador 2 en vertical;
                                    salir = true;
                                }
                            }
                        } else {
                            ganar1 = 0;
                            ganar2 = 0;
                        }
                    }
                }
                // Comprobar oblicuo. De izquierda a derecha
                for (int i = 0; i < _filas && !salir; i++) {
                    for (int j = 0; j < _columnas && !salir; j++) {
                        int a = i;
                        int b = j;
                        ganar1 = 0;
                        ganar2 = 0;
                        while (a < _filas && b < _columnas && !salir) {
                            if (tablero[a][b] != Main.VACIO) {
                                if (tablero[a][b] == Main.PLAYER1) {
                                    ganar1++;
                                } else {
                                    ganar1 = 0;
                                }
                                // Gana el jugador 1
                                if (ganar1 >= _conecta) {
                                    ganador = Main.PLAYER1;
                                    // Ganador 1 en oblicuo izquierda;
                                    salir = true;
                                }
                                if (ganador != Main.PLAYER1) {
                                    if (tablero[a][b] == Main.PLAYER2) {
                                        ganar2++;
                                    } else {
                                        ganar2 = 0;
                                    }
                                    // Gana el jugador 2
                                    if (ganar2 >= _conecta) {
                                        ganador = Main.PLAYER2;
                                        // Ganador 2 en oblicuo izquierda;
                                        salir = true;
                                    }
                                }
                            } else {
                                ganar1 = 0;
                                ganar2 = 0;
                            }
                            a++;
                            b++;
                        }
                    }
                }
                // Comprobar oblicuo de derecha a izquierda
                for (int i = _filas - 1; i >= 0 && !salir; i--) {
                    for (int j = 0; j < _columnas && !salir; j++) {
                        int a = i;
                        int b = j;
                        ganar1 = 0;
                        ganar2 = 0;
                        while (a >= 0 && b < _columnas && !salir) {
                            if (tablero[a][b] != Main.VACIO) {
                                if (tablero[a][b] == Main.PLAYER1) {
                                    ganar1++;
                                } else {
                                    ganar1 = 0;
                                }
                                // Gana el jugador 1
                                if (ganar1 >= _conecta) {
                                    ganador = Main.PLAYER1;
                                    // Ganador 1 en oblicuo derecha
                                    salir = true;
                                }
                                if (ganador != Main.PLAYER1) {
                                    if (tablero[a][b] == Main.PLAYER2) {
                                        ganar2++;
                                    } else {
                                        ganar2 = 0;
                                    }
                                    // Gana el jugador 2
                                    if (ganar2 >= _conecta) {
                                        ganador = Main.PLAYER2;
                                        // Ganador 2 en oblicuo derecha
                                        salir = true;
                                    }
                                }
                            } else {
                                ganar1 = 0;
                                ganar2 = 0;
                            }
                            a--;
                            b++;
                        }
                    }
                }

                return ganador;
            }

            /**
             * @brief Calcula si tras poner una ficha, por culpa de ese movimiento gana el rival.
             * @param tablero tablero después de lanzar nuestra ficha.
             * @param columna columna en la que hemos lanzado la ficha.
             * @return true si la ficha efectivamente nos hace perder, false si no es el caso.
             */
            private boolean isWinNext(byte[][] tablero, byte columna) {

                for (int i = 0; i < _columnas; i++) {
                    byte[][] aux = tablero;
                    for (int j = _filas-1; j >= 0; j--) {
                        if (aux[j][i]==0){
                            aux[j][i]=1;
                            if (checkWin(aux)==1){
                                return i == columna;
                            }
                            break;
                        }
                    }
                }

                return false;
            }

            /**
             * @param m Caso concreto de tablero.
             * @return matriz donde el (n,0) será la posicion x de un posivle movimiento y las (n,1) la posicion y del mismo. Null si el tablero estácompleto.
             * @brief Comprueba en cuantos huecos se puede poner en un turno dado un caso de tablero concreto.
             */
            private int[][] posiblesHuecos(byte[][] m) {
                int n = _columnas;
                for (int i = 0; i < _columnas; ++i) {
                    if (m[0][i] != 0) {
                        n -= 1;
                    }
                }
                if (n == 0 || checkWin(m) != 0) {
                    return null;
                }
                int[][] aux = new int[n][2];
                int cont = 0;
                for (int i = 0; i < _columnas; i++) {
                    for (int j = _filas - 1; j >= 0; j--) {
                        if (m[j][i] == 0) {
                            aux[cont][0] = i;
                            aux[cont][1] = j;
                            cont++;
                            break;
                        }
                    }
                }
                return aux;
            }

            /**
             * @param nodo nodo que contiene el estado a copiar.
             * @return copia del atributo estado del nodo.
             * @brief Devuelve la copia del estado de un nodo suministrado.
             */
            private byte[][] copiaEstado(Nodo nodo) {
                byte[][] copia = new byte[_filas][_columnas];
                for (int j = 0; j < _filas; j++) {
                    System.arraycopy(nodo._estado[j], 0, copia[j], 0, _columnas);
                }
                return copia;
            }

            /**
             * @brief Sobrecarga del método rellena para que se empiece por la raiz.
             */
            private void rellenaArbol() {
                rellena(_raiz);
            }

            /**
             * @param nodo  nodo suministrado para comprobar sus hijos.
             * @brief Se encarga de rellenar el arbol de manera recursiva con las distintas posibilidades que hay.
             */
            private void rellena(Nodo nodo) {
                if (checkWin(nodo._estado)==-1){
                    nodo.valor = -1;
                    return;
                } else if (checkWin(nodo._estado)==1){
                    nodo.valor = 1;
                    return;
                }

                int[][] vAux = posiblesHuecos(nodo._estado);
                if (vAux != null) {
                    for (byte i = 0; i < vAux.length; i++) {
                        Nodo aux = new Nodo(nodo);
                        if (nodo.profundidad%2==1) {
                            aux._estado[vAux[i][1]][vAux[i][0]] = -1;
                        } else {
                            aux._estado[vAux[i][1]][vAux[i][0]] = 1;
                        }
                        nodo.insertarHijo(aux);
                        nodo._hijos.get(i).profundidad = (byte) (nodo.profundidad+1);
                        nodo._hijos.get(i).columna = (byte) vAux[i][0];
                    }
                    if (nodo._hijos.get(0).profundidad!=profundidadMax) {
                        for (int i = 0; i < vAux.length; i++) {
                            rellena(nodo._hijos.get(i));
                        }
                    }
                }
            }

            /**
             * @brief Calcula y modifica la variable valores de la raiz y de sus hijos con los siguientes datos:
             * · -1: si en el nodo encontrado gana el agente inteligente.
             * · 1: si en el nodo encontrado gana el jugador.
             * · No se modifica: se queda a 0 en caso de empate o caso de paso.
             */
            private void calculaValores() {
                calculaValores(_raiz);
            }

            /**
             * @param nodo nodo desde el que se hacen las comprobaciones.
             * @brief Calcula y modifica la variable valores del nodo suministrado y de sus hijos con los siguientes datos:
             * · -1: si en el nodo encontrado gana el agente inteligente.
             * · 1: si en el nodo encontrado gana el jugador.
             * · No se modifica: se queda a 0 en caso de empate o caso de paso.
             */
            private void calculaValores(Nodo nodo) {
                if (nodo._hijos.isEmpty()) {
                    nodo.valor = (byte) checkWin(nodo._estado);

                    if (nodo.valor==0) {
                        if (posiblesHuecos(nodo._estado)!=null) {
                            nodo.valor = evaluaNodo(nodo);
                        }
                    }
                } else {
                    nodo.valor=0;
                    for (int i = 0; i < nodo._hijos.size(); i++) {
                        calculaValores(nodo._hijos.get(i));
                    }
                }
            }

            /**
             * @param nodo nodo raiz del subarbol.
             * @return suma de todos los valores de los nodos.
             * @brief Recorre en inorden el subarbol que tiene como raiz el nodo suministado
             * y suma todos los valores de sus nodos.
             */
            public int recorridoInorden(Nodo nodo) {
                byte guardaEstadoW = winwin, guardaEstadoL = loselose;
                int contador = 0;
                if (nodo._hijos.isEmpty()) {
                    return nodo.valor;
                } else {
                    int candidato;
                    boolean esPadreDeHojas = true;

                    for (int i = 0; i < nodo._hijos.size(); i++) {
                        if (!nodo._hijos.get(i)._hijos.isEmpty()) {
                            esPadreDeHojas = false;
                            break;
                        }
                    }

                    for (int i = 0; i < nodo._hijos.size(); i++) {
                        candidato = recorridoInorden(nodo._hijos.get(i));
                        if (nodo.profundidad%2==0 && candidato>0) {
                            contador+=nodo._hijos.get(i).valor;
                        } else if (nodo.profundidad%2!=0 && candidato<0){
                            contador+=nodo._hijos.get(i).valor;
                        }
                    }

                    if(esPadreDeHojas){
                        return contador;
                    }

                    byte aux = nodo._hijos.get(0).valor;
                    for (int i = 0; i < nodo._hijos.size(); i++) {
                        if (nodo.profundidad%2==0 && nodo._hijos.get(i).valor > aux) {
                            aux = nodo._hijos.get(i).valor;
                            if(nodo._hijos.get(i).valor==1){
                                loselose++;
                            }
                        } else if (nodo.profundidad%2!=0 && nodo._hijos.get(i).valor < aux){
                            aux = nodo._hijos.get(i).valor;
                            if(nodo._hijos.get(i).valor==-1){
                                winwin++;
                            }
                        }
                    }
                    if (winwin>=2){
                        return Byte.MIN_VALUE;
                    } else if (guardaEstadoW==winwin) {
                        winwin=0;
                    }
                    if (loselose>=2){
                        return Byte.MAX_VALUE;
                    } else if (guardaEstadoL==loselose) {
                        loselose=0;
                    }
                    return aux;
                }
            }

            /**
             * @brief Evalua los nodos hoja que no son finales, es decir, que no gana ningún jugador ni se rellena por completo el tablero.
             * @param nodo
             * @return valor del nodo.
             */
            private byte evaluaNodo(Nodo nodo) {
                byte valorAux = 0;
                int[][] vAux = posiblesHuecos(nodo._estado);

                if (vAux != null) {
                    for (byte i = 0; i < vAux.length; i++) {
                        byte[][] matrix = copiaEstado(nodo);
                        matrix[vAux[i][1]][vAux[i][0]] = -1;

                        ///Comprobación horizontales.
                        for (int j = 0; j < _filas; j++) {
                            int blancos=0;
                            for (int k = 0; k < _columnas; k++) {
                                if(matrix[j][k]==1) {
                                    blancos = 0;
                                } else if(matrix[j][k]==-1){
                                    int puestos=1;

                                    //Rama derecha.
                                    while(k<_columnas) {
                                        if (matrix[j][k] == 1) {
                                            break;
                                        } else if (matrix[j][k] == 0) {
                                            if (puestos==1){
                                                break;
                                            }
                                            blancos++;
                                        } else {
                                            puestos++;
                                        }
                                        k++;
                                    }
                                    if ((blancos+puestos)>3 && puestos>1){
                                        valorAux += puestos^2;
                                    }
                                }
                                blancos++;
                            }
                        }

                        ///Comprobación verticales.
                        for (int j = 0; j < _columnas; j++) {
                            int blancos = 0;
                            for (int k = _filas-1; k >= 0; k--) {
                                if(matrix[k][j]==-1){
                                    int puestos=1;

                                    //Rama derecha.
                                    while(k>=0) {
                                        if (matrix[k][j] == 1) {
                                            break;
                                        } else if (matrix[k][j] == 0) {
                                            if (puestos==1){
                                                break;
                                            }
                                            blancos++;
                                        } else {
                                            puestos++;
                                        }
                                        k--;
                                    }
                                    if ((blancos+puestos)>3 && puestos>1){
                                        valorAux += puestos^2;
                                    }
                                }
                            }
                        }

                        ///Comprobación oblicuos izquierda a derecha.
                        boolean[][] matrixBoolean = new boolean[_filas][_columnas];
                        for (int j = 0; j < _columnas; j++) {
                            int blancos = 0;
                            for (int k = 0; k < _filas; k++) {
                                if(matrix[k][j]==-1 && !matrixBoolean[k][j]){
                                    int puestos=1;

                                    //derecha arriba.
                                    int w = j-1, t = k+1;
                                    matrixBoolean[k][j]=true;
                                    while (w>=0 && t<_columnas) {
                                        if (matrix[w][t]==1){
                                            break;
                                        } else if (matrix[w][t]==-1) {
                                            puestos++;
                                            matrixBoolean[k][j]=true;
                                        } else {
                                            blancos++;
                                        }
                                        w--;
                                        t++;
                                    }

                                    if ((blancos+puestos)>3 && puestos>1){
                                        valorAux += puestos^2;
                                    }
                                }
                            }
                        }

                        //Comprobación oblicuos izquierda a derecha.
                        matrixBoolean = new boolean[_filas][_columnas];
                        for (int j = _columnas-1; j >=0; j--) {
                            int blancos = 0;
                            for (int k = 0; k < _filas; k++) {
                                if(matrix[k][j]==-1 && !matrixBoolean[k][j]){
                                    int puestos=1;

                                    //izquierda arriba.
                                    int w = j-1, t = k-1;
                                    matrixBoolean[k][j]=true;
                                    while (w>=0 && t>=0) {
                                        if (matrix[w][t]==1){
                                            break;
                                        } else if (matrix[w][t]==-1) {
                                            puestos++;
                                            matrixBoolean[k][j]=true;
                                        } else {
                                            blancos++;
                                        }
                                        w--;
                                        t--;
                                    }

                                    if ((blancos+puestos)>3 && puestos>1){
                                        valorAux += puestos^2;
                                    }
                                }
                            }
                        }
                    }
                }
                return valorAux;
            }

        }
        private byte turno = 1; /// Variable usada para hacer cosas única y exclusivamente el primer turno.
        private Arbol_N_Ario arbol = null;/// Declaracion del arbol.


        /**
         * @param tablero Tablero de juego
         * @param conecta Número de fichas consecutivas adyacentes necesarias para
         *                ganar
         * @return Devuelve si ha ganado algun jugador
         * @brief funcion que determina donde colocar la ficha este turno
         */
        @Override
        public int turno(Grid tablero, int conecta) {
            int posicion;

            // Comprobación del primer movimiento a hacer y creación del árbol de posibilidades.
            turno+=2;
            System.gc();
            arbol = new Arbol_N_Ario((byte) tablero.getColumnas(), (byte) tablero.getFilas(), conecta, byteToInt(tablero.copyGrid(), tablero.getFilas(), tablero.getColumnas()));

            posicion = getmejorOpcion();
            if(posicion==Byte.MIN_VALUE){
                posicion=getRandomColumn(tablero);
            }

            return posicion;

        } // turno

        /**
         * @param m       Matriz de enteros a cambiar.
         * @param fila    Número de filas.
         * @param columna Número de columnas.
         * @return la matriz con datos tipo byte en vez ve tipo int.
         * @brief Cambia el tipo de una matriz de enteros a bytes.
         */
        private byte[][] byteToInt(int[][] m, int fila, int columna) {
            byte[][] bytes = new byte[fila][columna];
            for (int i = 0; i < fila; i++) {
                for (int j = 0; j < columna; j++) {
                    bytes[i][j] = (byte) m[i][j];
                }
            }
            return bytes;
        }

        /**
         * @return entero con la columna.
         * @brief Devuelve en que columna es mejor poner la ficha o Byte.MIN_VALUE en caso de que solo haya una columna disponible.
         */
        private int getmejorOpcion() {
            int[] mejor = new int[2];
            int candidato;
            boolean sinPreferencia = true;
            ArrayList<Byte> win = new ArrayList<>();
            win.add(isWin(arbol._raiz));
            if (win.get(0) >= 0) {
                return win.get(0);
            }
            if (win.get(0)!=Byte.MIN_VALUE) {
                win.set(0, (byte) -win.get(0));
            }
            mejor[0] = arbol.recorridoInorden(arbol._raiz._hijos.get(0));
            mejor[1] = arbol._raiz._hijos.get(0).columna;
            for (int i = 0; i < arbol._raiz._hijos.size(); i++) {
                candidato = arbol.recorridoInorden(arbol._raiz._hijos.get(i));
                if (arbol._raiz._hijos.get(i).columna!=win.get(0)) {
                    if (candidato < mejor[0]) {
                        sinPreferencia = false;
                        mejor[0] = arbol.recorridoInorden(arbol._raiz._hijos.get(i));
                        mejor[1] = arbol._raiz._hijos.get(i).columna;
                    } else if (candidato > mejor[0]) {
                        win.add(arbol._raiz._hijos.get(i).columna);
                    }
                }
            }
            if (sinPreferencia) {
                int size = (win.get(0)==Byte.MIN_VALUE) ? win.size()-1 : win.size();
                int n = arbol._raiz._hijos.size()/2, s = arbol._raiz._hijos.size()/2;
                if (size==arbol._raiz._hijos.size()){
                    return (winRapida(n)==-1) ? arbol._raiz._hijos.get(n).columna : winRapida(n);
                }
                int[][] m = arbol.posiblesHuecos(arbol._raiz._estado);
                if(m.length==1){
                    return Byte.MIN_VALUE;
                }

                for (int i = n; i >= 0; i--) {
                    if (!find(win, arbol._raiz._hijos.get(i).columna)) {
                        n = i;
                        break;
                    }
                }
                for (int i = s; i < arbol._raiz._hijos.size(); i++) {
                    if (!find(win, arbol._raiz._hijos.get(i).columna)) {
                        s = i;
                        break;
                    }
                }
                n = (((arbol._raiz._hijos.size()/2)-n)<(s-(arbol._raiz._hijos.size()/2))) ? n : s;
                return (winRapida(n)==-1) ? arbol._raiz._hijos.get(n).columna : winRapida(n);
            }

            return mejor[1];
        }

        /**
         * @brief Comprueba si en el siguiente movimiento gana alguno de los dos jugadores y mueve para ganar o para cortar la victoria.
         * @param nodo nodo a comprobar.
         * @return columna en la que echar para ganar o cortar la victoria (número positivo), columna en la que no se debe
         * de echar la ficha, ya que habilitamos al otro jugador una jugada ganadora (número negativo) o Byte.MIN_VALUE
         * en caso de que nadie gane en el siguiente movimiento.
         */
        public byte isWin(Arbol_N_Ario.Nodo nodo) {
        //gana IA en este movimiento.
        for (int i = 0; i < nodo._hijos.size(); i++) {
            if (arbol.checkWin(nodo._hijos.get(i)._estado) == -1) {
                return nodo._hijos.get(i).columna;
            }
        }
        //gana jugador después de que la IA ponga.
        for (int i = 0; i < nodo._hijos.size(); i++) {
            for (int j = 0; j < nodo._hijos.get(i)._hijos.size(); j++) {
                if (arbol.checkWin(nodo._hijos.get(i)._hijos.get(j)._estado) == 1) {
                    if (arbol.isWinNext(nodo._hijos.get(i)._estado, nodo._hijos.get(i).columna)) {
                        return (byte) -nodo._hijos.get(i)._hijos.get(j).columna;
                    } else {
                        return nodo._hijos.get(i)._hijos.get(j).columna;
                    }
                }
            }
        }
        return Byte.MIN_VALUE;
    }

        /**
     * @brief Función usada para evitar que el contrincante gane cuando se está al pincipio de la partida con 4 fichas horizontales en la primera fila.
     * @param n
     * @return columna en la que hay que echar la ficha.
     */
        private int winRapida (int n) {

            if (arbol._raiz._estado[arbol._filas-1][n]==1 && (arbol._columnas>4 && arbol._filas>4) && turno<(arbol._filas*2+1)){
                if (arbol._raiz._estado[arbol._filas-1][n-2]==1 || arbol._raiz._estado[arbol._filas-1][n+1]==1){
                    return n-1;
                } else if (arbol._raiz._estado[arbol._filas-1][n-1]==1 || arbol._raiz._estado[arbol._filas-1][n+2]==1) {
                    return n+1;
                }
            }
            return -1;
        }

        /**
         * @brief Busca en una lista de elementos el dato suministrado.
         * @param win ArrayList de bytes en el que se busca.
         * @param columna dato a buscar.
         * @return true si lo encuetra, false si no lo encuentra.
         */
        private boolean find(ArrayList<Byte> win, byte columna) {
            for (Byte aByte : win) {
                if (aByte == columna) {
                    return true;
                }
            }
            return false;
        }


}// MiniMaxRestrainedPlayer
