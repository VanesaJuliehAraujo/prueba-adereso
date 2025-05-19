package co.adereso.pageobjects;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

/**
 * Page Object para la página de Adereso Studio.
 * Encapsula la interacción con los elementos clave de la interfaz de creación y edición de bots.
 */
public class StudioPage extends BasePage {

    private static final String ADD_BOT_BUTTON_NAME = "Añadir bot";
    private static final String CREATE_BOT_BUTTON_NAME = "Crear un bot";
    private static final String BOT_NAME_PLACEHOLDER = "Ej: bot de servicio al cliente";
    private static final String BOT_URL_PLACEHOLDER = "Ej: Comercio Express";
    private static final String BOT_DISPLAY_NAME_PLACEHOLDER = "Ej: Express Bot";
    private static final String BOT_DESCRIPTION_PLACEHOLDER = "Define tu negocio/ marca en";
    private static final String CREATE_FINAL_BUTTON_NAME = "Crear bot";
    private static final String ERROR_DUPLICATE_NAME = "El nombre del bot ya existe";
    private static final String ERROR_MISSING_TRAINING_PHRASES = "Se requieren frases de entrenamiento";

    private static final String ADD_INTENT_BUTTON_TEXT = "Añadir Intención";
    private static final String INTENT_NAME_FIELD = "[placeholder='Nombre de la intención']";
    private static final String ADD_TRAINING_PHRASE_BUTTON = "Añadir frase";
    private static final String TRAINING_PHRASE_FIELD = "[placeholder='Escribe una frase de entrenamiento']";
    private static final String SAVE_INTENT_BUTTON = "Guardar";

    public StudioPage(Page page) {
        super(page);
    }

    @Override
    public void navigate() {
        throw new UnsupportedOperationException("No se puede navegar directamente a Studio, debe acceder desde el Dashboard");
    }

    @Override
    public boolean isPageLoaded() {
        try {
            page.waitForLoadState();
            page.waitForTimeout(2000);
            return !page.getByText("Adereso Studio").isVisible();
        } catch (Exception e) {
            System.err.println("Error al verificar carga de página: " + e.getMessage());
            return true;
        }
    }

