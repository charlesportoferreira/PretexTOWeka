package pretexttoweka;

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
 *
 * @author charleshenriqueportoferreira
 */
public class PretextTOWeka {

    private static String nomeArquivo;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        nomeArquivo = args.length > 0 ? args[0] : "resultadoPretext.arff";
        System.out.println("Lendo Atributos");
        String atributos = lerArquivoNames("discover", ".names");
        atributos = converteArquivoNames(atributos);
        salvarArquivo(atributos, nomeArquivo, false);

        System.out.println("Lendo dados");
        String dados = "@DATA" + "\n";
        salvarArquivo(dados, nomeArquivo, true);
        dados = lerArquivoData("discover", ".data");
        if (dados != null) {
            salvarArquivo(dados, nomeArquivo, true);
        }

    }

    public static String pretextToARFF(String arquivoData, String arquivoNames) {
        arquivoNames = converteArquivoNames(arquivoNames);

        arquivoData = converteArquivoData(arquivoData);

        String arquivoFinal = arquivoNames + "\n" + arquivoData;
        return arquivoFinal;
    }

    private static String converteArquivoNames(String arquivoNames) {
        System.out.println("convertendo atributos");
        // arquivoNames = "@RELATION teste-macbook \n" + arquivoNames;
        arquivoNames = arquivoNames.replaceAll("att_class.\n", "@RELATION teste-incial");
        arquivoNames = arquivoNames.replaceAll("filename:string:ignore.", "\n");
        arquivoNames = arquivoNames.replaceAll("\":integer\\.", " NUMERIC");
        arquivoNames = arquivoNames.replaceAll("\":real\\.", " NUMERIC");
        arquivoNames = arquivoNames.replaceAll("(att_class:nominal\\(\")", "@ATTRIBUTE classe {");
        arquivoNames = arquivoNames.replaceAll("\"\\)\\.", "}");
        arquivoNames = arquivoNames.replaceAll("\",\"", ",");
        arquivoNames = arquivoNames.replaceAll("\"", "@ATTRIBUTE ");
        return arquivoNames;
    }

    private static String converteArquivoData(String arquivoData) {
        // System.out.println("convertendo dados");
        //arquivoData = "@DATA" + "\n" + arquivoData;
        arquivoData = arquivoData.replaceAll("\".*\",", "");
        return arquivoData;
    }

    public static String lerArquivoNames(String nome, String extensao) {
        StringBuilder linha = new StringBuilder();
        File arquivo = new File(nome + extensao);
        int qtdLinha = 0;

        // logica para contar o numero de linhas do arquivo
        LineNumberReader linhaLeitura;
        try {
            linhaLeitura = new LineNumberReader(new FileReader(arquivo));
            try {
                linhaLeitura.skip(arquivo.length());
                qtdLinha = linhaLeitura.getLineNumber();
                // System.out.println("numero de linhas = " + qtdLinha);
            } catch (IOException ex) {
                Logger.getLogger(PretextTOWeka.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(PretextTOWeka.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            FileReader fr = new FileReader(arquivo);
            BufferedReader br = new BufferedReader(fr);
            int i = 0;
            try {
                while (br.ready()) {

                    linha.append(br.readLine());
                    linha.append("\n");
                    i++;
                    System.out.print("\r" + (i * 100) / qtdLinha + "% lido");
                }
                System.out.println("");
                br.close();
                fr.close();

            } catch (IOException ex) {
                Logger.getLogger(PretextTOWeka.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PretextTOWeka.class.getName()).log(Level.SEVERE, null, ex);
        }

        return linha.toString();
    }

    public static String lerArquivoData(String nome, String extensao) {

        StringBuilder linha = new StringBuilder();
        File arquivo = new File(nome + extensao);
        int qtdLinha = 0;

        // logica para contar o numero de linhas do arquivo
        LineNumberReader linhaLeitura;
        try {
            linhaLeitura = new LineNumberReader(new FileReader(arquivo));
            try {
                linhaLeitura.skip(arquivo.length());
                qtdLinha = linhaLeitura.getLineNumber();
                System.out.println("numero de linhas = " + qtdLinha);
            } catch (IOException ex) {
                Logger.getLogger(PretextTOWeka.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(PretextTOWeka.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            FileReader fr = new FileReader(arquivo);
            //System.out.println(arquivo.length());
            BufferedReader br = new BufferedReader(fr);
            int i = 0;
            try {
                while (br.ready()) {
                    linha.append(br.readLine());
                    // linha.append("\n");
                    i++;
                    System.out.print("\r" + (i * 100) / qtdLinha + "% lido");
                    //imprime de dez em dez %
                  //  if (((i * 100) / qtdLinha) % 10 == 0) {

                        // System.out.println("entrei linha atual = " + i);
                        String ar = converteArquivoData(linha.toString());
                        // System.out.println(ar);
                        salvarArquivo(ar, nomeArquivo, true);
                        linha = new StringBuilder();
                 //   }

                }
                System.out.println("");
                br.close();
                fr.close();

            } catch (IOException ex) {
                Logger.getLogger(PretextTOWeka.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PretextTOWeka.class.getName()).log(Level.SEVERE, null, ex);
        }

        return linha.toString();
    }

    public static void salvarArquivo(String texto, String nomeArquivo, boolean append) {
        // System.out.println("Salvando Arquivo " + nomeArquivo);
        File arquivo = new File(nomeArquivo);
        boolean existe = arquivo.exists();
        try {
            if (!existe) {
                arquivo.createNewFile();
            }
        } catch (IOException ex) {
            Logger.getLogger(PretextTOWeka.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            try (FileWriter fw = new FileWriter(arquivo, append);
                    BufferedWriter bw = new BufferedWriter(fw)) {
                bw.write(texto);
                bw.newLine();
                bw.close();
                fw.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(PretextTOWeka.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
