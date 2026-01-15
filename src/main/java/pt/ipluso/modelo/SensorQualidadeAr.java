package pt.ipluso.modelo;
import java.util.Random;

public class SensorQualidadeAr extends SensorAbstrato {
    private final Random random = new Random();

    public SensorQualidadeAr(String localizacao) { super(localizacao); }

    @Override
    public DadosSensor lerDados() { return gerarDadosEspecificos(); }

    @Override
    public DadosSensor gerarDadosEspecificos() {
        double valor;
        String alerta = "OK";

        if (random.nextInt(100) < 5) {
            valor = -5.0; // Erro (negativo)
            alerta = "ERRO_SENSOR";
        } else {
            valor = 10 + (100 - 10) * random.nextDouble();
            if (valor > 50.0) alerta = "ALERTA";
        }
        return new DadosSensor(valor, "AQI", alerta, System.currentTimeMillis());
    }
}