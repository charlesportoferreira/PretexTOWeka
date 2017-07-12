package pretexttoweka;

/**
 * Created by charles on 07/07/17.
 */
public class Start {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        help();

        String nomeArquivo = possuiPrimeiroArgumento(args) ? "/" + args[0] : "/resultadoPretext.arff";
        boolean isReduzido = (possuiSegundoArgumento(args) && isOpcaoReduzido(args[1])) ? true : false;

        PretextTOWeka pretextTOWeka = new PretextTOWeka(nomeArquivo, isReduzido);
        pretextTOWeka.convert();
    }

    private static void help() {
        System.out.println("#############################################################\n" +
                "Coloque os discover.names e discover.data dentro de \n" +
                "uma pasta chamada discover situada no mesmo diretorio \n" +
                "deste executavel\n" +
                "Informe o nome do arquivo como primeiro parametro e a \n" +
                "letra r no segundo parametro para informar se deseja a \n" +
                "forma reduzida\n" +
                "Exemplo: java -jar PretextTOWeka.jar genes.arff r\n" +
                "ou\n" +
                "java -jar PretextTOWeka.jar genes.arff\n" +
                "Se nenhum parametro for informado será carregado as opcoes default\n" +
                "nome do arquivo = resultadoPretext, nao sera usada a forma reduzida\n"+
                "#############################################################\n");
    }

    private static boolean isOpcaoReduzido(String arg) {
        return arg.equals("r");
    }

    private static boolean possuiSegundoArgumento(String[] args) {
        return args.length > 1;
    }

    private static boolean possuiPrimeiroArgumento(String[] args) {
        return args.length > 0;
    }

}
