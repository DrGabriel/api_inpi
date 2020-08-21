package br.adv.daniel.api_inpi.bo.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Writer;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.jvnet.hk2.annotations.Service;

import br.adv.daniel.api_inpi.bo.DownloadGruBO;
import br.adv.daniel.api_inpi.modelo.gru.RetornoRequisicaoDownloadGru;
import br.adv.daniel.api_inpi.modelo.tentativas.ArquivoTentativas;

@Service
public class DownloadGRUBOImpl implements DownloadGruBO{
	
	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko)s";

	private static final String GET_URL = "https://gru.inpi.gov.br/pag/gru";

	private static final String POST_URL = "https://gru.inpi.gov.br/pag/gru";
	
	/*
	 * private static final int MAX_TENTATIVAS = 3; private static final int
	 * MAX_TEMPO = 60; private static final int TIMEOUT_MAXIMO = 70*1000; // 1 min e
	 * 10 segundos
	 */	
	
	@Inject
	private LeituraArquivoTentativasBOImpl arquivoTentativas;
	private Object lock = new Object();
	
	private static final Logger LOGGER = Logger.getLogger(DownloadGRUBOImpl.class.toString());
	public DownloadGRUBOImpl() {
		
	}
	
	
	@Override
	public RetornoRequisicaoDownloadGru download(String json, int tentativas) throws Exception{
		ArquivoTentativas arquivo = arquivoTentativas.retornaArquivo();
		try {
			LOGGER.info("Arquivo de tentativas carregado com sucesso com os seguintes parametros para essa requisição: " + 
					" maxTentativas - " + arquivo.getMaxTentativas()+
					" maxTimeout em segundos - " + arquivo.getMaxTimeout() +
					" getTempoEsperaTentativa em segundos - " + arquivo.getTempoEsperaTentativa());
			LOGGER.info("DownloadGRUBOImpl - tentativa : " + tentativas + " par ao json - " + json);

			
			return sendPOST(json, arquivo);
		
		}catch (Exception e) {
			LOGGER.info("Ocorreu um erro para o json - " + json);
			LOGGER.info("DownloadGRUBOImpl - download na tentativa " + tentativas + " - erro +" + e);
			if(tentativas < arquivo.getMaxTentativas() ) {
				tentativas ++;
				synchronized(lock) {
					lock.wait(arquivo.getTempoEsperaTentativa()*1000);
				}
				LOGGER.info("Tentando novamente requisição para o json - " + json);
				return download(json,tentativas);
			}else {
				RetornoRequisicaoDownloadGru retorno = new RetornoRequisicaoDownloadGru();
				retorno.setRecebeuPDF(false);
				retorno.setOcorreuErro(true);
				retorno.setJsonRetorno("{\"campo\": \"erro\", \"mensagem\": \'"+ e.getCause().getMessage() +"\"}");
				LOGGER.info("Fim das tentativas para o json - " + json);
				LOGGER.info("DownloadGRUBOImpl - download - erro +" + e);
				return retorno;
			}
		}
		
		
	}
	
	private static RetornoRequisicaoDownloadGru sendPOST(String json, ArquivoTentativas arquivo) throws Exception {
		RetornoRequisicaoDownloadGru retorno = new RetornoRequisicaoDownloadGru();
		Writer writer = null;
		int BUFFER_SIZE = 4096;
		File fileBoleto = null;

		try {

			CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));

			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

				@Override
				public X509Certificate[] getAcceptedIssuers() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					// TODO Auto-generated method stub

				}

				@Override
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					// TODO Auto-generated method stub

				}
			} };

			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

			HostnameVerifier allHostsValid = new HostnameVerifier() {

				@Override
				public boolean verify(String hostname, SSLSession session) {
					// TODO Auto-generated method stub
					return true;
				}
			};

			// Install the all-trusting host verifier
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
			/* End of the fix */

			URL obj = new URL(POST_URL);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			con.setReadTimeout(arquivo.getMaxTimeout() *  1000);
			
			con.setDoOutput(true);
			OutputStream os = con.getOutputStream();
			os.write(json.getBytes());
			os.flush();
			os.close();

			int responseCode = con.getResponseCode();
			LOGGER.info("POST Response Code :: " + responseCode);

			if (responseCode == HttpURLConnection.HTTP_OK) { // success
				LOGGER.info("Requisição  foi 200 OK, retornando arquivo");
				retorno.setRecebeuPDF(true);

				String fileName = "";
				String disposition = con.getHeaderField("Content-Disposition");
				int contentLength = con.getContentLength();

				if (disposition != null) {
					int index = disposition.indexOf("filename=");
					if (index > 0) {
						fileName = disposition.substring(index + 10, disposition.length());
					}
				} else {
					// extracts file name from URL
					fileName = "gruInpi";
				}
				
				retorno.setPdfFileName(fileName);
				
				LOGGER.info("Content-Disposition = " + disposition);
				LOGGER.info("Content-Length = " + contentLength);
				LOGGER.info("fileName = " + fileName);

				InputStream inputStream = con.getInputStream();
				//o random serve para que em chamadas concorrentes ao mesmo arquivo sejam criados caras com nomes diferentes
				Path tempDirWithPrefix = Files.createTempFile(fileName + ThreadLocalRandom.current().nextInt(), ".tmp");
				retorno.setPdfPath(tempDirWithPrefix);
				FileOutputStream outputStream = new FileOutputStream(tempDirWithPrefix.toString());

				int bytesRead = -1;
				byte[] buffer = new byte[BUFFER_SIZE];
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}

				outputStream.close();
				inputStream.close();
	
				
			} else {
				retorno.setRecebeuPDF(false);
				LOGGER.info("Requisição não foi 200 OK, retornando JSON");

				BufferedReader in = new BufferedReader(new InputStreamReader(con.getErrorStream(), Charset.forName("ISO-8859-1").toString()));
				String decodedString;
				StringBuilder retornoJson = new StringBuilder("");
				while ((decodedString = in.readLine()) != null) {
					retornoJson.append(decodedString);
				}
				retorno.setJsonRetorno(retornoJson.toString());
			}

		} catch (Exception e) {
			LOGGER.info("post download gru erro: " + e);
			throw (e);
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (Exception e) {
				LOGGER.info("post download gru erro: " + e);
				throw(e);
			}
		}
		retorno.setOcorreuErro(false);
		return retorno;
	}

}
