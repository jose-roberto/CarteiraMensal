package com.ifmg.carteiramensal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

import modelo.Event;

public class VisualizarEventos extends AppCompatActivity {

    private TextView titulo;
    private ListView lista;
    private TextView valorTotal;
    private Button btnCancelar;
    private Button btnNovo;

    private ArrayList<Event> eventos;
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
                if(operacao != - 1) {
                    Intent trocaAct = new Intent(VisualizarEventos.this, CadastroEdicaoEvento.class);

                    //Indicar qual operação executar.
                    if(operacao == 0) {
                        trocaAct.putExtra("acao", 0);
                    } else {
                        trocaAct.putExtra("acao", 1);
                    }

                    startActivity(trocaAct);
                }
            }
        });
    }

    private void ajusteOperacao() {
        if(operacao == 0) {
            titulo.setText("Entradas");
        } else if(operacao == 1) {
            titulo.setText("Saídas");
        } else {
            //Erro!
            Toast.makeText(VisualizarEventos.this, "Erro na configuração da intent!", Toast.LENGTH_LONG).show();
        }
    }


    private void carregaEventosLista(){
        eventos = new ArrayList<>();

        eventos.add(new Event( "Padaria",10.60, new Date(), new Date(), new Date(),null));
        eventos.add(new Event("Supermercado", 358.70, new Date(), new Date(), new Date(),null));

        adapter = new ItemListaEvento(getApplicationContext(), eventos);
        lista.setAdapter(adapter);
    }
}