package br.adv.daniel.api_inpi;


import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;
import br.adv.daniel.api_inpi.MyApplicationBinder;


public class AppConfig extends ResourceConfig {
    public AppConfig() {
        packages(true, "br.adv.daniel.api_inpi.rest");
        register(new MyApplicationBinder());
        
    }
	
}