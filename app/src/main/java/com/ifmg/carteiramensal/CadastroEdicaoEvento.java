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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import modelo.Event;
import tools.DB_Events;

public class CadastroEdicaoEvento extends AppCompatActivity {

    private TextView titulo;
    private EditText txtNome;
    private EditText txtValor;
    private TextView txtData;
    private CheckBox cbRepete;
    private Spinner mesesSpinner;
    private ImageView foto;
    private Button addFoto;
    private Button cancelarCadastro;
    private Button salvarCadastro;

    private DatePickerDialog calendario;
    private Calendar calendarioTemp;

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
        mesesSpinner = (Spinner) findViewById(R.id.mesesRepeticao);
        foto = (ImageView) findViewById(R.id.fotoCadastro);
        addFoto = (Button) findViewById(R.id.btnAddFoto);
        cancelarCadastro = (Button) findViewById(R.id.btnCancelarCadastro);
        salvarCadastro = (Button) findViewById(R.id.btnSalvarCadastro);

        //Obtém a data atual;
        Calendar hoje = Calendar.getInstance();
        SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");
        txtData.setText(formatador.format(hoje.getTime()));

        Intent intencao = getIntent();
        acao = intencao.getIntExtra("acao", -1);
        ajustesAcao();

        cadastrarEventos();

        configurarSpinner();
    }

    private void configurarSpinner() {
        ArrayList<String> meses = new ArrayList<>();

        //Poderá repetir no máximo por 24 meses.
        for (int i = 1; i <= 24; i++) {
            meses.add(i + "");
        }

        //Define o comportamento do spinner.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, meses);

        mesesSpinner.setAdapter(adapter);
        mesesSpinner.setEnabled(false);
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

        cbRepete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbRepete.isChecked()) {
                    mesesSpinner.setEnabled(true);
                } else {
                    mesesSpinner.setEnabled(false);
                }
            }
        });

        //Finaliza a execução.
        cancelarCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //Reutilização de Activitys
    private void ajustesAcao() {
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

        /*SimpleDateFormat formatador = new SimpleDateFormat("dd/M/yyyy");
        String strData = txtData.getText().toString();

        try {
        Date dataOcorreu = formatador.parse(strData);*/

        Date dataOcorreu = calendarioTemp.getTime();
        //Nova calendário para calcular a data limite.
        Calendar dataLimite = Calendar.getInstance();
        dataLimite.setTime(calendarioTemp.getTime());

        if (cbRepete.isChecked()) {
            String mesStr = (String) mesesSpinner.getSelectedItem();
            dataLimite.add(Calendar.MONTH, Integer.parseInt(mesStr));
        }

        //Setando o limite para o último dia do mês.
        dataLimite.set(Calendar.DAY_OF_MONTH, dataLimite.getActualMaximum(Calendar.DAY_OF_MONTH));

        Event novoEvento = new Event(nome, valor, null, dataOcorreu, new Date(), dataLimite.getTime());

        DB_Events db = new DB_Events(CadastroEdicaoEvento.this);
        db.insert(novoEvento);

        Toast.makeText(CadastroEdicaoEvento.this, "Cadastro feito com sucesso!", Toast.LENGTH_LONG).show();

        finish();
        /*} catch (ParseException ex) {
            System.err.println("Erro na formatação da data!");
        }*/
    }
}