Utilizaremos esta carpeta para la conexión con las bases de datos que utilicemos, 
en el caso de mongodb o cualquier libreria/driver seria:

1. Nos iremos a libraries haremos botón derecho 
2.Add JAR
3. Buscaremos dentro de nuestro proyecto la carpeta lib 
4. importaremos los jar que están en la carpeta mongo
 

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class TestMongo {
    public static void main(String[] args) {
        try (MongoClient client = MongoClients.create("mongodb://localhost:27017")) {
            MongoDatabase db = client.getDatabase("NombreBaseDeDatos");
            System.out.println("Conexión correcta: " + db.getName());
        }
    }
}


El output de ejecutar este file es:

mar 06, 2026 6:20:06 PM com.mongodb.internal.diagnostics.logging.Loggers shouldUseSLF4J
ADVERTENCIA: SLF4J not found on the classpath.  Logging is disabled for the 'org.mongodb.driver' component
Conexión correcta: NombreBaseDeDatos
BUILD SUCCESSFUL (total time: 0 seconds)


No os preocupeis porque salga la advertencia, es solo un mensaje cualquiera, no tiene importancia. 

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