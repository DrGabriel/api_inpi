package br.adv.daniel.api_inpi.modelo.tentativas;

public class ArquivoTentativas {

	private Integer maxTentativas;
	private Integer maxTimeout;
	private Integer tempoEsperaTentativa;
	
	
	public Integer getMaxTentativas() {
		return maxTentativas;
	}
	public void setMaxTentativas(Integer maxTentativas) {
		this.maxTentativas = maxTentativas;
	}
	public Integer getMaxTimeout() {
		return maxTimeout;
	}
	public void setMaxTimeout(Integer maxTimeout) {
		this.maxTimeout = maxTimeout;
	}
	public Integer getTempoEsperaTentativa() {
		return tempoEsperaTentativa;
	}
	public void setTempoEsperaTentativa(Integer tempoEsperaTentativa) {
		this.tempoEsperaTentativa = tempoEsperaTentativa;
	}
}
