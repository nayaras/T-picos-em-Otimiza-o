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

public class P3{

	ArrayList<Integer> agendamentos = new ArrayList<Integer>(); //vetor para alocar tempos de inicios
	ArrayList<Integer> posicoes = new ArrayList<Integer>();
	//ArrayList<ArrayList<Integer>> populacao = new ArrayList<ArrayList<Integer>>();
	ArrayList<Integer> solucoes = new ArrayList<Integer>();

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
	public ArrayList<Integer> reconstroi_entrada(ArrayList<Integer> pos, ArrayList<Integer> entrada_inicial){
		ArrayList<Integer> entrada_nova = new ArrayList<Integer>();
		for(int i = 0; i < pos.size(); ++i){
			entrada_nova.add(entrada_inicial.get(pos.get(i)));
		}
		return entrada_nova;
	
	}	
	public ArrayList<Integer> ranking(ArrayList<Integer> soluc){
		ArrayList<Integer> solucao_copia = new ArrayList<Integer>();
		ArrayList<Integer> rank = new ArrayList<Integer>();
		for(int i=0; i <soluc.size();++i){
			solucao_copia.add(soluc.get(i));
			rank.add(0);
		}
		int max = Collections.max(solucao_copia);
		for(int i = 0; i < solucao_copia.size(); ++i){
			int min = Collections.min(solucao_copia);
			int ind = solucao_copia.indexOf(min);
			rank.set(ind, i+1);
			solucao_copia.set(ind, max+1);
			
		}
		return rank;
	
	}	
	public ArrayList<Double> probabilidade(double s, int n, ArrayList<Integer> rank){
		ArrayList<Double> prob = new ArrayList<Double>();
		//p(i) = (2-s)/n+ (2* (r(i)*(s-1)))/(n(n-1))
		for(int i = 0; i < n; ++i){
			double conta = (2-s)/n+ (2* (rank.get(i)*(s-1)))/(n*(n-1));
			prob.add(conta);
		}
		return prob;
	}	
	
	public static void main(String[] args) throws ParseException {
	
		long start = System.nanoTime(); //tempo inicial
		P3 inst = new P3();
		ArrayList<Integer> entrada = new ArrayList<Integer>();
		ArrayList<Integer> entrada_inicial = new ArrayList<Integer>();
		ArrayList<Integer> entrada_random = new ArrayList<Integer>();
		ArrayList<Integer> pos_random = new ArrayList<Integer>();
		ArrayList<Integer> rank_final = new ArrayList<Integer>();
		ArrayList<Double> pi = new ArrayList<Double>();
		ArrayList<Double> ci = new ArrayList<Double>();
		
		int sol_inicial = 0;
		
		//leitura do arquivo de entrada	
		if (args.length == 1) {
			try {
				FileReader arq = new FileReader(args[0]);
				BufferedReader lerArq = new BufferedReader(arq);
				String linha = lerArq.readLine(); // lê a primeira linha (tamanho da entrada)
        		System.out.println("\nEntrada: ");
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
			entrada_inicial.add(entrada.get(i));
		}
		int size = entrada.size();
		int [][] populacao = new int[40][size];
		for(int i = 0; i < 40; ++i){
			for(int j = 0; j < size; ++j){
				populacao[i][j] = 0;
			}
		}
		
		sol_inicial = inst.solucao_inicial(entrada, inst.posicoes); //ordem inicial
		
		for(int j = 0; j < size; ++j){
			populacao[0][j] = inst.posicoes.get(j);
		}
		inst.solucoes.add(sol_inicial);
		//constrói população
		for(int i = 1; i < 40; ++i){
			Collections.shuffle(inst.posicoes);
			for(int j = 0; j < inst.posicoes.size(); ++j){
				populacao[i][j] = inst.posicoes.get(j);
			}
			entrada_random = inst.reconstroi_entrada(inst.posicoes, entrada_inicial);
			int sol_random = inst.executa(entrada_random);
			inst.solucoes.add(sol_random);
		}
		rank_final = inst.ranking(inst.solucoes);
		
		pi = inst.probabilidade(2.0, (int)inst.solucoes.size(), rank_final);
		
		//probabilidaade comulativa
		ci.add(pi.get(0));
		for(int i = 1; i < pi.size(); ++i){
			ci.add(pi.get(i)+ci.get(i-1));
		}
		//random
		Random r = new Random(); 
        double num_sorteio = r.nextDouble();

        if(num_sorteio < 1/entrada.size()){//faz swap
        	System.out.println("Num sorteado: "+ num_sorteio);
        }
        
		
		
		
	}
}
/*printar populacao
for(int i = 0; i < 40; ++i){
			for(int j = 0; j < size; ++j){
				System.out.print(populacao[i][j] + " ");
			}
			System.out.print("\nSol = "+ inst.solucoes.get(i)+"\n");
		}
		
		
	for(int j = 0; j < rank_final.size(); ++j){
			System.out.print(rank_final.get(j) + " ");
		}
		System.out.println();
		for(int j = 0; j < inst.solucoes.size(); ++j){
			System.out.print(inst.solucoes.get(j)+" ");
		}
		
		
		for(int j = 0; j < rank_final.size(); ++j){
			System.out.print(rank_final.get(j) + " ");
		}
		System.out.println();
		for(int j = 0; j < inst.solucoes.size(); ++j){
			System.out.print(inst.solucoes.get(j)+" ");
		}
		System.out.println();
		for(int j = 0; j < pi.size(); ++j){
			System.out.print(pi.get(j)+" ");
		}
		for(int i = 0; i < ci.size(); ++i){
			System.out.print(ci.get(i)+ " ");
		}
		System.out.println();
		

*/
