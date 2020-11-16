package tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class dbEvents extends SQLiteOpenHelper {

    private Context context;

    public dbEvents (Context context) {
        super(context, "evento", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String create = "CREATE TABLE IF NOT EXISTS evento(id INTEGER PRIMARY KEY AUTOINCREMENT, nome TEXT, valor REAL, imagem TEXT, dataOcorreu DATE, dataCadastro DATE, validadeData DATE)";

        db.execSQL(create);
    }

    public void insert() {
        try(SQLiteDatabase db = this.getWritableDatabase();) {
            /*
            String sql = "INSERT into evento(nome, valor) VALUES ('teste', 89)";
            db.execSQL(sql);
            */

            ContentValues valores = new ContentValues();

            valores.put("nome", "teste");
            valores.put("valor", -100);

            db.insert("evento", null, valores);
        } catch (SQLiteException ex) {
            ex.printStackTrace();
        }
    }

    public void update() {

    }

    public void search() {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //em obra
    }
}
