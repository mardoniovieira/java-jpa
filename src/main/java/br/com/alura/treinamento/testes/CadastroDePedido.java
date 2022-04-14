package br.com.alura.treinamento.testes;

import br.com.alura.treinamento.dao.ClienteDao;
import br.com.alura.treinamento.dao.PedidoDao;
import br.com.alura.treinamento.dao.ProdutoDao;
import br.com.alura.treinamento.modelo.Cliente;
import br.com.alura.treinamento.modelo.ItemPedido;
import br.com.alura.treinamento.modelo.Pedido;
import br.com.alura.treinamento.modelo.Produto;
import br.com.alura.treinamento.util.JPAUtil;
import br.com.alura.treinamento.vo.RelatorioDeVendasVo;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;

public class CadastroDePedido {

    public static void main(String[] args) {
        cadastrarClienteEPedidos();
    }

    public static void cadastrarClienteEPedidos() {
        CadastroDeProduto.cadastrarCategoriaEProduto();

        EntityManager em = JPAUtil.getEntityManager();
        PedidoDao pedidoDao = new PedidoDao(em);
        ClienteDao clienteDao = new ClienteDao(em);
        ProdutoDao produtoDao = new ProdutoDao(em);
        Produto produto = produtoDao.buscarPorId(1l);
        Produto produto2 = produtoDao.buscarPorId(2l);
        Produto produto3 = produtoDao.buscarPorId(3l);
        Cliente cliente = new Cliente("Rodrigo", "123456");

        Pedido pedido = new Pedido(cliente);
        pedido.adicionarItem(new ItemPedido(10, produto, pedido));
        pedido.adicionarItem(new ItemPedido(40, produto2, pedido));

        Pedido pedido2 = new Pedido(cliente);
        pedido2.adicionarItem(new ItemPedido(2, produto3, pedido));

        em.getTransaction().begin();

        clienteDao.cadastrar(cliente);
        pedidoDao.cadastrar(pedido);
        pedidoDao.cadastrar(pedido2);

        em.getTransaction().commit();

        BigDecimal valorTotalVendido = pedidoDao.valorTotalVendido();
        System.out.println("Valor total vendido: " + valorTotalVendido);

        List<RelatorioDeVendasVo> relatorio = pedidoDao.relatorioDeVendas();
        relatorio.forEach(System.out::println);

        em.close();
    }
}
