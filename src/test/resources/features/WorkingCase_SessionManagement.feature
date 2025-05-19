Feature: Gestión de sesión en Adereso
  Como usuario administrador
  Quiero poder iniciar y cerrar sesión en la plataforma
  Para gestionar de forma segura mis accesos

  @Session @Smoke
  Scenario: Inicio de sesión, acceso a Studio y cierre de sesión
    Given El usuario accede a la página de login de Adereso
    When El usuario ingresa sus credenciales sesion
    And El usuario da clic en el botón de iniciar sesión
    When Se valida que los datos sean correctos sesion
    Then El usuario da clic en el modulo de Administrador sesion
    And El usuario accede a Adereso Studio
    And El usuario activa un switch en la página de Studio
    And El usuario regresa a la página principal
    And El usuario cierra la sesión

  @Session @Regression
  Scenario: Ciclo completo de sesión con múltiples accesos
    Given El usuario accede a la página de login de Adereso
    When El usuario ingresa sus credenciales sesion
    And El usuario da clic en el botón de iniciar sesión
    When Se valida que los datos sean correctos sesion
    Then El usuario da clic en el modulo de Administrador sesion
    And El usuario accede a Adereso Studio
    And El usuario regresa a la página principal
    And El usuario cierra la sesión
    And El usuario accede a la página de login de Adereso
    When El usuario ingresa sus credenciales sesion
    And El usuario da clic en el botón de iniciar sesión
    When Se valida que los datos sean correctos sesion
    Then El usuario da clic en el modulo de Administrador sesion
    And El usuario accede a Adereso Studio

  @Session @SwitchTest
  Scenario: Prueba específica para activación de switch
    Given El usuario accede a la página de login de Adereso
    When El usuario ingresa sus credenciales sesion
    And El usuario da clic en el botón de iniciar sesión
    When Se valida que los datos sean correctos sesion
    Then El usuario da clic en el modulo de Administrador sesion
    And El usuario accede a Adereso Studio
    And El usuario activa el switch usando el método avanzado
    And El usuario regresa a la página principal