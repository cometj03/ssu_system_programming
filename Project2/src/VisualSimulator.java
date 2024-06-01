import javax.swing.*;
import java.io.File;

/**
 * 시뮬레이터의 동작을 GUI 방식으로 보여주는 모듈
 * 오직 GUI와 관련 있는 동작만 수행한다.
 */
public class VisualSimulator extends JFrame {
    private JPanel MainPanel;
    private JTextField fileNameTextField;
    private JButton openButton;
    private JTextField textField3;
    private JTextField textField4;
    private JTextField textField5;
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField6;
    private JTextField textField7;
    private JTextField textField8;
    private JTextField textField9;
    private JTextField textField10;
    private JTextField textField11;
    private JTextField textField12;
    private JTextField textField13;
    private JTextField textField15;
    private JTextField textField16;
    private JTextField textField17;
    private JTextField textField18;
    private JTextField textField19;
    private JTextField textField20;
    private JTextField textField21;


//    String curFileName = null;
//    JButton fileOpenBtn = new JButton("open");
//    JButton exitBtn = new JButton("종료");
//    JButton exeAllBtn = new JButton("실행 (All)");
//    JButton exeSingleBtn = new JButton("실행 (1 Step)");


    public VisualSimulator() {
        super();
        init();
    }

    private void init() {
        this.setSize(500, 700);
        this.setContentPane(MainPanel);
        this.setTitle("SIC/XE Visual Simulator");

        openButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            int returnVal = fileChooser.showOpenDialog(VisualSimulator.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                System.out.println(fileChooser.getSelectedFile().getName());
            }
        });
    }

    public static void main(String[] args) {
        VisualSimulator simulator = new VisualSimulator();
        simulator.setVisible(true);
        simulator.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
}