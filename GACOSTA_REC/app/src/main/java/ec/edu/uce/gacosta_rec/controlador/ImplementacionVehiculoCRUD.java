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
import java.util.Date;
import java.util.List;

import ec.edu.uce.gacosta_rec.interfaces.InterfazCRUD;
import ec.edu.uce.gacosta_rec.modelo.Vehiculo;
import ec.edu.uce.gacosta_rec.vista.ListaVehiculosActivity;

public class ImplementacionVehiculoCRUD implements InterfazCRUD {

    Context ctx;
    AdminSQLiteOpenHelper admin;

    public ImplementacionVehiculoCRUD(Context ctx) {
        this.ctx = ctx;
        admin = new AdminSQLiteOpenHelper(ctx);
    }

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public String crear(Object obj) {
        Vehiculo vehiculoNuevo = (Vehiculo) obj;

        do {
            if (buscarPorParametro(vehiculoNuevo.getPlaca()) == null) {
                SQLiteDatabase baseDeDatos = admin.getWritableDatabase();
                ContentValues registro = new ContentValues();
                registro.put("placa", vehiculoNuevo.getPlaca());
                registro.put("marca", vehiculoNuevo.getMarca());
                registro.put("fecha_fabricacion", sdf.format(vehiculoNuevo.getFechaFabricacion()));
                registro.put("costo", vehiculoNuevo.getCosto());
                if (vehiculoNuevo.getMatriculado() == true) {
                    registro.put("matriculado", 1);
                } else {
                    registro.put("matriculado", 2);
                }
                if (vehiculoNuevo.getColor().equalsIgnoreCase("Blanco")) {
                    registro.put("color", 1);
                } else if (vehiculoNuevo.getColor().equalsIgnoreCase("Negro")) {
                    registro.put("color", 2);
                } else {
                    registro.put("color", 3);
                }

                registro.put("foto", vehiculoNuevo.getFoto());

                if (vehiculoNuevo.getEstado() == true) {
                    registro.put("estado", 1);
                } else {
                    registro.put("estado", 2);
                }
                if (vehiculoNuevo.getTipo().equalsIgnoreCase("Automovil")) {
                    registro.put("tipo", 1);
                } else if (vehiculoNuevo.getTipo().equalsIgnoreCase("Camioneta")) {
                    registro.put("tipo", 2);
                } else if (vehiculoNuevo.getTipo().equalsIgnoreCase("Furgoneta")) {
                    registro.put("tipo", 3);
                } else {
                    registro.put("tipo", 4);
                }

                baseDeDatos.insert("vehiculo", null, registro);
                baseDeDatos.close();

                Toast.makeText(ctx, "Registro exitoso", Toast.LENGTH_SHORT).show();

                Intent siguiente;
                siguiente = new
                        Intent(ctx, ListaVehiculosActivity.class);
                ctx.startActivity(siguiente);

                return "Registro exitoso";
            } else {
                Toast.makeText(ctx, "No se puede ingresar vehiculos con la misma placa", Toast.LENGTH_SHORT).show();
                return "Registro fallido";
            }
        }
        while (buscarPorParametro(vehiculoNuevo.getPlaca()) != null);
    }

    @Override
    public String actualizar(Object obj) {

        SQLiteDatabase baseDeDatos = admin.getWritableDatabase();
        Vehiculo vehiculoNuevo = (Vehiculo) obj;

        ContentValues registro = new ContentValues();
        registro.put("placa", vehiculoNuevo.getPlaca());
        registro.put("marca", vehiculoNuevo.getMarca());
        registro.put("fecha_fabricacion", sdf.format(vehiculoNuevo.getFechaFabricacion()));
        registro.put("costo", vehiculoNuevo.getCosto());
        if (vehiculoNuevo.getMatriculado() == true) {
            registro.put("matriculado", 1);
        } else {
            registro.put("matriculado", 2);
        }
        if (vehiculoNuevo.getColor().equalsIgnoreCase("Blanco")) {
            registro.put("color", 1);
        } else if (vehiculoNuevo.getColor().equalsIgnoreCase("Negro")) {
            registro.put("color", 2);
        } else {
            registro.put("color", 3);
        }

        registro.put("foto", vehiculoNuevo.getFoto());

        if (vehiculoNuevo.getEstado() == true) {
            registro.put("estado", 1);
        } else {
            registro.put("estado", 2);
        }
        if (vehiculoNuevo.getTipo().equalsIgnoreCase("Automovil")) {
            registro.put("tipo", 1);
        } else if (vehiculoNuevo.getTipo().equalsIgnoreCase("Camioneta")) {
            registro.put("tipo", 2);
        } else if (vehiculoNuevo.getTipo().equalsIgnoreCase("Furgoneta")) {
            registro.put("tipo", 3);
        } else {
            registro.put("tipo", 4);
        }

        int cantidad = baseDeDatos.update("vehiculo", registro, "placa ='" + vehiculoNuevo.getPlaca() + "'", null);
        baseDeDatos.close();

        Intent siguiente;
        if (cantidad == 1) {
            Toast.makeText(ctx, "Vehiculo modificado correctamente", Toast.LENGTH_SHORT).show();
            siguiente = new
                    Intent(ctx, ListaVehiculosActivity.class);
            ctx.startActivity(siguiente);
            return "Registro exitoso";
        } else {
            Toast.makeText(ctx, "El vehiculo no existe", Toast.LENGTH_SHORT).show();
            return "Registro no esxitoso Error";
        }
    }

