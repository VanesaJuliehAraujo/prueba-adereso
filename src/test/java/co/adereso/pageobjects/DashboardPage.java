package co.adereso.pageobjects;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

/**
 * Page Object para la página de Dashboard.
 * Maneja todas las interacciones con los elementos del Dashboard después del inicio de sesión.
 */
public class DashboardPage extends BasePage {

    /**
     * Identificador para confirmar que estamos en la página del Dashboard.
     */
    private static final String DASHBOARD_IDENTIFIER = "AdministradorAdmin.";

    /**
     * Constructor para inicializar el Page Object con una instancia de Page.
     *
     * @param page La instancia de Page de Playwright.
     */
    public DashboardPage(Page page) {
        super(page);
    }

    /**
     * No se puede navegar directamente al Dashboard, se debe iniciar sesión primero.
     */
    @Override
    public void navigate() {
        throw new UnsupportedOperationException(
                "No se puede navegar directamente al Dashboard, debe iniciar sesión primero");
    }

    /**
     * Verifica si la página del Dashboard está cargada correctamente.
     *
     * @return true si la página está cargada correctamente, false en caso contrario.
     */
    @Override
    public boolean isPageLoaded() {
        return !page.getByText(DASHBOARD_IDENTIFIER).isVisible();
    }

    /**
     * Hace clic en el menú de Administrador.
     */
    public void clickAdministradorMenu() {
        page.getByText(DASHBOARD_IDENTIFIER).click();
    }

    /**
     * Navega a la página de Adereso Studio.
     *
     * @return La instancia de Page para la nueva ventana/pestaña.
     */
    public Page navigateToStudio() {
        System.out.println("Intentando navegar a Adereso Studio...");

        if (isPageLoaded()) {
            System.out.println("No estamos en el Dashboard. URL actual: " + page.url());
            throw new RuntimeException("No estamos en la página del Dashboard");
        }

        boolean linkVisible = page.getByRole(AriaRole.LINK,
                new Page.GetByRoleOptions().setName("Adereso Studio")).isVisible();
        System.out.println("¿El enlace a Adereso Studio es visible? " + linkVisible);

        if (!linkVisible) {
            System.out.println("El enlace no es visible, intentando hacer clic en el menú Administrador...");
            clickAdministradorMenu();
            page.waitForTimeout(1000);
        }

        try {
            System.out.println("Esperando a que el enlace de Adereso Studio esté disponible...");
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Adereso Studio")).waitFor(
                    new Locator.WaitForOptions().setTimeout(10000));

            System.out.println("Haciendo clic en el enlace y esperando por una nueva ventana...");
            Page studioPage = page.waitForPopup(() -> {
                page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Adereso Studio")).click();
            });

            System.out.println("Nueva ventana abierta, esperando a que cargue...");
            studioPage.waitForLoadState();
            studioPage.waitForTimeout(3000);

            System.out.println("Nueva página abierta: " + studioPage.url());
            System.out.println("Título de la página: " + studioPage.title());

            return studioPage;
        } catch (Exception e) {
            System.out.println("Error al navegar a Adereso Studio: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("No se pudo navegar a Adereso Studio", e);
        }
    }


}