package br.com.fiap.historicoapi.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class FormatadorData {

    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");

    private FormatadorData() {}

    public static String formatar(LocalDate data) {
        return data == null ? null : data.format(DATA);
    }

    public static String formatar(LocalDateTime dataHora) {
        return dataHora == null ? null : dataHora.format(DATA_HORA);
    }
}