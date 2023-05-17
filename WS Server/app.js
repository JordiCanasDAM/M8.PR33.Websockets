const express     = require('express')

const webSockets  = require('./utilsWebSockets.js')
global.ws = new webSockets()

// Start HTTP server
const app = express()
const port = process.env.PORT || 3000
// Publish static files from 'public' folder
app.use(express.static('public'))
// Activate HTTP server
const httpServer = app.listen(port, appListen)
function appListen () {
  console.log(`Listening for HTTP queries on: http://localhost:${port}`)
}

// Close connections when process is killed
process.on('SIGTERM', shutDown);
process.on('SIGINT', shutDown);
function shutDown() {
  console.log('Received kill signal, shutting down gracefully');
  httpServer.close()
  ws.end()
  process.exit(0);
}

// Run WebSocket server
ws.init(httpServer, port)
