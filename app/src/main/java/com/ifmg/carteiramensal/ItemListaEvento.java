package com.ifmg.carteiramensal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import modelo.Evento;

//Define o comportamento e as informações de cada um dos itens de eventos
public class ItemListaEvento extends ArrayAdapter<Evento> {

    private Context contexto;
    private ArrayList<Evento> eventos;

    private static class ViewHolder {
        private TextView txtNome;
        private TextView txtValor;
        private TextView txtData;
        private TextView txtRepeticao;
        private TextView txtFoto;
    }

    public ItemListaEvento(Context contexto, ArrayList<Evento> dados) {
        super(contexto, R.layout.item_lista_evento, dados);

        this.contexto = contexto;
        this.eventos = dados;
    }

    @NonNull
    @Override
    public View getView(int indice, @Nullable View convertView, @NonNull ViewGroup parent) {
        //return super.getView(indice, convertView, parent);

        Evento eventoAtual = eventos.get(indice);
        ViewHolder novaView;
        final View resultado;

        if (convertView == null) {
            //1º caso é quando a lista está sendo montada pela primeira vez.
            novaView = new ViewHolder();

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_lista_evento, parent, false);

            //Link entre elementos java e xml.
            novaView.txtNome = (TextView) convertView.findViewById(R.id.txtNomeItem);
            novaView.txtValor = (TextView) convertView.findViewById(R.id.txtValorItem);
            novaView.txtData = (TextView) convertView.findViewById(R.id.txtDataItem);
            novaView.txtRepeticao = (TextView) convertView.findViewById(R.id.txtRepeticaoItem);
            novaView.txtFoto = (TextView) convertView.findViewById(R.id.txtFotoItem);

            resultado = convertView;
            convertView.setTag(novaView);
        } else {
            //2º caso é quando algum item existente foi modificado.
            novaView = (ViewHolder) convertView.getTag();
            resultado = convertView;
        }

        //Definindo os valores de cada cmapo.
        novaView.txtNome.setText(eventoAtual.getNome());
        novaView.txtValor.setText(eventoAtual.getValor() + "");
        SimpleDateFormat formataData = new SimpleDateFormat("dd/MM/yyyy");
        novaView.txtData.setText(formataData.format(eventoAtual.getDataOcorreu()));
        novaView.txtFoto.setText(eventoAtual.getCaminhoFoto() == null ? "Não" : "Sim");

        //Verificando se o evento irá repetir.
        Calendar dataOcorreu = Calendar.getInstance();
        dataOcorreu.setTime(eventoAtual.getDataOcorreu());

        Calendar dataLimite = Calendar.getInstance();
        dataLimite.setTime(eventoAtual.getDataLimite());

        if (dataOcorreu.get(Calendar.MONTH) != dataLimite.get(Calendar.MONTH)) {
            novaView.txtRepeticao.setText("Sim");
        } else {
            novaView.txtRepeticao.setText("Não");
        }

        return resultado;
    }
}
