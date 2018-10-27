package publishsubscriber;

import java.text.SimpleDateFormat;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

/**
 *
 * @author gustavolazarottoschroeder
 */
public class Receiver {

    private ConnectionFactory factory = null;
    private Connection connection = null;
    private Session session = null;
    private Destination destination = null;
    private MessageConsumer consumer = null;
    private ContaCorrente contaCorrente;

    public Receiver() {
        this.contaCorrente = new ContaCorrente(500);
    }

    public void receberTotal() {
        new Thread() {
            @Override
            public void run() {
                receiveMessage();

            }
        }.start();

    }

    public void receiveMessage() {
        //SimpleDateFormat simpleDate = new SimpleDateFormat("HH:MM:SS");
        try {
            factory = new ActiveMQConnectionFactory(
                    ActiveMQConnection.DEFAULT_BROKER_URL);
            connection = factory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            destination = session.createQueue("SAMPLEQUEUE1");
            consumer = session.createConsumer(destination);

            while (true) {
                //Thread.sleep(1000);

                Message message = consumer.receive();

                if (message instanceof TextMessage) {
                    TextMessage text = (TextMessage) message;
                    String[] info = text.getText().split(";");
                    System.out.println(info[1] + ": - Cliente: " + info[0] + " - Saldo: " + this.contaCorrente.realizarOp(Integer.parseInt(info[0]), info[1], Double.parseDouble(info[2])) + " - " + message.getJMSTimestamp());
                    //System.out.println("Mensagem recebida: " + text.getText() + " - " + message.getJMSTimestamp());
                }
            }

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
    

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Receiver");
        for (int i = 0; i < 3; i++) {
            Receiver receiver = new Receiver();
            receiver.receberTotal();
        }
    }
}
