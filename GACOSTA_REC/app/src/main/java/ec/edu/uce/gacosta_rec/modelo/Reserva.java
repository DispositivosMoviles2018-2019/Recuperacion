package ec.edu.uce.gacosta_rec.modelo;

import java.io.Serializable;
import java.util.Date;

public class Reserva implements Serializable {
    private String placa;
    private Integer numeroReserva;
    private String email;
    private String celular;
    private Date fechaPrestamo;
    private Date fechaEntrega;
    private Integer valor;

    public Reserva() {
    }

    public Reserva(String placa, Integer numeroReserva, String email, String celular, Date fechaPrestamo, Date fechaEntrega, Integer valor) {
        this.placa = placa;
        this.numeroReserva = numeroReserva;
        this.email = email;
        this.celular = celular;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaEntrega = fechaEntrega;
        this.valor = valor;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public Integer getNumeroReserva() {
        return numeroReserva;
    }

    public void setNumeroReserva(Integer numeroReserva) {
        this.numeroReserva = numeroReserva;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public Date getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(Date fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public Date getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(Date fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public Integer getValor() {
        return valor;
    }

    public void setValor(Integer valor) {
        this.valor = valor;
    }
}
