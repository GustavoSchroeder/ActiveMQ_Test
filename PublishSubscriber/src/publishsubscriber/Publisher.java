package publishsubscriber;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class Publisher {

    private ConnectionFactory factory = null;
    private Connection connection = null;
    private Session session = null;
    private Destination destination = null;
    private MessageProducer producer = null;

    public Publisher() {

    }

    public void calculaTotal(Integer i) {
        new Thread() {

            @Override
            public void run() {
                try {
                    sendMessage(i);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Publisher.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }.start();

    }

    public void sendMessage(Integer i) throws InterruptedException {
        SimpleDateFormat simpleDate = new SimpleDateFormat("HH:MM:SS");
        Long log = 0L;
        try {
            factory = new ActiveMQConnectionFactory(
                    ActiveMQConnection.DEFAULT_BROKER_URL);
            connection = factory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            destination = session.createQueue("SAMPLEQUEUE1");
            producer = session.createProducer(destination);

            TextMessage message = session.createTextMessage();
            while (true) {
                //Thread.sleep(100);
                //message.setText("Sender: " + i + " - ID: " + log.toString() + " - Teste - " + simpleDate.format(new Date()));
                message.setText(i + ";" + randomOperation() + ";" + randomValueGenerator(10.00, 10000.00) + ";" + simpleDate.format(new Date()));
                log++;
                producer.send(message);
                System.out.println("Enviado: " + message.getText());
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
    
     private Double randomValueGenerator(Double rangeMin, Double rangeMax) {
        return (rangeMin + (rangeMax - rangeMin) * (new Random()).nextDouble());
    }

    private String randomOperation() {
        int random = (int) (Math.random() * 2 + 1);
        if (random == 1) {
            return "Depositar";
        } else {
            return "Sacar";
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Sender");
        for (int i = 0; i < 500; i++) {
            //System.out.println(i);
            Publisher sender = new Publisher();
            sender.calculaTotal(i);
            Thread.sleep(2);
        }
    }
}
