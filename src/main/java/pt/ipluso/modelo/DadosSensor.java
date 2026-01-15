package pt.ipluso.modelo;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public record DadosSensor(double valor, String unidade, String alerta, long timestamp) {


    public String getTimestampFormatado() {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.of("Europe/Lisbon"))
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    }

    @Override
    public String toString() {
        return String.format("%.2f %s (%s)", valor, unidade, alerta);
    }
}
