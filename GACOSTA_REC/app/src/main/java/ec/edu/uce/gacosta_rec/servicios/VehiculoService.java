package ec.edu.uce.gacosta_rec.servicios;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import ec.edu.uce.gacosta_rec.modelo.Vehiculo;

public interface VehiculoService {
    String API_ROUTE = "/post";

    @GET(API_ROUTE)
    Call<List<Vehiculo>> getPost();
}