    @Override
    public String borrar(Object id) {

        SQLiteDatabase BaseDatabase = admin.getWritableDatabase();

        String codigo = id.toString();

        int cantidad = BaseDatabase.delete("vehiculo", "placa ='" + codigo + "'", null);
        BaseDatabase.close();
        if (cantidad == 1 ) {
            Toast.makeText(ctx, "Vehiculo eliminado exitosamente", Toast.LENGTH_SHORT).show();
            return "Registro eliminado";
        } else {
            Toast.makeText(ctx, "El artículo no existe Error", Toast.LENGTH_SHORT).show();
            return "Error";
        }

    }

    @Override
    public Object buscarPorParametro(Object parametro) {

        SQLiteDatabase baseDeDatos = admin.getWritableDatabase();
        Vehiculo vehiculoNuevo = new Vehiculo();

        Cursor fila = baseDeDatos.rawQuery
                ("select placa, marca, fecha_fabricacion, costo, matriculado, color, foto, estado, tipo from vehiculo where placa ='" + parametro.toString() + "'", null);

        if (fila.moveToFirst()) {//revisa si la consulta si tiene valores
            vehiculoNuevo.setPlaca(fila.getString(0));
            vehiculoNuevo.setMarca(fila.getString(1));
            try {
                vehiculoNuevo.setFechaFabricacion(sdf.parse(fila.getString(2)));
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
            vehiculoNuevo.setCosto(Double.parseDouble(fila.getString(3)));
            if (fila.getString(4).equalsIgnoreCase("1")) {
                vehiculoNuevo.setMatriculado(true);
            } else {
                vehiculoNuevo.setMatriculado(false);
            }
            if (fila.getString(5).equalsIgnoreCase("1")) {
                vehiculoNuevo.setColor("Blanco");
            } else if (fila.getString(5).equalsIgnoreCase("2")) {
                vehiculoNuevo.setColor("Negro");
            } else {
                vehiculoNuevo.setColor("Otro");
            }

            vehiculoNuevo.setFoto(fila.getBlob(6));

            if (fila.getString(7).equalsIgnoreCase("1")) {
                vehiculoNuevo.setEstado(true);
            } else {
                vehiculoNuevo.setEstado(false);
            }
            if (fila.getString(8).equalsIgnoreCase("1")) {
                vehiculoNuevo.setTipo("Automovil");
            } else if (fila.getString(8).equalsIgnoreCase("2")) {
                vehiculoNuevo.setTipo("Camioneta");
            } else if (fila.getString(8).equalsIgnoreCase("3")) {
                vehiculoNuevo.setTipo("Furgoneta");
            } else {
                vehiculoNuevo.setTipo("Avion");
            }
            System.out.println("IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII" + vehiculoNuevo.getTipo());
            baseDeDatos.close();
            return vehiculoNuevo;
        } else {
            baseDeDatos.close();
            return null;
        }

    }

    @Override
    public List listar() {
        Date inicio,fin;

        List<Vehiculo> vehiculos = new ArrayList<Vehiculo>();
        Vehiculo vehiculoNuevo = new Vehiculo();
        SQLiteDatabase baseDeDatos = admin.getWritableDatabase();
        Cursor fila = baseDeDatos.rawQuery
                ("select placa, marca, fecha_fabricacion, costo, matriculado, color, foto, estado, tipo from vehiculo ", null);
        try {
            if (fila.moveToFirst()) {//revisa si la consulta si tiene valores
                do {
                    if (inicio.compareTo(vehiculoNuevo.getFechaFabricacion() )>0 && fin.compareTo(vehiculoNuevo.getFechaFabricacion() )<0){
                    vehiculoNuevo.setPlaca(fila.getString(0));
                    vehiculoNuevo.setMarca(fila.getString(1));

                    try {
                        vehiculoNuevo.setFechaFabricacion(sdf.parse(fila.getString(2)));
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }
                    vehiculoNuevo.setCosto(Double.parseDouble(fila.getString(3)));
                    if (fila.getString(4).equalsIgnoreCase("1")) {
                        vehiculoNuevo.setMatriculado(true);
                    } else {
                        vehiculoNuevo.setMatriculado(false);
                    }
                    if (fila.getString(5).equalsIgnoreCase("1")) {
                        vehiculoNuevo.setColor("Blanco");
                    } else if (fila.getString(5).equalsIgnoreCase("2")) {
                        vehiculoNuevo.setColor("Negro");
                    } else {
                        vehiculoNuevo.setColor("Otro");
                    }

                    vehiculoNuevo.setFoto(fila.getBlob(6));

                    if (fila.getString(7).equalsIgnoreCase("1")) {
                        vehiculoNuevo.setEstado(true);
                    } else {
                        vehiculoNuevo.setEstado(false);
                    }
                    if (fila.getString(8).equalsIgnoreCase("1")) {
                        vehiculoNuevo.setTipo("Automovil");
                    } else if (fila.getString(8).equalsIgnoreCase("2")) {
                        vehiculoNuevo.setTipo("Camioneta");
                    } else if (fila.getString(8).equalsIgnoreCase("3")) {
                        vehiculoNuevo.setTipo("Furgoneta");
                    } else {
                        vehiculoNuevo.setTipo("Avion");
                    }
                    System.out.println();
                    vehiculos.add(vehiculoNuevo);
                    vehiculoNuevo = new Vehiculo();
                }else{
                        continue;
                    }

                }
                while (fila.moveToNext());
                baseDeDatos.close();

                return vehiculos;


            } else {
                Toast.makeText(ctx, "No existe vehiculos en la base", Toast.LENGTH_SHORT).show();
                baseDeDatos.close();
                return vehiculos;
            }

        } catch (Exception ex) {
            Log.e("Base de datos", "Error al leer la base de datos");
            return vehiculos;
        }
    }
}
