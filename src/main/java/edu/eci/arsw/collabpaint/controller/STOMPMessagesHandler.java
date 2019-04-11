package edu.eci.arsw.collabpaint.controller;

import edu.eci.arsw.collabpaint.model.Point;
import edu.eci.arsw.collabpaint.model.Polygon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class STOMPMessagesHandler {

    @Autowired
    SimpMessagingTemplate msgt;

    private ConcurrentHashMap<String, Polygon> poligonos = new ConcurrentHashMap<String, Polygon>();

    @MessageMapping("/newpoint.{numdibujo}")
    public void handlePointEvent(Point pt, @DestinationVariable String numdibujo) throws Exception {
        System.out.println("Nuevo punto recibido en el servidor!:" + pt);
        
        if (poligonos.containsKey(numdibujo)) {
            Polygon p = poligonos.get(numdibujo);
            p.addPoint(pt);
            System.out.println("El poligono " + numdibujo + " tiene " + p.numberOfPoints() + " vertices");
            if (p.numberOfPoints() >= 3) {
                msgt.convertAndSend("/topic/newpolygon." + numdibujo, p);
            }
        } else {
            Polygon p = new Polygon();
            p.addPoint(pt);
            poligonos.put(numdibujo, p);
        }
        Polygon pol = poligonos.get(numdibujo);
        List<Point> pts = pol.getVertices();
        for(int i = 0; i < pts.size(); i++){
            msgt.convertAndSend("/topic/newpoint." + numdibujo, pts.get(i));
        }
    }
}
