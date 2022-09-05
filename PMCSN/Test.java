import Generator.Rng;
import Network.Network;
import org.json.JSONException;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class Test {

    public static void main(String[] args) throws IOException, ParseException, JSONException {
        Network net = new Network("src/main/java/Configuration.json");
        System.out.println(net.getNextServer("id1").getId());
    }
}
