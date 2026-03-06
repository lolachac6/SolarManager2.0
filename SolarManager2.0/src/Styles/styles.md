En esta carpeta incluiremos los estilos css correspondientes a cada FXML 

Recordatorio de como se hace : 

<AnchorPane xmlns:fx="http://javafx.com/fxml">

    <stylesheets>
        <URL value="@style.css"/> //aqui introducimos el nombre del css
    </stylesheets>

    <children>
        <Button text="Hola" styleClass="boton"/> // le damos un styleClass
    </children>

</AnchorPane>


.boton{  //con un punto y el nombre que le hemos dado en el FXML cambiamos las propiedades del boton en este caso 
    -fx-background-color: red;
    -fx-text-fill: white;
}

Nuestra arquitectura quedaría asi:

src
 ├─ assets
 │   ├─ images
 │   ├─ icons
 │   └─ fonts
 │
 ├─ styles
 │   ├─ base.css
 │   ├─ components.css
 │   └─ theme.css
 │
 ├─ components
 │   ├─ form
 │   │   ├─ FormComponent.fxml
 │   │   ├─ FormComponentController.java
 │   │   └─ form.css
 │   │
 │   ├─ navbar
 │   │   ├─ Navbar.fxml
 │   │   ├─ NavbarController.java
 │   │   └─ navbar.css
 │   │
 │   └─ card
 │       ├─ Card.fxml
 │       ├─ CardController.java
 │       └─ card.css
 │
 ├─ DB
 │   ├─ MongoConnection.java        
 │         
 │
 └─ lib
     ├─ javafx
     │   ├─ javafx-base.jar
     │   ├─ javafx-controls.jar
     │   ├─ javafx-fxml.jar
     │   └─ javafx-graphics.jar
     │
     ├─ mongodb
     │   ├─ bson.jar
     │   ├─ mongodb-driver-core.jar
     │   └─ mongodb-driver-sync.jar
     │
     └─ ui
         └─ bootstrapfx.jar