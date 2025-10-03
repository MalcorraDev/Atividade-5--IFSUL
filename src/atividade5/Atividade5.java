package atividade5;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Gera 350.000 alunos, adiciona cada aluno nas 3 listas, ordena as listas por
 * nome, exporta cada lista para CSV, mede o tempo (em ms) de inserção,
 * ordenação e exportação para cada tipo de lista.
 *
 *
 *
 * @author João Vitor Soares Malcorra
 */
public class Atividade5 {

    private static final int QUANTIDADE = 350_000; // número de alunos por lista
    private static final String[] NOMES = {
        "Ana", "João", "Carlos", "Maria", "Pedro", "Juliana", "Lucas", "Fernanda",
        "Gabriel", "Marcos", "Beatriz", "Rafael", "Camila", "Eduardo", "Paulo", "Larissa"
    };

    private static final String[] SOBRENOMES = {
        "Silva", "Souza", "Almeida", "Ferreira", "Oliveira", "Costa", "Rodrigues",
        "Pereira", "Lima", "Gomes", "Martins", "Barbosa", "Dias", "Rocha", "Batista"
    };

    private static final Random RNG = new Random();

    /**
     * Método principal do programa. Gera alunos, insere em ArrayList,
     * LinkedList e Vector, mede os tempos de inserção, ordenação e exportação,
     * e apresenta os resultados.
     *
     * @param args Argumentos de linha de comando (não utilizados)
     */
    public static void main(String[] args) {

        System.out.println("Iniciando geração de dados");
        // 1) gerar pool de 350k alunos 
        List<Aluno> pool = gerarPoolDeAlunos(QUANTIDADE);
        System.out.println("Pool gerado: " + pool.size() + " alunos.");

        // 2) preparar estruturas
        List<Aluno> arrayList = new ArrayList<>(QUANTIDADE);
        List<Aluno> linkedList = new LinkedList<>();
        List<Aluno> vector = new Vector<>(QUANTIDADE);

        // mapa para armazenar tempos
        Map<String, Long[]> tempos = new LinkedHashMap<>();
        // cada entrada

        // Inserção
        System.out.println("Medindo inserção");
        long t0 = System.nanoTime();
        for (Aluno a : pool) {
            arrayList.add(a);
        }
        long t1 = System.nanoTime();
        tempos.put("ArrayList", new Long[]{nanosToMs(t1 - t0), 0L, 0L});

        long t2 = System.nanoTime();
        for (Aluno a : pool) {
            linkedList.add(a);
        }
        long t3 = System.nanoTime();
        tempos.get("ArrayList")[1] = tempos.get("ArrayList")[1]; // sem-op para manter ordem
        tempos.put("LinkedList", new Long[]{nanosToMs(t3 - t2), 0L, 0L});

        long t4 = System.nanoTime();
        for (Aluno a : pool) {
            vector.add(a);
        }
        long t5 = System.nanoTime();
        tempos.put("Vector", new Long[]{nanosToMs(t5 - t4), 0L, 0L});

        // Ordenação: criar Comparator por nome (Nome + Sobrenome)
        Comparator<Aluno> cmpNome = Comparator.comparing(Aluno::getNomeCompleto, String.CASE_INSENSITIVE_ORDER);

        System.out.println("Medindo ordenação");
        long s0 = System.nanoTime();
        Collections.sort(arrayList, cmpNome);
        long s1 = System.nanoTime();
        tempos.get("ArrayList")[1] = nanosToMs(s1 - s0);

        long s2 = System.nanoTime();
        Collections.sort(linkedList, cmpNome);
        long s3 = System.nanoTime();
        tempos.get("LinkedList")[1] = nanosToMs(s3 - s2);

        long s4 = System.nanoTime();
        Collections.sort(vector, cmpNome);
        long s5 = System.nanoTime();
        tempos.get("Vector")[1] = nanosToMs(s5 - s4);

        // Exportação para CSV
        System.out.println("Medindo exportação para CSV");
        long e0 = System.nanoTime();
        exportToCSV(arrayList, "alunos_arraylist.csv");
        long e1 = System.nanoTime();
        tempos.get("ArrayList")[2] = nanosToMs(e1 - e0);

        long e2 = System.nanoTime();
        exportToCSV(linkedList, "alunos_linkedlist.csv");
        long e3 = System.nanoTime();
        tempos.get("LinkedList")[2] = nanosToMs(e3 - e2);

        long e4 = System.nanoTime();
        exportToCSV(vector, "alunos_vector.csv");
        long e5 = System.nanoTime();
        tempos.get("Vector")[2] = nanosToMs(e5 - e4);

        // Mostra tabela de tempos
        apresentarTabelaTempos(tempos);

        System.out.println("Concluído. Arquivos salvos.");
    }

