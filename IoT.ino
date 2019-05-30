

#include <BLEDevice.h>
#include <BLEUtils.h>
#include <BLEServer.h>

#include <WiFi.h>

#define SERVICE_UUID        "4fafc201-1fb5-459e-8fcc-c5c9c331914b"
#define CHARACTERISTIC_UUID "beb5483e-36e1-4688-b7f5-ea07361b26a8"

const char* ssid     = "";  //Enter Wi-Fi name here
const char* password = "";  //Enter Wi-Fi password here
const char* host = "";  //Enter Host name here
const char* streamId   = "....................";
const char* privateKey = "....................";

String data;
String package;


class MyCallbacks: public BLECharacteristicCallbacks {
    void onWrite(BLECharacteristic *pCharacteristic) {
      std::string value = pCharacteristic->getValue();
        package="";
      if (value.length() > 0) {
        Serial.println("*********");
        Serial.print("New value: ");
        for (int i = 0; i < value.length(); i++){
          package=package+value[i];
          Serial.print(value[i]);
        }

        Serial.println();
        Serial.println("*********");

        Serial.print("connecting to ");
    Serial.println(host);

    // Use WiFiClient class to create TCP connections
    WiFiClient client;
    const int httpPort = 80;
    if (!client.connect(host, httpPort)) {
        Serial.println("connection failed");
        return;
    }
  client.connect(host,80);
    //key is Security Key";
    data="id="+package+"&key=&place=E4-305";
    if(client.connect(host,80)){

      client.println("POST /update.php HTTP/1.1");
      client.println(String("Host: ") + host);
      client.println("Content-Type: application/x-www-form-urlencoded");
      client.print("Content-Length: ");
      client.println(data.length());
      client.println();
      client.println(data);
    }
    unsigned long timeout = millis();
    while (client.available() == 0) {
        if (millis() - timeout > 5000) {
            Serial.println(">>> Client Timeout !");
            client.stop();
            return;
        }
    }

    // Read all the lines of the reply from server and print them to Serial
    while(client.available()) {
        String line = client.readStringUntil('\r');
        Serial.print(line);
    }

    if(client.connected()){
      client.stop();
    }
    Serial.println();
    Serial.println("closing connection");
      }
    }
};

void setup() {
  Serial.begin(115200);
    delay(10);

    // We start by connecting to a WiFi network
    // Station Mode
    WiFi.mode(WIFI_STA);

    Serial.println();
    Serial.println();
    Serial.print("Connecting to ");
    Serial.println(ssid);
    WiFi.begin(ssid, password);

    while (WiFi.status() != WL_CONNECTED) {
        delay(500);
        Serial.print(".");
    }

    Serial.println("");
    Serial.println("WiFi connected");
    Serial.println("IP address: ");
    Serial.println(WiFi.localIP());
    delay(3000);

  BLEDevice::init("Campus IoT");
  
  BLEServer *pServer = BLEDevice::createServer();
  BLEService *pService = pServer->createService(SERVICE_UUID);

  BLECharacteristic *pCharacteristic = pService->createCharacteristic(
                                         CHARACTERISTIC_UUID,
                                         BLECharacteristic::PROPERTY_READ |
                                         BLECharacteristic::PROPERTY_WRITE
                                       );
  pCharacteristic->setCallbacks(new MyCallbacks());
  pCharacteristic->setValue("Hello World");
  pService->start();

  //BLEAdvertising *pAdvertising = pServer->getAdvertising();
  //pAdvertising->start();
  
  BLEAdvertising *pAdvertising = BLEDevice::getAdvertising();
  pAdvertising->addServiceUUID(SERVICE_UUID);
  pAdvertising->setScanResponse(true);
  pAdvertising->setMinPreferred(0x06);  // functions that help with iPhone connections issue
  pAdvertising->setMinPreferred(0x12);
  BLEDevice::startAdvertising();
 
  Serial.println("BLE Ready.");
}

void loop() {
  delay(2000);
}
