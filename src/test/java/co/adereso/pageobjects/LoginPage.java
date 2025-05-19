package co.adereso.pageobjects;

import com.microsoft.playwright.FrameLocator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

/**
 * Page Object para la página de inicio de sesión.
 * Maneja todas las interacciones con los elementos de la página de login.
 */
public class LoginPage extends BasePage {

    /**
     * URL de la página de inicio de sesión.
     */
    private static final String URL = "https://broly.adere.so/";

    /**
     * Selectores para los elementos de la página.
     */
    private static final String CAPTCHA_FRAME_SELECTOR = "iframe[title='reCAPTCHA'][width='304']";

    /**
     * Constructor para inicializar el Page Object con una instancia de Page.
     *
     * @param page La instancia de Page de Playwright.
     */
    public LoginPage(Page page) {
        super(page);
    }

    /**
     * Navega a la página de inicio de sesión.
     */
    @Override
    public void navigate() {
        page.navigate(URL);
        System.out.println("Título de la página: " + page.title());
    }

    /**
     * Verifica si la página de login está cargada correctamente.
     *
     * @return true si la página está cargada correctamente, false en caso contrario.
     */
    @Override
    public boolean isPageLoaded() {
        return !page.getByPlaceholder("Ej: Nombre@empresa.com").isVisible();
    }

    /**
     * Ingresa el email en el campo correspondiente.
     *
     * @param email El email a ingresar.
     */
    public void enterEmail(String email) {
        page.getByPlaceholder("Ej: Nombre@empresa.com").fill(email);
    }

    /**
     * Ingresa la contraseña en el campo correspondiente.
     *
     * @param password La contraseña a ingresar.
     */
    public void enterPassword(String password) {
        page.getByPlaceholder("Tu contraseña").fill(password);
    }

    /**
     * Hace clic en el botón de iniciar sesión o presiona Enter.
     */
    public void clickLogin() {
        page.getByPlaceholder("Tu contraseña").press("Enter");
    }

    /**
     * Resuelve el CAPTCHA haciendo clic en la casilla "No soy un robot".
     */
    public void solveCaptcha() {
        page.frameLocator(CAPTCHA_FRAME_SELECTOR)
                .getByRole(AriaRole.CHECKBOX, new FrameLocator.GetByRoleOptions().setName("No soy un robot"))
                .click();
    }
}