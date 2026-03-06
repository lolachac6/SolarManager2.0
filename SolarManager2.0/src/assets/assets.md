
En un proyecto de software, la carpeta assets se usa para guardar recursos que utiliza la aplicación pero que no son código.
Es decir, archivos que tu programa carga o muestra.

Por ejemplo: imagenes, iconos, logos, fondos,audios etc.. 

Nuestra arquitectura quedaría asi :

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