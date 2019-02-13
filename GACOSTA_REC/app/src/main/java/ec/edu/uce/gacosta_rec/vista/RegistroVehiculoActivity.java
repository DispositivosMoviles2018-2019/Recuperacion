package ec.edu.uce.gacosta_rec.vista;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ec.edu.uce.gacosta_rec.controlador.ImplementacionVehiculoCRUD;
import ec.edu.uce.gacosta_rec.R;
import ec.edu.uce.gacosta_rec.modelo.Vehiculo;

public class RegistroVehiculoActivity extends AppCompatActivity {

    private EditText placa;
    private EditText marca;
    private EditText costo;
    private Switch matriculado;
    private Spinner color;
    String[] opcionesColor = {"Negro", "Blanco", "Otro"};
    ArrayAdapter<String> adapterColor;
    public static Boolean modificacion = false;
    private Spinner tipo;
    String[] opcionesTipo = {"Automovil", "Camioneta", "Furgoneta", "Avion"};
    ArrayAdapter<String> adapterTipo;

    private ImageView foto;
    private Button tomarFoto;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    Bitmap imageBitmap;
    private final int SELECT_PICTURE = 300;


    Uri imageUri;


    private DatePickerDialog.OnDateSetListener fechaElegir;
    private Date fechaAux = new Date();
    private TextView fecha;

    ImplementacionVehiculoCRUD crudVehiculo = new ImplementacionVehiculoCRUD(this);

