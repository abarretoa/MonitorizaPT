package pt.ipluso.modelo;
import java.util.Random;

public class SensorTemperatura extends SensorAbstrato {
    private final Random random = new Random();

    public SensorTemperatura(String localizacao) { super(localizacao); }

    @Override
    public DadosSensor lerDados() { return gerarDadosEspecificos(); }

    @Override
    public DadosSensor gerarDadosEspecificos() {
        double valor;
        String alerta = "OK"; // Padrão visual para a tabela

        // Simulação de erro: 5% das vezes gera um valor impossível (-45ºC)
        // para testar a robustez do sistema, conforme pedido no projeto.
        if (random.nextInt(100) < 5) {
            valor = -45.0;
            alerta = "ERRO_SENSOR";
        } else {
            valor = 15 + (35 - 15) * random.nextDouble();
            if (valor > 30.0) alerta = "ALERTA";
        }
        return new DadosSensor(valor, "Celsius", alerta, System.currentTimeMillis());
    }
}