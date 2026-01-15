package pt.ipluso.modelo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.eclipse.paho.client.mqttv3.*;
import pt.ipluso.rede.HashUtil;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class SensorAbstrato implements Sensor, Runnable {

    protected final String localizacaoFixa;
    protected boolean ativo = false;
    protected MqttClient client;
    protected Gson gson;
    protected SensorListener listener;


    private static final String BROKER_URL = "tcp://172.237.103.61:1883";

    public SensorAbstrato(String localizacaoFixa) {
        this.localizacaoFixa = localizacaoFixa;
        this.gson = new GsonBuilder().disableHtmlEscaping().create();
        conectarMQTT();
    }

    public void setListener(SensorListener listener) {
        this.listener = listener;
    }

    private void conectarMQTT() {
        try {

            client = new MqttClient(BROKER_URL, getIDUnico());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setAutomaticReconnect(true);


            try {
                client.connect(options);
                String topicoComandos = "envira/pt/sensores/comandos/" + localizacaoFixa.replace(" ", "_");
                client.subscribe(topicoComandos, (topic, msg) -> processarComando(new String(msg.getPayload())));
                System.out.println("Conectado: " + getIDUnico());
            } catch (MqttException e) {
                System.out.println("Modo Offline (Broker indisponível): " + e.getMessage());
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getIDUnico() {
        return "PT-SENSOR-" + localizacaoFixa.replace(" ", "_").toUpperCase();
    }

    @Override
    public String getOwner() {
        return "AlessandroBarreto_a22510719";
    }

    @Override

    public void publicarMQTT(String json) {

       //  System.out.println("JSON: " + json);

        if (client != null && client.isConnected()) {
            try {
                String topico = "envira/pt/sensores/dados/" + localizacaoFixa.replace(" ", "_");
                MqttMessage msg = new MqttMessage(json.getBytes());
                msg.setQos(1);
                client.publish(topico, msg);
            } catch (MqttException e) {

            }
        }
    }

    @Override
    public void processarComando(String comandoJSON) {

        if (comandoJSON.contains("ATIVAR")) this.ativo = true;
        if (comandoJSON.contains("DESATIVAR")) this.ativo = false;
    }

    protected String prepararJson(DadosSensor dados) {
        // Usei LinkedHashMap para garantir a ordem, mas principalmente porque
        // o enunciado pede chaves com espaços ("ID Unico") e isso não dá para fazer
        // com variáveis normais de uma classe Java.
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("campus", "IP Lisboa");
        map.put("sensor", getIDUnico());
        map.put("ID Unico", String.valueOf(System.currentTimeMillis()));
        map.put("Owner", getOwner());
        map.put("tipo", this.getClass().getSimpleName().replace("Sensor", "").toLowerCase());
        map.put("valor", arredondar(dados.valor()));
        map.put("unidade", dados.unidade());
        map.put("alerta", dados.alerta().equals("ALERTA"));
        map.put("timestamp", dados.timestamp());
        // Geração do Hash de Validação
        String jsonParcial = gson.toJson(map);
        // O enunciado diz para remover espaços antes de gerar o hash
        String jsonSemEspacos = jsonParcial.replace(" ", "");
        String hash = HashUtil.gerarHashSHA256(jsonSemEspacos);

        map.put("hash_validacao", hash);
        return gson.toJson(map);
    }

    public abstract DadosSensor gerarDadosEspecificos();

    @Override
    public void run() {
        while (true) {
            try {
                if (ativo) {
                        // ... lógica de envio ...
                    DadosSensor dados = gerarDadosEspecificos();
                    String json = prepararJson(dados);

                    publicarMQTT(json);

                    if (listener != null) {
                        listener.onNovosDados(this, dados);
                    }
                }
                // Requisito: Exatamente 3333ms
                Thread.sleep(3333);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
    private double arredondar(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}