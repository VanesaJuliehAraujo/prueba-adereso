Feature: WorkingCase_CreaciónIntención
  Como usuario administrador
  Quiero poder crear nuevos bots
  Para ampliar las funcionalidades de la plataforma

  Background: Usuario inicia sesión
    Given El usuario accede al buscador google
    And El usuario copia la siguiente LINK https://broly.adere.so/
    When El usuario ingresa sus credenciales
    And El usuario da clic en ingresar
    When Se valida que los datos sean correctos
    Then El usuario da clic en el modulo de Administrador
    And luego da clic en la opcion de Adereso Studio

  @Intent @Smoke
  Scenario: Creación y verificación de intenciones
  # Primero creamos un bot
    And El usuario da clic nuevamente pero en la opcion aniadir Bot
    And El usuario llena los campos con nombre "bot_intenciones" url "https://broly.adere.so/intentbot" displayName "Bot de Intenciones" y descripcion "Bot para probar intenciones"
    And El usuario selecciona la zona horaria "America/Bogota"
    Then Clic en el boton crear
    And Se verifica que aparece mensaje de confirmación
      # Volver a la sección de Bots
    And El usuario da clic en la sección Bots
    And Se verifica que el bot "Bot de Intenciones" aparece en la lista

  @Smoke
  Scenario: Creación de un bot con validación de mensaje de confirmación
    And El usuario da clic nuevamente pero en la opcion aniadir Bot
    And El usuario llena los campos
    Then Clic en el boton crear
    And Se verifica que aparece mensaje de confirmación
    And Se verifica que el bot "automatizacion test" aparece en la lista

  @Regression @Multiple
  Scenario Outline: Creación de múltiples bots con diferentes configuraciones
    And El usuario da clic nuevamente pero en la opcion aniadir Bot
    And El usuario llena los campos con nombre "<nombre>" url "<url>" displayName "<displayName>" y descripcion "<descripcion>"
    And El usuario selecciona la zona horaria "<zonaHoraria>"
    Then Clic en el boton crear
    And Se verifica que aparece mensaje de confirmación
    And Se verifica que el bot "<displayName>" aparece en la lista

    Examples:
      | nombre | url | displayName | descripcion | zonaHoraria |
      | bot_soporte  | https://broly.adere.so/test1 | Soporte | Bot para atención de soporte | America/Bogota |
      | bot_ventas   | https://broly.adere.so/test2 | Ventas | Bot para gestión de ventas | America/Lima |
      | bot_capacitacion | https://broly.adere.so/test3 | Capacitación | Bot para capacitación de usuarios | America/Mexico_City |

  @Validacion @Negative
  Scenario: Validación de nombres duplicados
  # Primero creamos un bot
    And El usuario da clic nuevamente pero en la opcion aniadir Bot
    And El usuario llena los campos con nombre "bot_duplicado" url "https://broly.adere.so/duplicado" displayName "Bot Duplicado" y descripcion "Bot para prueba de duplicados"
    And El usuario selecciona la zona horaria "America/Bogota"
    Then Clic en el boton crear
    And Se verifica que aparece mensaje de confirmación
    And Se verifica que el bot "Bot Duplicado" aparece en la lista

  # Volver a la sección de Bots
    And El usuario da clic en la sección Bots

  # Intentamos crear otro bot con el mismo nombre
    And El usuario da clic nuevamente pero en la opcion aniadir Bot
    And El usuario llena los campos con nombre "bot_duplicado" url "https://broly.adere.so/duplicado2" displayName "Bot Duplicado 2" y descripcion "Bot para prueba de duplicados"
    And El usuario selecciona la zona horaria "America/Bogota"
    Then Clic en el boton crear
    And Se verifica que aparece mensaje de error de tipo "duplicado"