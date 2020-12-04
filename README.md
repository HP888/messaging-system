# messaging-system

Packet-based messaging system.

# Examples

**How to run server?**

You can specify port and host in java arguments for example:
java -Dhost=0.0.0.0 -Dport=9999 -jar messaging-server-1.0-SNAPSHOT.jar

or just run application:
java -jar messaging-server-1.0-SNAPSHOT.jar

# Using as API

Add this code to your pom.xml file.

```xml
    <repositories>
        <repository>
            <id>safemc-repository</id>
            <url>https://safemc.pl/repository/releases</url>
        </repository>
    </repositories>
```

And if you want to create your own MessengerClient implementation:

```xml
    <dependencies>
        <dependency>
            <groupId>me.hp888</groupId>
            <artifactId>messaging-client</artifactId>
            <version>1.1-SNAPSHOT</version>
        </dependency>
    </dependencies>
```

or if you want to use our plugin for bukkit/velocity/bungee:

```xml
    <dependencies>
        <dependency>
            <groupId>me.hp888</groupId>
            <artifactId>messenger-plugin</artifactId>
            <version>1.1-SNAPSHOT</version>
        </dependency>
    </dependencies>
```

**Example packet**

```java
public final class ExamplePacket implements Packet {

  private String text;
  
  public ExamplePacket() {
  }
  
  public ExamplePacket(String text) {
    this.text = text;
  }
  
  public String getText() {
    return text;
  }
 
  public void setText(String text) {
    this.text = text;
  }
  
  @Override
  public void processPacket(PacketHandler handler) {
    if (!(handler instanceof ExamplePacketHandler)) {
      return;
    }
    
    ((ExamplePacketHandler) handler).handlePacket(this);
  }

}
```

**Example packet handler**

```java
public final class ExamplePacketHandler implements PacketHandler {

  private final Client client;
  
  public ExamplePacketHandler(Client client) {
    this.client = client;
  }

  public void handlePacket(ExamplePacket packet) {
    System.out.println("Message: " + packet.getText());
  }
  
  public void handlePacket(ExampleCallbackPacket packet) {
    if (packet.isResponse()) {
      return;
    }
    
    packet.setText("test");
    packet.setResponse();
    
    client.sendPacket(packet); // sending packet back to handle callback
  }

}
```

**Example CallbackPacket**

```java
public final class ExampleCallbackPacket extends CallbackPacket {

  private String text;

  public ExampleCallbackPacket() {
  }

  public ExampleCallbackPacket(String text) {
    this.text = text;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  @Override
  public void processPacket(PacketHandler handler) {
    if (!(handler instanceof ExamplePacketHandler)) {
      return;
    }
    
    ((ExamplePacketHandler) handler).handlePacket(this);
  }

}
```

**Example connection to server**

```java
final Client client = new MessengerClient();
client.connect(new InetSocketAddress("127.0.0.1", 9999));
```

**Subscribing packets**

How it works?
Only subscribed packets can be fully readed by the client, so if you subscribe ExamplePacket and this packet will be sent by any client Packet#processPacket method will be called.

```java
client.subscribePackets(ExamplePacket.class.getName());
```

**Adding packet handlers**

```java
client.addPacketHandler(new ExamplePacketHandler());
```

**Removing packet handlers**

```java
client.removePacketHandler(ExamplePacketHandler.class);
```

**Sending packets**

```java
client.sendPacket(new ExamplePacket("test"));
```

**Sending packets with callback**
```java
client.sendPacket(new ExampleCallbackPacket(), new Callback<ExampleCallbackPacket>() {
    @Override
    public void done(ExampleCallbackPacket packet) {
      System.out.println("Response message: " + packet.getText());
    }

    @Override
    public void error(String message) {
      System.out.println("Error message: " + message);
    }
});
```
