package ec.edu.uce.gacosta_rec.vista;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

import harmony.java.awt.Color;
import ec.edu.uce.gacosta_rec.controlador.AdminSQLiteOpenHelper;
import ec.edu.uce.gacosta_rec.controlador.ImplementacionReservaCRUD;
import ec.edu.uce.gacosta_rec.R;
import ec.edu.uce.gacosta_rec.modelo.Reserva;

public class BusquedaReservaActivity extends AppCompatActivity {

    private final static String NOMBRE_DIRECTORIO = "MiPdf";
    //private final static String NOMBRE_DOCUMENTO = "prueba.pdf";
    private final static String ETIQUETA_ERROR = "ERROR";

    private TextView numero;
    private TextView placa;
    private TextView email;
    private TextView celular;
    private TextView fechaReserva;
    private TextView fechaEntrega;
    private TextView costo;
    private ImageView foto;
    Bitmap bitmap;
    //private Button descargar;

    ImplementacionReservaCRUD crudReserva;
    Reserva reserva;
    AdminSQLiteOpenHelper admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda_reserva);

        crudReserva = new ImplementacionReservaCRUD(this);
        reserva = new Reserva();
        if(getIntent().getStringExtra("valorPlaca") == null){
            reserva = (Reserva) crudReserva.buscarPorParametro(Integer.parseInt(getIntent().getStringExtra("valorReserva")));
        }else {
            reserva = (Reserva) crudReserva.buscarPorParametro(getIntent().getStringExtra("valorPlaca"));
        }
        numero = (TextView) findViewById(R.id.txt_busquedar_numero);
        numero.setText("Numero de reserva: " + String.valueOf(reserva.getNumeroReserva()));
        placa = (TextView) findViewById(R.id.txt_busquedar_placa);
        placa.setText("Placa: " + reserva.getPlaca());
        email = (TextView) findViewById(R.id.txt_busquedar_email);
        email.setText("Email: " + reserva.getEmail());
        celular = (TextView) findViewById(R.id.txt_busquedar_celular);
        celular.setText("Celular: " + reserva.getCelular());
        fechaReserva = (TextView) findViewById(R.id.txt_busquedar_fecha_prestamo);
        fechaReserva.setText("Fecha reserva: " + new SimpleDateFormat("dd-MM-yyyy").format(reserva.getFechaPrestamo()));
        fechaEntrega = (TextView) findViewById(R.id.txt_busquedar_fecha_entrega);
        fechaEntrega.setText("Fecha entrega: " + new SimpleDateFormat("dd-MM-yyyy").format(reserva.getFechaEntrega()));
        costo = (TextView) findViewById(R.id.txt_busquedar_costo);
        costo.setText("Valor: " + String.valueOf(reserva.getValor()));

        foto = (ImageView) findViewById(R.id.foto_busquedar);
        byte[] imagen;
        admin = new AdminSQLiteOpenHelper(this);
        SQLiteDatabase baseDeDatos = admin.getWritableDatabase();
        Cursor filaVehiculo = baseDeDatos.rawQuery
                ("select foto from vehiculo where placa ='" + reserva.getPlaca().toUpperCase() + "'", null);
        if (filaVehiculo.moveToFirst()) {//revisa si la consulta si tiene valores
            imagen = filaVehiculo.getBlob(0);
            bitmap = BitmapFactory.decodeByteArray(imagen, 0, imagen.length);
            foto.setImageBitmap(bitmap);
            baseDeDatos.close();
        } else {
            baseDeDatos.close();
        }

        //descargar = (Button) findViewById(R.id.btn_descargar);
    }

    public void regresar(View view) {
        Intent siguiente;
        siguiente = new Intent(this, ValidacionDisponibilidadActivity.class);
        this.startActivity(siguiente);
        finish();
    }

    public void descargar(View view) {
        Document documento = new Document();

        try {

            File f = crearFichero(numero.getText().toString().substring(19, numero.getText().toString().length()) + ".pdf");

            // Creamos el flujo de datos de salida para el fichero donde
            // guardaremos el pdf.
            FileOutputStream ficheroPdf = new FileOutputStream(
                    f.getAbsolutePath());

            // Asociamos el flujo que acabamos de crear al documento.
            PdfWriter writer = PdfWriter.getInstance(documento, ficheroPdf);

            // Incluimos el pie de pagina y una cabecera
            HeaderFooter cabecera = new HeaderFooter(new Phrase(
                    "Reserva de Vehiculo     " + numero.getText().toString()), false);
            HeaderFooter pie = new HeaderFooter(new Phrase(
                    "Optativa III"), false);

            documento.setHeader(cabecera);
            documento.setFooter(pie);

            // Abrimos el documento.
            documento.open();

            // Añadimos un titulo con la fuente por defecto.

            Font font = FontFactory.getFont(FontFactory.HELVETICA, 28,
                    Font.BOLD, Color.RED);
            documento.add(new Paragraph("Reserva", font));

            Font font1 = FontFactory.getFont(FontFactory.HELVETICA, 28,
                    Font.BOLD, Color.RED);
            documento.add(new Paragraph("", font1));

            // Insertamos una tabla.
            PdfPTable tabla = new PdfPTable(7);
            tabla.addCell("Numero reserva ");
            tabla.addCell("Placa Vehiculo ");
            tabla.addCell("E-mail ");
            tabla.addCell("Celular ");
            tabla.addCell("Fecha de Reserva ");
            tabla.addCell("Fecha de entrega ");
            tabla.addCell("Costo ");
            tabla.addCell(numero.getText().toString().substring(19, numero.getText().toString().length()));
            tabla.addCell(placa.getText().toString().substring(7, placa.getText().toString().length()));
            tabla.addCell(email.getText().toString().substring(7, email.getText().toString().length()));
            tabla.addCell(celular.getText().toString().substring(9, celular.getText().toString().length()));
            tabla.addCell(fechaReserva.getText().toString().substring(15, fechaReserva.getText().toString().length()));
            tabla.addCell(fechaEntrega.getText().toString().substring(15, fechaEntrega.getText().toString().length()));
            tabla.addCell(costo.getText().toString().substring(7, costo.getText().toString().length()));
            documento.add(tabla);

            // Insertamos una imagen que se encuentra en los recursos de la
            // aplicacion.
            Font font2 = FontFactory.getFont(FontFactory.HELVETICA, 28,
                    Font.BOLD, Color.RED);
            documento.add(new Paragraph("", font2));

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            Image imagen = Image.getInstance(stream.toByteArray());
            documento.add(imagen);
            Toast.makeText(this, "Descargando PDF", Toast.LENGTH_LONG).show();
        } catch (DocumentException e) {

            Log.e(ETIQUETA_ERROR, e.getMessage());

        } catch (IOException e) {

            Log.e(ETIQUETA_ERROR, e.getMessage());

        } finally {
            // Cerramos el documento.
            documento.close();
        }
    }

    public static File crearFichero(String nombreFichero) throws IOException {
        File ruta = getRuta();
        File fichero = null;
        if (ruta != null)
            fichero = new File(ruta, nombreFichero);
        return fichero;
    }

    /**
     * Obtenemos la ruta donde vamos a almacenar el fichero.
     *
     * @return
     */
    public static File getRuta() {

        // El fichero sera almacenado en un directorio dentro del directorio
        // Descargas
        File ruta = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            ruta = new File(
                    Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    NOMBRE_DIRECTORIO);

            if (ruta != null) {
                if (!ruta.mkdirs()) {
                    if (!ruta.exists()) {
                        return null;
                    }
                }
            }
        } else {
        }

        return ruta;
    }
}
