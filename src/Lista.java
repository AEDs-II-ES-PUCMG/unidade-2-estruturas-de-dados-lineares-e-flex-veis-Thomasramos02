import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;

public class Lista<E> {

	private Celula<E> inicio;
	private Celula<E> fim;
	private int quantidadeElementos;

	public Lista() {
		Celula<E> sentinela = new Celula<E>();
		inicio = sentinela;
		fim = sentinela;
		quantidadeElementos = 0;
	}

	public boolean vazia() {
		return quantidadeElementos == 0;
	}

	public int tamanho() {
		return quantidadeElementos;
	}

	public void inserirInicio(E item) {
		Celula<E> novaCelula = new Celula<E>(item, inicio.getProximo());
		inicio.setProximo(novaCelula);
		if (vazia()) {
			fim = novaCelula;
		}
		quantidadeElementos++;
	}

	public void inserirFim(E item) {
		Celula<E> novaCelula = new Celula<E>(item);
		fim.setProximo(novaCelula);
		fim = novaCelula;
		quantidadeElementos++;
	}

	public E consultarInicio() {
		if (vazia()) {
			throw new NoSuchElementException("Não há nenhum item na lista!");
		}
		return inicio.getProximo().getItem();
	}

	public E consultarFim() {
		if (vazia()) {
			throw new NoSuchElementException("Não há nenhum item na lista!");
		}
		return fim.getItem();
	}

	public E removerInicio() {
		E itemRemovido = consultarInicio();
		inicio.setProximo(inicio.getProximo().getProximo());
		quantidadeElementos--;
		if (vazia()) {
			fim = inicio;
		}
		return itemRemovido;
	}

	public E obter(int indice) {
		if (indice < 0 || indice >= quantidadeElementos) {
			throw new IndexOutOfBoundsException("Posição inválida na lista!");
		}

		Celula<E> atual = inicio.getProximo();
		for (int i = 0; i < indice; i++) {
			atual = atual.getProximo();
		}
		return atual.getItem();
	}

	public E buscarPor(Comparator<E> criterioDeBusca, E item) {
		Celula<E> atual = inicio.getProximo();
		while (atual != null) {
			if (criterioDeBusca.compare(atual.getItem(), item) == 0) {
				return atual.getItem();
			}
			atual = atual.getProximo();
		}

		return null;
	}

	public double somarMultiplicacoes(Function<E, Double> extratorValor, Function<E, Integer> extratorFator) {
		if (vazia()) {
			throw new IllegalStateException("A lista está vazia!");
		}

		double somatorio = 0.0;
		Celula<E> atual = inicio.getProximo();

		while (atual != null) {
			double valor = extratorValor.apply(atual.getItem());
			int fator = extratorFator.apply(atual.getItem());
			somatorio += valor * fator;
			atual = atual.getProximo();
		}

		return somatorio;
	}

	public Lista<E> filtrar(Predicate<E> condicional) {
		if (vazia()) {
			throw new IllegalStateException("A lista está vazia!");
		}

		Lista<E> listaFiltrada = new Lista<>();
		Celula<E> atual = inicio.getProximo();

		while (atual != null) {
			if (condicional.test(atual.getItem())) {
				listaFiltrada.inserirFim(atual.getItem());
			}
			atual = atual.getProximo();
		}

		return listaFiltrada;
	}

	@Override
	public String toString() {
		StringBuilder texto = new StringBuilder();
		Celula<E> atual = inicio.getProximo();

		while (atual != null) {
			texto.append(atual.getItem());
			if (atual.getProximo() != null) {
				texto.append(System.lineSeparator());
			}
			atual = atual.getProximo();
		}

		return texto.toString();
	}
}