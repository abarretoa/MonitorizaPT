package pt.ipluso.modelo;

public interface SensorListener {
    void onNovosDados(Sensor sensor, DadosSensor dados);
}