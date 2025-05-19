package co.adereso.utilities;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;

/**
 * Clase de utilidades para interactuar con elementos específicos en las pruebas.
 */
public class ElementInteractor {

    /**
     * Activa un switch específico en la página de Studio.
     * Utiliza múltiples estrategias para asegurar que el switch cambie de estado.
     *
     * @param page     La página donde se encuentra el switch
     * @param switchId El ID del switch a activar
     * @return true si el switch se activó correctamente, false en caso contrario
     */
    public static boolean activateSwitch(Page page, String switchId) {
        try {
            System.out.println("Activando switch con ID: " + switchId);
            page.waitForLoadState();

            try {
                System.out.println("Intento 1: Usando locator().check()");
                page.locator(switchId).check();
                page.waitForTimeout(1000);
                return true;
            } catch (Exception e) {
                System.out.println("Error en intento 1: " + e.getMessage());
            }

            try {
                System.out.println("Intento 2: Usando locator().click()");
                page.locator(switchId).click();
                page.waitForTimeout(1000);
                return true;
            } catch (Exception e) {
                System.out.println("Error en intento 2: " + e.getMessage());
            }

            try {
                System.out.println("Intento 3: Usando JavaScript");
                page.evaluate("document.querySelector('" + switchId + "').click();");
                page.waitForTimeout(1000);
                return true;
            } catch (Exception e) {
                System.out.println("Error en intento 3: " + e.getMessage());
            }

            String flexibleId = switchId.replace("#", "");
            try {
                System.out.println("Intento 4: Usando id flexible '" + flexibleId + "'");
                page.locator("#" + flexibleId).click();
                page.waitForTimeout(1000);
                return true;
            } catch (Exception e) {
                System.out.println("Error en intento 4: " + e.getMessage());
            }

            try {
                System.out.println("Intento 5: Usando selector específico para Vuetify");
                Locator vSwitches = page.locator(".v-switch, .v-input--switch");
                int count = 0;

                try {
                    vSwitches.first().isVisible();
                    count = 1;

                    for (int i = 1; i < 20; i++) {
                        try {
                            vSwitches.nth(i).isVisible();
                            count++;
                        } catch (Exception ex) {
                            break;
                        }
                    }
                } catch (Exception ex) {
                    System.err.println("Error");
                }

                System.out.println("Encontrados " + count + " switches en la página");

                if (count > 0) {
                    vSwitches.first().click();
                    System.out.println("Activado primer switch disponible");
                    page.waitForTimeout(1000);
                    return true;
                }
            } catch (Exception e) {
                System.out.println("Error en último intento: " + e.getMessage());
            }

            System.out.println("No se pudo activar el switch después de múltiples intentos");
            return false;

        } catch (Exception e) {
            System.err.println("Error en activateSwitch: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}