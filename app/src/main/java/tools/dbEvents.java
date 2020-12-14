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

import modelo.Event;

public class dbEvents extends SQLiteOpenHelper {

    private Context context;

    public dbEvents(Context context) {
        super(context, "evento", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String create = "CREATE TABLE IF NOT EXISTS evento(id INTEGER PRIMARY KEY AUTOINCREMENT, nome TEXT, valor REAL, imagem TEXT, dataOcorreu DATE, dataCadastro DATE, validadeData DATE)";

        db.execSQL(create);
    }

    public void insert(Event novoEvento) {
        try (SQLiteDatabase db = this.getWritableDatabase();) {
            /*
            String sql = "INSERT into evento(nome, valor) VALUES ('teste', 89)";
            db.execSQL(sql);
            */

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

    public void update() {

    }

    public ArrayList<Event> search(int op, Calendar data) {

        ArrayList<Event> resultado = new ArrayList<>();

        //1 dia do mes
        Calendar dia1 = Calendar.getInstance();
        dia1.setTime(data.getTime());
        dia1.set(Calendar.DAY_OF_MONTH, 1);
        dia1.set(Calendar.HOUR, -12);
        dia1.set(Calendar.MINUTE, 0);
        dia1.set(Calendar.SECOND, 0);


        //ultimo dia do mes
        Calendar dia2 = Calendar.getInstance();
        dia2.setTime(data.getTime());
        dia2.set(Calendar.DAY_OF_MONTH, dia2.getActualMaximum(Calendar.DAY_OF_MONTH));
        dia2.set(Calendar.HOUR, 23);
        dia2.set(Calendar.MINUTE, 59);
        dia2.set(Calendar.SECOND, 59);



        String sql = "SELECT * FROM evento WHERE ((datavalida < " +dia2.getTime().getTime() +
                " AND datavalida>= " + dia1.getTime().getTime() + ") OR (dataocorreu <= "+ dia2.getTime().getTime()+
                " AND datavalida>=" + dia1.getTime().getTime() + ") )";

        sql += " AND valor ";

        if(op == 0){
            //entradas
            sql += ">= 0";
        }
        else{
            //saídas negativo
            sql += "<= 0";
        }

        try(SQLiteDatabase db = this.getWritableDatabase()){

            Cursor tuplas = db.rawQuery(sql, null);

            //efetuar a leitura das tuplas
            if(tuplas.moveToFirst()){

                do{
                    int id = tuplas.getInt (0);
                    String nome = tuplas.getString(1);
                    double valor = tuplas.getDouble(2);
                    if(valor < 0){
                        valor*= -1;
                    }
                    String urlFoto = tuplas.getString(3);
                    Date dataocorreu = new Date(tuplas.getLong(4));
                    Date datacadastro = new Date(tuplas.getLong(5));
                    Date datavalida = new Date(tuplas.getLong(6));

                    Event temporario = new Event((long)id, nome, valor, datacadastro, datavalida, dataocorreu, urlFoto);

                    resultado.add(temporario);

                }while(tuplas.moveToNext());
            }


        }catch(SQLiteException ex){
            System.err.println("ocorreu um bug na consulta no banco");
            ex.printStackTrace();

        }


        return null;

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //em obra
    }
}
