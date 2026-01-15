package pt.ipluso.app;

import pt.ipluso.modelo.*;
import pt.ipluso.ui.MonitorizaFrame;
import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Sensor> sensores = new ArrayList<>();

        sensores.add(new SensorTemperatura("Lisboa Campus IPLuso"));
        sensores.add(new SensorHumidade("Lisboa Baixa"));
        sensores.add(new SensorQualidadeAr("Porto Matosinhos"));
        sensores.add(new SensorTemperatura("Coimbra Centro"));
        sensores.add(new SensorHumidade("Faro Marina"));
        sensores.add(new SensorQualidadeAr("Braga Sameiro"));
        sensores.add(new SensorTemperatura("Evora Universidade"));

        for (Sensor s : sensores) {
            if (s instanceof Runnable) {
                new Thread((Runnable) s).start();
            }
        }


        SwingUtilities.invokeLater(() -> {
            MonitorizaFrame frame = new MonitorizaFrame(sensores);
            frame.setVisible(true);
        });
    }
}