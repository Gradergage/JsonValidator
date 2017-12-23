import com.google.gson.*;
import com.sun.net.httpserver.HttpServer;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.stream.Collectors;

/**
 * Class implements Server interface
 * <p>
 * Class validates Json files
 */
public class Validator implements Server {

    @NotNull
    private final HttpServer server;
    @NotNull
    private final Gson builder;

    private static final int PORT = 80;
    private static final int CODE_OK = 200;
    private static final String ROOT = "/";

    /**
     * Checking Json file
     *
     * @throws IOException because method create() of HttpServer can throw IOException
     */
    public Validator() throws IOException {
        this.builder = new GsonBuilder().setPrettyPrinting().create();
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
        this.server.createContext(ROOT, http -> {
		int request_id=0;
            InputStreamReader input = new InputStreamReader(http.getRequestBody());
            final String jsonRequest = new BufferedReader(input).lines().collect(Collectors.joining());
            System.out.println("REQUEST:" + jsonRequest);
            String jsonResponse;
            try {
                Object object = builder.fromJson(jsonRequest, Object.class);
                jsonResponse = builder.toJson(object);
            } catch (JsonSyntaxException e) {
                String[] errorSplittedString = e.getMessage().split(".+: | at ");
                jsonResponse = builder.toJson(
                        new JsonErrorContainer(
                                e.hashCode(),
                                errorSplittedString[1],
                                "at " + errorSplittedString[2],
                                jsonRequest,
                                request_id
                        ));
            } finally {
            }
            request_id++;
            System.out.println("RESPONSE:" + jsonResponse);
            http.sendResponseHeaders(CODE_OK, jsonResponse.length());
            http.getResponseBody().write(jsonResponse.getBytes());
            http.close();
        });
    }

    /**
     *Function starts the server and waits for Json files
     *
     * @throws IOException - Validator consctructor can throw IOException
     */
    public static void main(String[] args) throws IOException {
        Validator validator = new Validator();
        validator.start();
        Runtime.getRuntime().addShutdownHook(new Thread(validator::stop));
    }

    /**
     * Method binds server to HTTP port and starts listening
     */
    @Override
    public void start() {
        this.server.start();
    }

    /**
     * Method terminates listening and frees all resources
     */
    @Override
    public void stop() {
        this.server.stop(0);
    }
}
