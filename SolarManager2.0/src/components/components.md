en esta carpeta incluiremos la lógica, convendria que cada componente tuviese su propia carpeta

Por ejemplo si tenemos el componente formulario -> form
Deberiamos crear una carpeta dentro de esta que se llame form e introducir toda la logica dentro 

Esta sería nuestra arquitectura de manejo de carpetas 

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