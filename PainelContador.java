package trabalho5;

import java.awt.BorderLayout;
import java.lang.reflect.InvocationTargetException;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * Painel que apresenta os valores produzidos por uma thread.
 *
 * @author Emerson da Silva Oliveira
 */
public class PainelContador extends JPanel implements Runnable {

    private final DefaultListModel<Integer> modelo;
    private final JList<Integer> lista;
    private final JLabel lblFim;

    private int valorMaximo;
    private Runnable aoTerminar;

    public PainelContador(String titulo, String mensagemFinal) {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createTitledBorder(titulo));

        modelo = new DefaultListModel<>();
        lista = new JList<>(modelo);
        lista.setVisibleRowCount(12);

        lblFim = new JLabel(mensagemFinal, SwingConstants.CENTER);
        lblFim.setVisible(false);

        add(new JScrollPane(lista), BorderLayout.CENTER);
        add(lblFim, BorderLayout.SOUTH);
    }

    /**
     * Configura a próxima execução.
     * Deve ser chamado na thread de eventos, antes de iniciar a contagem.
     */
    public void preparar(int valorMaximo, Runnable aoTerminar) {
        this.valorMaximo = valorMaximo;
        this.aoTerminar = aoTerminar;
        limpar();
    }

    /**
     * Limpa a lista e oculta o indicador de término.
     * Deve ser chamado na thread de eventos.
     */
    public void limpar() {
        modelo.clear();
        lblFim.setVisible(false);
    }

    @Override
    public void run() {
        boolean terminou = false;

        try {
            // Conta de 1 até o máximo informado, inclusive.
            for (long numero = 1; numero <= valorMaximo; numero++) {
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }

                final int valorAtual = (int) numero;

                // Aguarda a atualização da interface antes de continuar.
                SwingUtilities.invokeAndWait(() -> {
                    modelo.addElement(valorAtual);
                    lista.ensureIndexIsVisible(modelo.size() - 1);
                });
            }

            terminou = true;

        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();

        } catch (InvocationTargetException ex) {
            ex.getCause().printStackTrace();

        } finally {
            final boolean contagemConcluida = terminou;

            SwingUtilities.invokeLater(() -> {
                // Só exibe Fim quando a contagem terminou normalmente.
                lblFim.setVisible(contagemConcluida);

                if (aoTerminar != null) {
                    aoTerminar.run();
                }
            });
        }
    }
}