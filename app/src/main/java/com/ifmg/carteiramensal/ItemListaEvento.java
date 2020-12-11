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

import modelo.Event;
//classe que define o comportamento e informações de cada um dos itens da lista de eventos
public class ItemListaEvento extends ArrayAdapter<Event>{

    private Context contextoPai;
    private ArrayList<Event> eventos;

    private static class ViewHolder{
        private TextView nomeTxt;
        private TextView valorTxt;
        private TextView dataTxt;
        private TextView repeteTxt;
        private TextView fotoTxt;
    }

    public ItemListaEvento(Context contexto, ArrayList<Event> dados){
        super(contexto, R.layout.item_lista_eventos, dados);

        this.contextoPai = contexto;
        this.eventos = dados;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
       // return super.getView(position, convertView, parent);
        Event eventoAtual= eventos.get(position);
        ViewHolder novaView;

        final View resultado;

        //1 é quando a lista está sendo montada pela primeira vez
        if(convertView == null){
            novaView = new ViewHolder();

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_lista_eventos, parent,false);

            //linkando com os componentes do XML
            novaView.dataTxt = (TextView) convertView.findViewById(R.id.dataItem);
            novaView.fotoTxt = (TextView) convertView.findViewById(R.id.fotoItem);
            novaView.nomeTxt = (TextView) convertView.findViewById(R.id.nomeItem);
            novaView.repeteTxt = (TextView) convertView.findViewById(R.id.repeteItem);
            novaView.valorTxt = (TextView) convertView.findViewById(R.id.valorItem);

            resultado = convertView;
            convertView.setTag(novaView);
        }
        //2 item modificado
        else{
            novaView = (ViewHolder) convertView.getTag();
            resultado = convertView;
        }
        //vamos setar os valores de cada
        novaView.nomeTxt.setText(eventoAtual.getNome());
        novaView.valorTxt.setText(eventoAtual.getValor()+"");
        novaView.fotoTxt.setText(eventoAtual.getCaminhoFoto()==null ? "Não" : "Sim");
        SimpleDateFormat formataData = new SimpleDateFormat("dd/MM/yyyy");
        novaView.dataTxt.setText(formataData.format(eventoAtual.getDataOcorreu()));

        //verificando se o evento repete
        Calendar data1 = Calendar.getInstance();
        data1.setTime(eventoAtual.getDataOcorreu());

        Calendar data2 = Calendar.getInstance();
        data2.setTime(eventoAtual.getDataOcorreu());


        if(data1.get(Calendar.MONTH)!= data2.get(Calendar.MONTH)){
            novaView.repeteTxt.setText("Sim");
        }
        else{
            novaView.repeteTxt.setText("Não");
        }

        return resultado;


    }

}
