import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class App {

    /**
     * Nome do arquivo de dados. O arquivo deve estar localizado na raiz do projeto
     */
    static String nomeArquivoDados;

    /** Scanner para leitura de dados do teclado */
    static Scanner teclado;

    /** Vetor de produtos cadastrados */
    static Produto[] produtosCadastrados;

    /** Quantidade de produtos cadastrados atualmente no vetor */
    static int quantosProdutos = 0;

    /** Fila de pedidos aguardando processamento */
    static Fila<Pedido> filaPedidos = new Fila<>();

    /** Nome do arquivo de pedidos */
    static String nomeArquivoPedidos = "pedidos.txt";

    static void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /** Gera um efeito de pausa na CLI. Espera por um enter para continuar */
    static void pausa() {
        System.out.println("Digite enter para continuar...");
        teclado.nextLine();
    }

    /** Cabeçalho principal da CLI do sistema */
    static void cabecalho() {
        System.out.println("AEDs II COMÉRCIO DE COISINHAS");
        System.out.println("=============================");
    }

    static <T extends Number> T lerOpcao(String mensagem, Class<T> classe) {

        T valor;

        System.out.println(mensagem);
        try {
            valor = classe.getConstructor(String.class).newInstance(teclado.nextLine());
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            return null;
        }
        return valor;
    }

    /**
     * Imprime o menu principal, lê a opção do usuário e a retorna (int).
     * 
     * @return Um inteiro com a opção do usuário.
     */
    static int menu() {
        cabecalho();
        System.out.println("1 - Listar todos os produtos");
        System.out.println("2 - Procurar por um produto, por código");
        System.out.println("3 - Procurar por um produto, por nome");
        System.out.println("4 - Iniciar novo pedido");
        System.out.println("5 - Fechar pedido");
        System.out.println("6 - Listar produtos dos pedidos mais recentes");
        System.out.println("0 - Sair");
        System.out.print("Digite sua opção: ");
        return Integer.parseInt(teclado.nextLine());
    }

    /**
     * Lê os dados de um arquivo-texto e retorna um vetor de produtos. Arquivo-texto
     * no formato
     * N (quantidade de produtos) <br/>
     * tipo;descrição;preçoDeCusto;margemDeLucro;[dataDeValidade] <br/>
     * Deve haver uma linha para cada um dos produtos. Retorna um vetor vazio em
     * caso de problemas com o arquivo.
     * 
     * @param nomeArquivoDados Nome do arquivo de dados a ser aberto.
     * @return Um vetor com os produtos carregados, ou vazio em caso de problemas de
     *         leitura.
     */
    static Produto[] lerProdutos(String nomeArquivoDados) {

        Scanner arquivo = null;
        int numProdutos;
        String linha;
        Produto produto;
        Produto[] produtosCadastrados;

        try {
            arquivo = new Scanner(new File(nomeArquivoDados), Charset.forName("UTF-8"));

            numProdutos = Integer.parseInt(arquivo.nextLine());
            produtosCadastrados = new Produto[numProdutos];

            for (int i = 0; i < numProdutos; i++) {
                linha = arquivo.nextLine();
                produto = Produto.criarDoTexto(linha);
                produtosCadastrados[i] = produto;
            }
            quantosProdutos = numProdutos;

        } catch (IOException excecaoArquivo) {
            produtosCadastrados = null;
        } finally {
            if (arquivo != null) {
                arquivo.close();
            }
        }

        return produtosCadastrados;
    }

    /**
     * Localiza um produto no vetor de produtos cadastrados, a partir do código de
     * produto informado pelo usuário, e o retorna.
     * Em caso de não encontrar o produto, retorna null
     */
    static Produto localizarProduto() {

        Produto produto = null;
        Boolean localizado = false;

        cabecalho();
        System.out.println("Localizando um produto...");
        int idProduto = lerOpcao("Digite o código identificador do produto desejado: ", Integer.class);
        for (int i = 0; (i < quantosProdutos && !localizado); i++) {
            if (produtosCadastrados[i].hashCode() == idProduto) {
                produto = produtosCadastrados[i];
                localizado = true;
            }
        }

        return produto;
    }

    /**
     * Localiza um produto no vetor de produtos cadastrados, a partir do nome de
     * produto informado pelo usuário, e o retorna.
     * A busca não é sensível ao caso. Em caso de não encontrar o produto, retorna
     * null
     * 
     * @return O produto encontrado ou null, caso o produto não tenha sido
     *         localizado no vetor de produtos cadastrados.
     */
    static Produto localizarProdutoDescricao() {

        Produto produto = null;
        Boolean localizado = false;
        String descricao;

        cabecalho();
        System.out.println("Localizando um produto...");
        System.out.println("Digite o nome ou a descrição do produto desejado:");
        descricao = teclado.nextLine();
        for (int i = 0; (i < quantosProdutos && !localizado); i++) {
            if (produtosCadastrados[i].descricao.equals(descricao)) {
                produto = produtosCadastrados[i];
                localizado = true;
            }
        }

        return produto;
    }

    private static void mostrarProduto(Produto produto) {

        cabecalho();
        String mensagem = "Dados inválidos para o produto!";

        if (produto != null) {
            mensagem = String.format("Dados do produto:\n%s", produto);
        }

        System.out.println(mensagem);
    }

    /** Lista todos os produtos cadastrados, numerados, um por linha */
    static void listarTodosOsProdutos() {

        cabecalho();
        System.out.println("\nPRODUTOS CADASTRADOS:");
        for (int i = 0; i < quantosProdutos; i++) {
            System.out.println(String.format("%02d - %s", (i + 1), produtosCadastrados[i].toString()));
        }
    }

    /**
     * Inicia um novo pedido.
     * Permite ao usuário escolher e incluir produtos no pedido.
     * 
     * @return O novo pedido
     */
    public static Pedido iniciarPedido() {

        int formaPagamento = lerOpcao(
                "Digite a forma de pagamento do pedido, sendo 1 para pagamento à vista e 2 para pagamento a prazo",
                Integer.class);
        Pedido pedido = new Pedido(LocalDate.now(), formaPagamento);
        Produto produto;
        int numProdutos;
        int quantidade;

        listarTodosOsProdutos();
        System.out.println("Incluindo produtos no pedido...");
        numProdutos = lerOpcao("Quantos produtos serão incluídos no pedido?", Integer.class);
        for (int i = 0; i < numProdutos; i++) {
            produto = localizarProdutoDescricao();
            if (produto == null) {
                System.out.println("Produto não encontrado");
                i--;
            } else {
                quantidade = lerOpcao("Quantos itens desse produto serão incluídos no pedido?", Integer.class);
                pedido.incluirProduto(produto, quantidade);
            }
        }

        return pedido;
    }

    /**
     * Finaliza um pedido, momento no qual ele é armazenado em uma fila de
     * pedidos aguardando processamento.
     * 
     * @param pedido O pedido que deve ser finalizado.
     */
    public static void finalizarPedido(Pedido pedido) {
        if (pedido != null && pedido.getItensDoPedido()[0] != null) {
            filaPedidos.enfileirar(pedido);
            cabecalho();
            System.out.println("Pedido finalizado com sucesso!");
            System.out.println("\n" + pedido);
        } else {
            cabecalho();
            System.out.println("Erro: Nenhum pedido criado ou pedido vazio!");
        }
    }

    /**
     * Lista os produtos dos pedidos mais recentes (primeiros da fila).
     * Mostra até os 5 primeiros pedidos da fila.
     */
    public static void listarProdutosPedidosRecentes() {
        cabecalho();
        System.out.println("PEDIDOS NA FILA DE PROCESSAMENTO");
        System.out.println("================================\n");

        if (filaPedidos.vazia()) {
            System.out.println("Nenhum pedido aguardando processamento.");
        } else {
            // Criar uma fila temporária para não perder os dados
            Fila<Pedido> filaTemp = new Fila<>();
            int contador = 0;

            while (!filaPedidos.vazia() && contador < 5) {
                Pedido pedido = filaPedidos.desenfileirar();
                System.out.println(pedido);
                System.out.println();
                filaTemp.enfileirar(pedido);
                contador++;
            }

            // Restaurar os pedidos à fila original
            while (!filaTemp.vazia()) {
                filaPedidos.enfileirar(filaTemp.desenfileirar());
            }
        }
    }

    /**
     * Salva todos os pedidos da fila em um arquivo de texto.
     * Formato: cada pedido em uma linha com seus dados separados por
     * ponto-e-vírgula.
     */
    public static void salvarPedidos() {
        if (filaPedidos.vazia()) {
            return;
        }
    }

    /**
     * Carrega pedidos salvos anteriormente de um arquivo.
     * Restaura os pedidos da fila para continuar o processamento.
     */
    public static void carregarPedidos() {
        File arquivo = new File(nomeArquivoPedidos);
        if (!arquivo.exists()) {
            return;
        }
    }

    /**
     * Método de teste preliminar da estrutura Fila.
     * Cria uma fila de caracteres, insere caracteres do nome "Thomas Ramos",
     * testa enfileiramento, desenfileiramento e contagem de ocorrências.
     */
    public static void testarFila() {

        cabecalho();
        System.out.println("TESTE PRELIMINAR - FILA DE CARACTERES");
        System.out.println("====================================\n");

        Fila<Character> filaNomes = new Fila<>();

        String nome = "ThomasOliveira";

        System.out.println("1. ENFILEIRANDO CARACTERES:");
        System.out.println("Nome: " + nome);
        System.out.print("Caracteres enfileirados: ");

        for (char c : nome.toCharArray()) {
            filaNomes.enfileirar(c);
            System.out.print(c + " ");
        }
        System.out.println("\n");

        System.out.println("2. CONTAGEM DE OCORRÊNCIAS NA FILA:");
        char[] caracteresATestar = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
                'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
        for (char c : caracteresATestar) {
            int ocorrencias = filaNomes.contarOcorrencias(c);
            System.out.println("   Ocorrências de '" + c + "': " + ocorrencias);
        }
        System.out.println();

        filaNomes = new Fila<>();
        for (char c : nome.toCharArray()) {
            filaNomes.enfileirar(c);
        }

        System.out.println("3. DESENFILEIRANDO CARACTERES:");
        System.out.print("Caracteres desenfileirados (ordem FIFO): ");
        while (!filaNomes.vazia()) {
            System.out.print(filaNomes.desenfileirar() + " ");
        }
        System.out.println("\n");

        System.out.println("4. TESTE DE FILA VAZIA:");
        System.out.println("A fila está vazia? " + filaNomes.vazia());
        System.out.println();

        System.out.println("Teste preliminar concluído com sucesso!");
    }

    /**
     * Método de teste do extrairLote
     */
    public static void testarExtrairLote() {

        cabecalho();
        System.out.println("TESTE DO MÉTODO extrairLote");
        System.out.println("===========================\n");

        // Criar uma fila com números
        Fila<Integer> filaNumeros = new Fila<>();
        System.out.println("1. ENFILEIRANDO NÚMEROS:");
        for (int i = 1; i <= 10; i++) {
            filaNumeros.enfileirar(i);
            System.out.print(i + " ");
        }
        System.out.println("\n");

        System.out.println("2. EXTRAINDO LOTE DE 3 ELEMENTOS:");
        Fila<Integer> lote1 = filaNumeros.extrairLote(3);
        System.out.print("Elementos do lote: ");
        while (!lote1.vazia()) {
            System.out.print(lote1.desenfileirar() + " ");
        }
        System.out.println();
        System.out.println("Elementos restantes na fila original: ");

        int contador = 0;
        Fila<Integer> filaTemp = new Fila<>();
        while (!filaNumeros.vazia()) {
            int num = filaNumeros.desenfileirar();
            System.out.print(num + " ");
            filaTemp.enfileirar(num);
            contador++;
        }
        System.out.println("\nTotal restante: " + contador + " elementos\n");

        filaNumeros = filaTemp;

        System.out.println("3. EXTRAINDO LOTE DE 10 ELEMENTOS (mas há apenas " + (10 - 3) + " na fila):");
        Fila<Integer> lote2 = filaNumeros.extrairLote(10);
        System.out.print("Elementos do lote: ");
        int loteCount = 0;
        while (!lote2.vazia()) {
            System.out.print(lote2.desenfileirar() + " ");
            loteCount++;
        }
        System.out.println();
        System.out.println("Total extraído: " + loteCount + " elementos");
        System.out.println("Fila original vazia? " + filaNumeros.vazia() + "\n");

        System.out.println("Teste do extrairLote concluído com sucesso!");
    }

    public static void main(String[] args) {

        teclado = new Scanner(System.in, Charset.forName("UTF-8"));
        testarFila();
        pausa();

        testarExtrairLote();
        pausa();

        pausa();

        nomeArquivoDados = "produtos.txt";
        produtosCadastrados =

                lerProdutos(nomeArquivoDados);

        carregarPedidos();

        Pedido pedido = null;

        int opcao = -1;

        do {
            opcao = menu();
            switch (opcao) {
                case 1 -> listarTodosOsProdutos();
                case 2 -> mostrarProduto(localizarProduto());
                case 3 -> mostrarProduto(localizarProdutoDescricao());
                case 4 -> pedido = iniciarPedido();
                case 5 -> finalizarPedido(pedido);
                case 6 -> listarProdutosPedidosRecentes();
            }
            pausa();
        } while (opcao != 0);
        salvarPedidos();
        teclado.close();
    }
}
