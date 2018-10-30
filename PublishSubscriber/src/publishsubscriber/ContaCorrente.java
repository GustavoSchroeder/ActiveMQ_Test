package publishsubscriber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author gustavolazarottoschroeder
 */
public class ContaCorrente {

    private Map<Integer, Double> contaCorrente;
    private Map<Integer, List<String>> operationLog;

    public ContaCorrente(Integer size) {
        startMap(500);
    }
    
    public void printResults(){
        for (Map.Entry<Integer, Double> entry : contaCorrente.entrySet()) {
            Integer key = entry.getKey();
            Double value = entry.getValue();
            System.out.println(key + " - " + value);
        }
    }

    private void startMap(Integer size) {
        this.contaCorrente = new HashMap<>();
        this.operationLog = new HashMap<>();
        for (int i = 0; i < size; i++) {
            this.contaCorrente.put(i, randomValueGenerator(1.00, 15000.00));
            this.operationLog.put(i, new ArrayList<>());
        }
    }

    private Double randomValueGenerator(Double rangeMin, Double rangeMax) {
        return (rangeMin + (rangeMax - rangeMin) * (new Random()).nextDouble());
    }
    
    public Double realizarOp(Integer cliente, String operacao, Double valor){
        if(operacao.equalsIgnoreCase("Depositar")){
            return depositar(cliente, valor);
        }else{
            return sacar(cliente, valor);
        }
    }

    private Double sacar(Integer cliente, Double valor) {
        this.contaCorrente.put(cliente, (this.contaCorrente.get(cliente) - valor));
        return this.contaCorrente.get(cliente);
    }

    private Double depositar(Integer cliente, Double valor) {
        this.contaCorrente.put(cliente, (this.contaCorrente.get(cliente) + valor));
        return this.contaCorrente.get(cliente);
    }

    public Map<Integer, Double> getContaCorrente() {
        return contaCorrente;
    }

    public void setContaCorrente(Map<Integer, Double> contaCorrente) {
        this.contaCorrente = contaCorrente;
    }

    public Map<Integer, List<String>> getOperationLog() {
        return operationLog;
    }

    public void setOperationLog(Map<Integer, List<String>> operationLog) {
        this.operationLog = operationLog;
    }
}
