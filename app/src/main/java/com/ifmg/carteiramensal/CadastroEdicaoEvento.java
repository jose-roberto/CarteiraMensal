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

import modelo.Evento;
import tools.EventosDB;

public class CadastroEdicaoEvento extends AppCompatActivity {

    private TextView titulo;
    private EditText txtNome;
    private EditText txtValor;
    private TextView txtData;
    private CheckBox cbRepete;
    private Spinner mesesSpinner;
    private ImageView foto;
    private Button addFoto;
    private Button btnCancelar;
    private Button btnSalvar;

    private DatePickerDialog calendario;
    private Calendar calendarioTemp;

    //0 - cadastro de entrada
    //1 - cadastro de saída
    //2 - edição de entrada
    //3 - edição de saída
    private int acao = -1;

    Evento eventoSelecionado;

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
        btnCancelar = (Button) findViewById(R.id.btnCancelar);
        btnSalvar = (Button) findViewById(R.id.btnSalvar);

        //Obtém a data atual;
        Calendar hoje = Calendar.getInstance();
        SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");
        txtData.setText(formatador.format(hoje.getTime()));

        Intent intencao = getIntent();
        acao = intencao.getIntExtra("acao", -1);
        ajustesPorAcao();

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

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (acao < 2) {
                    //Cadastra um novo evento.
                    cadastrarNovoEvento();
                } else {
                    //Atualiza o evento.
                    updateEvento();
                }
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

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (acao < 2) {
                    //Finaliza a execução.
                    finish();
                } else {
                    //Deleta o evento.
                }
            }
        });
    }

    //Reutilização de Activitys
    private void ajustesPorAcao() {
        switch (acao) {
            case 0:
                titulo.setText("Cadastro de Entrada");
                break;
            case 1:
                titulo.setText("Cadastro de Saída");
                break;
            case 2:
                titulo.setText("Edição de Entrada");
                ajusteEdicao();
                break;
            case 3:
                titulo.setText("Edição de Saída");
                ajusteEdicao();
                break;
            default:
                break;
        }
    }

    private void ajusteEdicao() {
        //Carregando as informações do evento presentes no banco de dados.
        int id = Integer.parseInt(getIntent().getStringExtra("id"));

        if (id != 0) {
            EventosDB db = new EventosDB(CadastroEdicaoEvento.this);

            eventoSelecionado = db.buscaEventoId(id);

            //Carregando as informações dos campos recuperados
            SimpleDateFormat formatar = new SimpleDateFormat("dd/MM/yyyy");

            txtNome.setText(eventoSelecionado.getNome());
            txtValor.setText(eventoSelecionado.getValor() + "");
            txtData.setText(formatar.format(eventoSelecionado.getDataOcorreu()));

            Calendar d1 = Calendar.getInstance();
            d1.setTime(eventoSelecionado.getDataLimite());

            Calendar d2 = Calendar.getInstance();
            d2.setTime(eventoSelecionado.getDataOcorreu());

            cbRepete.setChecked(d1.get(Calendar.MONTH) != d2.get(Calendar.MONTH) ? true : false);
            if (cbRepete.isChecked()) {
                mesesSpinner.setEnabled(true);

                //Cálculo da diferença entre o mês de cadastro e o mês limite.
                mesesSpinner.setSelection((d1.get(Calendar.MONTH) - d2.get(Calendar.MONTH)) - 1);
            }
        }

        //Alteração dos botões
        btnCancelar.setText("Excluir");
        btnSalvar.setText("Atualizar");
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

        Evento novoEvento = new Evento(nome, valor, null, dataOcorreu, new Date(), dataLimite.getTime());

        EventosDB db = new EventosDB(CadastroEdicaoEvento.this);
        db.insert(novoEvento);

        Toast.makeText(CadastroEdicaoEvento.this, "Cadastro feito com sucesso!", Toast.LENGTH_LONG).show();

        finish();
        /*} catch (ParseException ex) {
            System.err.println("Erro na formatação da data!");
        }*/
    }

    private void updateEvento() {
        eventoSelecionado.setNome(txtNome.getText().toString());
        eventoSelecionado.setValor(Double.parseDouble(txtValor.getText().toString()));

        if (acao == 3) {
            eventoSelecionado.setValor(eventoSelecionado.getValor() * -1);
        }

        eventoSelecionado.setDataOcorreu(calendarioTemp.getTime());
        //Nova calendário para calcular a data limite.
        Calendar dataLimite = Calendar.getInstance();
        dataLimite.setTime(calendarioTemp.getTime());

        if (cbRepete.isChecked()) {
            String mesStr = (String) mesesSpinner.getSelectedItem();
            dataLimite.add(Calendar.MONTH, Integer.parseInt(mesStr));
        }

        //Setando o limite para o último dia do mês.
        dataLimite.set(Calendar.DAY_OF_MONTH, dataLimite.getActualMaximum(Calendar.DAY_OF_MONTH));

        eventoSelecionado.setDataLimite(dataLimite.getTime());

        EventosDB db = new EventosDB(CadastroEdicaoEvento.this);
        db.updateEvento(eventoSelecionado);
        finish();
    }
}