    private static void apresentarTabelaTempos(Map<String, Long[]> tempos) {
        System.out.println();
        System.out.println(String.format("%-12s | %-12s | %-12s | %-12s", "Estrutura", "Cadastro (ms)", "Ordenação (ms)", "Exportação (ms)"));
        System.out.println("-------------------------------------------------------------------");
        for (Map.Entry<String, Long[]> e : tempos.entrySet()) {
            Long[] t = e.getValue();
            System.out.println(String.format("%-12s | %-12d | %-12d | %-12d", e.getKey(), t[0], t[1], t[2]));
        }
        System.out.println();
    }

    private static long nanosToMs(long nanos) {
        return nanos / 1_000_000L;
    }

    /**
     * Exporta a lista de alunos para um arquivo CSV.
     *
     * @param lista Lista de alunos a ser exportada
     * @param filename Nome do arquivo de saída
     */
    private static void exportToCSV(List<Aluno> lista, String filename) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            // cabeçalho
            bw.write("Nome,Matrícula,Data de Nascimento");
            bw.newLine();
            for (Aluno a : lista) {
                String line = String.format("\"%s\",%s,%s",
                        a.getNomeCompleto(), a.getMatricula(), sdf.format(a.getDataNascimento()));
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException ex) {
            System.err.println("Erro ao escrever " + filename + ": " + ex.getMessage());
        }
    }

    /**
     * Gera uma lista de alunos com dados aleatórios.
     *
     * @param qtd Quantidade de alunos a gerar
     * @return Lista contendo os alunos criados
     */
    private static List<Aluno> gerarPoolDeAlunos(int qtd) {

        List<Aluno> pool = new ArrayList<>(qtd);
        for (int i = 0; i < qtd; i++) {
            String nome = gerarNomeAleatorio();
            String matricula = gerarMatricula();
            Date nasc = gerarDataNascimento();
            pool.add(new Aluno(nome, matricula, nasc));
        }
        return pool;
    }

    private static String gerarNomeAleatorio() {
        String nome = NOMES[RNG.nextInt(NOMES.length)];
        String sobrenome = SOBRENOMES[RNG.nextInt(SOBRENOMES.length)];
        // combina, por exemplo "João Silva"
        return nome + " " + sobrenome;
    }

    private static String gerarMatricula() {
        // 5 números aleatórios (com zeros à esquerda se necessário)
        int n = RNG.nextInt(100000); // 0..99999
        return String.format("%05d", n);
    }

    private static Date gerarDataNascimento() {
        // gera data entre 1950 e 2010 (exemplo)
        int year = 1950 + RNG.nextInt(61); // 1950..2010
        int month = 1 + RNG.nextInt(12); // 1..12
        int day;
        switch (month) {
            case 2:
                day = 1 + RNG.nextInt(28);
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                day = 1 + RNG.nextInt(30);
                break;
            default:
                day = 1 + RNG.nextInt(31);
        }
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month - 1);
        c.set(Calendar.DAY_OF_MONTH, day);
        // normaliza horas/minutos/segundos
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * Classe que representa um aluno.
     */
    private static class Aluno {

        private final String nomeCompleto;
        private final String matricula;
        private final Date dataNascimento;

        public Aluno(String nomeCompleto, String matricula, Date dataNascimento) {
            this.nomeCompleto = nomeCompleto;
            this.matricula = matricula;
            this.dataNascimento = dataNascimento;
        }

        public String getNomeCompleto() {
            return nomeCompleto;
        }

        public String getMatricula() {
            return matricula;
        }

        public Date getDataNascimento() {
            return dataNascimento;
        }
    }
}
