import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MedicineApp extends JFrame {

    private RoundedTextField medicineNameField;
    private RoundedButton searchButton;
    private JLabel resultLabel;

    public MedicineApp() {
        setTitle("Generic Medicine Finder");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        GradientPanel mainPanel = new GradientPanel(new Color(240, 255, 255), new Color(135, 206, 250));
        mainPanel.setLayout(new BorderLayout());

        // Header with left light color to darker right color gradient
        GradientPanel headerPanel = new GradientPanel(new Color(173, 216, 230), new Color(30, 144, 255)); // Light blue to darker blue
        headerPanel.setPreferredSize(new Dimension(0, 100)); // Increased header height
        headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20)); // Center aligned with vertical spacing

        // Load, resize, and add the logo to the header
        ImageIcon logoIcon = new ImageIcon("/Users/yasharthkesarwani/NetBeansProjects/Medicine/src/Resources/images/logo.png");
        Image logoImage = logoIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH); // Resize logo
        JLabel logoLabel = new JLabel(new ImageIcon(logoImage));

        // Header label
        JLabel headerLabel = new JLabel("Welcome to the Generic Medicine Finder");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 29));
        headerLabel.setForeground(Color.WHITE);

        // Adding logo and header label to the header panel
        headerPanel.add(logoLabel);
        headerPanel.add(headerLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel bodyPanel = new JPanel();
        bodyPanel.setOpaque(false);
        bodyPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        JLabel medicineLabel = new JLabel("Enter Medicine Name:");
        medicineLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        bodyPanel.add(medicineLabel, gbc);

        medicineNameField = new RoundedTextField(30);
        medicineNameField.setFont(new Font("Arial", Font.PLAIN, 18));
        medicineNameField.setBackground(Color.WHITE);

        gbc.gridx = 1;
        bodyPanel.add(medicineNameField, gbc);

        searchButton = new RoundedButton("🔍 Find Substitute");
        searchButton.setFont(new Font("Arial", Font.BOLD, 16));
        searchButton.setForeground(Color.WHITE);
        searchButton.setBackground(new Color(30, 144, 255));
        searchButton.setFocusPainted(false);
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        searchButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                searchButton.setBackground(new Color(255, 165, 0));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                searchButton.setBackground(new Color(30, 144, 255));
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        bodyPanel.add(searchButton, gbc);

        resultLabel = new JLabel();
        resultLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        resultLabel.setForeground(new Color(0, 100, 0));
        resultLabel.setHorizontalAlignment(SwingConstants.LEFT);
        resultLabel.setVerticalAlignment(SwingConstants.TOP);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        bodyPanel.add(resultLabel, gbc);

        mainPanel.add(bodyPanel, BorderLayout.CENTER);

        GradientPanel footerPanel = new GradientPanel(new Color(65, 105, 225), new Color(30, 144, 255));
        footerPanel.setPreferredSize(new Dimension(0, 50));
        footerPanel.setLayout(new GridBagLayout());
    JLabel footerLabel = new JLabel("© 2025 Generic Medicine Finder");
        footerLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        footerLabel.setForeground(Color.WHITE);
        footerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        footerLabel.setVerticalAlignment(SwingConstants.CENTER);
        GridBagConstraints footerGbc = new GridBagConstraints();
        footerGbc.gridx = 0;
        footerGbc.gridy = 0;
        footerGbc.anchor = GridBagConstraints.CENTER;
        footerPanel.add(footerLabel, footerGbc);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                findGenericSubstitute();
            }
        });

        add(mainPanel);
    }

    private void findGenericSubstitute() {
        String medicineName = medicineNameField.getText().trim();

        if (medicineName.isEmpty()) {
            resultLabel.setText("<html><font color='red'>Please enter a medicine name.</font></html>");
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT generic_substitute, dosage, brand, price, salt_name, availability FROM medicines WHERE medicine_name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, medicineName);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String substitute = resultSet.getString("generic_substitute");
                String dosage = resultSet.getString("dosage");
                String brand = resultSet.getString("brand");
                double price = resultSet.getDouble("price");
                String saltName = resultSet.getString("salt_name");
                String availability = resultSet.getString("availability");

                resultLabel.setText("<html><div style='padding:10px;'>" +
                        "<strong style='color:#2E8B57;'>Generic Substitute:</strong> " + substitute + "<br><br>" +
                        "<strong style='color:#2E8B57;'>Dosage:</strong> " + dosage + "<br><br>" +
                        "<strong style='color:#2E8B57;'>Brand:</strong> " + brand + "<br><br>" +
                        "<strong style='color:#2E8B57;'>Price:</strong> Rs. " + price + "<br><br>" +
                        "<strong style='color:#2E8B57;'>Salt Name:</strong> " + saltName + "<br><br>" +
                        "<strong style='color:#2E8B57;'>Availability:</strong> " + availability + "</div></html>");
            } else {
                resultLabel.setText("<html><font color='red'>No substitute found for " + medicineName + "</font></html>");
            }
        } catch (SQLException e) {
            resultLabel.setText("<html><font color='red'>Error: Unable to query the database.</font></html>");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MedicineApp app = new MedicineApp();
                app.setVisible(true);
            }
        });
    }

    static class RoundedTextField extends JTextField {
        private static final int BORDER_RADIUS = 20;

        public RoundedTextField(int columns) {
            super(columns);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder());
            setPreferredSize(new Dimension(400, 50));
            setFont(new Font("Arial", Font.PLAIN, 18));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), BORDER_RADIUS, BORDER_RADIUS);
            super.paintComponent(g);
            g2.dispose();
        }

        @Override
        public void setBackground(Color color) {
            super.setBackground(color);
            repaint();
        }

        @Override
        public Insets getInsets() {
            return new Insets(10, 10, 10, 10);
        }
    }

    static class RoundedButton extends JButton {
        private static final int BORDER_RADIUS = 30;

        public RoundedButton(String text) {
            super(text);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder());
            setPreferredSize(new Dimension(180, 40));
            setFont(new Font("Arial", Font.BOLD, 16));
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (getModel().isPressed()) {
                g.setColor(getBackground().darker());
            } else {
                g.setColor(getBackground());
            }
            g.fillRoundRect(0, 0, getWidth(), getHeight(), BORDER_RADIUS, BORDER_RADIUS);
            super.paintComponent(g);
        }
    }

    static class GradientPanel extends JPanel {
        private Color colorStart;
        private Color colorEnd;

        GradientPanel(Color colorStart, Color colorEnd) {
            this.colorStart = colorStart;
            this.colorEnd = colorEnd;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            int width = getWidth();
            int height = getHeight();
            GradientPaint gp = new GradientPaint(0, 0, colorStart, width, 0, colorEnd); // Horizontal gradient
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, width, height);
        }
    }
}