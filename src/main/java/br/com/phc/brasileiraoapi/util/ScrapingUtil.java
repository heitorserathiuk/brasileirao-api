package br.com.phc.brasileiraoapi.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.com.phc.brasileiraoapi.dto.PartidaGoogleDTO;

@Service
public class ScrapingUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(ScrapingUtil.class);
	private static final String BASE_URL_GOOGLE = "https://www.google.com.br/search?q=";
	private static final String COMPLEMENTO_URL_GOOGLE = "&hl=pt-BR";
	private static final String CASA = "casa";
	private static final String VISITANTE = "visitante";
		
	private static final String DIV_PARTIDA_ANDAMENTO = "div[class=imso_mh__lv-m-stts-cont]";
	private static final String DIV_PARTIDA_ENCERRADA = "span[class=imso_mh__ft-mtch imso-medium-font imso_mh__ft-mtchc]";
	private static final String DIV_DADOS_EQUIPE_CASA = "div[class=imso_mh__first-tn-ed imso_mh__tnal-cont imso-tnol]";
	private static final String DIV_DADOS_EQUIPE_VISITANTE = "div[class=imso_mh__second-tn-ed imso_mh__tnal-cont imso-tnol]";	
	private static final String ITEM_LOGO = "img[class=imso_btl__mh-logo]";
	private static final String DIV_PLACAR_EQUIPE_CASA = "div[class=imso_mh__l-tm-sc imso_mh__scr-it imso-light-font]";
	private static final String DIV_PLACAR_EQUIPE_VISITANTE = "div[class=imso_mh__r-tm-sc imso_mh__scr-it imso-light-font]";
	private static final String DIV_GOLS_EQUIPE_CASA = "div[class=imso_gs__tgs imso_gs__left-team]";
	private static final String DIV_GOLS_EQUIPE_VISITANTE = "div[class=imso_gs__tgs imso_gs__right-team]";
	private static final String ITEM_GOL = "div[class=imso_gs__gs-r]";
	private static final String DIV_PENALIDADES = "div[class=imso_mh_s__psn-sc]";	
	private static final String HTTPS = "https:";
	private static final String SRC = "src";
	private static final String SPAN = "span";
	private static final String PENALTIS = "Pênaltis";
	
	/*public static void main(String[] args) {
		//String PESQUISA = "parana x atletico 20/03/2021";//FUTURO
		//String PESQUISA = "cianorte x athletico 27/02/2021";//PASSADO
		//String PESQUISA = "corinthians x athletico 10/02";//MUITOS GOLS
		//String PESQUISA = "athletico x junior 12/12/18";//PENALIDADES
		//String url = BASE_URL_GOOGLE + PESQUISA.replace(" ", "+") + COMPLEMENTO_URL_GOOGLE;
		
		ScrapingUtil scraping = new ScrapingUtil();
		String url = scraping.montarUrlGoogle("são paulo", "santos");
		scraping.obtemInformacoesPartida(url);
	}*/
	
	public PartidaGoogleDTO obtemInformacoesPartida(String url) {
		PartidaGoogleDTO partida = new PartidaGoogleDTO();
		
		Document document = null;
		
		try {			
			document = Jsoup.connect(url).get();
			String title = document.title();
			LOGGER.info("Título da página: {}",title);	
			
			StatusPartida statusPartida = obtemStatusPartida(document);
			partida.setStatusPartida(statusPartida.toString());
			LOGGER.info("Status partida: {}",statusPartida);
			
			if(statusPartida != StatusPartida.PARTIDA_NAO_INICIADA) {
				String tempoPartida = obtemTempoPartida(document);
				partida.setTempoPartida(tempoPartida);
				LOGGER.info("Tempo partida: {}",tempoPartida);
				
				Integer placarEquipeCasa = recuperaPlacarEquipe(document,DIV_PLACAR_EQUIPE_CASA);
				partida.setPlacarEquipeCasa(placarEquipeCasa);
				LOGGER.info("Placar Equipe Casa: {}",placarEquipeCasa);
				Integer placarEquipeVisitante = recuperaPlacarEquipe(document, DIV_PLACAR_EQUIPE_VISITANTE);
				partida.setPlacarEquipeVisitante(placarEquipeVisitante);
				LOGGER.info("Placar Equipe Visitante: {}",placarEquipeVisitante);
				
				Integer placarEstendidoEquipeCasa = recuperaPenalidades(document, CASA);
				partida.setPlacarEstendidoEquipeCasa(placarEstendidoEquipeCasa.toString());
				LOGGER.info("Placar Estendido Equipe Casa: {}",placarEstendidoEquipeCasa);
				Integer placarEstendidoEquipeVisitante = recuperaPenalidades(document, VISITANTE);
				partida.setPlacarEstendidoEquipeCasa(placarEstendidoEquipeVisitante.toString());
				LOGGER.info("Placar Estendido Equipe Visitante: {}",placarEstendidoEquipeVisitante);
			}
			
			String nomeEquipeCasa = recuperaNomeEquipe(document,DIV_DADOS_EQUIPE_CASA);
			partida.setNomeEquipeCasa(nomeEquipeCasa);
			LOGGER.info("Nome Equipe Casa: {}",nomeEquipeCasa);
			
			String nomeEquipeVisitante = recuperaNomeEquipe(document,DIV_DADOS_EQUIPE_VISITANTE);
			partida.setNomeEquipeVisitante(nomeEquipeVisitante);
			LOGGER.info("Nome Equipe Casa: {}",nomeEquipeVisitante);
			
			String urlLogoEquipeCasa = recuperaLogoEquipe(document,DIV_DADOS_EQUIPE_CASA);
			partida.setUrlLogoEquipeCasa(urlLogoEquipeCasa);
			LOGGER.info("Logo Equipe Casa: {}",urlLogoEquipeCasa);
			
			String urlLogoEquipeVisitante = recuperaLogoEquipe(document,DIV_DADOS_EQUIPE_VISITANTE);
			partida.setUrlLogoEquipeVisitante(urlLogoEquipeVisitante);
			LOGGER.info("Logo Equipe Casa: {}",urlLogoEquipeVisitante);
			
			String golsEquipeCasa = recuperaGolsEquipe(document,DIV_GOLS_EQUIPE_CASA);
			partida.setGolsEquipeCasa(golsEquipeCasa);
			LOGGER.info("Gols Equipe Casa: {}",golsEquipeCasa);
			String golsEquipeVisitante= recuperaGolsEquipe(document,DIV_GOLS_EQUIPE_VISITANTE);
			partida.setGolsEquipeVisitante(golsEquipeVisitante);
			LOGGER.info("Gols Equipe Visitante: {}",golsEquipeVisitante);
			
			return partida;
			
		} catch (IOException e) {
			LOGGER.error("ERRO AO TENTAR CONECTAR NO GOOGLE COM JSOUP -> {}", e.getMessage());
		}
		
		return null;
	}
	
	public StatusPartida obtemStatusPartida(Document document) {
		StatusPartida statusPartida = StatusPartida.PARTIDA_NAO_INICIADA;
		
		boolean isTempoPartida = document.select(DIV_PARTIDA_ANDAMENTO).isEmpty();
		if(!isTempoPartida) {
			String tempoPartida = document.select(DIV_PARTIDA_ANDAMENTO).first().text();
			statusPartida = StatusPartida.PARTIDA_EM_ANDAMENTO;
			if(tempoPartida.contains(PENALTIS)) {
				statusPartida = StatusPartida.PARTIDA_PENALTIS;
			}
			LOGGER.info(tempoPartida);
		}
		isTempoPartida = document.select(DIV_PARTIDA_ENCERRADA).isEmpty();
		if(!isTempoPartida) {
			statusPartida = StatusPartida.PARTIDA_ENCERRADA;
		}
		
		return statusPartida;
	}
	
	public String obtemTempoPartida(Document document) {
		String tempoPartida = null;
		boolean isTempoPartida = document.select(DIV_PARTIDA_ANDAMENTO).isEmpty();
		if(!isTempoPartida) {
			tempoPartida = document.select(DIV_PARTIDA_ANDAMENTO).first().text();
		}
		
		isTempoPartida = document.select(DIV_PARTIDA_ENCERRADA).isEmpty();
		if(!isTempoPartida) {
			tempoPartida = document.select(DIV_PARTIDA_ENCERRADA).first().text();
		}
		
		return tempoPartida;		
	}
	
	public String corrigeTempoPartida(String tempo) {
		if(tempo.contains("'")){
			return tempo.replace(" ", "").replace("'", " min");
		}else  {
			return tempo;
		}
	}
	
	public String recuperaNomeEquipe(Document document, String itemHtml) {
		Element elemento = document.selectFirst(itemHtml);
		String nomeEquipe = elemento.select(SPAN).text();	
		return nomeEquipe;
	}
		
	public String recuperaLogoEquipe(Document document, String itemHtml) {
		Element elemento = document.selectFirst(itemHtml);
		String logoEquipe = elemento.select(ITEM_LOGO).attr(SRC);
		return logoEquipe;
	}
	
	public Integer recuperaPlacarEquipe(Document document, String itemHtml) {
		String placarEquipe = document.selectFirst(itemHtml).text();
		return formataPlacarStringInteger(placarEquipe);
	}
		
	public String recuperaGolsEquipe(Document document, String itemHtml) {
		List<String> golsEquipe = new ArrayList<>();
		
		Elements elementos = document.select(itemHtml).select(ITEM_GOL);
		
		for(Element e : elementos) {
			String infoGol = e.select(ITEM_GOL).text();
			golsEquipe.add(infoGol);
		}		
		return String.join(", ", golsEquipe);	
	}
	
	public Integer recuperaPenalidades(Document document, String tipoEquipe) {
		boolean isPenalidades = document.select(DIV_PENALIDADES).isEmpty();
		if(!isPenalidades) {
			String penalidades = document.select(DIV_PENALIDADES).text();
			String penalidadeCompleta = penalidades.substring(0,5).replace(" ", "");
			String[] divisao = penalidadeCompleta.split("-");			
			return tipoEquipe.equals(CASA)? formataPlacarStringInteger(divisao[0]) : formataPlacarStringInteger(divisao[1]);
		}
		return null;
	}
	
	public Integer formataPlacarStringInteger(String placar) {
		Integer valor;
		try {
			valor = Integer.parseInt(placar);
		}catch(Exception e) {
			valor = 0;
		}
		return valor;
	}
	
	public String montarUrlGoogle(String nomeEquipeCasa, String nomeEquipeVisitante) {
		try {
			String equipeCasa = nomeEquipeCasa.replace(" ", "+").replace("-", "+");
			String equipeVisitante = nomeEquipeVisitante.replace(" ", "+").replace("-", "+");
			
			return BASE_URL_GOOGLE + equipeCasa + "+x+" + equipeVisitante+ COMPLEMENTO_URL_GOOGLE;
		}catch(Exception e) {
			LOGGER.error("ERRO: {}",e.getMessage());
		}
		return null;
	}
}
