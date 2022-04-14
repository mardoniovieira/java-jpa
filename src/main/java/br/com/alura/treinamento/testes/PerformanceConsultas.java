package br.com.alura.treinamento.testes;

import br.com.alura.treinamento.dao.PedidoDao;
import br.com.alura.treinamento.modelo.Pedido;
import br.com.alura.treinamento.util.JPAUtil;

import javax.persistence.EntityManager;

public class PerformanceConsultas {

    public static void main(String[] args) {
        CadastroDePedido.cadastrarClienteEPedidos();

        EntityManager em = JPAUtil.getEntityManager();
        PedidoDao pedidoDao = new PedidoDao(em);
        Pedido pedido = pedidoDao.buscarPedidoComCliente(1l);
        em.close();
        System.out.println("Data do pedido: " + pedido.getData());
        System.out.println("Cliente do pedido: " + pedido.getCliente().getDadosPessoais().getNome());
    }
}
