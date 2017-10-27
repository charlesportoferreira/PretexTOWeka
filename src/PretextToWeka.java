

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author charleshenriqueportoferreira
 */
public class PretextToWeka {

    public String nomeArquivo;
    private boolean isreduzido;


    public void convert() {
        String currently = System.getProperty("user.dir");
        System.out.println("reading attributes");
        String attributes = lerArquivoNames(currently + "/discover/discover", ".names");
        String nomeArquivoCompleto = currently + nomeArquivo;
        System.out.println(nomeArquivoCompleto);
        attributes = converteArquivoNames(attributes);
        saveFile(attributes, nomeArquivoCompleto, false);
        System.out.println("Lendo dados");
        String dados = "@data" + "\n";
        saveFile(dados, nomeArquivoCompleto, true);
        processaArquivoData(currently + "/discover/discover", ".data", nomeArquivoCompleto);
    }

    public PretextToWeka(String nomeArquivo, boolean isReduzido) {
        this.nomeArquivo = nomeArquivo;
        this.isreduzido = isReduzido;
    }

    private String pretextToARFF(String arquivoData, String arquivoNames) {
        arquivoNames = converteArquivoNames(arquivoNames);
        arquivoData = converteArquivoDataReduzido(arquivoData);
        String arquivoFinal = arquivoNames + "\n" + arquivoData;
        return arquivoFinal;
    }


    private String converteArquivoNames(String arquivoNames) {
        System.out.println("convertendo atributos");
        arquivoNames = arquivoNames.replaceAll("att_class\\.\n", "@RELATION " + nomeArquivo);
        arquivoNames = arquivoNames.replaceAll("filename:string:ignore.", "\n");
        arquivoNames = arquivoNames.replaceAll("\":integer\\.", " NUMERIC");
        arquivoNames = arquivoNames.replaceAll("\":real\\.", " NUMERIC");
        arquivoNames = arquivoNames.replaceAll("(att_class:nominal\\(\")", "@ATTRIBUTE classe {");
        arquivoNames = arquivoNames.replaceAll("\"\\)\\.", "}");
        arquivoNames = arquivoNames.replaceAll("\",\"", ",");
        arquivoNames = arquivoNames.replaceAll("\"", "@ATTRIBUTE ");
        return arquivoNames;
    }


    private String converteArquivoDataReduzido(String arquivoData) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        String linha = arquivoData;
        linha = linha.replaceAll("\".*\",|", "");
        String[] dados = linha.split(",");
        double valorLido;
        for (int i = 0; i < dados.length; i++) {
            if (i == dados.length - 1) {
                sb.append(i).append(" ").append(dados[i]).append("}");
            } else {
                valorLido = Double.parseDouble(dados[i]);
                if (valorLido > 0) {
                    sb.append(i).append(" ").append(dados[i]).append(",");
                }
            }
        }
        if ("{".equals(String.valueOf(sb.charAt(sb.length() - 1)))) {
            sb.append("}");
        }
        return sb.toString();
    }

    private String converteArquivoData(String arquivoData) {
        arquivoData = arquivoData.replaceAll("\".*\",|", "");
        return arquivoData;
    }

    private String lerArquivoNames(String nome, String extensao) {
        StringBuilder linha = new StringBuilder();
        File arquivo = new File(nome + extensao);
        int qtdLinha = getNumLines(arquivo);
        try {
            FileReader fr = new FileReader(arquivo);
            BufferedReader br = new BufferedReader(fr);
            int i = 0;
            try {
                while (br.ready()) {
                    linha.append(br.readLine());
                    linha.append("\n");
                    System.out.print("\r" + (++i * 100) / qtdLinha + "% lido");
                }
                System.out.println("");
                br.close();
                fr.close();

            } catch (IOException ex) {
                Logger.getLogger(PretextToWeka.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PretextToWeka.class.getName()).log(Level.SEVERE, null, ex);
        }

        return linha.toString();
    }

    public void processaArquivoData(String nome, String extensao, String nomeCompleto) {
        StringBuilder linha = new StringBuilder();
        File arquivo = new File(nome + extensao);
        int qtdLinha = getNumLines(arquivo);
        try {
            FileReader fr = new FileReader(arquivo);
            BufferedReader br = new BufferedReader(fr);
            int i = 0;
            while (br.ready()) {
                linha.append(br.readLine());
                System.out.print("\r" + (++i * 100) / qtdLinha + "% lido");
                String ar;
                if (isreduzido) {
                    ar = converteArquivoDataReduzido(linha.toString());
                } else {
                    ar = converteArquivoData(linha.toString());
                }
                saveFile(ar, nomeCompleto, true);
                linha = new StringBuilder();
            }
            System.out.println("");
            br.close();
            fr.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PretextToWeka.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void processData(String oldFile, String newFile, String[] labels, int numFeatures) {
        File file = new File(oldFile);
        int numLine = getNumLines(file);
        System.out.println(numLine);
        try (FileReader fr = new FileReader(oldFile); BufferedReader br = new BufferedReader(fr)) {
            reduceAndSave(newFile, labels, br, numLine, numFeatures);
            br.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void reduceAndSave(String newFile, String[] labels, BufferedReader br, int numLine, int numFeatures) throws IOException {
        String line;
        int i = 0;
        while (br.ready()) {
            line = br.readLine();
            String reducedLine = nonSparseLineToSparseLine(line);
            reducedLine = addLabel(labels[i], numFeatures, reducedLine);
            saveFile(reducedLine, newFile, true);
            System.out.print("\r" + (++i * 100) / numLine + "% done");
        }
        System.out.println("");
    }

    private String addLabel(String label, int numLabels, String reducedLine) {
        return reducedLine.replace("}", "," + numLabels + " " + label + "}");
    }

    private String nonSparseLineToSparseLine(String nonSparseLine) {
        StringBuilder sb = new StringBuilder("{");
        String[] data = nonSparseLine.split(",");
        double value;
        for (int i = 0; i < data.length; i++) {
            value = Double.parseDouble(data[i]);
            if (value > 0) {
                sb.append(i).append(" ").append(data[i]).append(",");
            }
        }
        int lastIndex = sb.lastIndexOf(",");
        sb.replace(lastIndex, sb.length(), "}");
        return sb.toString();
    }

    private int getNumLines(File fileName) {
        try {
            return countLines(fileName);
        } catch (IOException ex) {
            Logger.getLogger(PretextToWeka.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("File not found: " + fileName);
        }
    }

    private int countLines(File fileName) throws IOException {
        LineNumberReader lnr = new LineNumberReader(new FileReader(fileName));
        lnr.skip(fileName.length());
        return lnr.getLineNumber();
    }

    public void saveFile(String data, String fileName, boolean append) {
        File file = new File(fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            try (FileWriter fw = new FileWriter(file, append); BufferedWriter bw = new BufferedWriter(fw)) {
                bw.write(data);
                bw.newLine();
                bw.close();
                fw.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(PretextToWeka.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
