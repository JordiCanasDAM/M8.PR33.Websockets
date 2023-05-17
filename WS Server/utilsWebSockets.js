const WebSocket = require('ws')
const { v4: uuidv4 } = require('uuid')

class Obj {
    // wss              WebSocket.Server
    // socketsClients   Map (All client connections)
    // ws               Connection (Specific client connection generated in "this.wss.on('connection',...")

    init (httpServer, port) {
        
        // Run WebSocket server
        this.wss = new WebSocket.Server({ server: httpServer })
        this.socketsClients = new Map()
        console.log(`Listening for WebSocket queries on ${port}`)
        
        // What to do when a websocket client connects
        this.wss.on('connection', (ws) => { this.onConnection(ws) })

    }

    end () {
        this.wss.close;
    }

    onConnection(ws) {
        
        console.log("Client connected")

        // TIE CLIENT CONNECTION WITH THE METHODS
        // What to do when a client is disconnected
        ws.on("close", () => { this.socketsClients.delete(ws)  })
        // What to do when a client message is received
        ws.on('message', (bufferedMessage) => { this.onMessage(ws, bufferedMessage)})
        console.log("Methods tied")

        // Add client to the clients list
        const id = uuidv4()
        const metadata = { id }
        this.socketsClients.set(ws, metadata)
        console.log("Client added to list")
        

        // Send connection id to client
        this.clients = [] // Llista d'ids
        this.socketsClients.forEach((value, key) => { this.clients.push(value.id) }) //SocketsClients = mapa key=ws (client connection) value=id
        this.wss.clients.forEach((client) => { //wss.clients -> client connections
            if (client.readyState === WebSocket.OPEN && this.socketsClients.get(client).id === this.clients[this.clients.length-1]) {
                var id = this.socketsClients.get(client).id
                var messageAsString = JSON.stringify({ request: "setId", id: this.clients[this.clients.length-1] })
                client.send(messageAsString)
                console.log(messageAsString)
            }
        })

    }

    broadcast (obj) {
        this.wss.clients.forEach((client) => {
            if (client.readyState === WebSocket.OPEN) {
                var messageAsString = JSON.stringify(obj)
                client.send(messageAsString)
                console.log("Message sent")
            }
        })
    }

    send (obj) {
        console.log("Sending message");
        this.wss.clients.forEach((client) => {
            if (this.socketsClients.get(client).id == obj.destination && client.readyState === WebSocket.OPEN) {
                var messageAsString = JSON.stringify(obj)
                client.send(messageAsString)
                return
            }
        })
    }

    onMessage(ws, bufferedMessage) {
        var messageStr = bufferedMessage.toString()
        console.log(messageStr)
    }

}

module.exports = Obj