package gym;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ProtocolSelectionScreen extends JFrame {

    private JRadioButton tcpRadioButton;
    private JRadioButton udpRadioButton;
    private JRadioButton rmiRadioButton;
    private JRadioButton jmsRadioButton;
    private ButtonGroup protocolGroup;
    private JButton nextButton;
    private JLabel titleLabel;
    private JLabel explanationLabel;

    private String selectedProtocol;

    public ProtocolSelectionScreen() {
        // 1. Configuration de la fenêtre principale (JFrame)
        setTitle("Configuration de la Connexion");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Quitte l'application quand on ferme la fenêtre
        setSize(500, 350); // Taille de la fenêtre
        setLocationRelativeTo(null); // Centrer la fenêtre sur l'écran
        setLayout(new BorderLayout(10, 10)); // Layout principal avec des marges

        // 2. Création des composants

        // Titre
        titleLabel = new JLabel("Configuration de la Connexion");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Texte explicatif (avec HTML pour les sauts de ligne)
        explanationLabel = new JLabel(
            "<html><div style='text-align: center;'>" +
            "Veuillez sélectionner le protocole réseau à utiliser pour cette session.<br>" +
            "Ce choix impactera la manière dont l'application communique avec le serveur." +
            "</div></html>"
        );
        explanationLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Panneau pour les boutons radio
        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.Y_AXIS)); // Organisation verticale
        radioPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Marges intérieures

        tcpRadioButton = new JRadioButton("TCP");
        udpRadioButton = new JRadioButton("UDP (Attention: non fiable, pour cas spécifiques)");
        rmiRadioButton = new JRadioButton("RMI (Remote Method Invocation)");
        jmsRadioButton = new JRadioButton("JMS (Java Message Service)");

        // Groupement des boutons radio (pour qu'un seul soit sélectionnable)
        protocolGroup = new ButtonGroup();
        protocolGroup.add(tcpRadioButton);
        protocolGroup.add(udpRadioButton);
        protocolGroup.add(rmiRadioButton);
        protocolGroup.add(jmsRadioButton);

        // Sélectionner TCP par défaut
        tcpRadioButton.setSelected(true);

        // Ajout des boutons radio au panneau radio
        // On utilise des Box pour un meilleur alignement et espacement
        radioPanel.add(tcpRadioButton);
        radioPanel.add(Box.createRigidArea(new Dimension(0, 5))); // Espace
        radioPanel.add(udpRadioButton);
        radioPanel.add(Box.createRigidArea(new Dimension(0, 5))); // Espace
        radioPanel.add(rmiRadioButton);
        radioPanel.add(Box.createRigidArea(new Dimension(0, 5))); // Espace
        radioPanel.add(jmsRadioButton);

        // Bouton d'action "Suivant"
        nextButton = new JButton("Suivant");

        // 3. Ajout des listeners d'événements

        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tcpRadioButton.isSelected()) {
                    selectedProtocol = "TCP";
                } else if (udpRadioButton.isSelected()) {
                    selectedProtocol = "UDP";
                } else if (rmiRadioButton.isSelected()) {
                    selectedProtocol = "RMI";
                } else if (jmsRadioButton.isSelected()) {
                    selectedProtocol = "JMS";
                } else {
                    // Normalement, avec ButtonGroup, un sera toujours sélectionné si un l'était par défaut
                    JOptionPane.showMessageDialog(ProtocolSelectionScreen.this,
                            "Veuillez sélectionner un protocole.",
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Afficher le protocole sélectionné (pour le débogage)
                System.out.println("Protocole sélectionné : " + selectedProtocol);

                // Ici, vous allez instancier et afficher la prochaine fenêtre
                // Par exemple: new RoleSelectionScreen(selectedProtocol).setVisible(true);
                // Et fermer celle-ci: ProtocolSelectionScreen.this.dispose();

                JOptionPane.showMessageDialog(ProtocolSelectionScreen.this,
                        "Protocole choisi : " + selectedProtocol + "\nPassage à l'écran suivant (non implémenté).",
                        "Information", JOptionPane.INFORMATION_MESSAGE);
                
                // Pour l'instant, on ferme juste cette fenêtre pour l'exemple
                // ProtocolSelectionScreen.this.dispose(); 
                // new RoleSelectionScreen(selectedProtocol).setVisible(true); // Prochaine étape
            }
        });

        // 4. Organisation des composants dans la fenêtre

        // Panneau pour le titre et l'explication en haut
        JPanel topPanel = new JPanel(new BorderLayout(0,10));
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(explanationLabel, BorderLayout.CENTER);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        // Panneau pour le bouton "Suivant" en bas
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Aligner le bouton à droite
        bottomPanel.add(nextButton);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));


        // Ajout des panneaux principaux à la JFrame
        add(topPanel, BorderLayout.NORTH);
        add(radioPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Méthode pour récupérer le protocole sélectionné par l'utilisateur.
     * @return Le nom du protocole sous forme de String, ou null si non défini.
     */
    public String getSelectedProtocol() {
        return selectedProtocol;
    }

    public static void main(String[] args) {
        // Exécuter la création de l'interface Swing dans le thread de dispatch des événements (EDT)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ProtocolSelectionScreen screen = new ProtocolSelectionScreen();
                screen.setVisible(true);
            }
        });
    }
}