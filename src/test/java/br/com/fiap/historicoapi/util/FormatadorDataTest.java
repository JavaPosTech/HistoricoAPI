package br.com.fiap.historicoapi.util;

import br.com.fiap.historicoapi.config.AbstractTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.time.LocalDateTime;

class FormatadorDataTest extends AbstractTest {

    @Test
    void formatarLocalDateTest() {
        var data = LocalDate.of(2026, 8, 29);
        Assertions.assertEquals("29/08/2026", FormatadorData.formatar(data));
    }

    @Test
    void formatarLocalDateComZeroAEsquerdaTest() {
        var data = LocalDate.of(2026, 1, 5);
        Assertions.assertEquals("05/01/2026", FormatadorData.formatar(data));
    }

    @Test
    void formatarLocalDateNullTest() {
        Assertions.assertNull(FormatadorData.formatar((LocalDate) null));
    }

    @Test
    void formatarLocalDateTimeTest() {
        var dataHora = LocalDateTime.of(2026, 8, 29, 14, 30, 45);
        Assertions.assertEquals("29/08/2026 - 14:30:45", FormatadorData.formatar(dataHora));
    }

    @Test
    void formatarLocalDateTimeLimitesDoDiaTest() {
        var meiaNoite = LocalDateTime.of(2026, 8, 29, 0, 0, 0);
        var fimDoDia = LocalDateTime.of(2026, 8, 29, 23, 59, 59);

        Assertions.assertEquals("29/08/2026 - 00:00:00", FormatadorData.formatar(meiaNoite));
        Assertions.assertEquals("29/08/2026 - 23:59:59", FormatadorData.formatar(fimDoDia));
    }

    @Test
    void formatarLocalDateTimeNullTest() {
        Assertions.assertNull(FormatadorData.formatar((LocalDateTime) null));
    }

    @Test
    void construtorPrivadoTest() throws Exception {
        Constructor<FormatadorData> construtor = FormatadorData.class.getDeclaredConstructor();
        Assertions.assertTrue(Modifier.isPrivate(construtor.getModifiers()));

        construtor.setAccessible(true);
        Assertions.assertNotNull(construtor.newInstance());
    }
}
