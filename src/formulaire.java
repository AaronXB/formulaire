import java.awt.*;
import java.sql.*;
import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class formulaire extends JFrame implements Runnable {
    JPanel contenu = new JPanel();
    JLabel texteNom = new JLabel("Nom:");
    JTextField remplissageNom = new JTextField(10);
    JLabel textePrenom = new JLabel("Prenom:");
    JLabel texteAnnee = new JLabel("Âge: ");
    JComboBox<Integer> age = new JComboBox<>();
    JTextField remplissagePrenom = new JTextField(10);
    JButton validation = this.getButton();
    DefaultListModel<String> model = new DefaultListModel<>();
    JList<String> liste;
    JScrollPane tableau;
    JButton supprimer;
    final String table_file;
    final String table_url;

    public static void main(String[] args) {
        formulaire fenetre = new formulaire();
        fenetre.modification();
        fenetre.recuperation();
        fenetre.setVisible(true);
        fenetre.run();
    }

    public formulaire() {
        this.liste = new JList<>(this.model);
        this.supprimer = this.getSupprimer();
        this.table_file = "data/utilisateurs.db";
        this.table_url = "jdbc:sqlite:data/utilisateurs.db";
        this.setTitle("Formulaire");
        this.setSize(1000, 500);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.add(this.contenu);
    }

    public void modification() {
        this.contenu.setBackground(Color.WHITE);
        this.contenu.setFocusable(false);
        this.contenu.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = 18;
        this.ajouter(0, 0, this.texteNom, gbc);
        this.ajouter(1, 0, this.remplissageNom, gbc);
        this.ajouter(0, 1, this.textePrenom, gbc);
        this.ajouter(1, 1, this.remplissagePrenom, gbc);
        this.ajouter(0, 2, this.texteAnnee, gbc);

        for(int i = 0; i <= 100; ++i) {
            this.age.addItem(i);
        }

        this.ajouter(1, 2, this.age, gbc);
        this.ajouter(1, 3, this.validation, gbc);
        this.liste.setSelectionMode(0);
        this.liste.setEnabled(false);
        this.tableau = new JScrollPane(this.liste);
        this.tableau.setVisible(true);
        this.ajouter(1, 4, this.tableau, gbc);
        this.supprimer.setBackground(Color.RED);
        this.ajouter(0, 5, this.supprimer, gbc);
    }

    private void ajouter(int gridx, int gridy, Component composant, GridBagConstraints gbc) {
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        this.contenu.add(composant, gbc);
    }

    private JButton getButton() {
        JButton validation = new JButton("Valider");
        validation.addActionListener((e) -> {
            String nom = this.remplissageNom.getText();
            String prenom = this.remplissagePrenom.getText();
            int age = this.age.getSelectedIndex();
            if (!nom.isBlank() && !prenom.isBlank()) {
                this.insertion(nom, prenom, age);
                this.recuperation();
                this.remplissageNom.setText("");
                this.remplissagePrenom.setText("");
            }

        });
        return validation;
    }

    public void run() {
        while(true) {
            if (this.remplissageNom.getText() != null && this.remplissagePrenom.getText() != null) {
                if (!this.remplissagePrenom.getText().isBlank() && !this.remplissageNom.getText().isBlank()) {
                    this.validation.setBackground(Color.CYAN);
                } else {
                    this.validation.setBackground(Color.GRAY);
                }
            } else {
                this.validation.setBackground(Color.GRAY);
            }

            if (this.model.isEmpty()) {
                this.supprimer.setBackground(Color.GRAY);
            } else {
                this.supprimer.setBackground(Color.RED);
            }
        }
    }

    public void insertion(String nom, String prenom, int age) {
        Logger logger = LoggerFactory.getLogger(formulaire.class);
        String insertSQL = "INSERT INTO utilisateurs (Prénom, Nom, Âge) VALUES (?, ?, ?)";

        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:data/utilisateurs.db");

            try {
                PreparedStatement pstmt = connection.prepareStatement(insertSQL);

                try {
                    pstmt.setString(1, prenom);
                    pstmt.setString(2, nom);
                    pstmt.setInt(3, age);
                    pstmt.executeUpdate();
                } catch (Throwable var12) {
                    if (pstmt != null) {
                        try {
                            pstmt.close();
                        } catch (Throwable var11) {
                            var12.addSuppressed(var11);
                        }
                    }

                    throw var12;
                }

                pstmt.close();
            } catch (Throwable var13) {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (Throwable var10) {
                        var13.addSuppressed(var10);
                    }
                }

                throw var13;
            }

            connection.close();
        } catch (SQLException var14) {
            logger.error("Erreur: ", var14);
        }

    }

    public void recuperation() {
        Logger logger = LoggerFactory.getLogger(formulaire.class);
        String insertSQL = "SELECT Prénom, Nom, Âge FROM utilisateurs";

        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:data/utilisateurs.db");

            try {
                Statement stmt = connection.createStatement();

                try {
                    ResultSet rs = stmt.executeQuery(insertSQL);

                    try {
                        this.model.clear();

                        while(rs.next()) {
                            String nom = rs.getString("Nom");
                            String prenom = rs.getString("Prénom");
                            int age = rs.getInt("Âge");
                            this.model.addElement(nom + " " + prenom + " " + age);
                        }
                    } catch (Throwable var12) {
                        if (rs != null) {
                            try {
                                rs.close();
                            } catch (Throwable var11) {
                                var12.addSuppressed(var11);
                            }
                        }

                        throw var12;
                    }

                    rs.close();
                } catch (Throwable var13) {
                    if (stmt != null) {
                        try {
                            stmt.close();
                        } catch (Throwable var10) {
                            var13.addSuppressed(var10);
                        }
                    }

                    throw var13;
                }

                stmt.close();
            } catch (Throwable var14) {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (Throwable var9) {
                        var14.addSuppressed(var9);
                    }
                }

                throw var14;
            }

            connection.close();
        } catch (SQLException var15) {
            logger.error("Erreur: " + var15.getMessage());
        }

    }

    public JButton getSupprimer() {
        Logger logger = LoggerFactory.getLogger(formulaire.class);
        JButton bouton = new JButton("Vider la table");
        String insertSQL = "DELETE FROM utilisateurs";
        bouton.addActionListener((e) -> {
            try {
                Connection connection = DriverManager.getConnection("jdbc:sqlite:data/utilisateurs.db");

                try {
                    Statement stmt = connection.createStatement();

                    try {
                        stmt.executeUpdate(insertSQL);
                        this.recuperation();
                    } catch (Throwable var10) {
                        if (stmt != null) {
                            try {
                                stmt.close();
                            } catch (Throwable var9) {
                                var10.addSuppressed(var9);
                            }
                        }

                        throw var10;
                    }

                    stmt.close();
                } catch (Throwable var11) {
                    if (connection != null) {
                        try {
                            connection.close();
                        } catch (Throwable var8) {
                            var11.addSuppressed(var8);
                        }
                    }

                    throw var11;
                }

                connection.close();
            } catch (SQLException var12) {
                logger.error("Erreur: " + var12.getMessage());
            }

        });
        return bouton;
    }
}

