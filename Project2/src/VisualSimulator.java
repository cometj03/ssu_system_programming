import simulator.Simulator;

import javax.swing.*;
import java.io.File;

/**
 * 시뮬레이터의 동작을 GUI 방식으로 보여주는 모듈
 * 오직 GUI와 관련 있는 동작만 수행한다.
 */
public class VisualSimulator extends JFrame {

    private final Simulator simulator;

    public static void main(String[] args) {
        VisualSimulator simulator = new VisualSimulator();
        simulator.setVisible(true);
        simulator.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    public VisualSimulator() {
        super();
        simulator = new Simulator();
        uiInit();
    }

    private void uiInit() {
        this.setSize(500, 700);
        this.setContentPane(MainPanel);
        this.setTitle("SIC/XE Visual simulator.Simulator");

        exitButton.addActionListener(e -> dispose());
        openButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            int returnVal = fileChooser.showOpenDialog(VisualSimulator.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                System.out.println(fileChooser.getSelectedFile().getName());
            }
        });
        exe1StepButton.addActionListener(e -> executeOneStep());
        exeAllButton.addActionListener(e -> executeAll());
    }

    void executeOneStep() {

    }

    void executeAll() {

    }


    /* ui 관련 필드 */
    private JPanel MainPanel;
    private JTextField tfFileName;
    private JTextField progNameTF;
    private JTextField progStartAddrTF;
    private JTextField progLenTF;
    private JTextField firstInstAddrTF;
    private JTextField regDecTF0;
    private JTextField regDecTF1;
    private JTextField regDecTF2;
    private JTextField regDecTF3;
    private JTextField regDecTF4;
    private JTextField regDecTF5;
    private JTextField regDecTF8;
    private JTextField regHexTF0;
    private JTextField regHexTF1;
    private JTextField regHexTF2;
    private JTextField regHexTF3;
    private JTextField regHexTF4;
    private JTextField regHexTF5;
    private JTextField regHexTF6;
    private JTextField regHexTF8;
    private JTextField regHexTF9;
    private JTextField startAddrMemTF;
    private JTextField targetAddressTF;
    private JTextField instructionsTF;
    private JTextField deviceTF;
    private JButton openButton;
    private JButton exe1StepButton;
    private JButton exeAllButton;
    private JButton exitButton;
}