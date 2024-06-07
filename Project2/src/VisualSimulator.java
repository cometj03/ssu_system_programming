import loader.Loader;
import simulator.Simulator;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;

/**
 * 시뮬레이터의 동작을 GUI 방식으로 보여주는 모듈
 * 오직 GUI와 관련 있는 동작만 수행한다.
 */
public class VisualSimulator extends JFrame {

    private final Simulator simulator;

    public static void main(String[] args) {
        try {
            VisualSimulator simulator = new VisualSimulator();
            simulator.setVisible(true);
            simulator.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        } catch (IOException e) {
            System.out.println("IOException : " + e.getMessage());
        }
    }

    public VisualSimulator() throws IOException {
        super();
        this.simulator = new Simulator("input.txt", "output.txt");
        uiInit();
    }

    private void uiInit() {
        this.setSize(500, 750);
        this.setContentPane(MainPanel);
        this.setTitle("SIC/XE Visual Simulator");

        exe1StepButton.setEnabled(false);
        exeAllButton.setEnabled(false);
        startAddrMemTF.setText("0");
        exitButton.addActionListener(e -> dispose());
        exe1StepButton.addActionListener(e -> executeOneStep());
        exeAllButton.addActionListener(e -> executeAll());
        openButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            fileChooser.setFileFilter(new FileNameExtensionFilter("object file", "obj"));
            int returnVal = fileChooser.showOpenDialog(VisualSimulator.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    int startAddr = Integer.parseInt(startAddrMemTF.getText(), 16);
                    Loader.LoaderInfo loaderInfo = simulator.load(fileChooser.getSelectedFile(), startAddr);

                    fileNameTF.setText(fileChooser.getSelectedFile().getAbsolutePath());
                    exe1StepButton.setEnabled(true);
                    exeAllButton.setEnabled(true);
                    setHeaderBox(loaderInfo.programName, startAddr, loaderInfo.programTotalLen);
                    updateRegisterBox();
                    setEndBox(loaderInfo.programStartAddr);
                    updateMemoryDump();
                    updatePC();
                } catch (NumberFormatException | IOException ee) {
                    System.out.println(ee.getMessage());
                }
            }
        });
    }


    void executeOneStep() {
        String instruction = simulator.executeSingleInst();
        if (instruction == null) {
            exe1StepButton.setEnabled(false);
            exeAllButton.setEnabled(false);
        } else {
            instructionsTextArea.append(instruction + "\n");
        }
        updateRegisterBox();
        updateMemoryDump();
        updatePC();
    }

    void executeAll() {
        String instruction;
        while ((instruction = simulator.executeSingleInst()) != null) {
            instructionsTextArea.append(instruction + "\n");
            updateRegisterBox();
            updateMemoryDump();
            updatePC();
        }
        exe1StepButton.setEnabled(false);
        exeAllButton.setEnabled(false);
    }

    private void setHeaderBox(String programName, int startAddr, int len) {
        progNameTF.setText(programName);
        progStartAddrTF.setText(String.format("%06X", startAddr));
        progLenTF.setText(String.format("%06X", len));
    }

    private void setEndBox(int firstInstAddr) {
        firstInstAddrTF.setText(String.format("%06X", firstInstAddr));
    }

    private void updateRegisterBox() {
        for (int i = 0; i < 10; i++) {
            if (i == 7) continue;
            int value = simulator.getRegister(i).getValue();
            if (regDecTFs[i] != null) {
                regDecTFs[i].setText(String.format("%d", value));
            }
            if (regHexTFs[i] != null) {
                regHexTFs[i].setText(String.format("%X", value));
            }
        }
    }

    private void updateMemoryDump() {
        String mem = simulator.getMemory().getMemString(0, 4400);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i * 8 + 8 <= mem.length(); i++) {
            builder.append(mem, i * 8, i * 8 + 8).append(" ");
            if (i % 8 == 7) builder.append("\n");
        }
        memoryTextArea.setText(builder.toString());
    }

    private void updatePC() {
        programcounterTF.setText(String.format("%X", simulator.getPC()));
    }


    /* ui 관련 필드 */
    private JPanel MainPanel;
    private JTextField fileNameTF;
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
    private JTextField programcounterTF;
    private JTextField deviceTF;
    private JButton openButton;
    private JButton exe1StepButton;
    private JButton exeAllButton;
    private JButton exitButton;
    private JTextArea memoryTextArea;
    private JTextArea instructionsTextArea;

    private final JTextField[] regDecTFs = {
            regDecTF0,
            regDecTF1,
            regDecTF2,
            regDecTF3,
            regDecTF4,
            regDecTF5,
            null,
            null,
            regDecTF8,
            null
    };
    private final JTextField[] regHexTFs = {
            regHexTF0,
            regHexTF1,
            regHexTF2,
            regHexTF3,
            regHexTF4,
            regHexTF5,
            regHexTF6,
            null,
            regHexTF8,
            regHexTF9,
    };
}