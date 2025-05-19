Feature: Edición de bots
  Como usuario administrador
  Quiero poder editar bots existentes
  Para actualizar su información

  Background: Usuario inicia sesión
    Given El usuario accede al buscador google
    And El usuario copia la siguiente LINK https://broly.adere.so/
    When El usuario ingresa sus credenciales
    And El usuario da clic en ingresar
    When Se valida que los datos sean correctos
    Then El usuario da clic en el modulo de Administrador
    And luego da clic en la opcion de Adereso Studio

  @Smoke @Edicion
  Scenario: Edición exitosa de un bot existente
    # Crear un nuevo bot para editarlo
    And El usuario da clic nuevamente pero en la opcion aniadir Bot
    And El usuario llena los campos con nombre "bot_editable" url "https://broly.adere.so/editable" displayName "Bot Editable" y descripcion "Descripción inicial"
    And El usuario selecciona la zona horaria "America/Bogota"
    Then Clic en el boton crear
    And Se verifica que aparece mensaje de confirmación
    And Se verifica que el bot "Bot Editable" aparece en la lista

      # Volver a la sección de Bots
    And El usuario da clic en la sección Bots
    # Editar el bot creado
    When El usuario selecciona el bot "Bot Editable"
    And El usuario hace clic en el botón de edición
    And El usuario edita la descripción del bot a "Descripción actualizada"

    # Verificar que el bot editado aparece en la lista
    And El usuario da clic en la sección Bots
    And Se verifica que el bot "Bot Editable" aparece en la lista

  @Regression @Edicion
  Scenario: Método completo para edición de bot
    # Crear un nuevo bot para editarlo
    And El usuario da clic nuevamente pero en la opcion aniadir Bot
    And El usuario llena los campos con nombre "bot_edicion_rapida" url "https://broly.adere.so/rapido" displayName "Bot Rápido" y descripcion "Descripción inicial"
    And El usuario selecciona la zona horaria "America/Bogota"
    Then Clic en el boton crear
    And Se verifica que aparece mensaje de confirmación

    # Editar usando el método completo
    And El usuario edita completamente el bot "Bot Rápido" con nueva descripción "Descripción con método único"

    # Verificar que el bot editado aparece en la lista
    And El usuario da clic en la sección Bots
    And Se verifica que el bot "Bot Rápido" aparece en la lista