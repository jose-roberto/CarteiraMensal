package com.ifmg.carteiramensal;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import modelo.Event;
import tools.dbEvents;
import android.content.Context;
public class CadastroEdicaoEvento extends AppCompatActivity {

    private TextView titulo;
    private EditText txtNome;
    private EditText txtValor;
    private TextView txtData;
    private CheckBox cbRepete;
    private ImageView foto;
    private Button addFoto;
    private Button cancelarCadastro;
    private Button salvarCadastro;
    private DatePickerDialog calendario;
    private Calendar calendarioTemp;
    private Spinner mesesRepeteSpi;

    //0 - cadastro de entrada
    //1 - cadastro de saída
    //2 - edição de entrada
    //3 - edição de saída
    private int acao = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_edicao_evento);

        titulo = (TextView) findViewById(R.id.tituloCEE);
        txtNome = (EditText) findViewById(R.id.txtNomeCadastro);
        txtValor = (EditText) findViewById(R.id.txtValorCadastro);
        txtData = (TextView) findViewById(R.id.txtDataCadastro);
        cbRepete = (CheckBox) findViewById(R.id.cbRepeticao);
        foto = (ImageView) findViewById(R.id.fotoCadastro);
        addFoto = (Button) findViewById(R.id.btnAddFoto);
        cancelarCadastro = (Button) findViewById(R.id.btnCancelarCadastro);
        salvarCadastro = (Button) findViewById(R.id.btnSalvarCadastro);
        mesesRepeteSpi = (Spinner) findViewById(R.id.spRepeticao);

        Intent intencao = getIntent();
        acao = intencao.getIntExtra("acao", -1);

        ajustesAcao();
        cadastrarEventos();
        confSpinners();
    }

    private void confSpinners(){
        List<String> meses = new ArrayList<>();
        //Vamos permitir nessa versão a repetição de apenas 24 meses de um evento
        for(int i = 1; i <=24; i++){
            meses.add(i + "");
        }

        ArrayAdapter<String>listaAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, meses);

        mesesRepeteSpi.setAdapter(listaAdapter);
        mesesRepeteSpi.setEnabled(false);
    }

    private void cadastrarEventos() {
        //Configurações do DatePicker

        calendarioTemp = Calendar.getInstance();
        calendario = new DatePickerDialog(CadastroEdicaoEvento.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendarioTemp.set(year, month, dayOfMonth);
                txtData.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
            }
        }, calendarioTemp.get(Calendar.YEAR), calendarioTemp.get(Calendar.MONTH), calendarioTemp.get(Calendar.DAY_OF_MONTH));


        txtData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendario.show();
            }
        });

        salvarCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cadastrarNovoEvento();
            }
        });

        //tratando a repeticao do evento
        cbRepete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cbRepete.isChecked()){
                    mesesRepeteSpi.setEnabled(true);
                }
                else{
                    mesesRepeteSpi.setEnabled(false);
                }
            }
        });
        cancelarCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //termina a execução
                finish();
            }
        });
    }

    //Reutilização de Activitys
    private void ajustesAcao() {
        //Obtém a data atual;
        Calendar hoje = Calendar.getInstance();
        SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");
        txtData.setText(formatador.format(hoje.getTime()));

        switch (acao) {
            case 0:
                titulo.setText("Cadastro de Entrada");
                break;
            case 1:
                titulo.setText("Cadastro de Saída");
                break;
            case 2:
                titulo.setText("Edião de Entrada");
                break;
            case 3:
                titulo.setText("Edição de Saída");
                break;
            default:
                break;
        }
    }

    private void cadastrarNovoEvento() {
        String nome = txtNome.getText().toString();

        double valor = Double.parseDouble(txtValor.getText().toString());
        if (acao == 1 || acao == 3) {
            valor *= -1;
        }

        //SimpleDateFormat formatador = new SimpleDateFormat("dd/M/yyyy");
       // String strData = txtData.getText().toString();

       // try {
            Date dataOcorreu = calendarioTemp.getTime();

            //Nova calendário para calcular a data limite.
            Calendar dataLimite = Calendar.getInstance();
            dataLimite.setTime(calendarioTemp.getTime());

            //Verifica se o evento irá repetir por mais alguns meses.
            if (cbRepete.isChecked()) {
               String mesStr = (String)mesesRepeteSpi.getSelectedItem();

               dataLimite.add(Calendar.MONTH, Integer.parseInt(mesStr));
            }

            //Setando o limite para o último dia do mês.
            dataLimite.set(Calendar.DAY_OF_MONTH, dataLimite.getActualMaximum(Calendar.DAY_OF_MONTH));

            Event novoEvento = new Event(nome, valor, new Date(), dataLimite.getTime(), dataOcorreu, null);

            dbEvents db = new dbEvents(CadastroEdicaoEvento.this);
            db.insert(novoEvento);

            Toast.makeText(CadastroEdicaoEvento.this, "Cadastro feito com sucesso!", Toast.LENGTH_LONG).show();

            finish();
        //} catch (ParseException ex) {
            System.err.println("Erro na formatação da data!");
        }
    }
}