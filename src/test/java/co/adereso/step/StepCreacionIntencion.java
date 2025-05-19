package co.adereso.step;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;
import co.adereso.pageobjects.LoginPage;
import co.adereso.pageobjects.DashboardPage;
import co.adereso.pageobjects.StudioPage;
import co.adereso.utilities.Driver;
import com.microsoft.playwright.options.AriaRole;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.nio.file.Paths;

/**
 * Clase que implementa los pasos de Cucumber para la creación de bots.
 * Incluye implementaciones para el Scenario Outline de múltiples bots.
 */
public class StepCreacionIntencion {

    /**
     * Página de Studio después de la navegación.
     */
    private Page studioPage;

    /**
     * Page Objects para las diferentes páginas.
     */
    private final LoginPage loginPage;
    private final DashboardPage dashboardPage;
    private StudioPage studioPageObject;

    /**
     * Constructor para inicializar los Page Objects.
     */
    public StepCreacionIntencion() {
        this.loginPage = new LoginPage(Driver.page);
        this.dashboardPage = new DashboardPage(Driver.page);
    }

    @Given("El usuario accede al buscador google")
    public void el_usuario_accede_al_buscador_google() {
        Driver.page.navigate("https://google.com.co");
        System.out.println(Driver.page.title());
    }


    @And("El usuario copia la siguiente LINK https:\\/\\/broly.adere.so\\/")
    public void el_usuario_copia_la_siguiente_link_https_broly_adere_so() {
        loginPage.navigate();
    }

    @When("El usuario ingresa sus credenciales")
    public void el_usuario_ingresa_sus_credenciales() {
        loginPage.enterEmail("saneva0810@gmail.com");
        loginPage.enterPassword("Vanessa2305.");
    }

    @And("El usuario da clic en ingresar")
    public void el_usuario_da_clic_en_ingresar() {
        loginPage.clickLogin();
    }

    @When("Se valida que los datos sean correctos")
    public void se_valida_que_los_datos_sean_correctos() {
        loginPage.solveCaptcha();
    }

    @Then("El usuario da clic en el modulo de Administrador")
    public void el_usuario_da_clic_en_el_modulo_de_administrador() {
        dashboardPage.clickAdministradorMenu();
    }

    @And("luego da clic en la opcion de Adereso Studio")
    public void luego_da_clic_en_la_opcion_de_adereso_studio() {
        studioPage = dashboardPage.navigateToStudio();
        studioPageObject = new StudioPage(studioPage);
    }

    @And("El usuario da clic nuevamente pero en la opcion aniadir Bot")
    public void el_usuario_da_clic_nuevamente_pero_en_la_opcion_aniadir_bot() {
        studioPageObject.clickAddBotButton();
    }

    @And("El usuario llena los campos")
    public void el_usuario_llena_los_campos() {
        studioPageObject.fillBotName("automatizacion1");
        studioPageObject.fillBotUrl("https://broly.adere.so/test");
        studioPageObject.fillBotDisplayName("automatizacion test");
        studioPageObject.fillBotDescription("esto es solo una prueba");
        studioPageObject.selectTimeZone("America/Bogota");
    }

    @Then("Clic en el boton crear")
    public void clic_en_el_boton_crear() {
        studioPageObject.clickCreateButton();
    }

    @And("El usuario llena los campos con nombre {string} url {string} displayName {string} y descripcion {string}")
    public void el_usuario_llena_los_campos_con_nombre_url_display_name_y_descripcion(
            String nombre, String url, String displayName, String descripcion) {

        System.out.println("Llenando formulario con datos:");
        System.out.println("Nombre: " + nombre);
        System.out.println("URL: " + url);
        System.out.println("Display Name: " + displayName);
        System.out.println("Descripción: " + descripcion);

        studioPageObject.fillBotName(nombre);
        studioPageObject.fillBotUrl(url);
        studioPageObject.fillBotDisplayName(displayName);
        studioPageObject.fillBotDescription(descripcion);
    }

    @And("El usuario selecciona la zona horaria {string}")
    public void el_usuario_selecciona_la_zona_horaria(String zonaHoraria) {
        System.out.println("Seleccionando zona horaria: " + zonaHoraria);
        studioPageObject.selectTimeZone(zonaHoraria);
    }

