package br.adv.daniel.api_inpi.bo.impl;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.logging.Logger;

import org.jvnet.hk2.annotations.Service;

import com.google.gson.Gson;

import br.adv.daniel.api_inpi.bo.LeituraArquivoTentativasBO;
import br.adv.daniel.api_inpi.modelo.tentativas.ArquivoTentativas;

@Service
public class LeituraArquivoTentativasBOImpl implements LeituraArquivoTentativasBO{

	private static final Logger LOGGER = Logger.getLogger(LeituraArquivoTentativasBOImpl.class.toString());
	private static final String FILENAME = "/var/lib/tomcat9/work" + File.separator + "tentativas.json";
	@Override
	public synchronized void atualizaArquivo(String json) throws Exception {
		try {
			Gson gson = new Gson();
			
			ArquivoTentativas objetoNovo = gson.fromJson(json, ArquivoTentativas.class);
			
			ArquivoTentativas objetoAtual = null;
		    
			
			objetoAtual = this.retornaArquivo();
			
			if(objetoNovo.getMaxTentativas() != null && objetoNovo.getMaxTentativas() >= 0)
				objetoAtual.setMaxTentativas(objetoNovo.getMaxTentativas());
			if(objetoNovo.getMaxTimeout() != null && objetoNovo.getMaxTimeout() >= 0)
				objetoAtual.setMaxTimeout(objetoNovo.getMaxTimeout() );
			if(objetoNovo.getTempoEsperaTentativa() != null && objetoNovo.getTempoEsperaTentativa() >= 0)
				objetoAtual.setTempoEsperaTentativa(objetoNovo.getTempoEsperaTentativa());
			
			String jsonAtualizado = gson.toJson(objetoAtual);
			
			File file = new File(FILENAME);
			FileWriter writer = new FileWriter(file, false);
		    writer.write(jsonAtualizado);
		    writer.flush();
		    writer.close();

			
		}catch(Exception e) {
			LOGGER.info("LeituraArquivoTentativasBOImpl - atualizaArquivo - Ocorreu um erro: " + e);
			throw (e);
		}
		
	}

	@Override
	public synchronized ArquivoTentativas retornaArquivo() throws Exception {
		ArquivoTentativas objetoAtual = null;
		try {
			
			File file = new File(FILENAME);
			Gson gson = new Gson();
			if (!file.exists()){
				file.createNewFile();
				objetoAtual = new ArquivoTentativas();
				objetoAtual.setMaxTentativas(3);
				objetoAtual.setMaxTimeout(20);
				objetoAtual.setTempoEsperaTentativa(2);
				FileWriter writer = new FileWriter(file);
				writer.write(gson.toJson(objetoAtual));
				writer.flush();
				writer.close();
			}else {
				String jsonAtual = Files.readString(Paths.get(FILENAME));
				objetoAtual = gson.fromJson(jsonAtual, ArquivoTentativas.class);
			}
		}catch(Exception e) {
			LOGGER.info("LeituraArquivoTentativasBOImpl - retornaArquivo - Ocorreu um erro: " + e);
			throw (e);
		}
   
		return objetoAtual;
	}
	
}
