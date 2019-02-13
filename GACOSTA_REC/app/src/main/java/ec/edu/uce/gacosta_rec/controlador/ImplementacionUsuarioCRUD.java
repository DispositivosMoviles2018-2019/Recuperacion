package ec.edu.uce.gacosta_rec.controlador;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ec.edu.uce.gacosta_rec.interfaces.InterfazCRUD;
import ec.edu.uce.gacosta_rec.modelo.Usuario;
import ec.edu.uce.gacosta_rec.vista.MainActivity;

public class ImplementacionUsuarioCRUD implements InterfazCRUD {

    Context ctx;
    AdminSQLiteOpenHelper admin;

    public ImplementacionUsuarioCRUD(Context ctx) {
        this.ctx = ctx;
        admin = new AdminSQLiteOpenHelper(ctx);
    }


    @Override
    public String crear(Object obj) {


        Usuario usuarioNuevo = (Usuario) obj;
        do {
            if (buscarPorParametro(usuarioNuevo.getUsuario()) == null) {
                SQLiteDatabase baseDeDatos = admin.getWritableDatabase();
                ContentValues registro = new ContentValues();
                registro.put("usuario", usuarioNuevo.getUsuario());
                registro.put("clave", usuarioNuevo.getClave());

                baseDeDatos.insert("usuario", null, registro);
                baseDeDatos.close();

                Toast.makeText(ctx, "Registro exitoso", Toast.LENGTH_SHORT).show();

                Intent siguiente;
                siguiente = new
                        Intent(ctx, MainActivity.class);
                ctx.startActivity(siguiente);

                return "Registro exitoso";
            } else {
                Toast.makeText(ctx, "No se puede ingresar usuarios duplicados", Toast.LENGTH_SHORT).show();
                return "Registro fallido";
            }
        }
        while (buscarPorParametro(usuarioNuevo.getUsuario()) != null);

    }

    @Override
    public String actualizar(Object obj) {
        return null;
    }

    @Override
    public String borrar(Object id) {
        return null;
    }

    @Override
    public Object buscarPorParametro(Object parametro) {

        SQLiteDatabase baseDeDatos = admin.getWritableDatabase();
        Usuario usuarioNuevo = new Usuario();

        Cursor fila = baseDeDatos.rawQuery
                ("select usuario, clave from usuario where usuario ='" + parametro.toString() + "'", null);

        if (fila.moveToFirst()) {//revisa si la consulta si tiene valores
            usuarioNuevo.setUsuario(fila.getString(0));
            usuarioNuevo.setClave(fila.getString(1));
            baseDeDatos.close();
            return usuarioNuevo;
        } else {
            baseDeDatos.close();
            return null;
        }

    }

    @Override
    public List listar() {
        List<Usuario> usuarios = new ArrayList<Usuario>();
        Usuario usuarioNuevo = new Usuario();
        SQLiteDatabase baseDeDatos = admin.getWritableDatabase();
        Cursor fila = baseDeDatos.rawQuery
                ("select usuario, clave from usuario  ", null);
        try {
            if (fila.moveToFirst()) {//revisa si la consulta si tiene valores
                do {
                    usuarioNuevo.setUsuario(fila.getString(0));
                    usuarioNuevo.setClave(fila.getString(1));

                    usuarios.add(usuarioNuevo);
                    usuarioNuevo = new Usuario();
                }
                while (fila.moveToNext());
                baseDeDatos.close();
                return usuarios;

            } else {
                Toast.makeText(ctx, "No existe usuarios en la base", Toast.LENGTH_SHORT).show();
                baseDeDatos.close();
                return usuarios;
            }

        } catch (Exception ex) {
            Log.e("Base de datos", "Error al leer la base de datos");
            return usuarios;
        }
    }
}