    @And("Se verifica que el bot {string} aparece en la lista")
    public void se_verifica_que_el_bot_aparece_en_la_lista(String displayName) {
        studioPage.waitForTimeout(3000);
        System.out.println("Verificando que el bot '" + displayName + "' aparece en la lista");

        boolean botFound = false;

        try {
            Locator tableCells = studioPage.getByRole(AriaRole.CELL);
            for (int i = 0; i < tableCells.count(); i++) {
                String cellText = tableCells.nth(i).textContent();
                if (cellText.contains(displayName)) {
                    botFound = true;
                    System.out.println("Bot encontrado en la celda " + i + " de la tabla");
                    break;
                }
            }

            if (!botFound) {
                Locator tableRows = studioPage.getByRole(AriaRole.ROW);
                for (int i = 0; i < tableRows.count(); i++) {
                    String rowText = tableRows.nth(i).textContent();
                    if (rowText.contains(displayName)) {
                        botFound = true;
                        System.out.println("Bot encontrado en la fila " + i + " de la tabla");
                        break;
                    }
                }
            }

            if (!botFound) {
                try {
                    botFound = studioPage.getByText(displayName,
                            new Page.GetByTextOptions().setExact(false)).first().isVisible();
                    System.out.println("Bot encontrado con búsqueda de texto no estricta");
                } catch (Exception e) {
                    System.out.println("Error en búsqueda no estricta: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("Error al buscar el bot: " + e.getMessage());
            e.printStackTrace();
        }

        if (!botFound) {
            try {
                botFound = studioPage.locator("span").filter(
                        new Locator.FilterOptions().setHasText(displayName)).isVisible();
                System.out.println("Bot encontrado usando locator con filtro");
            } catch (Exception e) {
                System.out.println("Error en último intento: " + e.getMessage());
            }
        }

        if (!botFound) {
            try {
                String screenshotPath = "target/screenshots/bot_not_found_" +
                        System.currentTimeMillis() + ".png";
                studioPage.screenshot(new Page.ScreenshotOptions().setPath(
                        Paths.get(screenshotPath)));
                System.out.println("Captura de pantalla guardada en: " + screenshotPath);
            } catch (Exception e) {
                System.out.println("No se pudo tomar captura de pantalla: " + e.getMessage());
            }
        }

        Assertions.assertTrue(botFound, "No se pudo encontrar el bot '" + displayName +
                "' en la lista. URL actual: " + studioPage.url());
    }

    @And("Se verifica que aparece mensaje de error de nombre duplicado")
    public void se_verifica_que_aparece_mensaje_de_error_de_nombre_duplicado() {
        se_verifica_que_aparece_mensaje_de_error_de_tipo("duplicado");
    }


    @And("El usuario da clic en añadir intención")
    public void el_usuario_da_clic_en_agregar_intencion() {
        Locator addIntentButton = studioPage.getByText("Añadir Intención",
                new Page.GetByTextOptions().setExact(false));
        addIntentButton.click();

        studioPage.waitForTimeout(1000);
    }

    @And("El usuario ingresa nombre de intención {string}")
    public void el_usuario_ingresa_nombre_de_intencion(String nombreIntencion) {
        Locator intentNameField = studioPage.getByLabel("Nombre",
                new Page.GetByLabelOptions().setExact(false));
        intentNameField.fill(nombreIntencion);
    }

    @And("El usuario intenta guardar la intención sin agregar frases de entrenamiento")
    public void el_usuario_intenta_guardar_la_intencion_sin_agregar_frases_de_entrenamiento() {
        Locator saveButton = studioPage.getByText("Guardar",
                new Page.GetByTextOptions().setExact(false));
        saveButton.click();

        studioPage.waitForTimeout(1000);
    }

    @Then("Se verifica que aparece mensaje de error de frases de entrenamiento requeridas")
    public void se_verifica_que_aparece_mensaje_de_error_de_frases_de_entrenamiento_requeridas() {
        se_verifica_que_aparece_mensaje_de_error_de_tipo("frases");
    }

    @And("El usuario da clic en la sección Bots")
    public void el_usuario_da_clic_en_la_seccion_bots() {
        System.out.println("Navegando a la sección de Bots...");
        try {
            studioPage.waitForLoadState();

            Locator botsLink = studioPage.getByRole(AriaRole.LINK,
                    new Page.GetByRoleOptions().setName("Bots"));

            if (botsLink.isVisible()) {
                System.out.println("Enlace 'Bots' encontrado, haciendo clic...");
                botsLink.click();
            } else {
                System.out.println("Enlace 'Bots' no visible, buscando alternativas...");

                Locator botsByText = studioPage.getByText("Bots",
                        new Page.GetByTextOptions().setExact(true));
                if (botsByText.count() > 0) {
                    System.out.println("Texto 'Bots' encontrado, haciendo clic...");
                    botsByText.first().click();
                } else {
                    System.out.println("Buscando en menú de navegación...");
                    Locator navLinks = studioPage.locator("nav a, nav button");
                    for (int i = 0; i < navLinks.count(); i++) {
                        String text = navLinks.nth(i).textContent();
                        if (text.contains("Bot")) {
                            System.out.println("Encontrado enlace de navegación con 'Bot': " + text);
                            navLinks.nth(i).click();
                            break;
                        }
                    }
                }
            }

            studioPage.waitForLoadState();
            studioPage.waitForTimeout(2000);

            System.out.println("Navegación a sección de Bots completada. URL actual: " + studioPage.url());

        } catch (Exception e) {
            System.err.println("Error al navegar a la sección de Bots: " + e.getMessage());
            e.printStackTrace();

            try {
                byte[] errorScreenshot = studioPage.screenshot();
                java.nio.file.Files.write(java.nio.file.Paths.get("error-navegacion-bots.png"), errorScreenshot);
            } catch (Exception screenshotError) {
                System.err.println("No se pudo guardar la captura del error");
            }

            throw new RuntimeException("No se pudo navegar a la sección de Bots", e);
        }
    }

    /**
     * Pasos relacionados con la validación de mensajes
     */
    @Then("Se verifica que aparece mensaje de confirmación")
    public void se_verifica_que_aparece_mensaje_de_confirmacion() {
        System.out.println("Verificando mensaje de confirmación...");

        try {
            studioPage.waitForTimeout(2000);
            boolean immediateCheck = studioPageObject.isSuccessMessageVisible();

            if (immediateCheck) {
                System.out.println("Mensaje de confirmación encontrado inmediatamente");
                Assertions.assertTrue(true);
                return;
            }

            System.out.println("Mensaje no visible inmediatamente, esperando...");
            boolean messageFound = studioPageObject.waitForSuccessMessage(7000);

            byte[] screenshot = studioPage.screenshot();
            try {
                java.nio.file.Files.write(java.nio.file.Paths.get("verificacion-confirmacion.png"), screenshot);
            } catch (Exception e) {
                System.err.println("No se pudo guardar la captura de pantalla");
            }

            if (!messageFound) {
                boolean lastAttempt = studioPage.getByText("creado", new Page.GetByTextOptions().setExact(false)).isVisible();

                if (lastAttempt) {
                    System.out.println("¡Encontrado texto 'creado' en último intento!");
                    Assertions.assertTrue(true);
                    return;
                }

                System.out.println("ALERTA: No se pudo encontrar el mensaje después de múltiples intentos");
                System.out.println("URL actual: " + studioPage.url());
                System.out.println("Título de la página: " + studioPage.title());
            }

            Assertions.assertTrue(messageFound,
                    "No se mostró el mensaje de confirmación después de crear/editar el bot. " +
                            "URL actual: " + studioPage.url());

        } catch (Exception e) {
            System.err.println("Error grave durante la verificación del mensaje: " + e.getMessage());
            e.printStackTrace();

            try {
                byte[] errorScreenshot = studioPage.screenshot();
                java.nio.file.Files.write(java.nio.file.Paths.get("error-verificacion-confirmacion.png"), errorScreenshot);
            } catch (Exception ex) {
                System.err.println("Error generando screen");
            }

            Assertions.fail("Error al verificar mensaje de confirmación: " + e.getMessage());
        }
    }

    @Then("Se verifica que aparece mensaje de error de tipo {string}")
    public void se_verifica_que_aparece_mensaje_de_error_de_tipo(String tipoError) {
        System.out.println("Verificando mensaje de error de tipo: " + tipoError);

        studioPage.waitForTimeout(1000);

        boolean errorFound = studioPageObject.isErrorMessageVisible(tipoError);

        byte[] screenshot = studioPage.screenshot();
        try {
            java.nio.file.Files.write(java.nio.file.Paths.get("verificacion-error-" + tipoError + ".png"), screenshot);
        } catch (Exception e) {
            System.err.println("No se pudo guardar la captura de pantalla");
        }

        Assertions.assertTrue(errorFound,
                "No se mostró el mensaje de error de tipo '" + tipoError + "'. " +
                        "URL actual: " + studioPage.url());
    }

    /**
     * Pasos relacionados con la creación y verificación de intenciones
     */
    @When("El usuario crea una intención con nombre {string} y frase {string}")
    public void el_usuario_crea_una_intencion_con_nombre_y_frase(String nombreIntencion, String fraseEntrenamiento) {
        System.out.println("Creando intención con nombre: " + nombreIntencion);
        studioPageObject.clickAddIntentButton();
        studioPageObject.fillIntentForm(nombreIntencion, fraseEntrenamiento);

        studioPageObject.saveIntent();
    }

    @When("El usuario selecciona el bot {string}")
    public void el_usuario_selecciona_el_bot(String botName) {
        studioPageObject.clickOnBot(botName);
    }

    @And("El usuario hace clic en el botón de edición")
    public void el_usuario_hace_clic_en_el_boton_de_edicion() {
        studioPageObject.clickFirstEditButton();
    }

    @And("El usuario edita la descripción del bot a {string}")
    public void el_usuario_edita_la_descripcion_del_bot(String newDescription) {
        studioPageObject.editBotDescription(newDescription);
    }

    @And("El usuario edita completamente el bot {string} con nueva descripción {string}")
    public void el_usuario_edita_completamente_el_bot(String botName, String newDescription) {
        studioPageObject.editBotDescription(botName, newDescription);
    }

}