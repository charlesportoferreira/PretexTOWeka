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

        nomeArquivo = args.length > 0 ? "/" + args[0] : "/resultadoPretext.arff";
//        String diretorio = args.length > 1 ? args[1] : System.getProperty("user.dir");
        String diretorio =  System.getProperty("user.dir");
        String nomeArquivoName = args.length > 1 ? args[1] : "";
        String nomeArquivoData = args.length > 2 ? args[2] : "";
        System.out.println("Lendo Atributos");
        String atributos = lerArquivoNames(diretorio + "/discover/discover", nomeArquivoName + ".names");
        //String atributos = lerArquivoNames(diretorio, ".names");
        nomeArquivo = diretorio + nomeArquivo;
        System.out.println(nomeArquivo);
        atributos = converteArquivoNames(atributos);
        salvarArquivo(atributos, nomeArquivo, false);

        System.out.println("Lendo dados");
        String dados = "@DATA" + "\n";
        salvarArquivo(dados, nomeArquivo, true);
        dados = lerArquivoData(diretorio + "/discover/discover", nomeArquivoData + ".data");
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
        //arquivoData = arquivoData.replaceAll("\".*\",", "");
        String linha;
        double valorLido;
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        linha = arquivoData;
        linha = linha.replaceAll("\".*\",|", "");
        String[] dados = linha.split(",");
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
        } //else {
        //  sb.replace(sb.lastIndexOf(","), sb.lastIndexOf(",") + 1, "}");
        // }

        //return arquivoData;
        return sb.toString();
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