    public void clickAddBotButton() {
        try {
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(ADD_BOT_BUTTON_NAME)).click();
        } catch (Exception e1) {
            System.out.println("Botón 'Añadir bot' no disponible, intentando con 'Crear un bot'...");
            try {
                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(CREATE_BOT_BUTTON_NAME)).click();
            } catch (Exception e2) {
                throw new RuntimeException("No se pudo hacer clic en ningún botón para añadir bot", e2);
            }
        }
    }

    public void fillBotName(String name) {
        page.getByPlaceholder(BOT_NAME_PLACEHOLDER).fill(name);
    }

    public void fillBotUrl(String url) {
        page.getByPlaceholder(BOT_URL_PLACEHOLDER).fill(url);
    }

    public void fillBotDisplayName(String displayName) {
        page.getByPlaceholder(BOT_DISPLAY_NAME_PLACEHOLDER).fill(displayName);
    }

    public void fillBotDescription(String description) {
        page.getByPlaceholder(BOT_DESCRIPTION_PLACEHOLDER).fill(description);
    }

    public void selectTimeZone(String timezone) {
        try {
            Locator timezoneInputs = page.locator(".v-field__input");
            int count = timezoneInputs.count();
            if (count == 0) {
                System.out.println("No se encontraron elementos para zona horaria.");
                return;
            }

            if (count > 2) {
                timezoneInputs.nth(2).click();
            } else {
                timezoneInputs.last().click();
            }

            page.waitForTimeout(1000);

            Locator menuItems = page.locator(".v-list-item-title").filter(new Locator.FilterOptions().setHasText(timezone));

            if (menuItems.count() > 0) {
                menuItems.first().click();
                System.out.println("Zona horaria seleccionada: " + timezone);
            } else {
                System.out.println("No se encontró la zona horaria: " + timezone);
            }
        } catch (Exception e) {
            System.err.println("Error seleccionando zona horaria: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void clickCreateButton() {
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(CREATE_FINAL_BUTTON_NAME)).click();
    }

    /**
     * Verifica si se muestra un mensaje de confirmación después de crear un bot
     * @return true si el mensaje está visible, false en caso contrario
     */
    public boolean isSuccessMessageVisible() {
        try {
            boolean exactMessageFound = page.getByText("Chatbot creado").isVisible();

            if (!exactMessageFound) {
                boolean alternativeMessages =
                        page.getByText("Bot creado").isVisible() ||
                                page.getByText("creado", new Page.GetByTextOptions().setExact(false)).isVisible() ||
                                page.getByText("éxito", new Page.GetByTextOptions().setExact(false)).isVisible();

                if (alternativeMessages) {
                    System.out.println("Encontrado mensaje alternativo de confirmación");
                    return true;
                }
            } else {
                System.out.println("Encontrado mensaje exacto 'Chatbot creado'");
                return true;
            }

            boolean greenSuccessElement = page.locator("div.v-snackbar.v-snackbar--active, " +
                    ".v-snack--active, " +
                    ".success-toast, " +
                    ".v-alert--success").isVisible();

            if (greenSuccessElement) {
                System.out.println("Encontrado elemento visual de confirmación (verde)");
                return true;
            }

            byte[] screenshotFailure = page.screenshot();
            java.nio.file.Files.write(java.nio.file.Paths.get("no-mensaje-confirmacion.png"), screenshotFailure);
            System.out.println("No se encontró mensaje de confirmación - guardada captura");

            return false;

        } catch (Exception e) {
            System.err.println("Error al verificar mensaje de confirmación: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Verifica si se muestra un mensaje de error específico
     * @param errorType el tipo de error a verificar (puede ser "duplicado" o "frases")
     * @return true si el mensaje de error está visible, false en caso contrario
     */
    public boolean isErrorMessageVisible(String errorType) {
        try {
            String expectedErrorMessage;

            if (errorType.equalsIgnoreCase("duplicado")) {
                expectedErrorMessage = ERROR_DUPLICATE_NAME;
            } else if (errorType.equalsIgnoreCase("frases")) {
                expectedErrorMessage = ERROR_MISSING_TRAINING_PHRASES;
            } else {
                expectedErrorMessage = errorType;
            }

            boolean errorFound = page.getByText(expectedErrorMessage,
                    new Page.GetByTextOptions().setExact(false)).isVisible();

            boolean errorClassFound = page.locator(".error-message, .v-snackbar--error, .toast-error, .v-messages__message").isVisible();

            if (errorFound || errorClassFound) {
                System.out.println("Mensaje de error '" + errorType + "' detectado");
                try {
                    byte[] screenshot = page.screenshot();
                    java.nio.file.Files.write(java.nio.file.Paths.get("error-" + errorType + ".png"), screenshot);
                } catch (Exception e) {
                    System.err.println("No se pudo guardar captura del mensaje de error");
                }
            }

            return errorFound || errorClassFound;
        } catch (Exception e) {
            System.err.println("Error al verificar mensaje de error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Espera a que aparezca un mensaje de confirmación
     * @param timeoutMs tiempo máximo de espera en milisegundos
     * @return true si el mensaje aparece dentro del tiempo de espera, false en caso contrario
     */
    public boolean waitForSuccessMessage(int timeoutMs) {
        System.out.println("Esperando mensaje de confirmación...");

        try {
            page.waitForSelector(".v-snackbar, .v-snack, .v-alert, .snack, .toast, .notification",
                    new Page.WaitForSelectorOptions().setTimeout(timeoutMs));

            if (isSuccessMessageVisible()) {
                return true;
            }

            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < timeoutMs) {
                if (isSuccessMessageVisible()) {
                    return true;
                }

                page.waitForTimeout(300);
            }

            System.out.println("¡Tiempo de espera agotado! Tomando captura final");
            byte[] finalScreenshot = page.screenshot();
            java.nio.file.Files.write(java.nio.file.Paths.get("timeout-confirmacion.png"), finalScreenshot);

            return false;
        } catch (Exception e) {
            System.err.println("Error mientras se esperaba el mensaje de confirmación: " + e.getMessage());
            try {
                byte[] errorScreenshot = page.screenshot();
                java.nio.file.Files.write(java.nio.file.Paths.get("error-espera-confirmacion.png"), errorScreenshot);
            } catch (Exception ex) {
                System.err.println("Además, no se pudo guardar captura del error: " + ex.getMessage());
            }

            return false;
        }
    }

    /**
     * Hace clic en el botón para añadir una nueva intención
     */
    public void clickAddIntentButton() {
        try {
            System.out.println("Buscando botón 'Añadir Intención'...");

            Locator addIntentButton = page.getByText(ADD_INTENT_BUTTON_TEXT,
                    new Page.GetByTextOptions().setExact(false));

            if (addIntentButton.isVisible()) {
                System.out.println("Botón 'Añadir Intención' encontrado, haciendo clic...");
                addIntentButton.click();
                page.waitForTimeout(1000);
            } else {
                System.out.println("Botón no visible, buscando alternativas...");

                Locator addButton = page.locator("button.add-button, button.v-btn--icon")
                        .filter(new Locator.FilterOptions().setHasText(""));

                if (addButton.count() > 0) {
                    System.out.println("Botón alternativo encontrado, haciendo clic...");
                    addButton.first().click();
                } else {
                    throw new RuntimeException("No se pudo encontrar el botón para añadir intención");
                }
            }

            boolean formVisible = page.locator(".v-dialog--active, .modal, #intent-form").isVisible();
            if (!formVisible) {
                System.out.println("¡Alerta! El formulario no parece estar visible después de hacer clic");
            }

        } catch (Exception e) {
            System.err.println("Error al hacer clic en botón para añadir intención: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al añadir intención", e);
        }
    }

    /**
     * Completa el formulario de intención con datos de prueba
     * @param intentName Nombre de la intención
     * @param trainingPhrases Lista de frases de entrenamiento
     */
    public void fillIntentForm(String intentName, String... trainingPhrases) {
        try {
            System.out.println("Completando formulario de intención: " + intentName);

            Locator nameField = page.locator(INTENT_NAME_FIELD);
            if (!nameField.isVisible()) {
                nameField = page.getByLabel("Nombre", new Page.GetByLabelOptions().setExact(false));

                if (!nameField.isVisible()) {
                    nameField = page.locator(".v-dialog--active input, .modal input").first();
                }
            }

            nameField.fill(intentName);
            System.out.println("Nombre de intención ingresado: " + intentName);

            if (trainingPhrases.length > 0) {
                for (String phrase : trainingPhrases) {
                    Locator addPhraseButton = page.getByText(ADD_TRAINING_PHRASE_BUTTON,
                            new Page.GetByTextOptions().setExact(false));

                    if (addPhraseButton.isVisible()) {
                        addPhraseButton.click();
                    } else {
                        page.locator("button").filter(new Locator.FilterOptions()
                                .setHasText("frase")).first().click();
                    }

                    page.waitForTimeout(500);

                    Locator phraseField = page.locator(TRAINING_PHRASE_FIELD);
                    if (!phraseField.isVisible()) {
                        phraseField = page.locator("input").last();
                    }

                    phraseField.fill(phrase);
                    phraseField.press("Enter");

                    System.out.println("Frase de entrenamiento añadida: " + phrase);
                }
            } else {
                System.out.println("¡Advertencia! No se proporcionaron frases de entrenamiento");
            }

        } catch (Exception e) {
            System.err.println("Error al completar formulario de intención: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al completar formulario de intención", e);
        }
    }

    /**
     * Guarda la intención haciendo clic en el botón guardar
     */
    public void saveIntent() {
        try {
            Locator saveButton = page.getByRole(AriaRole.BUTTON,
                    new Page.GetByRoleOptions().setName(SAVE_INTENT_BUTTON));

            if (saveButton.isVisible()) {
                System.out.println("Haciendo clic en botón 'Guardar'...");
                saveButton.click();
            } else {
                Locator altSaveButton = page.getByText(SAVE_INTENT_BUTTON,
                        new Page.GetByTextOptions().setExact(true));

                if (altSaveButton.isVisible()) {
                    altSaveButton.click();
                } else {
                    throw new RuntimeException("No se pudo encontrar el botón para guardar la intención");
                }
            }

            page.waitForTimeout(2000);

        } catch (Exception e) {
            System.err.println("Error al guardar intención: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al guardar intención", e);
        }
    }

    /**
     * Busca un bot específico por su nombre en la tabla y hace clic en él.
     * @param botDisplayName El nombre de visualización del bot a seleccionar
     */
    public void clickOnBot(String botDisplayName) {
        System.out.println("Buscando y haciendo clic en el bot: " + botDisplayName);

        try {
            page.getByRole(AriaRole.CELL, new Page.GetByRoleOptions().setName(botDisplayName))
                    .locator("span").click();

            page.waitForLoadState();
            page.waitForTimeout(1000);

            System.out.println("Bot seleccionado con éxito");
        } catch (Exception e) {
            System.err.println("Error al hacer clic en el bot: " + e.getMessage());
            throw new RuntimeException("No se pudo hacer clic en el bot " + botDisplayName, e);
        }
    }

    /**
     * Hace clic en el primer botón de edición que se encuentra en la página del bot.
     */
    public void clickFirstEditButton() {
        System.out.println("Haciendo clic en el primer botón de edición");

        try {
            page.locator("div:nth-child(3) > .v-btn").first().click();

            page.waitForTimeout(1000);
            System.out.println("Botón de edición presionado con éxito");
        } catch (Exception e) {
            System.err.println("Error al hacer clic en el botón de edición: " + e.getMessage());
            throw new RuntimeException("No se pudo hacer clic en el botón de edición", e);
        }
    }

    /**
     * Edita la descripción del bot.
     * @param newDescription La nueva descripción para el bot
     */
    public void editBotDescription(String newDescription) {
        System.out.println("Editando descripción del bot a: " + newDescription);

        try {
            Locator descriptionField = page.getByPlaceholder("Define tu negocio/ marca en");
            descriptionField.click();
            descriptionField.clear();

            descriptionField.fill(newDescription);
            System.out.println("Descripción actualizada con éxito");
        } catch (Exception e) {
            System.err.println("Error al editar la descripción del bot: " + e.getMessage());
            throw new RuntimeException("No se pudo editar la descripción del bot", e);
        }
    }

    /**
     * Método para editar completamente un bot, combinando los pasos individuales.
     * @param botName Nombre del bot a editar
     * @param newDescription Nueva descripción para el bot
     */
    public void editBotDescription(String botName, String newDescription) {
        clickOnBotsSection();
        clickOnBot(botName);
        clickFirstEditButton();
        editBotDescription(newDescription);

        System.out.println("Bot editado exitosamente");
    }

    /**
     * Navega a la sección de bots haciendo clic en el enlace "Bots"
     */
    public void clickOnBotsSection() {
        System.out.println("Navegando a la sección de Bots");

        try {
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Bots")).click();
            page.waitForLoadState();
            page.waitForTimeout(1000);
            System.out.println("Navegación exitosa a la sección de Bots");
        } catch (Exception e) {
            System.err.println("Error al navegar a la sección de Bots: " + e.getMessage());
            throw new RuntimeException("No se pudo navegar a la sección de Bots", e);
        }
    }
}
