package com.ifmg.carteiramensal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import modelo.Evento;
import tools.EventosDB;

public class VisualizarEventos extends AppCompatActivity {

    private TextView titulo;
    private ListView lista;
    private TextView valorTotal;
    private Button btnCancelar;
    private Button btnNovo;

    private ArrayList<Evento> eventos;
    private ItemListaEvento adapter;

    //operacao == 0 -> entrada
    //operacao == 1 -> saida
    private int operacao = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_eventos);

        titulo = (TextView) findViewById(R.id.tituloEventos);
        lista = (ListView) findViewById(R.id.listaEventos);
        valorTotal = (TextView) findViewById(R.id.txtValor);
        btnCancelar = (Button) findViewById(R.id.btnCancelar);
        btnNovo = (Button) findViewById(R.id.btnNovo);

        Intent intencao = getIntent();
        operacao = intencao.getIntExtra("acao", -1);

        cadastrarEventos();
        ajusteOperacao();
        carregaEventosLista();
    }

    private void cadastrarEventos() {
        btnNovo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (operacao != -1) {
                    Intent trocaAct = new Intent(VisualizarEventos.this, CadastroEdicaoEvento.class);

                    //Indicar qual operação executar.
                    if (operacao == 0) {
                        trocaAct.putExtra("acao", 0);
                        startActivityForResult(trocaAct, 0);

                    } else {
                        trocaAct.putExtra("acao", 1);
                        startActivityForResult(trocaAct, 1);
                    }
                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void ajusteOperacao() {
        if (operacao == 0) {
            titulo.setText("Entradas");
        } else if (operacao == 1) {
            titulo.setText("Saídas");
        } else {
            //Erro!
            Toast.makeText(VisualizarEventos.this, "Erro na configuração da intent!", Toast.LENGTH_LONG).show();
        }
    }

    private void carregaEventosLista() {
        eventos = new ArrayList<>();
        /* eventos.add(new Evento("Padaria", 10.60, new Date(), new Date(), new Date(), null));
        eventos.add(new Evento("Supermercado", 358.70, new Date(), new Date(), new Date(), null));*/
        //Busca do eventos no banco de dados
        EventosDB db = new EventosDB(this);
        eventos = db.search(operacao, MainActivity.dataApp);

        adapter = new ItemListaEvento(getApplicationContext(), eventos);
        lista.setAdapter(adapter);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int indice, long id) {
                Evento eventoSelecionado = eventos.get(indice);

                Intent novoFluxo = new Intent(VisualizarEventos.this, CadastroEdicaoEvento.class);

                //Selecionando qual o tipo de evento será alterado.
                if (operacao == 0) {
                    novoFluxo.putExtra("acao", 2);
                } else {
                    novoFluxo.putExtra("acao", 3);
                }

                novoFluxo.putExtra("id", eventoSelecionado.getId() + "");

                startActivityForResult(novoFluxo, operacao);
            }
        });

        //Gerando o valor total.
        double total = 0.00;

        for (int i = 0; i < eventos.size(); i++) {
            total += eventos.get(i).getValor();
        }

        valorTotal.setText(String.format("%.2f", total));
    }

    protected void onActivityResult(int codigoRequest, int codigoResultado, Intent data) {
        super.onActivityResult(codigoRequest, codigoResultado, data);

        carregaEventosLista();
    }
}