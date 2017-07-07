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
 * @author charleshenriqueportoferreira
 */
public class PretextTOWeka {

    private String nomeArquivo;
    private boolean isreduzido;


    public void convert() {
        String diretorio = System.getProperty("user.dir");
        System.out.println("Lendo Atributos");
        String atributos = lerArquivoNames(diretorio + "/discover/discover", ".names");
        String nomeArquivoCompleto = diretorio + nomeArquivo;
        System.out.println(nomeArquivoCompleto);
        atributos = converteArquivoNames(atributos);
        salvarArquivo(atributos, nomeArquivoCompleto, false);

        System.out.println("Lendo dados");
        String dados = "@data" + "\n";
        salvarArquivo(dados, nomeArquivoCompleto, true);
        processaArquivoData(diretorio + "/discover/discover", ".data", nomeArquivoCompleto);

    }

    public PretextTOWeka(String nomeArquivo, boolean isReduzido) {
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
        int qtdLinha = getQtdLinha(arquivo);
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
                Logger.getLogger(PretextTOWeka.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PretextTOWeka.class.getName()).log(Level.SEVERE, null, ex);
        }

        return linha.toString();
    }

    private void processaArquivoData(String nome, String extensao, String nomeCompleto) {
        StringBuilder linha = new StringBuilder();
        File arquivo = new File(nome + extensao);
        int qtdLinha = getQtdLinha(arquivo);
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
                salvarArquivo(ar, nomeCompleto, true);
                linha = new StringBuilder();
            }
            System.out.println("");
            br.close();
            fr.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PretextTOWeka.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getQtdLinha(File arquivo) {
        int qtdLinha = 0;
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
            throw new RuntimeException("File not found: " + arquivo);
        }
        return qtdLinha;
    }

    private void salvarArquivo(String texto, String nomeArquivo, boolean append) {
        File arquivo = new File(nomeArquivo);
        try {
            if (!arquivo.exists()) {
                arquivo.createNewFile();
            }
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
