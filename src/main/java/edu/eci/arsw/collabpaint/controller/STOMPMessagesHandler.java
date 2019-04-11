package edu.eci.arsw.collabpaint.controller;

import edu.eci.arsw.collabpaint.model.Point;
import edu.eci.arsw.collabpaint.model.Polygon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Controller
public class STOMPMessagesHandler {

    @Autowired
    SimpMessagingTemplate msgt;

    private ConcurrentHashMap<String,Polygon> poligonos = new ConcurrentHashMap<String,Polygon>();

    @MessageMapping("/newpoint.{numdibujo}")
    public void handlePointEvent(Point pt, @DestinationVariable String numdibujo) throws Exception {
        System.out.println("Nuevo punto recibido en el servidor!:"+pt);
        msgt.convertAndSend("/topic/newpoint."+numdibujo, pt);
        if(poligonos.containsKey(numdibujo)){
            Polygon p = poligonos.get(numdibujo);
            p.addPoint(pt);
            System.out.println("El poligono "+numdibujo+" tiene "+p.numberOfPoints()+"vertices");
            if(p.numberOfPoints() >= 2){
                msgt.convertAndSend("/topic/newpolygon."+numdibujo, p);
            }
        }else{
            Polygon p = new Polygon();
            p.addPoint(pt);
            poligonos.put(numdibujo,p);
        }
    }
}
