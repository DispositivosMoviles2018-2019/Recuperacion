package ec.edu.uce.gacosta_rec.vista;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

import ec.edu.uce.gacosta_rec.controlador.ImplementacionReservaCRUD;
import ec.edu.uce.gacosta_rec.controlador.ImplementacionVehiculoCRUD;
import ec.edu.uce.gacosta_rec.R;
import ec.edu.uce.gacosta_rec.modelo.Reserva;
import ec.edu.uce.gacosta_rec.modelo.Vehiculo;
import ec.edu.uce.gacosta_rec.servicios.Mail;


public class RegistroReservaActivity extends AppCompatActivity {


    private TextView placa;
    private TextView numeroReserva;
    private EditText emial;
    private EditText celular;
    private TextView fechaReserva;
    private TextView fechaEntrega;
    private TextView costo;
    private Button btnReserva;
    private DatePickerDialog.OnDateSetListener fechaReservaElegir;
    private DatePickerDialog.OnDateSetListener fechaEntregaElegir;
    private Date fechaReservaAux = new Date();
    private Date fechaEntregaAux = new Date();

    private String user;
    private String pass;
    private String subject;
    private String body;
    private String recipient;

    ImplementacionVehiculoCRUD crudVehiculo;
    ImplementacionReservaCRUD crudReserva;

