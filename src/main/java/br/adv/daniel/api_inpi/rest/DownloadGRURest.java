package br.adv.daniel.api_inpi.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
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

import br.adv.daniel.api_inpi.bo.impl.DownloadGRUBOImpl;
import br.adv.daniel.api_inpi.modelo.gru.RetornoRequisicaoDownloadGru;

@Path("gru")
@RequestScoped
public class DownloadGRURest {

	private static final Logger LOGGER = Logger.getLogger(DownloadGRURest.class.toString());
	
	@Inject
	private DownloadGRUBOImpl downloadService;
	@Path("download")
    @POST
	@Produces({MediaType.APPLICATION_OCTET_STREAM,MediaType.APPLICATION_JSON})
	@Consumes(MediaType.APPLICATION_JSON)
    public Response downloadGru(@Context HttpServletRequest requestContext, String json) throws Exception {
		LOGGER.info("DownloadGRURest - requisição recebida de remote host addr: " + requestContext.getRemoteAddr().toString());
		final RetornoRequisicaoDownloadGru retorno = downloadService.download(json, 0);
		
		if(retorno.isRecebeuPDF()) {
			
			LOGGER.info("Enviando boleto para o host");
	        return Response.ok().entity(new StreamingOutput() {
	            @Override
	            public void write(final OutputStream output) throws IOException, WebApplicationException {
	                try {
	                    Files.copy(retorno.getPdfPath(), output);
	                } finally {
	                    Files.delete(retorno.getPdfPath());
	                }
	            }
	        }).header("content-disposition","attachment; filename = " + retorno.getPdfFileName()).build();

	    }else {
	    	if(retorno.isOcorreuErro()) {
	    		LOGGER.info("Erro no Resquest Enviando json para o host");
	    		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON).entity(retorno.getJsonRetorno()).build();

	    	}else {
	    		
	    		LOGGER.info("Bad Resquest Enviando json para o host");
	    		return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).entity(retorno.getJsonRetorno()).build();
	    	}
	    }
		
       
    }
}
