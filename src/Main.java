import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * Genererador de ficheros de laberintos de prueba para la práctica de IASI
 *
 * @author Juan Pablo García Plaza Pérez
 */

public class Main {

    public static final char CHAR_SEP = ',';
    public static final String CHAR_OBSTACULO = "▓";
    public static final String CHAR_TELETRASNPORTE = "◙";

    /**
     * @param args Parámetros:
     *             1. Valor umbral para todos los laberintos _umbral_. No puede ser negativo (0 >= umbral)
     *             2. Dimensión del laberinto _D_. Debe ser mayor que 1 (D > 1)
     *             3. Número de salidas _nSal_. Debe ser mayor que 0 y menor que la dimensión al cuadrado (0 > nSal >= D²)
     *             4. Número de parejas de teletransportes _nTP_. Debe ser mayor o igual que 0 y menor o igual que la mitad
     *             de la dimensión al cuadrado (0 >= nTP >= D²/2)
     *             5. Número de obstáculos _nObs_. Debe ser mayor o igual que 0 y menor o igual que la dimensión de al cuadrado
     *             (0 > nSal >= D²)
     *             6. Número de ficheros de laberinto a generar. Debe ser mayor que 0
     *             7. Nombre del directorio donde guardar los ficheros de laberinto
     *             8. Si se desen incluir las características de las ampliaciones (0 -> No, 1 -> Sí)
     *             <p>
     *             NOTA: La suma de (nSal + nTP*2 + nObs) no debe superar la dimensión al cuadrado
     *             nSal + nTP*2 + nObs >= D²
     */
    public static void main(String[] args) {
        int umbral, D, nSal, nTP, nObs, nFicheros;
        boolean ampliaciones;
        String dir;

        umbral = Integer.parseInt(args[0]);
        D = Integer.parseInt(args[1]);
        nSal = Integer.parseInt(args[2]);
        nTP = Integer.parseInt(args[3]);
        nObs = Integer.parseInt(args[4]);
        nFicheros = Integer.parseInt(args[5]);
        dir = args[6];
        ampliaciones = Boolean.parseBoolean(args[7]);

        // Crea la colección de laberintos generados
        Laberinto[] laberintos = new Laberinto[nFicheros];
        Random random = new Random(System.nanoTime());

        // Crea la carpeta si no existe
        File carpeta = new File(dir);
        if (!carpeta.exists()) carpeta.mkdir();

        for (int i = 0; i < laberintos.length; i++) {
            laberintos[i] = new Laberinto(D, umbral);
            for (int j = 0; j < laberintos[i].getD(); j++) {
                for (int k = 0; k < laberintos[i].getD(); k++) {
                    laberintos[i].setValor(j, k, String.valueOf(random.nextInt(9) + 1));
                }
            }
            laberintos[i].setSalidas(nSal);
            if (ampliaciones) {
                laberintos[i].setTeletransportes(nTP);
                laberintos[i].setObstaculos(nObs);
            }
            try (FileWriter fileWriter = new FileWriter(new File(dir + "/laberinto" + i + ".lab"))) {
                fileWriter.write(laberintos[i].toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static class Laberinto {
        int D;
        int umbral;
        private String[][] laberinto;

        public Laberinto(int D, int umbral) {
            this.D = D;
            this.umbral = umbral;
            laberinto = new String[D][D];
        }

        public int getD() {
            return D;
        }

        public int getUmbral() {
            return umbral;
        }

        public void setUmbral(int umbral) {
            this.umbral = umbral;
        }

        public String getValor(int i, int j) {
            return laberinto[i][j];
        }

        public void setValor(int i, int j, String valor) {
            laberinto[i][j] = valor;
        }

        public void setSalidas(int nSal) {
            Random random = new Random(System.nanoTime());
            int iIdx;
            int jIdx;

            for (int i = 0; i < nSal; i++) {
                iIdx = random.nextInt(getD() - 1) + 1;
                jIdx = random.nextInt(getD() - 1) + 1;
                if (!getValor(iIdx, jIdx).equals("0")) setValor(iIdx, jIdx, "0");
                else i--;
            }
        }

        public void setTeletransportes(int nTP) {
            Random random = new Random(System.nanoTime());
            int iIdx;
            int jIdx;

            for (int i = 1; i <= nTP; i++) {
                for (int j = 0; j < 2; j++) {
                    iIdx = random.nextInt(getD() - 1) + 1;
                    jIdx = random.nextInt(getD() - 1) + 1;
                    if (!getValor(iIdx, jIdx).equals(CHAR_TELETRASNPORTE) &&
                            !getValor(iIdx, jIdx).equals("0"))
                        setValor(iIdx, jIdx, i + CHAR_TELETRASNPORTE);
                    else j--;
                }
            }
        }

        public void setObstaculos(int nObs) {
            Random random = new Random(System.nanoTime());
            int iIdx;
            int jIdx;

            for (int i = 0; i < nObs; i++) {
                iIdx = random.nextInt(getD() - 1) + 1;
                jIdx = random.nextInt(getD() - 1) + 1;
                if (!getValor(iIdx, jIdx).equals(CHAR_OBSTACULO) &&
                        !getValor(iIdx, jIdx).matches("\\d\\" + CHAR_TELETRASNPORTE) &&
                        !getValor(iIdx, jIdx).equals("0"))
                    setValor(iIdx, jIdx, CHAR_OBSTACULO);
                else i--;
            }
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append(getUmbral()).append('\n');
            for (int i = 0; i < getD(); i++) {
                for (int j = 0; j < getD(); j++) {
                    stringBuilder.append(getValor(i, j));
                    if (j < getD() - 1) stringBuilder.append(CHAR_SEP);
                }
                stringBuilder.append('\n');
            }

            return stringBuilder.toString();
        }
    }
}
