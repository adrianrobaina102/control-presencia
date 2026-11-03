package software.ulpgc.controlpresencia.infrastructure.adapters.in;

import software.ulpgc.controlpresencia.application.ports.in.DeviceReader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class MockDeviceReader implements DeviceReader {

    @Override
    public String read() {
        return "emp-01;Oficina;In";
    }
    public void execute(ActionListener listener) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Lector de Presencia");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(300, 150);
            frame.setLayout(new FlowLayout());
            JButton button = new JButton("¡He llegado!");
            button.addActionListener(listener);
            frame.add(button);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
