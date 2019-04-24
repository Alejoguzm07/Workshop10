var app = (function () {

    class Point{
        constructor(x,y){
            this.x=x;
            this.y=y;
        }        
    }
    
    var stompClient = null;

    var addPointToCanvas = function (point) {
        stompClient.send("/app/newpoint."+drowingNumber, {}, JSON.stringify(point));
        var canvas = document.getElementById("canvas");
        var ctx = canvas.getContext("2d");
        ctx.beginPath();
        ctx.arc(point.x, point.y, 3, 0, 2 * Math.PI);
        ctx.stroke();

    };

    var addPolygonToCanvas = function (points) {
        var vcs = points.vertices;
        var ctx = canvas.getContext("2d");
        ctx.fillStyle = "#f8cfc0";
        ctx.beginPath();
        ctx.moveTo(vcs[0].x, vcs[0].y);
        for (var i = 1; i < vcs.length; i++) {
            ctx.lineTo(vcs[i].x, vcs[i].y);
        }
        ctx.moveTo(vcs[0].x, vcs[0].y);
        ctx.closePath();
        ctx.fill();
    };

    
    var getMousePosition = function (evt) {
        canvas = document.getElementById("canvas");
        var rect = canvas.getBoundingClientRect();
        return {
            x: evt.clientX - rect.left,
            y: evt.clientY - rect.top
        };
    };


    var connectAndSubscribe = function () {
        console.info('Connecting to WS...');
        var socket = new SockJS('/stompendpoint');
        stompClient = Stomp.over(socket);
        
        //subscribe to /topic/newpoint when connections succeed
        stompClient.connect("acsvovma", "EdkVoWP61rzzd65oMsIrlqcpk0OJlavZ", function (frame) {
            console.log('Connected: ' + frame);
            stompClient.subscribe('/topic/newpoint.'+drowingNumber, function (eventbody) {
                var theObject=JSON.parse(eventbody.body);
                var canvas = document.getElementById("canvas");
                var ctx = canvas.getContext("2d");
                ctx.beginPath();
                ctx.arc(theObject.x, theObject.y, 3, 0, 2 * Math.PI);
                ctx.stroke();
            });
            stompClient.subscribe('/topic/newpolygon.'+drowingNumber, function (eventbody) {
                var theObject = JSON.parse(eventbody.body);
                addPolygonToCanvas(theObject);
            });
        });

    };

    var click = function(event){
        var pos = getMousePosition(event);
        var pt = new Point(pos.x,pos.y);
        addPointToCanvas(pt);
    };
    
    

    return {

        init: function () {
            var can = document.getElementById("canvas");
            var ctx = can.getContext("2d");
            ctx.clearRect(0, 0, can.width, can.height);
            can.addEventListener("mousedown",click,false);

            //websocket connection
            drowingNumber = $("#numberOfDrawing").val();
            connectAndSubscribe();
        },
        publishPoint: function(px,py){
            var pt=new Point(px,py);
            console.info("publishing point at "+pt);
            addPointToCanvas(pt);

            //publicar el evento
        },

        disconnect: function () {
            if (stompClient !== null) {
                stompClient.disconnect();
            }
            setConnected(false);
            console.log("Disconnected");
        }
    };

})();