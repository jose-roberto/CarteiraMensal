package com.ifmg.carteiramensal;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import modelo.Event;
import tools.DB_Events;

public class MainActivity extends AppCompatActivity {

    private TextView titulo;
    private TextView entrada;
    private TextView saida;
    private TextView saldo;
    private ImageButton btn_entrada;
    private ImageButton btn_saida;
    private Button btn_anterior;
    private Button btn_proximo;
    private Button btn_novo;

    private Calendar diaAtual;
    static Calendar dataApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Link entre .XML e .JAVA
        titulo = (TextView) findViewById(R.id.tituloMain);
        entrada = (TextView) findViewById(R.id.txtEntrada);
        saida = (TextView) findViewById(R.id.txtSaida);
        saldo = (TextView) findViewById(R.id.txtSaldo);

        btn_entrada = (ImageButton) findViewById(R.id.btnEntrada);
        btn_saida = (ImageButton) findViewById(R.id.btnSaida);

        btn_anterior = (Button) findViewById(R.id.btnAnteior);
        btn_proximo = (Button) findViewById(R.id.btnProximo);
        btn_novo = (Button) findViewById(R.id.btnNovo);

        cadastrarEventos();

        //getInstance() requisita a data e o horário que estão definidos no aplicativo.
        dataApp = Calendar.getInstance();
        diaAtual = Calendar.getInstance();

        exibeDataApp();
        atualizaValores();
    }

    //Implementa todos os eventos dos botões
    private void cadastrarEventos() {
        btn_anterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_atualizarMes(-1);
            }
        });

        btn_proximo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_atualizarMes(+1);
            }
        });

        btn_novo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dbEvents db = new dbEvents(MainActivity.this);
                //db.insert();

                //Toast.makeText(MainActivity.this, db.getDatabaseName(), Toast.LENGTH_LONG).show();
            }
        });

        btn_entrada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Redirecionamento do usuário:
                //Intent(contextoAtual, objetivo);
                Intent trocaAct = new Intent(MainActivity.this, VisualizarEventos.class);

                //Pasando informações durante a troca de activity.
                trocaAct.putExtra("acao", 0);

                //Inicia a nova activity considerando o valor passado.
                startActivityForResult(trocaAct, 0);
            }
        });

        btn_saida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Redirecionamento do usuário:
                //Intent(contextoAtual, objetivo);
                Intent trocaAct = new Intent(MainActivity.this, VisualizarEventos.class);

                //Pasando informações durante a troca de activity.
                trocaAct.putExtra("acao", 1);

                //Inicia a nova activity considerando o valor passado.
                startActivityForResult(trocaAct, 1);
            }
        });
    }

    private void exibeDataApp() {
        String nomeMes[] = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};

        //0 - janeiro, 1 - fevereiro, ..., 11 - dezembro
        int mes = dataApp.get(Calendar.MONTH);
        int ano = dataApp.get(Calendar.YEAR);

        titulo.setText(nomeMes[mes] + "/" + ano);
    }

    private void btn_atualizarMes(int ajuste) {
        dataApp.add(Calendar.MONTH, ajuste);

        if (ajuste > 0) {
            if (dataApp.after(diaAtual)) {
                dataApp.add(Calendar.MONTH, -1);
            }
        } else {
            //avaliar se existem dados anteriores cadastrados
        }

        exibeDataApp();
        atualizaValores();
    }

    //Atualiza o total de entradas, o total de saídas e o saldo.
    private void atualizaValores() {

        //Busca todas as entradas e saídas cadastradas no banco.
        DB_Events db = new DB_Events(this);

        ArrayList<Event> listaEntradas = db.search(0, dataApp);
        ArrayList<Event> listaSaidas = db.search(1, dataApp);

        //Soma o valor total de entradas, o valor total de saídas e o valor do saldo.
        double totalEntradas = 0.00;
        double totalSaidas = 0.00;

        for (int i = 0; i < listaEntradas.size(); i++) {
            totalEntradas += listaEntradas.get(i).getValor();
        }

        for (int i = 0; i < listaSaidas.size(); i++) {
            totalSaidas += listaSaidas.get(i).getValor();
        }

        double totalSaldo = totalEntradas - totalSaidas;

        //Exibe as informações obtidas.
        entrada.setText(String.format("%.2f", totalEntradas));
        saida.setText(String.format("%.2f", totalSaidas));
        saldo.setText(String.format("%.2f", totalSaldo));
    }

    @Override
    protected void onActivityResult(int codigoRequest, int codigoResultado, Intent data) {
        super.onActivityResult(codigoRequest, codigoResultado, data);

        atualizaValores();
    }
}