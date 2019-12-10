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
	ArrayList<Integer> pai1 = new ArrayList<Integer>();
	ArrayList<Integer> pai2 = new ArrayList<Integer>();

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
	public void copia_pais(int ind1, int ind2, int [][] pop){
		for(int i = 0; i < posicoes.size(); ++i){
			pai1.add(pop[ind1][i]);
			pai2.add(pop[ind2][i]);
		}
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
		int num_geracoes = 0;
		int max_geracoes = 2;
		
		//leitura do arquivo de entrada	
		if (args.length == 1) {
			try {
				FileReader arq = new FileReader(args[0]);
				BufferedReader lerArq = new BufferedReader(arq);
				String linha = lerArq.readLine(); // lê a primeira linha (tamanho da entrada)

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
		
		/*
		for(int i = 0; i < 40; ++i){
			for(int j = 0; j < size; ++j){
				System.out.print(populacao[i][j] + " ");
			}
			System.out.print("\nSol = "+ inst.solucoes.get(i)+"\n");
		}*/
		
		
		int ultimos = 32;//ultimos 20%
		while(num_geracoes < max_geracoes){
			ArrayList<Integer> f1 = new ArrayList<Integer>();//filhos
			ArrayList<Integer> f2 = new ArrayList<Integer>();
			ArrayList<Integer> entrada_filho1 = new ArrayList<Integer>();//filhos
			ArrayList<Integer> entrada_filho2 = new ArrayList<Integer>();
			
			rank_final = inst.ranking(inst.solucoes);
			
			pi = inst.probabilidade(2.0, (int)inst.solucoes.size(), rank_final);
			
			//probabilidaade comulativa
			ci.add(pi.get(0));
			for(int i = 1; i < pi.size(); ++i){
				ci.add(pi.get(i)+ci.get(i-1));
			}
			int index = 8;//20% nao muda
			while(index < 24){//60% da populacao
				//random
				Random r = new Random(); 
				
		    	//System.out.println(num_sorteio + " "+num_sorteio2);
		    
				int ind1 = -1;
				int ind2 = -1;
				while(ind1 == -1 || ind2 == -1){
					double num_sorteio = r.nextDouble();
					double num_sorteio2 = r.nextDouble();

					for(int i = 1; i < ci.size(); ++i){
						if(ci.get(i-1) <= num_sorteio && num_sorteio < ci.get(i) && ind1 == -1){
							ind1 = i;
						}
						if(ci.get(i-1) <= num_sorteio2 && num_sorteio2 < ci.get(i) && ind2 == -1){
							ind2 = i;
						}
						if(ind1 != -1 && ind2 != -1)
							break;				
					}
				}
				
				
				
				//System.out.println(ind1 + " "+ind2);
				inst.copia_pais(ind1, ind2, populacao);
				
				for(int i = 0; i < inst.solucoes.size(); ++i){
					double num_sorteio3 = r.nextDouble();
					if(num_sorteio3 < (1/inst.pai1.size())){
						Collections.swap(inst.pai1, i, i+1);
						Collections.swap(inst.pai2, i, i+1);
					}
				}
				//System.out.println(inst.pai1);
				//System.out.println(inst.pai2);
								System.out.println();
				for(int i = 0; i < inst.pai1.size()/2; ++i){
					if(f2.size() != size && f1.size() != size){
						f1.add(inst.pai1.get(i));
						f2.add(inst.pai2.get(i));
					}
				}
				for(int i = 0; i < size; ++i){
					if(f1.indexOf(inst.pai2.get(i)) == -1 && f1.size() != size){
						f1.add(inst.pai2.get(i));
					}
					if(f2.indexOf(inst.pai1.get(i)) == -1 && f2.size() != size){
						f2.add(inst.pai1.get(i));
					}
				}
				//System.out.println(f1);
				//System.out.println(f2);
				
				entrada_filho1 = inst.reconstroi_entrada(f1, entrada_inicial);
				entrada_filho2 = inst.reconstroi_entrada(f2, entrada_inicial);
				
				int sol1 = inst.executa(entrada_filho1);
				int sol2 = inst.executa(entrada_filho2);
				//System.out.println("sol1: "+sol1+" sol2: "+sol2);
				
					if(sol1 < inst.solucoes.get(index)){
						for(int i = 0; i < size; ++i){
							//System.out.println("index: "+index);
							populacao[index][i] = f1.get(i);
							inst.solucoes.set(index, sol1);
							index++;
							break;
						}
					}
					else{
						for(int i = 0; i < size; ++i){
							if(ultimos < 40){
								populacao[ultimos][i] = f1.get(i);
								inst.solucoes.set(ultimos, sol1);
								ultimos++;
							}
						}
					}
					if(sol2 < inst.solucoes.get(index)){
						for(int i = 0; i < size; ++i){
							//System.out.println("index: "+index);
							populacao[index][i] = f2.get(i);
							inst.solucoes.set(index, sol2);
							index++;
							break;
						}
					}
					else{
						for(int i = 0; i < size; ++i){
							if(ultimos < 40){
								populacao[ultimos][i] = f2.get(i);
								inst.solucoes.set(ultimos, sol2);
								ultimos++;
							}
						}
					}
					
				index++;
				inst.pai1.clear();
				inst.pai2.clear();
				f1.clear();
				f2.clear();
				

			}
			
			
       	int sol_min = Collections.min(inst.solucoes);
       	System.out.println("Solucao minima da geracao = "+ sol_min);
		num_geracoes++;
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
		
		for(int i = 0; i < 40; ++i){
			for(int j = 0; j < size; ++j){
				System.out.print(populacao[i][j] + " ");
			}
			System.out.println();
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
