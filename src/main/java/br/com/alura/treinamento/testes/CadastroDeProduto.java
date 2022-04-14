package br.com.alura.treinamento.testes;

import br.com.alura.treinamento.dao.CategoriaDao;
import br.com.alura.treinamento.dao.ProdutoDao;
import br.com.alura.treinamento.modelo.Categoria;
import br.com.alura.treinamento.modelo.CategoriaId;
import br.com.alura.treinamento.modelo.Produto;
import br.com.alura.treinamento.util.JPAUtil;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;

public class CadastroDeProduto {

    public static void main(String[] args) {
        cadastrarCategoriaEProduto();

        EntityManager em = JPAUtil.getEntityManager();
        ProdutoDao produtoDao = new ProdutoDao(em);
        Produto produto = produtoDao.buscarPorId(1l);
        System.out.println(produto.getPreco());
        List<Produto> produtos = produtoDao.buscarPorNomeDaCategoria("CELULARES");
        produtos.forEach(p -> System.out.println("Busca por categoria (CELULARES): " + p.getNome()));
        System.out.println("Preco do Produto: " + produtoDao.buscarPrecoDoProdutoComNome("Xiaomi Redmi"));
    }

    public static void cadastrarCategoriaEProduto() {
        Categoria celulares = new Categoria("CELULARES");
        Categoria videogames = new Categoria("VIDEOGAMES");
        Categoria informatica = new Categoria("INFORMATICA");
        Produto celular = new Produto("Xiaomi Redmi", "Muito legal",
                new BigDecimal("800"), celulares);
        Produto videogame = new Produto("PS5", "Playstation 5",
                new BigDecimal("5000"), videogames);
        Produto macbook = new Produto("Macbook", "Macbook Pro",
                new BigDecimal("13000"), informatica);

        EntityManager em = JPAUtil.getEntityManager();
        ProdutoDao produtoDao = new ProdutoDao(em);
        CategoriaDao categoriaDao = new CategoriaDao(em);
        em.getTransaction().begin();

        categoriaDao.cadastrar(celulares);
        categoriaDao.cadastrar(videogames);
        categoriaDao.cadastrar(informatica);
        produtoDao.cadastrar(celular);
        produtoDao.cadastrar(videogame);
        produtoDao.cadastrar(macbook);

        em.getTransaction().commit();

        Categoria categoria = em.find(Categoria.class, new CategoriaId("VIDEOGAMES", "tipo"));
        System.out.println("Categoria: " + categoria.getNome());

        em.close();
    }

}
