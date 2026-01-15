package pt.ipluso.modelo;

public interface Sensor {
    String getIDUnico();
    DadosSensor lerDados();
    void publicarMQTT(String json);
    void processarComando(String comandoJSON);
    String getOwner();
}