    Vehiculo vehiculoModificado;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_vehiculo);

        placa = (EditText) findViewById(R.id.txt_placa_vehiculo);
        marca = (EditText) findViewById(R.id.txt_marca_vehiculo);
        costo = (EditText) findViewById(R.id.txt_costo_vehiculo);
        matriculado = (Switch) findViewById(R.id.switch_matriculado);

        color = (Spinner) findViewById(R.id.spinner_color);
        adapterColor = new ArrayAdapter<String>(this, R.layout.spinner_item_color, opcionesColor);//poner el texto dentro del menu desplegable


        color.setAdapter(adapterColor);

        tipo = (Spinner) findViewById(R.id.spinner_tipo);
        adapterTipo = new ArrayAdapter<String>(this, R.layout.spinner_item_tipo, opcionesTipo);//poner el texto dentro del menu desplegable


        tipo.setAdapter(adapterTipo);

        foto = (ImageView) findViewById(R.id.imagen_auto);
        tomarFoto = (Button) findViewById(R.id.btn_tomar_foto);
        tomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verOpciones();
            }
        });

        fecha = (TextView) findViewById(R.id.txt_fecha_prestamo_reserva);

        fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar fechaHoy = Calendar.getInstance();
                int year = fechaHoy.get(Calendar.YEAR);
                int month = fechaHoy.get(Calendar.MONTH);
                int day = fechaHoy.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(RegistroVehiculoActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, fechaElegir, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        fechaElegir = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = dayOfMonth + "/" + month + "/" + year;
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                fechaAux.setTime(calendar.getTimeInMillis());

                fecha.setText("Fecha de Fabricacion: " + date);
            }
        };

        vehiculoModificado = new Vehiculo();

        if (modificacion == true) {
            String placa = getIntent().getStringExtra("valorPlaca");
            //System.out.println(placa);
            llenarParaModificar(placa);
        }
    }

    public void llenarParaModificar(String placa) {


        //List<Vehiculo> vehiculos = MainActivity.vehiculos;
        //for (Vehiculo v : vehiculos) {
        Vehiculo v = (Vehiculo) crudVehiculo.buscarPorParametro(placa);

        //if (v.getPlaca().equals(placa)) {
        this.placa.setFocusable(false);
        this.placa.setText(v.getPlaca());
        this.marca.setText(v.getMarca());
        this.fechaAux = v.getFechaFabricacion();
        this.fecha.setText("Fecha de fabricacion: " + sdf.format(this.fechaAux).toString());
        this.costo.setText(v.getCosto().toString());
        if (v.getMatriculado() == true) {
            this.matriculado.setChecked(true);
        } else {
            this.matriculado.setChecked(false);
        }
        if (v.getColor() != null) {
            int spinnerPosition = adapterColor.getPosition(v.getColor());
            color.setSelection(spinnerPosition);
        }

        byte[] fotoBase = v.getFoto();
        Bitmap bitmap = BitmapFactory.decodeByteArray(fotoBase, 0, fotoBase.length);
        imageBitmap = bitmap;
        this.foto.setImageBitmap(bitmap);
        //FALTA EL ESTADO
        if (v.getTipo() != null) {
            int spinnerPosition = adapterTipo.getPosition(v.getTipo());
            tipo.setSelection(spinnerPosition);
        }

    }

    public void guardar(View view) throws IOException {

        Vehiculo vehiculo = new Vehiculo();
        if (!modificacion) {
            if (this.placa.getText().toString().isEmpty()) {
                Toast.makeText(this, "Campo Placa vacio", Toast.LENGTH_SHORT).show();
            } else {
                this.placa.setText(this.placa.getText().toString().toUpperCase());
                System.out.println(this.placa.getText().toString());

                Pattern pattern1 = Pattern.compile("^[A-Z]{3}-\\d{4}$");
                Matcher matcher1 = pattern1.matcher(this.placa.getText().toString());
                Boolean repetido = false;
                if (matcher1.matches()) {

                    if (crudVehiculo.buscarPorParametro(this.placa.getText().toString()) == null) {
                        if (this.marca.getText().toString().isEmpty()) {
                            Toast.makeText(this, "Campo Marca vacio", Toast.LENGTH_SHORT).show();
                        } else {
                            if (this.costo.getText().toString().isEmpty()) {
                                Toast.makeText(this, "Campo Costo vacio", Toast.LENGTH_SHORT).show();
                            } else {

                                if (foto.getDrawable() == null) {
                                    Toast.makeText(this, "Debe insertar una imagen", Toast.LENGTH_SHORT).show();
                                } else {

                                    String placa = this.placa.getText().toString();
                                    String marca = this.marca.getText().toString();
                                    String costo = this.costo.getText().toString();

                                    vehiculo.setPlaca(placa);
                                    vehiculo.setMarca(marca);
                                    vehiculo.setFechaFabricacion(fechaAux);
                                    vehiculo.setCosto(Double.parseDouble(costo));

                                    Boolean matriculado;
                                    if (this.matriculado.isChecked() == true) {
                                        matriculado = true;
                                    } else {
                                        matriculado = false;
                                    }
                                    vehiculo.setMatriculado(matriculado);

                                    vehiculo.setColor(color.getSelectedItem().toString());

                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    if (imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream) == true) {
                                        byte[] byteArray = stream.toByteArray();
                                        imageBitmap.recycle();
                                        vehiculo.setFoto(byteArray);
                                    }

                                    vehiculo.setEstado(true);
                                    vehiculo.setTipo(tipo.getSelectedItem().toString());


                                    if (crudVehiculo.crear(vehiculo).equalsIgnoreCase("Registro exitoso")) {
                                        Intent siguiente;
                                        siguiente = new
                                                Intent(this, ListaVehiculosActivity.class);
                                        startActivity(siguiente);
                                        finish();
                                    }

                                }
                            }
                        }
                    } else {
                        Toast.makeText(this, "Vehiculo ya existente ingrese otra placa", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, "Formato de placa incorrecto tipo de formmato: FRT-8907", Toast.LENGTH_LONG).show();
                }
            }
        } else {

            Boolean matriculado;
            if (this.matriculado.isChecked() == true) {
                matriculado = true;
            } else {
                matriculado = false;
            }

            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            if (imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream) == true) {
                byte[] byteArray = stream.toByteArray();
                imageBitmap.recycle();
                vehiculoModificado.setPlaca(this.placa.getText().toString());
                vehiculoModificado.setMarca(this.marca.getText().toString());

                Calendar calendar = Calendar.getInstance();
                calendar.set(Integer.parseInt(this.fecha.getText().toString().substring(28, this.fecha.length())), Integer.parseInt(this.fecha.getText().toString().substring(25, 26)), Integer.parseInt(this.fecha.getText().toString().substring(22, 23)));
                fechaAux.setTime(calendar.getTimeInMillis());
                vehiculoModificado.setFechaFabricacion(fechaAux);

                vehiculoModificado.setCosto(Double.parseDouble(this.costo.getText().toString()));
                vehiculoModificado.setMatriculado(matriculado);
                vehiculoModificado.setColor(this.color.getSelectedItem().toString());
                vehiculoModificado.setEstado(true);
                vehiculoModificado.setTipo(this.tipo.getSelectedItem().toString());
                vehiculoModificado.setFoto(byteArray);

                if (crudVehiculo.actualizar(vehiculoModificado).equalsIgnoreCase("Actualizacion exitoso")) {
                    this.placa.setText("");
                    this.marca.setText("");
                    this.costo.setText("");
                    this.placa.setText("");
                    this.matriculado.setChecked(false);
                    Intent siguiente;
                    siguiente = new
                            Intent(this, ListaVehiculosActivity.class);
                    startActivity(siguiente);
                    finish();
                }
            }
        }

    }

    private void verOpciones() {
        final CharSequence[] option = {"Tomar foto", "Elegir de galeria", "Cancelar"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(RegistroVehiculoActivity.this);
        builder.setTitle("Eleige una opción");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (option[which] == "Tomar foto") {
                    abrirCamara();
                } else if (option[which] == "Elegir de galeria") {
                    Intent intent = new Intent(Intent.ACTION_PICK/*lanzar una actividad para elegir opciones*/, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");//muestra todas las imagenes de cualquier opcion
                    startActivityForResult(intent.createChooser(intent, "Selecciona app de imagen"), SELECT_PICTURE);
                } else {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void abrirCamara() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == SELECT_PICTURE) {
            imageUri = data.getData();
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                foto.setImageBitmap(imageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            foto.setImageBitmap(imageBitmap);
        }
    }

}
