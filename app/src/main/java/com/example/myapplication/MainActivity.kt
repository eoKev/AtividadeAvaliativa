package com.example.myapplication

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.Produto
import com.google.gson.Gson
import com.example.myapplication.ui.theme.Estoque

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LayoutMain()
        }
    }
}
@Composable
fun LayoutMain() {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "cadastro") {
        composable("cadastro") { CadastroProduto(navController = navController) }
        composable("produtos") { Produtos(navController) }
        composable("detalhes/{produtoJSON}") { backStackEntry ->
            val produtoJSON = backStackEntry.arguments?.getString("produtoJSON")
            val produto = Gson().fromJson(produtoJSON, Produto::class.java)
            DetalhesProduto(navController, produto)
        }
        composable("estatisticas") { Estatisticas(navController = navController)  }
    }
}


val produtosCadastrados = mutableListOf<Produto>()

@Composable
fun CadastroProduto(navController: NavController) {
    var nome by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var preco by remember { mutableStateOf("") }
    var estoque by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Cadastro de Produto", fontSize = 30.sp)

        Spacer(modifier = Modifier.height(25.dp))
        TextField(value = nome, onValueChange = { nome = it },
            label = { Text(text = "Nome do produto") }
        )

        Spacer(modifier = Modifier.height(25.dp))
        TextField(value = categoria, onValueChange = { categoria = it },
            label = { Text(text = "Categoria do produto") }
        )

        Spacer(modifier = Modifier.height(25.dp))
        TextField(value = preco, onValueChange = { preco = it },
            label = { Text(text = "Preço") }
        )

        Spacer(modifier = Modifier.height(25.dp))
        TextField(value = estoque, onValueChange = { estoque = it },
            label = { Text(text = "Quantidade em estoque") }
        )

        Spacer(modifier = Modifier.height(25.dp))
        Button(onClick = {
            when {
                nome.isEmpty() -> {
                    Toast.makeText(context, "Nome do produto deve ser preenchido!", Toast.LENGTH_SHORT).show()
                }
                categoria.isEmpty() -> {
                    Toast.makeText(context, "Categoria deve ser informada!", Toast.LENGTH_SHORT).show()
                }
                preco.isEmpty() -> {
                    Toast.makeText(context, "Preço deve ser informado!", Toast.LENGTH_SHORT).show()
                }
                preco.toDoubleOrNull() == null || preco.toDouble() <= 0 -> {
                    Toast.makeText(context, "Preço deve ser maior que 0!", Toast.LENGTH_SHORT).show()
                }
                estoque.isEmpty() -> {
                    Toast.makeText(context, "Estoque deve ser informado!", Toast.LENGTH_SHORT).show()
                }
                estoque.toIntOrNull() == null || estoque.toInt() <= 1 -> {
                    Toast.makeText(context, "Estoque deve ser maior que 1!", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    val produto = Produto(nome, categoria, preco.toDouble(), estoque.toInt())
                    Estoque.adicionarProduto(produto) // Chamada à classe Estoque
                    Toast.makeText(context, "Produto cadastrado com sucesso!", Toast.LENGTH_SHORT).show()
                    nome = ""
                    categoria = ""
                    preco = ""
                    estoque = ""
                }
            }
        }) {
            Text(text = "Cadastrar produto")
        }

        Spacer(modifier = Modifier.height(15.dp))

        Button(onClick = {
            navController.navigate("produtos") // Navega para a lista de produtos
        }) {
            Text(text = "Ver produtos cadastrados")
        }


    }
}



@Composable
fun Produtos(navController: NavController) {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Produtos Cadastrados", fontSize = 35.sp)

        Spacer(modifier = Modifier.height(15.dp))

        LazyColumn {
            items(Estoque.getProdutos()) { produto ->
                Text(
                    text = "${produto.nome}",
                    fontSize = 22.sp,
                    modifier = Modifier.padding(10.dp).clickable {
                        val produtoJSON = Gson().toJson(produto)
                        navController.navigate("detalhes/$produtoJSON")
                    }
                )
            }

        }
        Spacer(modifier = Modifier.height(25.dp))
        Button(onClick = { navController.navigate("estatisticas")}) {
            Text(text = "Estatísticas")
        }
        Button(onClick = { navController.popBackStack() }) {
            Text(text = "Voltar")
        }
    }
}


@Composable
fun DetalhesProduto(navController: NavController, produto: Produto) {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Detalhes de '${produto.nome}'", fontSize = 30.sp)

        Spacer(modifier = Modifier.height(25.dp))

        Text(text = "Categoria: ${produto.categoria}", fontSize = 20.sp)
        Text(text = "Preço: R$ ${produto.preco}", fontSize = 20.sp)
        Text(text = "Estoque: ${produto.estoque} unidade(s)", fontSize = 20.sp)

        Spacer(modifier = Modifier.height(25.dp))
        Button(onClick = { navController.popBackStack() }) {
            Text(text = "Voltar")
        }
    }
}

@Composable
fun Estatisticas( navController: NavController) {
    val valorTotalEstoque = Estoque.calcularValorTotalEstoque()
    val quantidadeTotalProdutos = Estoque.getProdutos().sumOf { it.estoque }

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Estatísticas do Estoque", fontSize = 30.sp)

        Spacer(modifier = Modifier.height(25.dp))

        Text(text = "Valor Total do Estoque: R$ $valorTotalEstoque", fontSize = 20.sp)
        Text(text = "Quantidade Total de Produtos: $quantidadeTotalProdutos unidades", fontSize = 20.sp)

        Spacer(modifier = Modifier.height(25.dp))

        Button(onClick = {  navController.popBackStack()  }) {
            Text(text = "Voltar")
        }
    }
}



@Preview(showBackground = true)
@Composable
fun Preview() {
    LayoutMain()
}