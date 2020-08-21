package br.adv.daniel.api_inpi.modelo.gru;

import java.io.FileOutputStream;
import java.nio.file.Path;

public class RetornoRequisicaoDownloadGru {
	
	private boolean recebeuPDF;
	private boolean ocorreuErro;
	private Path pdfPath;
	private String pdfFileName;
	private FileOutputStream outputStreamPdf;
	private String JsonRetorno;

	public boolean isRecebeuPDF() {
		return recebeuPDF;
	}

	public void setRecebeuPDF(boolean recebeuPDF) {
		this.recebeuPDF = recebeuPDF;
	}


	public String getPdfFileName() {
		return pdfFileName;
	}

	public void setPdfFileName(String pdfFileName) {
		this.pdfFileName = pdfFileName;
	}

	public FileOutputStream getOutputStreamPdf() {
		return outputStreamPdf;
	}

	public void setOutputStreamPdf(FileOutputStream outputStreamPdf) {
		this.outputStreamPdf = outputStreamPdf;
	}

	public Path getPdfPath() {
		return pdfPath;
	}

	public void setPdfPath(Path pdfPath) {
		this.pdfPath = pdfPath;
	}

	public String getJsonRetorno() {
		return JsonRetorno;
	}

	public void setJsonRetorno(String jsonRetorno) {
		JsonRetorno = jsonRetorno;
	}

	public boolean isOcorreuErro() {
		return ocorreuErro;
	}

	public void setOcorreuErro(boolean ocorreuErro) {
		this.ocorreuErro = ocorreuErro;
	}
	

}
