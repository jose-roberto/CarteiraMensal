package modelo;

import java.util.Date;

public class Event {
    private int id;
    private String nome;
    private double valor;
    private Date dataCadastro, dataLimite, dataOcorreu;
    private String caminhoFoto;

    public Event(int id, String nome, double valor, String caminhoFoto, Date dataOcorreu, Date dataCadastro, Date dataLimite) {
        this.id = id;
        this.nome = nome;
        this.valor = valor;
        this.caminhoFoto = caminhoFoto;
        this.dataCadastro = dataCadastro;
        this.dataLimite = dataLimite;
        this.dataOcorreu = dataOcorreu;
    }

    public Event(String nome, double valor, String caminhoFoto, Date dataOcorreu, Date dataCadastro, Date dataLimite) {
        this.nome = nome;
        this.valor = valor;
        this.caminhoFoto = caminhoFoto;
        this.dataCadastro = dataCadastro;
        this.dataLimite = dataLimite;
        this.dataOcorreu = dataOcorreu;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public Date getDataLimite() {
        return dataLimite;
    }

    public void setDataLimite(Date dataLimite) {
        this.dataLimite = dataLimite;
    }

    public Date getDataOcorreu() {
        return dataOcorreu;
    }

    public void setDataOcorreu(Date dataOcorreu) {
        this.dataOcorreu = dataOcorreu;
    }

    public String getCaminhoFoto() {
        return caminhoFoto;
    }

    public void setCaminhoFoto(String caminhoFoto) {
        this.caminhoFoto = caminhoFoto;
    }
}
