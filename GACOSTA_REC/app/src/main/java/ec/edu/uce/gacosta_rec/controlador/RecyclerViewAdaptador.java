package ec.edu.uce.gacosta_rec.controlador;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.List;

import ec.edu.uce.gacosta_rec.R;
import ec.edu.uce.gacosta_rec.modelo.Vehiculo;
import ec.edu.uce.gacosta_rec.vista.ListaVehiculosActivity;
import ec.edu.uce.gacosta_rec.vista.RegistroVehiculoActivity;

public class RecyclerViewAdaptador extends RecyclerView.Adapter<RecyclerViewAdaptador.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
        private TextView placa;
        private TextView marca;
        private TextView fecha;
        private TextView costo;
        private TextView matriculado;
        private TextView color;
        private ImageView foto;
        private TextView estado;
        private TextView tipo;
        private Button modificar;
        private Button eliminar;

        Context context;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            placa = (TextView) itemView.findViewById(R.id.txt_placa);
            marca = (TextView) itemView.findViewById(R.id.txt_marca);
            fecha = (TextView) itemView.findViewById(R.id.txt_fecha_prestamo_reserva);
            costo = (TextView) itemView.findViewById(R.id.txt_costo);
            matriculado = (TextView) itemView.findViewById(R.id.txt_matricualdo);
            color = (TextView) itemView.findViewById(R.id.txt_color);
            foto = (ImageView) itemView.findViewById(R.id.imagen_vehiculo);
            estado = (TextView) itemView.findViewById(R.id.txt_estado);
            tipo = (TextView) itemView.findViewById(R.id.txt_tipo);
            modificar = (Button) itemView.findViewById(R.id.btn_modificar);
            eliminar = (Button) itemView.findViewById(R.id.btn_eliminar);
            itemView.setOnCreateContextMenuListener(this);

        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Elija la Accion");
            MenuItem editar = menu.add(Menu.NONE, 1, 1, "Modificar");
            MenuItem eliminar = menu.add(Menu.NONE, 2, 2, "Eliminar");
            editar.setOnMenuItemClickListener(onEditMenu);
            eliminar.setOnMenuItemClickListener(onEditMenu);
        }

        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent siguiente;
                switch (item.getItemId()) {
                    case 1:
                        RegistroVehiculoActivity.modificacion = true;
                        siguiente = new Intent(context, RegistroVehiculoActivity.class);
                        siguiente.putExtra("valorPlaca", placa.getText().toString().substring(7, 15));
                        context.startActivity(siguiente);
                        ((ListaVehiculosActivity) context).finish();
                        break;

                    case 2:
                        Vehiculo ve= new Vehiculo() ;
                        ImplementacionVehiculoCRUD crudVehiculo = new ImplementacionVehiculoCRUD(context);
                        ve=(Vehiculo) crudVehiculo.buscarPorParametro(placa.getText().toString().substring(7, 15));
                        if (ve.getEstado()== true){
                            Toast.makeText(context, "ERROR, VEHICULO RESERVADO", Toast.LENGTH_SHORT).show();
                            break;
                        }else{


                        crudVehiculo.borrar(placa.getText().toString().substring(7, 15));
                        siguiente = new Intent(context, ListaVehiculosActivity.class);
                        context.startActivity(siguiente);
                        ((ListaVehiculosActivity) context).finish();
                        break;
                        }
                }
                return true;
            }
        };


        void setOnClickListener() {
            modificar.setOnClickListener(this);
            eliminar.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent siguiente;
            switch (v.getId()) {
                case R.id.btn_modificar:
                    RegistroVehiculoActivity.modificacion = true;
                    siguiente = new Intent(context, RegistroVehiculoActivity.class);
                    siguiente.putExtra("valorPlaca", placa.getText().toString().substring(7, 15));
                    context.startActivity(siguiente);
                    ((ListaVehiculosActivity) context).finish();
                    break;
                case R.id.btn_eliminar:
                    ImplementacionVehiculoCRUD crudVehiculo = new ImplementacionVehiculoCRUD(context);
                    crudVehiculo.borrar(placa.getText().toString().substring(7, 15));
                    siguiente = new Intent(context, ListaVehiculosActivity.class);
                    context.startActivity(siguiente);
                    ((ListaVehiculosActivity) context).finish();
                    break;
            }
        }


    }

    public static List<Vehiculo> vehiculos;

    public RecyclerViewAdaptador(List<Vehiculo> vehiculos) {
        this.vehiculos = vehiculos;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_vehiculo, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.placa.setText("Placa: " + vehiculos.get(i).getPlaca());
        viewHolder.marca.setText("Marca: " + vehiculos.get(i).getMarca());
        viewHolder.fecha.setText("Fecha fabricacion: " + new SimpleDateFormat("dd-MM-yyyy").format(vehiculos.get(i).getFechaFabricacion()));
        viewHolder.costo.setText("Costo: " + vehiculos.get(i).getCosto());
        if (vehiculos.get(i).getMatriculado() == true) {
            viewHolder.matriculado.setText("Matriculado: si");
        } else {
            viewHolder.matriculado.setText("Matriculado: no");
        }

        viewHolder.color.setText("Color: " + vehiculos.get(i).getColor());

        byte[] fotoBase = vehiculos.get(i).getFoto();
        Bitmap bitmap = BitmapFactory.decodeByteArray(fotoBase, 0, fotoBase.length);
        viewHolder.foto.setImageBitmap(bitmap);

        if (vehiculos.get(i).getEstado() == true) {
            viewHolder.estado.setText("Estado: Disponible");
        } else {
            viewHolder.estado.setText("Estado: No Disponible");
        }

        viewHolder.tipo.setText("Tipo: "+ vehiculos.get(i).getTipo());
        viewHolder.setOnClickListener();

    }

    @Override
    public int getItemCount() {
        return vehiculos.size();
    }

}
