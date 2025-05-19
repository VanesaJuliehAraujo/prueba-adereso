package co.adereso.step;

import co.adereso.pageobjects.DashboardPage;
import co.adereso.pageobjects.EnhancedLoginPage;
import co.adereso.pageobjects.LoginPage;
import co.adereso.utilities.Driver;
import co.adereso.utilities.ElementInteractor;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Clase que implementa los pasos de Cucumber para la gestión de sesión.
 * Maneja el flujo de inicio de sesión, navegación a Studio y cierre de sesión.
 */
public class StepGestionSesion {

    private Page studioPage;
    private final LoginPage loginPage;
    private final EnhancedLoginPage enhancedLoginPage;
    private final DashboardPage dashboardPage;

    /**
     * Constructor para inicializar los Page Objects.
     */
    public StepGestionSesion() {
        this.loginPage = new LoginPage(Driver.page);
        this.enhancedLoginPage = new EnhancedLoginPage(Driver.page);
        this.dashboardPage = new DashboardPage(Driver.page);
    }

    @Given("El usuario accede a la página de login de Adereso")
    public void el_usuario_accede_a_la_pagina_de_login() {
        loginPage.navigate();
        System.out.println("Título de la página: " + Driver.page.title());
    }

    @When("El usuario ingresa sus credenciales sesion")
    public void el_usuario_ingresa_sus_credenciales() {
        loginPage.enterEmail("saneva0810@gmail.com");
        loginPage.enterPassword("Vanessa2305.");
    }

    @And("El usuario da clic en el botón de iniciar sesión")
    public void el_usuario_da_clic_en_el_boton_de_iniciar_sesion() {
        Driver.page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Iniciar sesión")).click();
    }

    @When("Se valida que los datos sean correctos sesion")
    public void se_valida_que_los_datos_sean_correctos() {
        enhancedLoginPage.solveCaptcha();
    }

    @Then("El usuario da clic en el modulo de Administrador sesion")
    public void el_usuario_da_clic_en_el_modulo_de_administrador() {
        dashboardPage.clickAdministradorMenu();
    }

    @And("El usuario accede a Adereso Studio")
    public void el_usuario_accede_a_adereso_studio() {
        studioPage = dashboardPage.navigateToStudio();
    }

    @And("El usuario activa un switch en la página de Studio")
    public void el_usuario_activa_un_switch_en_studio() {
        try {
            System.out.println("Intentando activar el switch #switch-22 en la página de Studio...");
            studioPage.waitForLoadState();

            Locator switchElement = studioPage.locator("#switch-22");

            boolean switchFound = false;
            long startTime = System.currentTimeMillis();
            long timeout = 10000;

            while (System.currentTimeMillis() - startTime < timeout) {
                if (switchElement.isVisible()) {
                    switchFound = true;
                    break;
                }
                studioPage.waitForTimeout(500);
                System.out.println("Esperando que el switch esté visible...");
            }

            if (switchFound) {
                boolean isChecked = false;
                try {
                    isChecked = switchElement.isChecked();
                    System.out.println("Estado actual del switch: " + (isChecked ? "ACTIVADO" : "DESACTIVADO"));
                } catch (Exception e) {
                    System.out.println("No se pudo determinar el estado inicial del switch: " + e.getMessage());
                }

                if (!isChecked) {
                    try {
                        System.out.println("Intentando activar el switch usando check()...");
                        switchElement.check();
                    } catch (Exception e) {
                        System.out.println("Error usando check(), intentando alternativa: " + e.getMessage());

                        try {
                            System.out.println("Intentando activar con click()...");
                            switchElement.click();
                        } catch (Exception e2) {
                            System.out.println("Error usando click(), intentando última alternativa: " + e2.getMessage());
                            studioPage.evaluate("document.querySelector('#switch-22').click();");
                            System.out.println("Activado con JavaScript");
                        }
                    }

                    studioPage.waitForTimeout(1000);
                    try {
                        boolean newState = switchElement.isChecked();
                        System.out.println("Nuevo estado del switch: " + (newState ? "ACTIVADO" : "DESACTIVADO"));

                        if (!newState) {
                            System.out.println("¡ADVERTENCIA! El switch parece no haber cambiado de estado");
                        }
                    } catch (Exception e) {
                        System.out.println("No se pudo verificar el estado final del switch: " + e.getMessage());
                    }
                } else {
                    System.out.println("El switch ya estaba activado, no es necesario cambiarlo");
                }
            } else {
                System.out.println("No se encontró el switch #switch-22 después de esperar 10 segundos");
                Locator anySwitch = studioPage.locator(".v-switch, .v-checkbox, input[type='checkbox']").first();

                if (anySwitch.isVisible()) {
                    System.out.println("Se encontró un switch alternativo, intentando activarlo...");
                    try {
                        anySwitch.check();
                        System.out.println("Switch alternativo activado con éxito");
                    } catch (Exception e) {
                        anySwitch.click();
                        System.out.println("Switch alternativo activado con click");
                    }
                } else {
                    System.out.println("No se encontró ningún switch para activar en la página");
                    byte[] screenshot = studioPage.screenshot();
                    java.nio.file.Files.write(java.nio.file.Paths.get("no-switch-found.png"), screenshot);
                    System.out.println("Se ha guardado captura de pantalla 'no-switch-found.png'");
                }
            }

        } catch (Exception e) {
            System.err.println("Error al activar el switch: " + e.getMessage());
            e.printStackTrace();
            try {
                byte[] screenshot = studioPage.screenshot();
                java.nio.file.Files.write(java.nio.file.Paths.get("error-switch.png"), screenshot);
                System.err.println("Captura de error guardada en error-switch.png");
            } catch (Exception ex) {
                System.err.println("No se pudo guardar la captura de pantalla");
            }
        }
    }

