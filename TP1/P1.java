import java.util.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.*;
import java.text.ParseException;
import java.util.concurrent.TimeUnit;

public class P1{

	public static void main(String[] args) throws ParseException {
	
		long start = System.nanoTime(); //tempo inicial
		ArrayList<Integer> entrada = new ArrayList<Integer>();
		ArrayList<Integer> agendamentos = new ArrayList<Integer>(); //vetor para alocar tempos de inicios
		
		//leitura do arquivo de entrada	
		if (args.length == 1) {
			try {
				FileReader arq = new FileReader(args[0]);
				BufferedReader lerArq = new BufferedReader(arq);
				String linha = lerArq.readLine(); // lê a primeira linha (tamanho da entrada)
        		//System.out.println("\nEntrada: ");
				while (linha != null) {
					//System.out.printf("%s\n", linha);
					entrada.add(Integer.parseInt(linha));
					linha = lerArq.readLine(); // lê da segunda até a última linha
				}
				arq.close();
			} catch (IOException e) {
				System.err.printf("Erro na abertura do arquivo: %s. Abrir com: java P1 entrada.extensao \n",
				e.getMessage());
			}
		}
		entrada.remove(0); //remove o tamanho 
		
		int max = Collections.max(entrada);
		int index = entrada.indexOf(max);
		Collections.swap(entrada, 0, index);

		int p1 = entrada.get(0);
		int s_i = 0;
		int s_j = 0;
		int pos_menor = 0;
		int menor = Integer.MAX_VALUE;
		int soma = 0;
		int soma_atual = 0;
		agendamentos.add(s_i);
		for(int i = 0; i < entrada.size()-1; ++i){
			soma_atual = 0;
			for(int j = i+1; j < entrada.size(); ++j){	
				int min = Math.min(entrada.get(i), entrada.get(j));
				
				s_j = Math.abs(min - s_i);
				if(Math.abs(s_i - s_j) < min)
					s_j = min + s_i;
					
				if(s_j < menor){
					menor = s_j;
					pos_menor = j;
				}
			}
			soma_atual = s_i + entrada.get(i);
			
			
			//System.out.print("s_: "+s_i + " |p_: " + entrada.get(i) + " | "+soma_atual + "\n");
			
			if(pos_menor != i+1 && (i+1) < entrada.size()){
				Collections.swap(entrada, i+1, pos_menor); //troca menor s_j encontrado de lugar com processamento da pos i+1
			}
			if(soma_atual > soma)
				soma = soma_atual;
				
			s_i = menor;
			agendamentos.add(s_i); 
			
			menor = Integer.MAX_VALUE;
		}
		
		soma_atual = s_j +entrada.get(entrada.size()-1); //calcula soma da ultima pos do vetor
		//System.out.print("s_: "+s_j + " |p_: " + entrada.get(entrada.size()-1) + " | "+  soma_atual + "\n");
		if(soma_atual > soma)
			System.out.println("Ponto de termino da ultima tarefa executada = "+soma_atual);
		else
			System.out.println("Ponto de termino da ultima tarefa executada = "+soma);
		
		
		long finish = System.nanoTime(); //tempo final
		long time = (finish - start);
		long time_ms = TimeUnit.NANOSECONDS.toMillis(time);
		
		System.out.println("\nTempo de execução (ms): "+ time_ms);
	}
}
