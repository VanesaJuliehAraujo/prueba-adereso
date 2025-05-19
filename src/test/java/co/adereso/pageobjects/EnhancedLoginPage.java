package co.adereso.pageobjects;

import com.microsoft.playwright.FrameLocator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.Locator;

/**
 * Page Object mejorado para la página de inicio de sesión.
 * Incluye manejo robusto del captcha.
 */
public class EnhancedLoginPage extends LoginPage {

    /**
     * Selectores mejorados para el captcha.
     */
    private static final String[] CAPTCHA_FRAME_SELECTORS = {
            "iframe[title='reCAPTCHA'][width='304']",
            "iframe[name^='a-']",
            "iframe[src*='recaptcha']",
            "iframe.g-recaptcha"
    };

    /**
     * Constructor para inicializar el Page Object con una instancia de Page.
     *
     * @param page La instancia de Page de Playwright.
     */
    public EnhancedLoginPage(Page page) {
        super(page);
    }

    /**
     * Método mejorado para resolver el CAPTCHA con múltiples estrategias.
     * Intenta diferentes selectores para encontrar el iframe del captcha.
     */
    @Override
    public void solveCaptcha() {
        System.out.println("Intentando resolver el captcha con estrategia mejorada...");

        try {
            page.waitForTimeout(2000);

            boolean captchaSolved = false;

            for (String selector : CAPTCHA_FRAME_SELECTORS) {
                try {
                    System.out.println("Intentando con selector: " + selector);

                    Locator iframeLocator = page.locator(selector);
                    if (iframeLocator.isVisible()) {
                        FrameLocator captchaFrame = page.frameLocator(selector);

                        if (captchaFrame.getByRole(AriaRole.CHECKBOX).isVisible()) {
                            captchaFrame.getByRole(AriaRole.CHECKBOX).click();
                            System.out.println("Captcha resuelto utilizando selector: " + selector);
                            captchaSolved = true;
                            break;
                        } else if (captchaFrame.getByText("No soy un robot").isVisible()) {
                            captchaFrame.getByText("No soy un robot").click();
                            System.out.println("Captcha resuelto utilizando texto 'No soy un robot'");
                            captchaSolved = true;
                            break;
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error con selector " + selector + ": " + e.getMessage());
                }
            }

            if (!captchaSolved) {
                System.out.println("Intentando obtener todos los iframes disponibles...");

                Locator allIframes = page.locator("iframe");
                int iframeCount = 0;

                try {
                    allIframes.first().isVisible();
                    iframeCount = 1;

                    for (int i = 1; i < 100; i++) {
                        try {
                            allIframes.nth(i).isVisible();
                            iframeCount++;
                        } catch (Exception e) {
                            break;
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error solved catcha");
                }

                System.out.println("Total de iframes encontrados: " + iframeCount);

                for (int i = 0; i < iframeCount; i++) {
                    try {
                        String iframeLocator = "iframe >> nth=" + i;
                        FrameLocator frameLocator = page.frameLocator(iframeLocator);

                        if (frameLocator.getByRole(AriaRole.CHECKBOX).isVisible()) {
                            frameLocator.getByRole(AriaRole.CHECKBOX).click();
                            System.out.println("Captcha resuelto usando iframe #" + i);
                            captchaSolved = true;
                            break;
                        }
                    } catch (Exception e) {
                        System.out.println("Error iframe");
                    }
                }
            }

            if (captchaSolved) {
                page.waitForTimeout(3000);
                System.out.println("Esperando después de resolver el captcha...");
            } else {
                System.out.println("No se pudo resolver el captcha automáticamente");

                byte[] screenshot = page.screenshot();
                java.nio.file.Files.write(java.nio.file.Paths.get("captcha-no-resuelto.png"), screenshot);
            }

        } catch (Exception e) {
            System.err.println("Error al resolver captcha: " + e.getMessage());
            e.printStackTrace();
            try {
                byte[] screenshot = page.screenshot();
                java.nio.file.Files.write(java.nio.file.Paths.get("error-captcha.png"), screenshot);
            } catch (Exception ex) {
                System.err.println("No se pudo guardar la captura de pantalla");
            }
        }
    }
}