    Vehiculo vehiculo = new Vehiculo();
    Reserva reserva;
    Mail ma = new Mail();
    Integer valor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_reserva);

        crudVehiculo = new ImplementacionVehiculoCRUD(this);
        crudReserva = new ImplementacionReservaCRUD(this);
        placa = (TextView) findViewById(R.id.txt_placa_reserva);
        placa.setText("Placa: " + getIntent().getStringExtra("valorPlaca"));
        numeroReserva = (TextView) findViewById(R.id.txt_numero_reserva);
        btnReserva = (Button) findViewById(R.id.btn_reservar);

        /*btnReserva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });*/

        Random rnd = new Random();
        Boolean repetido = false;
        String numeroAleatorio = String.valueOf(rnd.nextInt(9) + 1) + String.valueOf(rnd.nextInt(9) + 1) + String.valueOf(rnd.nextInt(9) + 1) + String.valueOf(rnd.nextInt(9) + 1);

        do {
            if (revisarNumeroRepetido(numeroAleatorio)) {
                numeroAleatorio = String.valueOf(rnd.nextInt(9) + 1) + String.valueOf(rnd.nextInt(9) + 1) + String.valueOf(rnd.nextInt(9) + 1) + String.valueOf(rnd.nextInt(9) + 1);
            } else {
                numeroReserva.setText("Numero reserva: " + numeroAleatorio);
            }
        } while (revisarNumeroRepetido(numeroAleatorio));
        emial = (EditText) findViewById(R.id.txt_email_reserva);
        celular = (EditText) findViewById(R.id.txt_celular_reserva);

        fechaReserva = (TextView) findViewById(R.id.txt_fecha_prestamo_reserva);
        fechaReserva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar fechaHoy = Calendar.getInstance();
                int year = fechaHoy.get(Calendar.YEAR);
                int month = fechaHoy.get(Calendar.MONTH);
                int day = fechaHoy.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(RegistroReservaActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, fechaReservaElegir, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        fechaReservaElegir = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = dayOfMonth + "/" + month + "/" + year;
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                fechaReservaAux.setTime(calendar.getTimeInMillis());

                fechaReserva.setText("Fecha de Reserva: " + date);
            }
        };
        costo = (TextView) findViewById(R.id.txt_precio_reserva);
        fechaEntrega = (TextView) findViewById(R.id.txt_fecha_entrega_reserva);
        fechaEntrega.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar fechaHoy = Calendar.getInstance();
                int year = fechaHoy.get(Calendar.YEAR);
                int month = fechaHoy.get(Calendar.MONTH);
                int day = fechaHoy.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(RegistroReservaActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, fechaEntregaElegir, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        fechaEntregaElegir = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = dayOfMonth + "/" + month + "/" + year;
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                fechaEntregaAux.setTime(calendar.getTimeInMillis());

                fechaEntrega.setText("Fecha de entrega: " + date);

                vehiculo = (Vehiculo) crudVehiculo.buscarPorParametro(getIntent().getStringExtra("valorPlaca").toUpperCase());
                Integer dias = (int) ((fechaEntregaAux.getTime() - fechaReservaAux.getTime()) / 86400000);
                if (vehiculo.getTipo().equalsIgnoreCase("Automovil")) {
                    valor = (60 * dias);

                } else if (vehiculo.getTipo().equalsIgnoreCase("Camioneta")) {
                    valor = (75 * dias);

                } else {
                    valor = (100 * dias);
                }
                costo.setText("Cos a pagar: " + String.valueOf(valor));

            }
        };

    }

    public Boolean revisarNumeroRepetido(String numeroAleatorio) {
        for (Reserva r : (List<Reserva>) crudReserva.listar()) {
            if ((numeroAleatorio).equalsIgnoreCase(String.valueOf(r.getNumeroReserva()))) {
                return true;
            }
        }
        return false;
    }

    public void reservar(View view) {
        reserva = new Reserva();

        if (emial.getText().toString().isEmpty()) {
            Toast.makeText(this, "Campo Email vacio", Toast.LENGTH_SHORT).show();
        } else {

            Pattern pattern1 = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
            Matcher matcher1 = pattern1.matcher(this.emial.getText().toString());
            if (matcher1.matches()) {

                if (celular.getText().toString().isEmpty()) {
                    Toast.makeText(this, "Campo celular vacio", Toast.LENGTH_SHORT).show();
                } else {
                    reserva.setPlaca(placa.getText().toString().substring(7, placa.getText().toString().length()));
                    reserva.setNumeroReserva(Integer.parseInt(numeroReserva.getText().toString().substring(16, numeroReserva.getText().toString().length())));
                    reserva.setEmail(emial.getText().toString());
                    reserva.setCelular(celular.getText().toString());
                    reserva.setFechaPrestamo(fechaReservaAux);
                    reserva.setFechaEntrega(fechaEntregaAux);
                    reserva.setValor(valor);

                    if (crudReserva.crear(reserva).equalsIgnoreCase("Registro exitoso")) {


                        Intent siguiente;
                        siguiente = new
                                Intent(this, MenuOpcionesActivity.class);
                        startActivity(siguiente);
                        finish();
                        sendMessage();
                    } else {
                        Toast.makeText(this, "Registro Fallido", Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                Toast.makeText(this, "Ingrese Formato de EMAIL", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void sendMessage() {
        user = "roberthestrella1993@gmail.com";
        pass = "JINZOPSV000";
        subject = "RESERVAS";

        StringBuilder sb = new StringBuilder();
        sb.append("La reserva del auto con placa: ");
        sb.append(reserva.getPlaca().toString());
        sb.append(" y numero de reserva: ");
        sb.append(String.valueOf(reserva.getNumeroReserva()));
        sb.append(" Fue realizada exitosamente desde la fecha: ");
        sb.append(new SimpleDateFormat("dd/MM/yyyy").format(reserva.getFechaPrestamo()));
        sb.append(" hasta la fecha: ");
        sb.append(new SimpleDateFormat("dd/MM/yyyy").format(reserva.getFechaPrestamo()));

        body = sb.toString();

        String correo = reserva.getEmail();

        recipient = correo;

        String[] recipients = {recipient};

        SendEmailAsyncTask email = new SendEmailAsyncTask();
        email.activity = this;
        email.m = new Mail(user, pass);
        email.m.set_from(user);
        email.m.setBody(body);
        email.m.set_to(recipients);
        email.m.set_subject(subject);
        email.execute();
    }

    public void displayMessage(String message) {
        Snackbar.make(findViewById(R.id.btn_reservar), message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

}

class SendEmailAsyncTask extends AsyncTask<Void, Void, Boolean> {
    Mail m;
    RegistroReservaActivity activity;

    public SendEmailAsyncTask() {
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            if (m.send()) {
                activity.displayMessage("Email sent.");
            } else {
                activity.displayMessage("Email failed to send.");
            }

            return true;
        } catch (AuthenticationFailedException e) {
            Log.e(SendEmailAsyncTask.class.getName(), "Bad account details");
            e.printStackTrace();
            activity.displayMessage("Authentication failed.");
            return false;
        } catch (MessagingException e) {
            Log.e(SendEmailAsyncTask.class.getName(), "Email failed");
            e.printStackTrace();
            activity.displayMessage("Email failed to send.");
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            activity.displayMessage("Unexpected error occured.");
            return false;
        }
    }
}


