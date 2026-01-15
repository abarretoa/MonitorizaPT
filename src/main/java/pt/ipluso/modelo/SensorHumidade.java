package pt.ipluso.modelo;
import java.util.Random;

public class SensorHumidade extends SensorAbstrato {
    private final Random random = new Random();

    public SensorHumidade(String localizacao) { super(localizacao); }

    @Override
    public DadosSensor lerDados() { return gerarDadosEspecificos(); }

    @Override
    public DadosSensor gerarDadosEspecificos() {
        double valor = 30 + (90 - 30) * random.nextDouble();
        String alerta = (valor > 80.0) ? "ALERTA" : "OK";
        return new DadosSensor(valor, "%", alerta, System.currentTimeMillis());
    }
}