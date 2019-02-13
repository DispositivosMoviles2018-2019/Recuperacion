package ec.edu.uce.gacosta_rec.vista;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
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
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.DateConverter;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import harmony.java.awt.Color;
import ec.edu.uce.gacosta_rec.controlador.ImplementacionReservaCRUD;
import ec.edu.uce.gacosta_rec.controlador.ImplementacionUsuarioCRUD;
import ec.edu.uce.gacosta_rec.controlador.ImplementacionVehiculoCRUD;
import ec.edu.uce.gacosta_rec.controlador.RecyclerViewAdaptador;
import ec.edu.uce.gacosta_rec.R;
import ec.edu.uce.gacosta_rec.modelo.Reserva;
import ec.edu.uce.gacosta_rec.modelo.Usuario;
import ec.edu.uce.gacosta_rec.modelo.Vehiculo;

public class ListaVehiculosActivity extends AppCompatActivity {

    private RecyclerView reciclerViewVehiculo;
    private RecyclerViewAdaptador adaptadorVehiculo;
    private Toolbar toolbar;
    private final static String ETIQUETA_ERROR = "ERROR";
    private final static String NOMBRE_DIRECTORIO = "MiPdf";
    public List<Vehiculo> vehiculos;
    public List<Reserva> reservas;
    public List<Usuario> usuarios;
    ImplementacionVehiculoCRUD crudVehiculo;
    ImplementacionReservaCRUD crudReserva;
    ImplementacionUsuarioCRUD crudUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_vehiculos);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        reciclerViewVehiculo = (RecyclerView) findViewById(R.id.recycler_vehiculo);
        reciclerViewVehiculo.setLayoutManager(new LinearLayoutManager(this));

        ImplementacionVehiculoCRUD crudVehiculo = new ImplementacionVehiculoCRUD(this);

        List<Vehiculo> listaReciclerView = crudVehiculo.listar();
        Collections.sort(listaReciclerView, new CompararVehiculo("placa"));
        adaptadorVehiculo = new RecyclerViewAdaptador(listaReciclerView);
        reciclerViewVehiculo.setAdapter(adaptadorVehiculo);
    }

    public void nuevo(View view) {
        Intent siguiente;
        siguiente = new Intent(this, RegistroVehiculoActivity.class);
        startActivity(siguiente);
        finish();
    }

    public void buscar(View view) {
        Intent siguiente;
        siguiente = new Intent(this, BusquedaAutoActivity.class);
        startActivity(siguiente);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        SharedPreferences prefs =
                getSharedPreferences("MisPreferencias",Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("email", "modificado@email.com");
        editor.putString("nombre", "Prueba");
        editor.commit();




        switch (item.getItemId()) {
            case R.id.persistirXML:
                try {
                    persistirXML();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.persistirJSON:
                try {
                    persistirJSON();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.regresar:
                Intent siguiente;
                siguiente = new Intent(this, MenuOpcionesActivity.class);
                this.startActivity(siguiente);
                finish();
                break;
            case R.id.ordenar_ascendente:
                Vehiculo.setAscendente(true);
                siguiente = new Intent(this, ListaVehiculosActivity.class);
                this.startActivity(siguiente);
                finish();
                break;
            case R.id.ordenar_descendente:
                Vehiculo.setAscendente(false);
                siguiente = new Intent(this, ListaVehiculosActivity.class);
                this.startActivity(siguiente);
                finish();
                break;
            case R.id.exportar_pdf:
                descargar();
                break;
        }
        return true;
    }

    public void persistirXML() throws IOException {
        vehiculos = new ArrayList();
        reservas = new ArrayList();
        usuarios = new ArrayList();

        crudVehiculo = new ImplementacionVehiculoCRUD(this);
        crudReserva = new ImplementacionReservaCRUD(this);
        crudUsuario = new ImplementacionUsuarioCRUD(this);

        vehiculos = (List<Vehiculo>)crudVehiculo.listar();
        reservas =(List<Reserva>) crudReserva.listar();
        usuarios =(List<Usuario>) crudUsuario.listar();


        Date date = new Date();
        DateFormat hourdateFormat = new SimpleDateFormat("HH:mm:ssdd-MM-yyyy");

        File file;
        File localFile = new File(Environment.getExternalStorageDirectory() + "/archivos/");
        file = new File(localFile, hourdateFormat.format(date) + "vehiculos.txt");
        file.delete();
        XStream xs = new XStream(new DomDriver());
        final String[] PATRONES = new String[]{"dd-MMM-yyyy",
                "dd-MMM-yy",
                "yyyy-MMM-dd",
                "yyyy-MM-dd",
                "yyyy-dd-MM",
                "yyyy/MM/dd",
                "yyyy.MM.dd",
                "MM-dd-yy",
                "dd-MM-yyyy"};
        DateConverter dateConverter = new DateConverter("dd/MM/yyyy", PATRONES);//pone todas las fecha que encuentre en un solo formato

        xs.registerConverter(dateConverter);
        xs.alias("vehiculos", List.class);//pone autos enbes de list en el xml
        xs.alias("vehiculo", Vehiculo.class);//pone sola auto enbes de pones todo el paquete
        String xml = xs.toXML(vehiculos);

        FileWriter escribir = new FileWriter(file, true);
        escribir.write("");
        escribir.write(xml);
        escribir.close();

        File fileReserva;
        File localFileReserva = new File(Environment.getExternalStorageDirectory() + "/archivos/");
        fileReserva = new File(localFileReserva, hourdateFormat.format(date) + "reservas.txt");
        fileReserva.delete();

        dateConverter = new DateConverter("dd/MM/yyyy", PATRONES);//pone todas las fecha que encuentre en un solo formato

        xs.registerConverter(dateConverter);
        xs.alias("reservas", List.class);//pone autos enbes de list en el xml
        xs.alias("reserva", Reserva.class);//pone sola auto enbes de pones todo el paquete
        String xmlReserva = xs.toXML(reservas);

        FileWriter escribirReserva = new FileWriter(fileReserva, true);
        escribirReserva.write("");
        escribirReserva.write(xmlReserva);
        escribirReserva.close();

        File fileUsuario;
        File localFileUsuario = new File(Environment.getExternalStorageDirectory() + "/archivos/");
        fileUsuario = new File(localFileUsuario, hourdateFormat.format(date) + "usuarios.txt");
        fileUsuario.delete();

        dateConverter = new DateConverter("dd/MM/yyyy", PATRONES);//pone todas las fecha que encuentre en un solo formato

        xs.registerConverter(dateConverter);
        xs.alias("usuarios", List.class);//pone autos enbes de list en el xml
        xs.alias("usuario", Usuario.class);//pone sola auto enbes de pones todo el paquete
        String xmlUsuario = xs.toXML(usuarios);

        FileWriter escribirUsuario = new FileWriter(fileUsuario, true);
        escribirUsuario.write("");
        escribirUsuario.write(xmlUsuario);
        escribirUsuario.close();
    }

    public void persistirJSON() throws IOException {
        vehiculos = new ArrayList();
        reservas = new ArrayList();
        usuarios = new ArrayList();

        crudVehiculo = new ImplementacionVehiculoCRUD(this);
        crudReserva = new ImplementacionReservaCRUD(this);
        crudUsuario = new ImplementacionUsuarioCRUD(this);

        vehiculos = (List<Vehiculo>)crudVehiculo.listar();
        reservas =(List<Reserva>) crudReserva.listar();
        usuarios =(List<Usuario>) crudUsuario.listar();


        Date date = new Date();
        DateFormat hourdateFormat = new SimpleDateFormat("HH:mm:ssdd-MM-yyyy");

        File file;
        File localFile = new File(Environment.getExternalStorageDirectory() + "/archivos/");
        file = new File(localFile, hourdateFormat.format(date) + "vehiculos.txt");
        file.delete();
        Gson gson = new Gson();
        String json = gson.toJson(vehiculos);
        FileWriter escribir = new FileWriter(file, true);
        escribir.write("");
        escribir.write(json);
        escribir.close();

        File file2;
        File localFile2 = new File(Environment.getExternalStorageDirectory() + "/archivos/");
        file2 = new File(localFile2, hourdateFormat.format(date) + "reservas.txt");
        file2.delete();
        Gson gson2 = new Gson();
        String json2 = gson2.toJson(reservas);
        FileWriter escribir2 = new FileWriter(file2, true);
        escribir2.write("");
        escribir2.write(json2);
        escribir2.close();

        File file3;
        File localFile3 = new File(Environment.getExternalStorageDirectory() + "/archivos/");
        file3 = new File(localFile3, hourdateFormat.format(date) + "usuarios.txt");
        file3.delete();
        Gson gson3 = new Gson();
        String json3 = gson3.toJson(usuarios);
        FileWriter escribir3 = new FileWriter(file3, true);
        escribir3.write("");
        escribir3.write(json3);
        escribir3.close();
    }
    class CompararVehiculo implements Comparator<Vehiculo> {//crear un metodo dinamico para comparar sin modificar el POJO

        String parametro;

        public CompararVehiculo() {
        }

        public CompararVehiculo(String parametro) {
            this.parametro=parametro;
        }

        @Override
        public int compare(Vehiculo vehiculo, Vehiculo vehiculo1) {
            int resultado = 0;

            if(parametro.equals("placa")){
                resultado = vehiculo.getPlaca().compareTo(vehiculo1.getPlaca());
            }
            else {
                resultado = vehiculo.getPlaca().compareTo(vehiculo1.getPlaca());
            }
            return resultado * ((Vehiculo.getAscendente()) ? 1 : -1);//implementacion del metodo estatico para ascendente y descendente
        }
    }
    public void descargar() {
        Document documento = new Document();

        try {

            File f = crearFichero("ListaAutos" + ".pdf");

            // Creamos el flujo de datos de salida para el fichero donde
            // guardaremos el pdf.
            FileOutputStream ficheroPdf = new FileOutputStream(
                    f.getAbsolutePath());

            // Asociamos el flujo que acabamos de crear al documento.
            PdfWriter writer = PdfWriter.getInstance(documento, ficheroPdf);

            // Incluimos el pie de pagina y una cabecera
            HeaderFooter cabecera = new HeaderFooter(new Phrase(
                    "Lista de Vehiculos     "), false);
            HeaderFooter pie = new HeaderFooter(new Phrase(
                    "Optativa III"), false);

            documento.setHeader(cabecera);
            documento.setFooter(pie);

            // Abrimos el documento.
            documento.open();

            // Añadimos un titulo con la fuente por defecto.

            Font font = FontFactory.getFont(FontFactory.HELVETICA, 28,
                    Font.BOLD, Color.RED);
            documento.add(new Paragraph("Vehiculos", font));
            documento.add(new Paragraph("    ", font));


            ImplementacionVehiculoCRUD crudVehiculo = new ImplementacionVehiculoCRUD(this);
            List<Vehiculo> vehiculos = crudVehiculo.listar();
            // Insertamos una tabla.
            PdfPTable tabla1 = new PdfPTable(9);

            tabla1.addCell("Placa Vehiculo ");
            tabla1.addCell("Marca ");
            tabla1.addCell("Fecha Fabricacion ");
            tabla1.addCell("Costo ");
            tabla1.addCell("Matriculado ");
            tabla1.addCell("Color ");
            tabla1.addCell("Estado ");
            tabla1.addCell("Tipo ");
            tabla1.addCell("Foto ");
            documento.add(tabla1);
            for (Vehiculo v : vehiculos) {
                PdfPTable tabla = new PdfPTable(9);
                tabla.addCell(v.getPlaca());
                tabla.addCell(v.getMarca());
                tabla.addCell(new SimpleDateFormat("yyyy/MM/dd").format(v.getFechaFabricacion()));
                tabla.addCell(v.getCosto().toString());
                if(v.getMatriculado()==true){
                tabla.addCell("Matriculado");
                }
                else{
                    tabla.addCell("No Matriculado");
                }
                tabla.addCell(v.getColor());
                if(v.getEstado()==true){
                    tabla.addCell("Reservado");
                }
                else{
                    tabla.addCell("No Reservado");
                }

                tabla.addCell(v.getTipo());

                Bitmap bitmap = BitmapFactory.decodeByteArray(v.getFoto(),0,v.getFoto().length);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                Image imagen = Image.getInstance(stream.toByteArray());
                tabla.addCell(imagen);
                documento.add(tabla);

            }



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

