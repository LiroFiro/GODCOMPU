import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class SimulacionProcesos {
    // Parámetros para configurar la simulación
    private static final int RANDOM_SEED = 42;
    private static int MEMORIA_TOTAL = 100;
    private static int VELOCIDAD_CPU = 1; // Velocidad del CPU: una instrucción por unidad de tiempo
    private static final int TIEMPO_COMPARTIDO = 3; // Cantidad de instrucciones por atención del CPU
    private static int NUM_PROCESADORES = 1; // Número de procesadores

    // Estados de los procesos
    private static final String ESTADO_NEW = "New";
    private static final String ESTADO_READY = "Ready";
    private static final String ESTADO_RUNNING = "Running";
    private static final String ESTADO_WAITING = "Waiting";
    private static final String ESTADO_TERMINATED = "Terminated";

    // Lista para almacenar los procesos
    private static List<Proceso> procesos = new ArrayList<>();
    private static Random random = new Random(RANDOM_SEED);

    static class Proceso {
        private final int id;
        private final int memoria;
        private int instruccionesTotales;
        private String estado;
        private long tiempoEnCPU;

        Proceso(int id) {
            this.id = id;
            this.memoria = random.nextInt(10) + 1;
            this.instruccionesTotales = random.nextInt(10) + 1;
            this.estado = ESTADO_NEW;
            this.tiempoEnCPU = 0;
        }

        void ejecutar() {
            this.estado = ESTADO_READY;
            long inicioProceso = System.currentTimeMillis();
            while (instruccionesTotales > 0) {
                try {
                    TimeUnit.SECONDS.sleep(VELOCIDAD_CPU); // Tiempo que tarda en ejecutar una instrucción
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                instruccionesTotales--;
                this.tiempoEnCPU += VELOCIDAD_CPU * 1000;
                if (instruccionesTotales <= 0) {
                    this.estado = ESTADO_TERMINATED;
                    break;
                }
                if (instruccionesTotales % TIEMPO_COMPARTIDO == 0) {
                    this.estado = ESTADO_READY;
                    break;
                }
            }
            if (instruccionesTotales > 0) {
                int randomNum = random.nextInt(21);
                if (randomNum == 1) {
                    this.estado = ESTADO_WAITING;
                } else if (randomNum == 2) {
                    this.estado = ESTADO_READY;
                }
            }
            long finProceso = System.currentTimeMillis();
            this.tiempoEnCPU += finProceso - inicioProceso;
        }

        long getTiempoEnCPU() {
            return tiempoEnCPU;
        }
    }

    public static void main(String[] args) {
        System.out.println("Simulación de Procesos en un Sistema Operativo de Tiempo Compartido");
        System.out.println();

        Scanner scanner = new Scanner(System.in);

        System.out.print("Ingrese la cantidad de procesos: ");
        int numProcesos = scanner.nextInt();

        System.out.print("Ingrese el intervalo de llegada de procesos (en segundos): ");
        int intervaloLlegada = scanner.nextInt();

        System.out.println("Seleccione una opción:");
        System.out.println("1. Incrementar la memoria");
        System.out.println("2. Añadir más procesadores");
        System.out.print("Opción: ");
        int opcion = scanner.nextInt();

        if (opcion == 1) {
            System.out.print("Ingrese la cantidad de memoria adicional: ");
            int memoriaAdicional = scanner.nextInt();
            MEMORIA_TOTAL += memoriaAdicional;
        } else if (opcion == 2) {
            System.out.print("Ingrese el número de procesadores adicionales: ");
            int procesadoresAdicionales = scanner.nextInt();
            NUM_PROCESADORES += procesadoresAdicionales;
        }

        System.out.println();

        // Ejecutar la simulación con los parámetros dados
        ejecutarSimulacion(numProcesos, intervaloLlegada);
    }

    private static void ejecutarSimulacion(int numProcesos, int intervaloLlegada) {
        procesos.clear();
        for (int i = 0; i < numProcesos; i++) {
            Proceso proceso = new Proceso(i + 1);
            procesos.add(proceso);
            System.out.println("Proceso " + proceso.id + " creado en estado: " + proceso.estado);
            proceso.ejecutar();
            System.out.println("Proceso " + proceso.id + " terminado en estado: " + proceso.estado);
            System.out.println();
            try {
                TimeUnit.SECONDS.sleep(intervaloLlegada);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Calcular promedio de tiempo en CPU y desviación estándar
        long sumaTiempos = 0;
        for (Proceso proceso : procesos) {
            sumaTiempos += proceso.getTiempoEnCPU();
        }
        double promedio = sumaTiempos / (double) numProcesos;

        double sumaDiferenciaCuadrados = 0;
        for (Proceso proceso : procesos) {
            sumaDiferenciaCuadrados += Math.pow(proceso.getTiempoEnCPU() - promedio, 2);
        }
        double desviacionEstandar = Math.sqrt(sumaDiferenciaCuadrados / numProcesos);

        System.out.println("Promedio de tiempo en CPU: " + promedio + " milisegundos");
        System.out.println("Desviación estándar: " + desviacionEstandar + " milisegundos");
    }
}