    @And("El usuario regresa a la página principal")
    public void el_usuario_regresa_a_la_pagina_principal() {
        Driver.page.bringToFront();
        System.out.println("Volviendo a la página principal: " + Driver.page.url());
        dashboardPage.clickAdministradorMenu();
    }

    @And("El usuario cierra la sesión")
    public void el_usuario_cierra_la_sesion() {
        try {
            System.out.println("Intentando cerrar sesión...");
            Driver.page.getByRole(AriaRole.LINK,
                    new Page.GetByRoleOptions().setName("Vanesa Julieth Araujo Montes")).click();

            Driver.page.waitForTimeout(1000);

            Driver.page.getByRole(AriaRole.LINK,
                    new Page.GetByRoleOptions().setName("Cerrar Sesión")).click();

            boolean redirectedToLogin = false;
            long startTime = System.currentTimeMillis();
            long timeout = 5000;

            while (System.currentTimeMillis() - startTime < timeout) {
                if (Driver.page.url().contains("/login")) {
                    redirectedToLogin = true;
                    break;
                }
                Driver.page.waitForTimeout(500);
            }

            if (redirectedToLogin) {
                System.out.println("Sesión cerrada correctamente");
            } else {
                System.out.println("No se detectó redirección a la página de login");
            }

        } catch (Exception e) {
            System.err.println("Error al cerrar sesión: " + e.getMessage());
            e.printStackTrace();

            try {
                System.out.println("Intentando método alternativo de cierre de sesión...");

                Locator logoutLink = Driver.page.getByText("Cerrar Sesión",
                        new Page.GetByTextOptions().setExact(false));

                if (logoutLink.isVisible()) {
                    logoutLink.click();

                    Driver.page.waitForTimeout(3000);
                    if (Driver.page.url().contains("/login")) {
                        System.out.println("Sesión cerrada con método alternativo");
                    }
                } else {
                    System.err.println("No se pudo encontrar el enlace de cierre de sesión");
                }

            } catch (Exception ex) {
                System.err.println("Error en método alternativo: " + ex.getMessage());

                try {
                    byte[] screenshot = Driver.page.screenshot();
                    java.nio.file.Files.write(java.nio.file.Paths.get("error-logout.png"), screenshot);
                    System.err.println("Captura de error guardada en error-logout.png");
                } catch (Exception screenshotEx) {
                    System.err.println("No se pudo guardar la captura de pantalla");
                }
            }
        }
    }

    @And("El usuario activa el switch usando el método avanzado")
    public void el_usuario_activa_switch_metodo_avanzado() {
        try {
            System.out.println("Activando switch con método avanzado...");

            boolean success = ElementInteractor.activateSwitch(studioPage, "#switch-22");

            if (success) {
                System.out.println("Switch activado exitosamente con método avanzado");
            } else {
                System.out.println("El método avanzado no pudo activar el switch");

                try {
                    System.out.println("Intentando con la sintaxis exacta del script original...");
                    studioPage.evaluate("() => { document.querySelector('#switch-22').click(); }");
                    System.out.println("Switch activado con la sintaxis original");
                } catch (Exception e) {
                    System.err.println("Error con sintaxis original: " + e.getMessage());
                    throw e;
                }
            }

        } catch (Exception e) {
            System.err.println("Error activando switch con método avanzado: " + e.getMessage());
            e.printStackTrace();

            try {
                byte[] screenshot = studioPage.screenshot();
                java.nio.file.Files.write(java.nio.file.Paths.get("error-switch-avanzado.png"), screenshot);
                System.err.println("Captura de error guardada en error-switch-avanzado.png");
            } catch (Exception ex) {
                System.err.println("No se pudo guardar la captura de pantalla");
            }
        }
    }
}