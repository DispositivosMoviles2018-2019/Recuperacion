package ec.edu.uce.gacosta_rec.controlador;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import ec.edu.uce.gacosta_rec.interfaces.InterfazCRUD;
import ec.edu.uce.gacosta_rec.modelo.Reserva;
import ec.edu.uce.gacosta_rec.vista.MenuOpcionesActivity;

public class ImplementacionReservaCRUD implements InterfazCRUD {

    Context ctx;
    AdminSQLiteOpenHelper admin;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public ImplementacionReservaCRUD(Context ctx) {
        this.ctx = ctx;
        admin = new AdminSQLiteOpenHelper(ctx);
    }

    @Override
    public String crear(Object obj) {
        Reserva reservaNueva = (Reserva) obj;

        SQLiteDatabase baseDeDatos = admin.getWritableDatabase();
        ContentValues registro = new ContentValues();
        registro.put("numero_reserva", reservaNueva.getNumeroReserva());
        registro.put("placa", reservaNueva.getPlaca());
        registro.put("email", reservaNueva.getEmail());
        registro.put("celular", reservaNueva.getCelular());
        registro.put("fecha_prestamo", sdf.format(reservaNueva.getFechaPrestamo()));
        registro.put("fecha_entrega", sdf.format(reservaNueva.getFechaEntrega()));
        registro.put("valor", reservaNueva.getValor());

        baseDeDatos.insert("reserva", null, registro);
        baseDeDatos.close();

        Toast.makeText(ctx, "Registro exitoso", Toast.LENGTH_SHORT).show();

        Intent siguiente;
        siguiente = new
                Intent(ctx, MenuOpcionesActivity.class);
        ctx.startActivity(siguiente);

        return "Registro exitoso";


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
        Reserva reservaNuevo = new Reserva();


        if (isString(parametro)) {
            Cursor fila = baseDeDatos.rawQuery
                    ("select numero_reserva, placa, email, celular, fecha_prestamo, fecha_entrega, valor from reserva where placa ='" + parametro.toString() + "'", null);
               if (fila.moveToFirst()) {//revisa si la consulta si tiene valores
                reservaNuevo.setNumeroReserva(fila.getInt(0));
                reservaNuevo.setPlaca(fila.getString(1));
                reservaNuevo.setEmail(fila.getString(2));
                reservaNuevo.setCelular(fila.getString(3));
                try {
                    reservaNuevo.setFechaPrestamo(sdf.parse(fila.getString(4)));
                    reservaNuevo.setFechaEntrega(sdf.parse(fila.getString(5)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                reservaNuevo.setValor(Integer.parseInt(fila.getString(6)));

                baseDeDatos.close();
                return reservaNuevo;
            } else {
                baseDeDatos.close();
                return null;
            }
        }else{
            Cursor fila = baseDeDatos.rawQuery
                    ("select numero_reserva, placa, email, celular, fecha_prestamo, fecha_entrega, valor from reserva where numero_reserva =" + parametro.toString() + "", null);
            if (fila.moveToFirst()) {//revisa si la consulta si tiene valores
                reservaNuevo.setNumeroReserva(fila.getInt(0));
                reservaNuevo.setPlaca(fila.getString(1));
                reservaNuevo.setEmail(fila.getString(2));
                reservaNuevo.setCelular(fila.getString(3));
                try {
                    reservaNuevo.setFechaPrestamo(sdf.parse(fila.getString(4)));
                    reservaNuevo.setFechaEntrega(sdf.parse(fila.getString(5)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                reservaNuevo.setValor(Integer.parseInt(fila.getString(6)));

                baseDeDatos.close();
                return reservaNuevo;
            } else {
                baseDeDatos.close();
                return null;
            }

        }
    }

    @Override
    public List listar() {
        List<Reserva> reservas = new ArrayList<Reserva>();
        Reserva reservaNueva = new Reserva();
        SQLiteDatabase baseDeDatos = admin.getWritableDatabase();
        Cursor fila = baseDeDatos.rawQuery
                ("select numero_reserva, placa, email, celular, fecha_prestamo, fecha_entrega, valor from reserva ", null);
        try {
            if (fila.moveToFirst()) {//revisa si la consulta si tiene valores
                do {
                    reservaNueva.setNumeroReserva(fila.getInt(0));
                    reservaNueva.setPlaca(fila.getString(1));
                    reservaNueva.setEmail(fila.getString(2));
                    reservaNueva.setCelular(fila.getString(3));
                    reservaNueva.setFechaPrestamo(sdf.parse(fila.getString(4)));
                    reservaNueva.setFechaEntrega(sdf.parse(fila.getString(5)));
                    reservaNueva.setValor(Integer.parseInt(fila.getString(6)));

                    reservas.add(reservaNueva);
                    reservaNueva = new Reserva();
                }
                while (fila.moveToNext());
                baseDeDatos.close();
                return reservas;

            } else {
                Toast.makeText(ctx, "No existe reservas en la base", Toast.LENGTH_SHORT).show();
                baseDeDatos.close();
                return reservas;
            }

        } catch (Exception ex) {
            Log.e("Base de datos", "Error al leer la base de datos");
            return reservas;
        }
    }

    public boolean isString(Object str) {
        if (str.equals(str.toString())) {
            return true;
        } else {
            return false;
        }

    }
}
