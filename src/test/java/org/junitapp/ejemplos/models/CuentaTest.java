package org.junitapp.ejemplos.models;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestReporter;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.DisabledOnJre;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junitapp.ejemplos.exceptions.DineroInsuficienteException;

public class CuentaTest {

    Cuenta cuenta;
    TestInfo testInfo;
    TestReporter testReporter;

    @BeforeAll
    static void beforeAll() {
        System.out.println("Incializando test");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("Finalizando test");
    }

    @BeforeEach
    void initMethodTest(TestInfo testInfo, TestReporter testReporter) {// Se puede usar en metodos Test y los metodos
                                                                       // before y after each
        this.testInfo = testInfo;
        this.testReporter = testReporter;
        // System.out.println("Ejecutando " + testInfo.getDisplayName());
        this.cuenta = new Cuenta("Andres", new BigDecimal("15233.22"));
    }

    @Tag("Prueba")
    @Test
    @Timeout(value = 1, unit = TimeUnit.SECONDS)
    @DisplayName("Probando el nombre de la cuenta corriente")
    void testNombreCuenta() throws InterruptedException {
        TimeUnit.SECONDS.sleep(2);
        // fail(); se usa para que falle a proposito
        if (testInfo.getTags().contains("Prueba")) {
            testReporter.publishEntry("Contiene tag prueba");
        }
        String esperado = "Andres";
        assertNotNull(cuenta, () -> "Cuenta igual a null");
        assertEquals(esperado, cuenta.getPersona(), () -> "La cuenta no es la esperada");
    }

    @Test
    @Disabled
    void testSaldoCuenta() {
        BigDecimal esperado = new BigDecimal("15233.22");
        assertNotNull(cuenta.getSaldo());
        assertEquals(esperado, cuenta.getSaldo());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
    }

    @Test
    void testReferenciaCuenta() {
        Cuenta cuenta = new Cuenta("Jhon Doe", new BigDecimal("8900.997"));
        Cuenta cuenta2 = new Cuenta("Jhon Doe", new BigDecimal("8900.997"));
        // assertNotEquals(cuenta,cuenta2);
        assertEquals(cuenta, cuenta2);
    }

    @Test
    void testDebitoCuenta() {
        Cuenta cuenta = new Cuenta("Andres", new BigDecimal("8900.997"));
        cuenta.debito(new BigDecimal("100.00"));
        assertNotNull(cuenta.getSaldo());
        assertEquals(8800, cuenta.getSaldo().intValue());
        assertEquals("8800.997", cuenta.getSaldo().toPlainString());
    }

    @Test
    void testCreditoCuenta() {
        Cuenta cuenta = new Cuenta("Andres", new BigDecimal("8900.997"));
        cuenta.credito(new BigDecimal("100.00"));
        assertNotNull(cuenta.getSaldo());
        assertEquals(9000, cuenta.getSaldo().intValue());
        assertEquals("9000.997", cuenta.getSaldo().toPlainString());
    }

