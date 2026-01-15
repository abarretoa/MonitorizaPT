package pt.ipluso.ui;

import pt.ipluso.modelo.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MonitorizaFrame extends JFrame implements SensorListener {

    private DefaultTableModel tableModel;
    private JTextArea logArea;
    private JLabel lblEstadoMQTT;
    private JComboBox<String> comboLocais;
    private List<Sensor> sensores;

    public MonitorizaFrame(List<Sensor> sensores) {
        this.sensores = sensores;

        setTitle("MonitorizaPT - Sensores Ambientais v1.0");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panelNorth = new JPanel(new BorderLayout());
        lblEstadoMQTT = new JLabel("Estado MQTT: [DESCONHECIDO]", SwingConstants.CENTER);
        lblEstadoMQTT.setOpaque(true);
        lblEstadoMQTT.setBackground(Color.LIGHT_GRAY);
        JButton btnTestar = new JButton("TESTAR BROKER");
        panelNorth.add(lblEstadoMQTT, BorderLayout.CENTER);
        panelNorth.add(btnTestar, BorderLayout.EAST);
        add(panelNorth, BorderLayout.NORTH);

        String[] colunas = {"ID", "Localização", "Tipo", "Valor Atual", "Alerta"};
        tableModel = new DefaultTableModel(colunas, 0);
        JTable table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        for (Sensor s : sensores) {
            if (s instanceof SensorAbstrato) {
                ((SensorAbstrato) s).setListener(this);
            }

            String localLimpo = s.getIDUnico().replace("PT-SENSOR-", "").replace("_", " ");
            Object[] linha = {s.getIDUnico(), localLimpo, "...", "Inativo", ""};
            tableModel.addRow(linha);
        }


        JPanel panelSouth = new JPanel(new BorderLayout());
        JPanel controles = new JPanel();

        comboLocais = new JComboBox<>();
        for (Sensor s : sensores) comboLocais.addItem(s.getIDUnico());

        JTextField txtIntervalo = new JTextField("3333", 5);
        JButton btnIniciar = new JButton("INICIAR");
        JButton btnParar = new JButton("PARAR");
        JButton btnLimpar = new JButton("LIMPAR LOGS");

        controles.add(comboLocais);
        controles.add(new JLabel("Intervalo:"));
        controles.add(txtIntervalo);
        controles.add(btnIniciar);
        controles.add(btnParar);
        controles.add(btnLimpar);

        logArea = new JTextArea(6, 50);
        logArea.setEditable(false);

        panelSouth.add(controles, BorderLayout.NORTH);
        panelSouth.add(new JScrollPane(logArea), BorderLayout.CENTER);
        add(panelSouth, BorderLayout.SOUTH);

        // --- LISTENERS ---
        btnTestar.addActionListener(e -> {
            // Simulação visual, pois conexão real é feita nos sensores
            lblEstadoMQTT.setText("Estado MQTT: VERDE (Ver Log)");
            lblEstadoMQTT.setBackground(Color.GREEN);
            logArea.append("Teste solicitado...\n");
        });

        btnIniciar.addActionListener(e -> enviarComando("ATIVAR"));
        btnParar.addActionListener(e -> enviarComando("DESATIVAR"));
        btnLimpar.addActionListener(e -> logArea.setText(""));
    }

    private void enviarComando(String acao) {
        String idSelecionado = (String) comboLocais.getSelectedItem();
        for (Sensor s : sensores) {
            if (s.getIDUnico().equals(idSelecionado)) {
                s.processarComando(acao);
                logArea.append("Comando manual: " + acao + " -> " + idSelecionado + "\n");
            }
        }
    }

    @Override
    public void onNovosDados(Sensor sensor, DadosSensor dados) {
        // Como os dados vêm da Thread do sensor, não posso atualizar a tabela diretamente.
        // O SwingUtilities.invokeLater garante que a interface gráfica não bloqueie.
        SwingUtilities.invokeLater(() -> {
            // Atualiza Tabela
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (tableModel.getValueAt(i, 0).equals(sensor.getIDUnico())) {
                    tableModel.setValueAt(String.format("%.2f %s", dados.valor(), dados.unidade()), i, 3);
                    tableModel.setValueAt(dados.alerta(), i, 4);
                    // Atualiza tipo dinamicamente
                    tableModel.setValueAt(sensor.getClass().getSimpleName(), i, 2);
                }
            }
            // Log formatado
            String log = dados.getTimestampFormatado() + " [INFO] Sensor " +
                    sensor.getIDUnico() + " publicou " + String.format("%.2f", dados.valor()) + "\n";
            logArea.append(log);
        });
    }
}