package ec.edu.uce.gacosta_rec.controlador;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;


public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

    public AdminSQLiteOpenHelper(Context context) {

        super(context, Environment.getExternalStorageDirectory() + "/archivos/primer.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase BaseDeDatos) {
        BaseDeDatos.execSQL("create table usuario(usuario text, clave text)");
        BaseDeDatos.execSQL("create table vehiculo(placa text primary key, marca text, fecha_fabricacion text, costo real," +
                " matriculado int, color int, foto BLOB, estado int, tipo int)");
        BaseDeDatos.execSQL("create table reserva(numero_reserva int primary key, placa text, email text, celular text, fecha_prestamo text," +
                " fecha_entrega text, valor real)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + " usuario");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + " vehiculo");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + " reserva");
        onCreate(sqLiteDatabase);
    }
}
