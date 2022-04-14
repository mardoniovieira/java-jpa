package br.com.alura.treinamento.testes;

import br.com.alura.treinamento.dao.ProdutoDao;
import br.com.alura.treinamento.modelo.Produto;
import br.com.alura.treinamento.util.JPAUtil;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;

public class TesteCriteria {

    public static void main(String[] args) {
        CadastroDeProduto.cadastrarCategoriaEProduto();

        EntityManager em = JPAUtil.getEntityManager();
        ProdutoDao produtoDao = new ProdutoDao(em);
        List<Produto> produtos = produtoDao.buscarPorParametrosComCriteria(
                "Macbook", new BigDecimal(13000), null);
        em.close();

        produtos.forEach(System.out::println);
    }
}
