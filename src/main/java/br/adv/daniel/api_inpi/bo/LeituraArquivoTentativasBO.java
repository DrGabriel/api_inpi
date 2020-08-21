package br.adv.daniel.api_inpi.bo;

import org.jvnet.hk2.annotations.Contract;

import br.adv.daniel.api_inpi.modelo.tentativas.ArquivoTentativas;

@Contract
public interface LeituraArquivoTentativasBO {
	public abstract void atualizaArquivo(String json) throws Exception;
	public ArquivoTentativas retornaArquivo() throws Exception;
}
