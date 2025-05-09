package gym;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DashboardClient extends JFrame {

    private String nomClient;

    // Composants du contenu principal
    private JLabel labelBienvenue, labelProchainCours, labelAbonnement, labelMessages;

    public DashboardClient(String nomClient) {
        this.nomClient = nomClient;
        initUI();
    }

    private void initUI() {
        setTitle("Tableau de Bord - Client");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // === Barre de navigation ===
        JPanel sideBar = new JPanel();
        sideBar.setLayout(new GridLayout(8, 1));
        sideBar.setPreferredSize(new Dimension(200, 600));
        sideBar.setBackground(new Color(45, 45, 45));

        String[] navItems = {
            "Accueil", "Mon Profil", "Mes Abonnements", "Réserver une Séance",
            "Mes Réservations", "Communiquer avec l'Admin",
            "Historique des Paiements", "Déconnexion"
        };

        for (String item : navItems) {
            JButton btn = new JButton(item);
            btn.setForeground(Color.WHITE);
            btn.setBackground(new Color(60, 60, 60));
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            if (item.equals("Déconnexion")) {
                btn.setBackground(new Color(150, 0, 0));
            }

            btn.addActionListener(e -> handleNavigation(item));
            sideBar.add(btn);
        }

        add(sideBar, BorderLayout.WEST);

        // === Contenu principal ===
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        labelBienvenue = new JLabel("Bienvenue, " + nomClient + " !");
        labelBienvenue.setFont(new Font("Segoe UI", Font.BOLD, 24));
        labelBienvenue.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Prochain cours
        JPanel prochainCoursPanel = new JPanel(new BorderLayout());
        prochainCoursPanel.setBorder(BorderFactory.createTitledBorder("Mon Prochain Cours"));
        prochainCoursPanel.setMaximumSize(new Dimension(800, 80));
        prochainCoursPanel.add(new JLabel("Yoga - Mardi 14 Mai à 18h00"), BorderLayout.CENTER);

        // Abonnement actuel
        JPanel abonnementPanel = new JPanel(new BorderLayout());
        abonnementPanel.setBorder(BorderFactory.createTitledBorder("Mon Abonnement Actuel"));
        abonnementPanel.setMaximumSize(new Dimension(800, 80));
        abonnementPanel.add(new JLabel("Formule Premium - Expire le 30 Juin 2025"), BorderLayout.CENTER);
        JButton btnRenouveler = new JButton("Renouveler");
        abonnementPanel.add(btnRenouveler, BorderLayout.EAST);

        // Messages / notifications
        JPanel messagesPanel = new JPanel(new BorderLayout());
        messagesPanel.setBorder(BorderFactory.createTitledBorder("Messages Non Lus"));
        messagesPanel.setMaximumSize(new Dimension(800, 80));
        messagesPanel.add(new JLabel("2 nouveaux messages de l'administrateur."), BorderLayout.CENTER);
        JButton btnVoirMessages = new JButton("Voir tous les messages");
        messagesPanel.add(btnVoirMessages, BorderLayout.EAST);

        // Ajout au panel principal
        mainContent.add(labelBienvenue);
        mainContent.add(Box.createRigidArea(new Dimension(0, 20)));
        mainContent.add(prochainCoursPanel);
        mainContent.add(Box.createRigidArea(new Dimension(0, 10)));
        mainContent.add(abonnementPanel);
        mainContent.add(Box.createRigidArea(new Dimension(0, 10)));
        mainContent.add(messagesPanel);

        add(mainContent, BorderLayout.CENTER);
    }

    private void handleNavigation(String action) {
        switch (action) {
            case "Déconnexion":
                JOptionPane.showMessageDialog(this, "Déconnexion réussie.");
                dispose();
                new LoginScreen("Client").setVisible(true);
                break;
            default:
                JOptionPane.showMessageDialog(this, "Navigation vers : " + action);
                break;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DashboardClient("John Doe").setVisible(true);
        });
    }
}
