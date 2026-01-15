# MonitorizaPT - IoT Environmental Monitoring System

## 📘 Project Overview
**MonitorizaPT** is a Java-based IoT simulation system developed as the final project for the **Object-Oriented Programming (POO)** course at **Instituto Politécnico da Lusofonia**.

The system simulates a network of 7 environmental sensors (Temperature, Humidity, and Air Quality) spread across Portugal. These sensors generate realistic (and occasionally erroneous) data, publish it to a public **MQTT Broker**, and display the real-time status on a local **Java Swing Dashboard**.

> **Note:** This project strictly follows the assignment requirements, including custom SHA-256 hashing validation, deterministic thread timing, and specific JSON formatting.

## 👤 Author
* **Name:** Alessandro Barreto
* **Student ID:** a22510719
* **Course:** Desenvolvimento para a Web e Dispositivos Móveis

---

## 🚀 Key Features
This application implements all mandatory functional requirements:

1.  **7 Fixed Locations:** Sensors are mapped to specific locations (e.g., *Lisboa Campus* -> Temperature, *Faro* -> Humidity) as defined in the brief.
2.  **Deterministic Cycle:** Data is generated exactly every **3333ms** using thread management.
3.  **Alert System:** Automatic alert triggering based on thresholds:
    * Temperature > 30°C
    * Humidity > 80%
    * Air Quality Index (AQI) > 50
4.  **Security & Integrity:** Implements a custom **SHA-256 hash** calculation based on the JSON payload (stripping spaces) to ensure data validity.
5.  **Robustness Testing:** Includes logic to generate "nonsense" data (e.g., -45°C or negative AQI) to test system resilience.
6.  **GUI Dashboard:** A Swing interface (`JFrame`) compliant with the layout requirements, showing a status table and real-time logs.

---

## 🛠️ Technical Architecture
The project follows a clean architecture organized in the `pt.ipluso` package:

* **`pt.ipluso.modelo`**: Core business logic. Contains the `Sensor` interface, the `SensorAbstrato` base class (Template Method pattern), and the `DadosSensor` Record (Java 14+ feature).
* **`pt.ipluso.rede`**: Networking utilities, specifically the `HashUtil` class for proper byte-to-hex SHA-256 conversion.
* **`pt.ipluso.ui`**: The User Interface layer containing `MonitorizaFrame` and table models.
* **`pt.ipluso.app`**: The entry point (`Main`) that bootstraps the sensors and the GUI.

### Dependencies
* **Java JDK 17+**
* **Maven** (Dependency Management)
* **Eclipse Paho MQTTv3** (MQTT Client)
* **Google Gson** (JSON Serialization)

---

## 📡 MQTT & Data Protocol
The system communicates using a strict JSON schema.

* **Broker:** `tcp://172.237.103.61:1883` (configurable)
* **Publish Topic:** `envira/pt/sensores/dados/<Location>`
* **Subscribe Topic:** `envira/pt/sensores/comandos/<Location>`

🧪 Testing & Validation

1. To verify the system functionality during the presentation:

2. Launch the App: The GUI will open with the status "Inativo".

3. Start a Sensor: Select a location (e.g., Évora Universidade) and click INICIAR.

4. Check Logs: The bottom log area will update every 3.3 seconds.

5. Verify Hash: A debug line can be enabled in the console to inspect the generated JSON and its SHA-256 hash.

6. Test Remote Commands: The system listens to ATIVAR/DESATIVAR commands via MQTT.