package com.example.myapplication.ui.theme

class Estoque {
    companion object {
        private val produtos = mutableListOf<Produto>()

        fun adicionarProduto(produto: Produto) {
            produtos.add(produto)
        }

        fun calcularValorTotalEstoque(): Double {
            return produtos.sumOf { it.preco * it.estoque }
        }

        fun getProdutos(): List<Produto> {
            return produtos
        }
    }
}
