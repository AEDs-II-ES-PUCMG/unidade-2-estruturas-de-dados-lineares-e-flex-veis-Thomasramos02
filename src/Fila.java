import java.util.NoSuchElementException;

public class Fila<E> {

    private Celula<E> inicio;
    private Celula<E> fim;

    public Fila() {
        Celula<E> sentinela = new Celula<E>();
        inicio = sentinela;
        fim = sentinela;
    }

    public boolean vazia() {
        return inicio == fim;
    }

    public void enfileirar(E item) {
        fim.setProximo(new Celula<E>(item));
        fim = fim.getProximo();
    }

    public E desenfileirar() {
        E desenfileirado = consultarInicio();
        inicio = inicio.getProximo();
        return desenfileirado;
    }

    public E consultarInicio() {
        if (vazia()) {
            throw new NoSuchElementException("Não há nenhum item na fila!");
        }
        return inicio.getProximo().getItem();
    }

    /**
     * Conta as ocorrências de um determinado elemento na fila.
     * 
     * @param elemento o elemento a ser contado
     * @return o número de ocorrências do elemento na fila
     */
    public int contarOcorrencias(E elemento) {
        int contador = 0;
        Celula<E> atual = inicio.getProximo();

        while (atual != null) {
            if (atual.getItem() != null && atual.getItem().equals(elemento)) {
                contador++;
            }
            atual = atual.getProximo();
        }

        return contador;
    }

    /**
     * Extrai um lote dos primeiros K elementos da fila atual.
     * Desenfileira os primeiros numItens elementos, respeitando a ordem FIFO,
     * e os retorna estruturados em uma nova Fila flexível.
     * 
     * Se a fila original possuir menos de K itens, extrai apenas os disponíveis,
     * esvaziando a fila de origem.
     * 
     * @param numItens o número de itens a serem extraídos da fila
     * @return uma nova Fila contendo os primeiros numItens elementos extraídos
     */
    public Fila<E> extrairLote(int numItens) {
        Fila<E> filaNova = new Fila<>();

        while (!vazia() && numItens > 0) {
            filaNova.enfileirar(this.desenfileirar());
            numItens--;
        }

        return filaNova;
    }
}
