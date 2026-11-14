import applicazione.casiuso.*;
import applicazione.interfacce.*;
import com.sun.net.httpserver.HttpServer;
import infrastruttura.persistenza.jdbc.*;
import infrastruttura.sicurezza.GestioneSessioniMemoria;
import infrastruttura.sicurezza.HashPasswordBCrypt;
import presentazione.http.RisorseStaticheHttp;
import presentazione.http.controller.*;
import presentazione.http.swagger.DocumentazioneSwagger;
import presentazione.http.swagger.RisorseSwagger;
import presentazione.http.swagger.SpecificaOpenApi;

import java.net.InetSocketAddress;

public class Main {

    public static void main(String[] args) throws Exception {
        IGestioneSessioni gestioneSessioni = new GestioneSessioniMemoria();
        SessioniServizio sessioniServizio = new SessioniServizio(gestioneSessioni);

        // Infrastructure
        IUtenteRepository userRepository = new OttieniUtente();
        IHashPassword passwordHasher = new HashPasswordBCrypt();
        IPrestazioneRepository prestazioneRepository = new OttieniPrestazioni();
        IMedicoRepository medicoRepository = new OttieniMedici();
        IDisponibilitaRepository disponibilitaRepository = new OttieniDisponibilita();
        IPrenotazioneRepository prenotazioneRepository = new RegistraPrenotazione();
        IPrenotazioniPazienteRepository prenotazioniPazienteRepository = new OttieniPrenotazioniPaziente();
        IAnnullamentoPrenotazioneRepository annullamentoRepository = new AnnullaPrenotazione();

        // Application
        LoginServizio loginServizio = new LoginServizio(userRepository, passwordHasher);
        PrestazioniServizio prestazioniServizio = new PrestazioniServizio(prestazioneRepository);
        MediciServizio mediciServizio = new MediciServizio(medicoRepository);
        DisponibilitaServizio disponibilitaServizio = new DisponibilitaServizio(disponibilitaRepository);
        PrenotazioniServizio prenotazioniServizio = new PrenotazioniServizio(prenotazioneRepository, sessioniServizio);
        MiePrenotazioniServizio miePrenotazioniServizio = new MiePrenotazioniServizio(prenotazioniPazienteRepository, sessioniServizio);
        AnnullamentoPrenotazioneServizio annullamentoServizio = new AnnullamentoPrenotazioneServizio(annullamentoRepository, sessioniServizio);

        // Presentation
        LoginController loginController = new LoginController(loginServizio, sessioniServizio);
        LogoutController logoutController = new LogoutController(sessioniServizio);
        PrestazioniController prestazioniController = new PrestazioniController(prestazioniServizio);
        MediciController mediciController = new MediciController(mediciServizio);
        DisponibilitaController disponibilitaController = new DisponibilitaController(disponibilitaServizio);
        PrenotazioniController prenotazioniController = new PrenotazioniController(prenotazioniServizio, miePrenotazioniServizio, annullamentoServizio);

        // HTTP Server
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/login", loginController);
        server.createContext("/api/logout", logoutController);
        server.createContext("/api/prestazioni", prestazioniController);
        server.createContext("/api/prestazioni/", mediciController);
        server.createContext("/api/medici/", disponibilitaController);
        server.createContext("/api/prenotazioni", prenotazioniController);

        // SWAGGER
        server.createContext("/swagger", new DocumentazioneSwagger());
        server.createContext( "/swagger-assets/",new RisorseSwagger());
        server.createContext("/openapi.yaml",new SpecificaOpenApi());

        //FRONTEND
        server.createContext("/", new RisorseStaticheHttp());

        server.setExecutor(null);
        server.start();

        System.out.println( "Server avviato su http://localhost:8080");
    }
}