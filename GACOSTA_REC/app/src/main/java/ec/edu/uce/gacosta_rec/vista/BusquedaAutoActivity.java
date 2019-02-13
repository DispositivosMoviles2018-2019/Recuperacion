package ec.edu.uce.gacosta_rec.vista;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;

import ec.edu.uce.gacosta_rec.controlador.ImplementacionVehiculoCRUD;
import ec.edu.uce.gacosta_rec.R;
import ec.edu.uce.gacosta_rec.modelo.Vehiculo;

public class BusquedaAutoActivity extends AppCompatActivity {

    private EditText placaParaBuscar;
    private TextView placa;
    private TextView marca;
    private TextView fecha;
    private TextView costo;
    private TextView matriculado;
    private TextView color;
    private TextView estado;
    private TextView tipo;
    private ImageView foto;
    ImplementacionVehiculoCRUD crudVehiculo;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy");
    Vehiculo vehiculo;
    Intent siguiente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda_auto);

        placaParaBuscar = (EditText) findViewById(R.id.txt_placa);
        placa = (TextView) findViewById(R.id.txt_busquedar_numero);
        marca = (TextView) findViewById(R.id.txt_busqueda_marca);
        fecha = (TextView) findViewById(R.id.txt_busqueda_fecha);
        costo = (TextView) findViewById(R.id.txt_busqueda_costo);
        matriculado = (TextView) findViewById(R.id.txt_busqueda_matriculado);
        color = (TextView) findViewById(R.id.txt_busqueda_color);
        estado = (TextView) findViewById(R.id.txt_busqueda_estado);
        tipo = (TextView) findViewById(R.id.txt_busqueda_tipo);
        foto = (ImageView) findViewById(R.id.foto_busqueda);
        crudVehiculo = new ImplementacionVehiculoCRUD(this);
    }

    public void buscarLlenar(View view) {
        if (!this.placaParaBuscar.getText().toString().isEmpty()) {
            if(crudVehiculo.buscarPorParametro(this.placaParaBuscar.getText().toString().toUpperCase()) != null) {
                vehiculo = (Vehiculo) crudVehiculo.buscarPorParametro(this.placaParaBuscar.getText().toString().toUpperCase());
                this.placa.setText("Placa: " + vehiculo.getPlaca());
                this.marca.setText("Marca: " + vehiculo.getMarca());
                this.fecha.setText("Fecha de fabricacion: " + sdf.format(vehiculo.getFechaFabricacion()).toString());
                this.costo.setText("Costo: " + vehiculo.getCosto().toString());
                if (vehiculo.getMatriculado() == true) {
                    this.matriculado.setText("Matriculado: SI");
                } else {
                    this.matriculado.setText("Matriculado: NO");
                }
                this.color.setText("Color: " + vehiculo.getColor());
                byte[] fotoBase = vehiculo.getFoto();
                Bitmap bitmap = BitmapFactory.decodeByteArray(fotoBase, 0, fotoBase.length);
                this.foto.setImageBitmap(bitmap);
                if (vehiculo.getEstado() == true) {
                    this.estado.setText("Estado: Disponible");
                } else {
                    this.estado.setText("Estado: No Disponible");
                }
                this.tipo.setText("Tipo: " + vehiculo.getTipo());
            }else{
                Toast.makeText(this, "Placa incorrecta", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Dede llenar el campo de placa", Toast.LENGTH_SHORT).show();
        }
    }

    public void eliminar(View view) {
        crudVehiculo = new ImplementacionVehiculoCRUD(this);
        crudVehiculo.borrar(placa.getText().toString().substring(7, 15));
        siguiente = new Intent(this, ListaVehiculosActivity.class);
        this.startActivity(siguiente);
        finish();
    }

    public void modificar(View view) {
        RegistroVehiculoActivity.modificacion = true;
        siguiente = new Intent(this, RegistroVehiculoActivity.class);
        siguiente.putExtra("valorPlaca", placa.getText().toString().substring(7, 15));
        this.startActivity(siguiente);
        finish();
    }

    public void regresar(View view) {
        siguiente = new Intent(this, ListaVehiculosActivity.class);
        this.startActivity(siguiente);
        finish();
    }

}
