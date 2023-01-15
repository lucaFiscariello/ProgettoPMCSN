package Experiment;

import Code.Simulation.Horizon.Long.BatchMeans;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class MainVerifica {

    public static void main(String[] args) throws IOException, ParseException {

        //Selezionare la verifica da effettuare
        String verificaMulti_1 = "src/main/java/ConfigurationFile/VerificaMultiServer_1.json";
        String verificaMulti_2 = "src/main/java/ConfigurationFile/VerificaMultiServer_2.json";
        String verificaMulti_3 = "src/main/java/ConfigurationFile/VerificaMultiServer_3.json";

        String verificaSingle_1 = "src/main/java/ConfigurationFile/VerificaSingleServer_1.json";
        String verificaSingle_2 = "src/main/java/ConfigurationFile/VerificaSingleServer_2.json";
        String verificaSingle_3 = "src/main/java/ConfigurationFile/VerificaSingleServer_3.json";   //Con pareto si sceglie alpha=2.100 e k=2.0952

        String verificaSottoRete_1 = "src/main/java/ConfigurationFile/VerificaSottorete_1.json";
        String verificaSottoRete_2 = "src/main/java/ConfigurationFile/VerificaSottorete_2.json";
        String verificaSottoRete_3 = "src/main/java/ConfigurationFile/VerificaSottorete_3.json";
        String verificaSottoRete_4 = "src/main/java/ConfigurationFile/VerificaSottorete_4.json";

        BatchMeans batchMeans = new BatchMeans(1,100000,verificaMulti_1);
        batchMeans.simule();
    }


}
