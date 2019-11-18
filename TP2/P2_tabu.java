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

public class P2_tabu{

	ArrayList<Integer> agendamentos = new ArrayList<Integer>(); //vetor para alocar tempos de inicios
	ArrayList<Integer> posicoes = new ArrayList<Integer>();

	/********
		Soluçaõ inicial
	********/
	public int solucao_inicial(ArrayList<Integer> entrada, ArrayList<Integer> posicoes){
	
		int max = Collections.max(entrada);
		int index = entrada.indexOf(max);
		Collections.swap(entrada, 0, index);
		Collections.swap(posicoes, 0, index);

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
				Collections.swap(posicoes, i+1, pos_menor);
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
			return soma_atual;
		else
			return soma;		
	}
	
	/********
		Executa
	********/
	public int executa(ArrayList<Integer> entrada1){
		int s_i = 0;
		int s_j = 0;
		int soma = s_i + entrada1.get(0);
		
		for(int i = 0; i < (entrada1.size()-1); ++i){
			int min = Math.min(entrada1.get(i), entrada1.get(i+1));
			s_j = Math.abs(min - s_i);
			if(Math.abs(s_i - s_j) < min)
				s_j = min + s_i;
			s_i = s_j;	
			if((s_j+entrada1.get(i+1)) > soma){
				soma = s_j + entrada1.get(i+1);
			}
		}
		return soma;
	}
	

	public static void main(String[] args) throws ParseException {
	
		long start = System.nanoTime(); //tempo inicial
		P2_tabu inst = new P2_tabu();
		ArrayList<Integer> entrada = new ArrayList<Integer>();
		ArrayList<Integer> s_linha_pos;
		ArrayList<Integer> s_linha_proc;
		int sol_atual = 0;
		
		
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
		
		for(int i = 0; i < entrada.size(); ++i){
			inst.posicoes.add(i);
		}
		
		
		sol_atual = inst.solucao_inicial(entrada, inst.posicoes); //ordem inicial
		

		int resp = 0;
		int iter = 0;
		int melhorIter = 0;
		boolean improved = true;
		int tabuTenure = 4;
		
		int size = entrada.size();
		int [][] lista_tabu = new int[size][size];
		
		for(int i = 0; i < size; ++i){
			for(int j = 0; j < size; ++j){
				lista_tabu[i][j] = 0;
			}
		}
		
		long time_count = 0;
		long time_ms2 = 0;
		boolean aux = true;
		ArrayList<Integer> melhor_vizinho_pos = new ArrayList<Integer>(entrada);
		ArrayList<Integer> melhor_vizinho_proc = new ArrayList<Integer>(entrada);
		int melhor_sol = 0;
		int pos_1 = 0;
		int pos_2 = 0;
		while(time_ms2 < 200){

			for(int i = 0; i < (entrada.size()-1); i++){
				//contarTempo
				time_count = System.nanoTime();
				long time2 = (time_count - start);
				time_ms2 = TimeUnit.NANOSECONDS.toMillis(time2);
				//System.out.println("Time count "+ time_ms2);
				if(time_ms2 > 200)
					break;
					
				//System.out.println("Time count "+ time_ms2);
				if((iter - lista_tabu[inst.posicoes.get(i)][inst.posicoes.get(i+1)]) > tabuTenure)
				
					s_linha_pos = new ArrayList<Integer>(inst.posicoes);
					s_linha_proc = new ArrayList<Integer>(entrada);
					s_linha_pos = inst.posicoes;
					s_linha_proc = entrada;
					
					Collections.swap(s_linha_pos, i, i+1);
					Collections.swap(s_linha_proc, i, i+1);
					resp = inst.executa(s_linha_proc);
					if(aux){
						melhor_vizinho_pos = s_linha_pos;
						melhor_vizinho_proc = s_linha_proc;
						melhor_sol = resp;
						aux = false;
					}
					if(resp < melhor_sol){
						//System.out.println("entrou");
						melhor_vizinho_pos = s_linha_pos;
						melhor_vizinho_proc = s_linha_proc;
						melhor_sol = resp;
						pos_1 = i;
						pos_2 = i+1;
						
					}
				}
				entrada = melhor_vizinho_proc;
				inst.posicoes = melhor_vizinho_pos;
				lista_tabu[inst.posicoes.get(pos_1)][inst.posicoes.get(pos_2)] = iter;
				iter++;
				
		}
		System.out.println("Melhor solucao: "+melhor_sol);
		


		
		long finish = System.nanoTime(); //tempo final
		long time = (finish - start);
		long time_ms = TimeUnit.NANOSECONDS.toMillis(time);
		
		System.out.println("\nTempo de execução (ms): "+ time_ms);
		
	}
}


/*lista_tabu[inst.posicoes.get(i)][inst.posicoes.get(i+1)] = iter;
				if(iter - lista_tabu[i][i+1] > tabuTenure){*/
		
		
			/*
			int iter = 0;
			boolean improved = true;
			while(imrpoved) {
				iter++;
				improved = false;
				for(todos pares) {
					//so pra tabu if novo
					if(iterAtual - tabu[par.first, par.second] > tabuTenure) {}
						novo = swap;
						if(novo < corrente) {//na tabu esse if sai, pra permitir piora
							improved = true;
							break; //sai do for//na tabu sai
						}
					}
				}
			}
			*/	
			
			
/*
		System.out.print("\n");
		for(int i = 0; i < inst.posicoes.size(); ++i){
			System.out.print(inst.posicoes.get(i) + " ");
		}
		System.out.print("\n");
		for(int i = 0; i < inst.posicoes.size(); ++i){
			System.out.print(entrada.get(i) + " ");
		}
		System.out.println("\nSolucao inicial: "+sol_atual+"\n");
		*/	
