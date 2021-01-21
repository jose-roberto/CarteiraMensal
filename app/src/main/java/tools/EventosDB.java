package tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import modelo.Evento;

public class EventosDB extends SQLiteOpenHelper {

    private Context context;

    public EventosDB(Context context) {
        super(context, "evento", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String create = "CREATE TABLE IF NOT EXISTS evento(id INTEGER PRIMARY KEY AUTOINCREMENT, nome TEXT, valor REAL, imagem TEXT, dataOcorreu DATE, dataCadastro DATE, validadeData DATE)";

        db.execSQL(create);
    }

    public void insert(Evento novoEvento) {
        try (SQLiteDatabase db = this.getWritableDatabase();) {
            /*String sql = "INSERT into evento(nome, valor) VALUES ('teste', 89)";
            db.execSQL(sql);*/

            ContentValues valores = new ContentValues();

            valores.put("nome", novoEvento.getNome());
            valores.put("valor", novoEvento.getValor());
            valores.put("imagem", novoEvento.getCaminhoFoto());
            valores.put("dataOcorreu", novoEvento.getDataOcorreu().getTime());
            valores.put("dataCadastro", novoEvento.getDataCadastro().getTime());
            valores.put("validadeData", novoEvento.getDataLimite().getTime());

            db.insert("evento", null, valores);
        } catch (SQLiteException ex) {
            ex.printStackTrace();
        }
    }

    public void updateEvento(Evento eventoAtualizado) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues valores = new ContentValues();

            valores.put("nome", eventoAtualizado.getNome());
            valores.put("valor", eventoAtualizado.getValor());
            valores.put("imagem", eventoAtualizado.getCaminhoFoto());
            valores.put("dataOcorreu", eventoAtualizado.getDataOcorreu().getTime());
            valores.put("validadeData", eventoAtualizado.getDataLimite().getTime());

            db.update("evento", valores, "id = ?", new String[]{eventoAtualizado.getId() + ""});

        } catch (SQLiteException ex) {
            System.err.println("Erro ao atualizar o evento!");
            ex.printStackTrace();
        }
    }

    public Evento buscaEventoId(int idEvento) {
        String sql = "SELECT * FROM evento WHERE id = " + idEvento;

        Evento resultado = null;

        try (SQLiteDatabase db = this.getWritableDatabase()) {
            //Executando a sql
            Cursor tupla = db.rawQuery(sql, null);

            //Extraindo as informações do evento
            if (tupla.moveToFirst()) {
                String nome = tupla.getString(1);
                double valor = tupla.getDouble(2);
                if (valor < 0) {
                    valor *= -1;
                }
                String imagem = tupla.getString(3);
                Date dataOcorreu = new Date(tupla.getLong(4));
                Date dataCadastro = new Date(tupla.getLong(5));
                Date validadeData = new Date(tupla.getLong(6));

                //Instanciação do objeto
                resultado = new Evento(idEvento, nome, valor, imagem, dataOcorreu, dataCadastro, validadeData);
            }
        } catch (SQLiteException ex) {
            System.err.println("Erro ao selecionar evento pelo id!");
            ex.printStackTrace();
        }

        return resultado;
    }

    public ArrayList<Evento> search(int operacao, Calendar data) {
        ArrayList<Evento> resultado = new ArrayList<>();

        Calendar primeiro_dia = Calendar.getInstance();
        primeiro_dia.setTime(data.getTime());
        primeiro_dia.set(Calendar.DAY_OF_MONTH, 1);
        primeiro_dia.set(Calendar.HOUR, -12);
        primeiro_dia.set(Calendar.MINUTE, 0);
        primeiro_dia.set(Calendar.SECOND, 0);

        Calendar ultimo_dia = Calendar.getInstance();
        ultimo_dia.setTime(data.getTime());
        ultimo_dia.set(Calendar.DAY_OF_MONTH, ultimo_dia.getActualMaximum(Calendar.DAY_OF_MONTH));
        ultimo_dia.set(Calendar.HOUR, 23);
        ultimo_dia.set(Calendar.MINUTE, 59);
        ultimo_dia.set(Calendar.SECOND, 59);

        String sql = "SELECT * FROM evento WHERE ((validadeData <= " + ultimo_dia.getTime().getTime() +
                " AND validadeData >=  " + primeiro_dia.getTime().getTime() + ") OR (dataOcorreu <= " + ultimo_dia.getTime().getTime() +
                " AND validadeData >= " + primeiro_dia.getTime().getTime() + "))";

        sql += " AND valor ";

        if (operacao == 0) {
            sql += ">= 0";
        } else {
            sql += "< 0";
        }

        try (SQLiteDatabase db = this.getWritableDatabase()) {

            Cursor tuplas = db.rawQuery(sql, null);

            //Efetua a leitura das tuplas.
            if (tuplas.moveToFirst()) {
                do {
                    int id = tuplas.getInt(0);
                    String nome = tuplas.getString(1);
                    double valor = tuplas.getDouble(2);
                    if (valor < 0) {
                        valor *= -1;
                    }
                    String imagem = tuplas.getString(3);
                    Date dataOcorreu = new Date(tuplas.getLong(4));
                    Date dataCadastro = new Date(tuplas.getLong(5));
                    Date validadeData = new Date(tuplas.getLong(6));

                    Evento temp = new Evento(id, nome, valor, imagem, dataOcorreu, dataCadastro, validadeData);

                    resultado.add(temp);
                } while (tuplas.moveToNext());
            }
        } catch (SQLiteException ex) {
            System.err.println("Erro ao selecionar eventos!");
            ex.printStackTrace();
        }

        return resultado;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //em obra
    }
}
