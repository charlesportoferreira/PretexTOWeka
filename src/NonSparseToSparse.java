

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class NonSparseToSparse {

    public static void main(String args[]) {
        System.out.println("Reading data...");
        String oldData = args[0];
        String newData = args[1];
        String[] labels = readLabels(args[2]);
        int numFeatures = Integer.parseInt(args[3]);
        String header = generateHeader(numFeatures);
        PretextToWeka ptw = new PretextToWeka(newData, true);
        ptw.saveFile(header, newData, true);
        ptw.processData(oldData, newData, labels,numFeatures);
    }

    private static String generateHeader(int numFeatures) {
        StringBuilder sb = new StringBuilder("@relation csv2arff\n\n");
        for (int i = 0; i < numFeatures; i++) {
            sb.append("@attribute f").append(i).append(" numeric\n");
        }
        sb.append("@attribute @@class@@ {1,2,3,4}\n\n@data\n");
        return sb.toString();
    }

    private static String[] readLabels(String filename) {
        StringBuilder sb = new StringBuilder();
        try (FileReader fr = new FileReader(filename); BufferedReader br = new BufferedReader(fr)) {
            while (br.ready()) {
                sb.append(br.readLine()).append(",");
            }
            br.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("file not found");
        }
        return sb.toString().split(",");
    }
}
