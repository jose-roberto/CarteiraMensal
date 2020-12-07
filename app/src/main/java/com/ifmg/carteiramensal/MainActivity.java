package com.ifmg.carteiramensal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import tools.dbEvents;

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
    private Calendar dataApp;

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
                startActivity(trocaAct);
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
                startActivity(trocaAct);
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
    }
}