package br.adv.daniel.api_inpi.bo;

import org.jvnet.hk2.annotations.Contract;

import br.adv.daniel.api_inpi.modelo.gru.RetornoRequisicaoDownloadGru;

@Contract
public interface DownloadGruBO {
	
	public RetornoRequisicaoDownloadGru download(String json, int tentativas) throws Exception;
}
