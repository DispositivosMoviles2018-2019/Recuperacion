package ec.edu.uce.gacosta_rec.vista;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import ec.edu.uce.gacosta_rec.controlador.ImplementacionReservaCRUD;
import ec.edu.uce.gacosta_rec.controlador.ImplementacionVehiculoCRUD;
import ec.edu.uce.gacosta_rec.R;
import ec.edu.uce.gacosta_rec.modelo.Vehiculo;

public class ValidacionDisponibilidadActivity extends AppCompatActivity {

    private EditText placa;
    ImplementacionVehiculoCRUD crudVehiculo;
    ImplementacionReservaCRUD crudReserva;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validacion_disponibilidad);

        placa = (EditText) findViewById(R.id.txt_validar);
        crudVehiculo = new ImplementacionVehiculoCRUD(this);
        crudReserva = new ImplementacionReservaCRUD(this);

    }

    public void validar(View view) {
        Vehiculo vehiculo;
        if (placa.getText().toString().isEmpty()) {
            Toast.makeText(this, "Debe llenar el campo placa", Toast.LENGTH_LONG).show();
        } else {
            if (crudVehiculo.buscarPorParametro(placa.getText().toString().toUpperCase()) != null) {
                vehiculo = (Vehiculo) crudVehiculo.buscarPorParametro(placa.getText().toString().toUpperCase());
                //System.out.println("KKKKKKKKKKKKKKKK" + vehiculo.getEstado());
                if (vehiculo.getEstado() == true) {
                    Intent siguiente = new Intent(this, RegistroReservaActivity.class);
                    siguiente.putExtra("valorPlaca", placa.getText().toString());
                    this.startActivity(siguiente);
                    finish();
                } else {
                    Toast.makeText(this, "El vehiculo no esta disponible", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Placa incorrecta", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void buscarPlaca(View view) {
        Vehiculo vehiculo;
        if (placa.getText().toString().isEmpty()) {
            Toast.makeText(this, "Debe llenar el campo placa", Toast.LENGTH_LONG).show();
        } else {
            //validar con la placa de la reserva
            if (crudVehiculo.buscarPorParametro(placa.getText().toString().toUpperCase()) != null) {
                Intent siguiente;
                siguiente = new Intent(this, BusquedaReservaActivity.class);
                siguiente.putExtra("valorPlaca", placa.getText().toString());
                this.startActivity(siguiente);
                finish();
            } else {
                Toast.makeText(this, "Placa no coincide con reserva", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void bucarNumeroReserva(View view) {
        if (placa.getText().toString().isEmpty()) {
            Toast.makeText(this, "Debe llenar el campo placa", Toast.LENGTH_LONG).show();
        } else {
            if (crudReserva.buscarPorParametro(placa.getText().toString().toUpperCase()) != null) {
            Intent siguiente;
            siguiente = new Intent(this, BusquedaReservaActivity.class);
            siguiente.putExtra("valorReserva", placa.getText().toString());
            siguiente.putExtra("valorPlaca", "no");
            this.startActivity(siguiente);
            finish();
            } else {
                Toast.makeText(this, "Numero de reserva no coincide con ninguna reseva", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void regresar(View view) {
        Intent siguiente;
        siguiente = new Intent(this, MenuOpcionesActivity.class);
        this.startActivity(siguiente);
        finish();
    }
}