    @Test
    void testDineroInsuficienteException() {
        Cuenta cuenta = new Cuenta("Andres", new BigDecimal("100.00"));
        Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
            cuenta.debito(new BigDecimal("101.00"));
        });
        String actual = exception.getMessage();
        String esperado = "Dinero Insuficiente";
        assertEquals(esperado, actual);
    }

    @Test
    void testTransferirDineroCuentas() {
        Cuenta cuenta1 = new Cuenta("Andres", new BigDecimal("14900.00"));
        Cuenta cuenta2 = new Cuenta("Jhon Doe", new BigDecimal("8900.997"));
        Banco banco = new Banco();
        banco.setName("BBVA");
        banco.transferir(cuenta1, cuenta2, new BigDecimal(500));
        assertEquals("14400.00", cuenta1.getSaldo().toPlainString());
        assertEquals("9400.997", cuenta2.getSaldo().toPlainString());
    }

    @Test
    void testRelacionBancoCuentas() {
        Cuenta cuenta1 = new Cuenta("Andres", new BigDecimal("14900.00"));
        Cuenta cuenta2 = new Cuenta("Jhon Doe", new BigDecimal("8900.997"));
        Banco banco = new Banco();
        banco.setName("BBVA");
        banco.addCuenta(cuenta1);
        banco.addCuenta(cuenta2);
        assertAll(
                () -> {
                    assertEquals(2, banco.getCuentas().size());
                },
                () -> {
                    assertEquals("BBVA", cuenta1.getBanco().getName());
                },
                () -> {
                    assertEquals("BBVA", cuenta2.getBanco().getName());
                },
                () -> {
                    assertEquals("Andres", banco.getCuentas().stream()
                            .filter(cuenta -> cuenta.getPersona().equals("Andres"))
                            .findFirst()
                            .get().getPersona());
                },
                () -> {
                    assertTrue(banco.getCuentas().stream()
                            .anyMatch(cuenta -> cuenta.getPersona().equals("Andres")));
                });
    }

    @Nested
    @DisplayName("Pruebas de Sistema Operativo Clase anidada")
    class enableOnOs {
        @Test
        @EnabledOnOs(OS.WINDOWS)
        void soloWindows() {
        }

        @Test
        @EnabledOnOs({ OS.MAC, OS.LINUX })
        void soloMacAndLinux() {
        }

        @Test
        @DisabledOnOs(OS.WINDOWS)
        void noWindows() {
        }
    }

    @Nested
    class enableOnJre {
        @Test
        @EnabledOnJre(JRE.JAVA_8)
        void soloJdk8() {

        }

        @Test
        @EnabledOnJre(JRE.JAVA_11)
        void soloJdk11() {

        }

        @Test
        @DisabledOnJre(JRE.JAVA_11)
        void noJdk11() {

        }
    }

    @Nested // Si un hijo falla falla toda la clase
    class enableByPropertiesEnv {
        @Test
        @EnabledIfSystemProperty(named = "file.encoding", matches = "UTF-8")
        void systemPropertieFileEncoding() {
            assertTimeout(Duration.ofSeconds(1),()->{
                TimeUnit.SECONDS.sleep(2);
            });
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches = "sinruta")
        void systemEnvironmentJavaHome() {

        }

        @Test
        @DisabledIfEnvironmentVariable(named = "JAVA_HOME", matches = "sinruta")
        void systemEnvironmentDisabledIf() {

        }
    }

    @Test
    void testSaldoCuentaDev() {
        boolean esDev = "DEV".equals("PROD"); // Aca iria una consulta a las variables del sistema o cualquier tipo de
                                              // validacion
        BigDecimal esperado = new BigDecimal("15233.22");
        assumeTrue(esDev); // si es false se salta el test
        assertNotNull(cuenta.getSaldo());
        assertEquals(esperado, cuenta.getSaldo());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
    }

    @RepeatedTest(value = 5, name = "{displayName} - Repeticion Numero {currentRepetition} de {totalRepetitions}")
    void testSaldoCuentaDev2(RepetitionInfo info) {
        if (info.getCurrentRepetition() == 2) {
            System.out.println("Es la repeticion " + info.getCurrentRepetition());
        }
        boolean esDev = "DEV".equals("prod"); // Aca iria una consulta a las variables del sistema o cualquier tipo de
                                              // validacion
        BigDecimal esperado = new BigDecimal("15233.22");
        assumingThat(esDev, () -> {
            assertNotNull(cuenta.getSaldo());
            assertEquals(esperado, cuenta.getSaldo());
        });// si es true se ejecutan las pruebas dentro si es false solo las de fuera

        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
    }

    @Nested
    class parametrizedTest {
        @ParameterizedTest
        @ValueSource(strings = { "100", "200", "300", "400", "500.6554" })
        void testDebitoCuentaParametrized(String monto) {
            Cuenta cuenta = new Cuenta("Andres", new BigDecimal("8900.997"));

            BigDecimal esperado = cuenta.getSaldo().subtract(new BigDecimal(monto));

            cuenta.debito(new BigDecimal(monto));

            assertNotNull(cuenta.getSaldo());

            assertEquals(esperado, cuenta.getSaldo());
        }

        @ParameterizedTest
        @CsvSource({ "1,100", "2,200", "3,300", "4,400", "5,500.6554" })
        void testDebitoCuentaParametrized2(String index, String monto) {
            Cuenta cuenta = new Cuenta("Andres", new BigDecimal("8900.997"));
            if (index.equals("4")) {
                System.out.println("Prueba numero " + index);
            }
            BigDecimal esperado = cuenta.getSaldo().subtract(new BigDecimal(monto));

            cuenta.debito(new BigDecimal(monto));

            assertNotNull(cuenta.getSaldo());

            assertEquals(esperado, cuenta.getSaldo());
        }

        @ParameterizedTest
        @CsvFileSource(resources = "/data.csv")
        void testDebitoCuentaParametrized3(String index, String monto) {
            Cuenta cuenta = new Cuenta("Andres", new BigDecimal("8900.997"));
            if (index.equals("4")) {
                System.out.println("Prueba numero " + index);
            }
            BigDecimal esperado = cuenta.getSaldo().subtract(new BigDecimal(monto));

            cuenta.debito(new BigDecimal(monto));

            assertNotNull(cuenta.getSaldo());

            assertEquals(esperado, cuenta.getSaldo());
        }

    }

    @ParameterizedTest
    @MethodSource("montoList")
    void testDebitoCuentaParametrized4(String monto) {

        BigDecimal esperado = cuenta.getSaldo().subtract(new BigDecimal(monto));

        cuenta.debito(new BigDecimal(monto));

        assertNotNull(cuenta.getSaldo());

        assertEquals(esperado, cuenta.getSaldo());
    }

    private static List<String> montoList() {
        return Arrays.asList("100", "200", "300", "400", "500.6554");
    }

}