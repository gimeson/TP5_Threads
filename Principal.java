package trabalho5;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Formulário principal do Trabalho Prático 5 de LP2 - Java.
 *
 * @author Emerson da Silva Oliveira
 */
public class Principal extends JFrame {

    private final JTextField txtMaximo = new JTextField("1000", 12);
    private final JTextField txtPrioridade1 = new JTextField("1", 12);
    private final JTextField txtPrioridade2 = new JTextField("10", 12);

    private final JButton btnIniciar = new JButton("Iniciar");
    private final JButton btnLimpar = new JButton("Limpar");

    private final PainelContador painel1 =
            new PainelContador("Lista 1", "Fim1");

    private final PainelContador painel2 =
            new PainelContador("Lista 2", "Fim2");

    private int threadsFinalizadas;

    public Principal() {
        super("Trabalho Prático 5 - Threads");

        montarInterface();
        configurarEventos();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
    }

    private void montarInterface() {
        JPanel conteudo = new JPanel(new BorderLayout(15, 15));
        conteudo.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        JPanel campos = new JPanel(new GridLayout(3, 2, 10, 10));

        campos.add(new JLabel("Máximo:"));
        campos.add(txtMaximo);

        campos.add(new JLabel("Prioridade 1 (1 a 10):"));
        campos.add(txtPrioridade1);

        campos.add(new JLabel("Prioridade 2 (1 a 10):"));
        campos.add(txtPrioridade2);

        JPanel areaIniciar =
                new JPanel(new FlowLayout(FlowLayout.CENTER));
        areaIniciar.add(btnIniciar);

        JPanel topo = new JPanel(new BorderLayout(10, 10));
        topo.add(campos, BorderLayout.CENTER);
        topo.add(areaIniciar, BorderLayout.SOUTH);

        JPanel listas = new JPanel(new GridLayout(1, 2, 30, 0));
        listas.add(painel1);
        listas.add(painel2);

        JPanel rodape =
                new JPanel(new FlowLayout(FlowLayout.CENTER));
        rodape.add(btnLimpar);

        conteudo.add(topo, BorderLayout.NORTH);
        conteudo.add(listas, BorderLayout.CENTER);
        conteudo.add(rodape, BorderLayout.SOUTH);

        setContentPane(conteudo);
    }

    private void configurarEventos() {
        btnIniciar.addActionListener(evento -> iniciarContagens());
        btnLimpar.addActionListener(evento -> limparContagens());
    }

    private void iniciarContagens() {
        try {
            int maximo = lerInteiro(txtMaximo, "Máximo");
            int prioridade1 =
                    lerInteiro(txtPrioridade1, "Prioridade 1");
            int prioridade2 =
                    lerInteiro(txtPrioridade2, "Prioridade 2");

            if (maximo <= 0) {
                throw new IllegalArgumentException(
                        "O valor máximo deve ser maior que zero."
                );
            }

            validarPrioridade(prioridade1, "Prioridade 1");
            validarPrioridade(prioridade2, "Prioridade 2");

            Thread thread1 = new Thread(painel1, "Contador-1");
            Thread thread2 = new Thread(painel2, "Contador-2");

            thread1.setPriority(prioridade1);
            thread2.setPriority(prioridade2);

            threadsFinalizadas = 0;

            painel1.preparar(maximo, this::registrarTermino);
            painel2.preparar(maximo, this::registrarTermino);

            alterarEstadoExecucao(true);

            // Cada painel é executado por sua própria thread.
            thread1.start();
            thread2.start();

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Dados inválidos",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private int lerInteiro(JTextField campo, String nome) {
        try {
            return Integer.parseInt(campo.getText().trim());

        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(
                    "Informe um número inteiro válido em \"" + nome + "\"."
            );
        }
    }

    private void validarPrioridade(int prioridade, String nome) {
        if (prioridade < Thread.MIN_PRIORITY
                || prioridade > Thread.MAX_PRIORITY) {

            throw new IllegalArgumentException(
                    nome + " deve estar entre 1 e 10."
            );
        }
    }

    /**
     * Executado na thread de eventos ao terminar cada contador.
     */
    private void registrarTermino() {
        threadsFinalizadas++;

        if (threadsFinalizadas == 2) {
            alterarEstadoExecucao(false);
        }
    }

    private void alterarEstadoExecucao(boolean executando) {
        btnIniciar.setEnabled(!executando);
        btnLimpar.setEnabled(!executando);

        txtMaximo.setEnabled(!executando);
        txtPrioridade1.setEnabled(!executando);
        txtPrioridade2.setEnabled(!executando);
    }

    private void limparContagens() {
        painel1.limpar();
        painel2.limpar();
        txtMaximo.requestFocusInWindow();
    }

    /**
     * Ponto de entrada no próprio JFrame, sem classe Main separada.
     *
     * @param args argumentos da linha de comando
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Principal().setVisible(true);
        });
    }
}