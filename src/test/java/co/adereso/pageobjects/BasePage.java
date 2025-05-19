package co.adereso.pageobjects;

import com.microsoft.playwright.Page;

/**
 * Clase base para todos los Page Objects.
 * Define la estructura común y comportamientos que deben implementar todas las páginas.
 */
public abstract class BasePage {

    /**
     * La instancia de Page de Playwright que representa la página web actual.
     */
    protected final Page page;

    /**
     * Constructor para inicializar el Page Object con una instancia de Page.
     *
     * @param page La instancia de Page de Playwright.
     */
    public BasePage(Page page) {
        this.page = page;
    }

    /**
     * Método para navegar a la URL base de la página.
     * Cada Page Object debe implementar este método para su URL específica.
     */
    public abstract void navigate();

    /**
     * Método para verificar que la página está cargada correctamente.
     * Cada Page Object debe implementar este método para verificar sus elementos específicos.
     *
     * @return true si la página está cargada correctamente, false en caso contrario.
     */
    public abstract boolean isPageLoaded();

}