package modelo;

import java.util.ArrayList;
import java.util.List;

public class Memoria {
	
	private enum TipoComando {
		ZERAR, NUMERO, DIVISAO, MULTIPLICACAO, SUBTRACAO, SOMA, IGUALDADE, VIRGULA, MUDAR_SINAL
	};
	
	private static final Memoria INSTANCIA = new Memoria(); 
	
	private final List<MemoriaObservador> observadores = new ArrayList<>();
	
	private TipoComando ultimaOperacao = null;
	private boolean substituir = false;
	private String textoAtual = "";
	private String textoBuffer = "";
	
	private Memoria() {
		
	}
	
	public static Memoria getInstancia() {
		return INSTANCIA;
	}
	
	public String getTextoAtual() {	
		return textoAtual.isEmpty() ? "0" : textoAtual;
	}
	
	public void adicionarObservador(MemoriaObservador observador) {
		observadores.add(observador);
	}
	
	public void processarComando(String texto) {
		
		TipoComando tipoComando = detectarTipoDeComando(texto);
		
		if(tipoComando == null) {
			return;
		}
		
		else if(tipoComando == TipoComando.ZERAR) {
			textoAtual = "";
			textoBuffer = "";
			substituir = false;
			ultimaOperacao = null;
		}
		
		else if(tipoComando == TipoComando.MUDAR_SINAL) {
			
			if(textoAtual.contains("-")) {
				textoAtual = textoAtual.replace("-", "");
			}
			
			else {
				textoAtual = "-" + textoAtual;
			}
			
		}
		
		else if(tipoComando == TipoComando.NUMERO || tipoComando == TipoComando.VIRGULA) {		
			textoAtual = substituir ? texto : textoAtual + texto;
			substituir = false;
		}
		
		else {
			substituir = true;
			textoAtual = obterResultadoDaOperacao();
			ultimaOperacao = tipoComando;
			textoBuffer = textoAtual;
			
		}
		
		observadores.forEach(o -> o.valorAlterado(textoAtual));
		
	}

	private TipoComando detectarTipoDeComando(String texto) {
		
		if(texto.isEmpty() && texto.equals("0")) {
			return null;
		}
		
		try {
			Integer.parseInt(texto);
			return TipoComando.NUMERO;
		}
		catch(NumberFormatException e) {
			
			if(texto.equals("AC")) {
				return TipoComando.ZERAR;
			}
			
			if(texto.equals("x")) {
				return TipoComando.MULTIPLICACAO;
			}
			
			if(texto.equals("/")) {
				return TipoComando.DIVISAO;
			}
			
			if(texto.equals("+")) {
				return TipoComando.SOMA;
			}
			
			if(texto.equals("-")) {
				return TipoComando.SUBTRACAO;
			}
			
			if(texto.equals("=")) {
				return TipoComando.IGUALDADE;
			}
			
			if(texto.equals("±")) {
				return TipoComando.MUDAR_SINAL;
			}
			
			if(texto.equals(",") && !textoAtual.contains(",")) {
				return TipoComando.VIRGULA;
			}
			
		}
		
		return null;
		
	}
	
	private String obterResultadoDaOperacao() {
		if(ultimaOperacao == null || ultimaOperacao == TipoComando.IGUALDADE) {
			return textoAtual;
		}
		
		double valorBuffer = Double.parseDouble(textoBuffer.replace(",", "."));
		double valorAtual = Double.parseDouble(textoAtual.replace(",", "."));
		
		double resultado = 0;
		
		if(ultimaOperacao == TipoComando.SOMA) {
			resultado = valorBuffer + valorAtual;
		}
		
		else if(ultimaOperacao == TipoComando.SUBTRACAO) {
			resultado = valorBuffer - valorAtual;
		}
		
		else if(ultimaOperacao == TipoComando.MULTIPLICACAO) {
			resultado = valorBuffer * valorAtual;
		}
		
		else if(ultimaOperacao == TipoComando.DIVISAO) {
			resultado = valorBuffer / valorAtual;
		}
		
		String resultadoString = Double.toString(resultado).replace(".", ",");
		boolean isInteiro = resultadoString.endsWith(",0");
		
		resultadoString = isInteiro ? resultadoString.replace(",0", "") : resultadoString;
		
		return resultadoString;
	}
	
}
