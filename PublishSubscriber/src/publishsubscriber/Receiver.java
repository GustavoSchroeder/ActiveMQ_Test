package publishsubscriber;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    List<String> mensagensRecebidas;

    public Receiver() {
        this.contaCorrente = new ContaCorrente(500);
        this.mensagensRecebidas = new ArrayList<>();
    }

    public void receberTotal() {
        final Thread t;
        t = new Thread() {
            @Override
            public void run() {
                try {
                    receiveMessage();
                } catch (InterruptedException ex) {
                    try {
                        writeFile(mensagensRecebidas);
                    } catch (IOException e) {
                        Logger.getLogger(Receiver.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    contaCorrente.printResults();
                }
            }
        };
        t.start();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                System.out.println("30 Seconds Later");
                if (null != t) {
                    t.interrupt();
                }
            }
        }, 300000);

    }

    public void receiveMessage() throws InterruptedException {

        try {
            factory = new ActiveMQConnectionFactory(
                    ActiveMQConnection.DEFAULT_BROKER_URL);
            connection = factory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            destination = session.createQueue("SAMPLEQUEUE1");
            consumer = session.createConsumer(destination);

            while (true) {
                Message message = consumer.receive();

                if (message instanceof TextMessage) {
                    TextMessage text = (TextMessage) message;

                    try {
                        String[] info = text.getText().split(";");
                        mensagensRecebidas.add(text.getText() + message.getJMSTimestamp());
                        this.contaCorrente.realizarOp(Integer.parseInt(info[0]), info[1], Double.parseDouble(info[2]));
                        //System.out.println(info[1] + ": - Cliente: " + info[0] + " - Saldo: " + this.contaCorrente.realizarOp(Integer.parseInt(info[0]), info[1], Double.parseDouble(info[2])) + " - " + message.getJMSTimestamp());
                        //System.out.println("Mensagem recebida: " + text.getText() + " - " + message.getJMSTimestamp());
                    } catch (NumberFormatException e) {

                    }
                }
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private void writeFile(List<String> result) throws IOException {
        String content = "";
        for (String output : result) {
            content += (output) + "\n";
        }

        BufferedWriter bw = null;
        FileWriter fw = null;

        try {
            fw = new FileWriter("/output/receiver.csv");
            bw = new BufferedWriter(fw);
            bw.write(content);

            System.out.println("Done");

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {
                if (bw != null) {
                    bw.close();
                }

                if (fw != null) {
                    fw.close();
                }

            } catch (IOException ex) {

                ex.printStackTrace();

            }

        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Receiver");
        for (int i = 0; i < 10; i++) {
            Receiver receiver = new Receiver();
            receiver.receberTotal();
        }
    }
}
