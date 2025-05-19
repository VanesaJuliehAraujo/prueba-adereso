package co.adereso.utilities;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Hooks {

    /**
     * Se ejecuta antes de cada escenario.
     * Inicializa el driver de Playwright.
     *
     * @param scenario El escenario que se va a ejecutar.
     */
    @Before
    public void beforeScenario(Scenario scenario) {
        System.out.println("Iniciando escenario: " + scenario.getName());
        Driver.initializeDriver();
    }
    /**
     * Se ejecuta después de cada escenario.
     * Cierra el driver de Playwright.
     *
     * @param scenario El escenario que se ha ejecutado.
     */
    @After
    public void afterScenario(Scenario scenario) {
        System.out.println("Finalizando escenario: " + scenario.getName());

        if (scenario.isFailed() && Driver.page != null) {
            try {
                byte[] screenshot = Driver.page.screenshot();
                scenario.attach(screenshot, "image/png", "screenshot-failure");

                Files.write(Paths.get("failure-" + scenario.getName().replaceAll("\\s+", "_") + ".png"), screenshot);
            } catch (Exception e) {
                System.err.println("Error al tomar captura de pantalla: " + e.getMessage());
            }
        }

        Driver.closeDriver();
    }
}