package ec.edu.uce.gacosta_rec.vista;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import ec.edu.uce.gacosta_rec.controlador.ImplementacionUsuarioCRUD;
import ec.edu.uce.gacosta_rec.R;
import ec.edu.uce.gacosta_rec.modelo.Usuario;

public class RegistroUsuarioActivity extends AppCompatActivity {

    private EditText usuario;
    private EditText clave;
    //AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "/storage/emulated/0/archivos/primer.db", null, 1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario);

        usuario = (EditText) findViewById(R.id.txt_usuario_registro);
        clave = (EditText) findViewById(R.id.txt_clave);
    }

    public void guardar(View view) throws IOException {

        Usuario usuarioAux;

        if (this.usuario.getText().toString().isEmpty()) {
            Toast.makeText(this, "Campo Usuario vacio", Toast.LENGTH_SHORT).show();
        } else {
            if (this.clave.getText().toString().isEmpty()) {
                Toast.makeText(this, "Campo Clave vacio", Toast.LENGTH_SHORT).show();
            } else {
                String usuario = this.usuario.getText().toString();
                String clave = this.clave.getText().toString();
                usuarioAux = new Usuario(usuario, clave);
                ImplementacionUsuarioCRUD crudUsuario = new ImplementacionUsuarioCRUD(this);

                if(crudUsuario.crear(usuarioAux).equalsIgnoreCase("Registro exitoso")){
                    finish();
                }
            }
        }
    }
/*
    @Override
    public String crear(Object obj) {

        SQLiteDatabase baseDeDatos = admin.getWritableDatabase();
        Usuario usuarioNuevo = (Usuario) obj;

        ContentValues registro = new ContentValues();
        registro.put("usuario", usuarioNuevo.getUsuario());
        registro.put("clave", usuarioNuevo.getClave());

        baseDeDatos.insert("usuario", null, registro);
        baseDeDatos.close();

        Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();

        Intent siguiente;
        siguiente = new
                Intent(this, MainActivity.class);
        startActivity(siguiente);
        finish();

        return "Registro exitoso";
    }

    @Override
    public String actualizar(Object id) {
        return null;
    }

    @Override
    public String borrar(Object id) {
        return null;
    }

    @Override
    public Object buscarPorParametro( Object parametro) {

        return null;
    }

    @Override
    public Collection listar() {
       /* Collection<Usuario> usuarios = new ArrayList<Usuario>();
        Usuario usuario = new Usuario();
        SQLiteDatabase baseDeDatos = admin.getWritableDatabase();
        Cursor fila = baseDeDatos.rawQuery
                ("select usuario, clave from usuario ", null);

        if (fila.moveToFirst()) {//revisa si la consulta si tiene valores
            usuario.setUsuario(fila.getString(0));
            usuario.setClave(fila.getString(1));
            ((ArrayList<Usuario>) usuarios).add(usuario);
            baseDeDatos.close();
        } else {
            Toast.makeText(this, "No existe usuarios en la base", Toast.LENGTH_SHORT).show();
            baseDeDatos.close();
        }
        return null;
    }*/
}
