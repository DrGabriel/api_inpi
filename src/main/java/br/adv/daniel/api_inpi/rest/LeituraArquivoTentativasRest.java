package br.adv.daniel.api_inpi.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import org.glassfish.jersey.process.internal.RequestScoped;

import br.adv.daniel.api_inpi.bo.impl.LeituraArquivoTentativasBOImpl;
import br.adv.daniel.api_inpi.modelo.gru.RetornoRequisicaoDownloadGru;

@Path("propriedades")
@RequestScoped
public class LeituraArquivoTentativasRest {
	
private static final Logger LOGGER = Logger.getLogger(LeituraArquivoTentativasRest.class.toString());
	
	@Inject
	private LeituraArquivoTentativasBOImpl leituraService;
	
	
	@Path("atualiza")
    @POST
	@Produces({MediaType.TEXT_PLAIN})
	@Consumes(MediaType.APPLICATION_JSON)
    public Response updateTentativasPropertites(@Context HttpServletRequest requestContext, String json) throws Exception {
		LOGGER.info("LeituraArquivoTentativasRest - requisição recebida de remote host addr: " + requestContext.getRemoteAddr().toString());
		LOGGER.info("LeituraArquivoTentativasRest - alterando json de configurações para: " + json);
		try {
			
			leituraService.atualizaArquivo(json);
		}catch(Exception e) {
			Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getCause().getMessage()).build();
		}
		return Response.ok().entity("Arquivo alterado com sucesso").build();
		
       
    }

}
