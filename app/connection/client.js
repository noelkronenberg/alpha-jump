
//Setup for detecting gRPC
var PROTO_PATH = __dirname + '\\fenService.proto'

//if you get errors with the following 3 lines, make sure to have Node installed and write:
var parseArgs = require('minimist');                                                    //npm install minimist
var grpc = require('@grpc/grpc-js');                                                    //npm install @grpc/grpc-js
var protoLoader = require('@grpc/proto-loader');                                        //npm install @grpc/proto-loader

//define Proto Package
var packageDefinition = protoLoader.loadSync(
    PROTO_PATH,
    {keepCase: true,
     longs: String,
     enums: String,
     defaults: true,
     oneofs: true
    });

    
//get The "proto Instance with all its functions"
var fen_proto = grpc.loadPackageDefinition(packageDefinition).de.mcc;

// function to get the FEN from the server
function getFen(moveFEN, callback) {
    target = 'localhost:8080';
    var client = new fen_proto.FENService(target, grpc.credentials.createInsecure());

    const req = 'MOVE';
    const msg = moveFEN;

    client.getFen({ request: req, message: msg }, function(err, response) {
        if (err) {
            console.log(err);
        } else {
            console.log('Answer:\n', response.answer);
            return response.answer;
        }
        if (callback) {
            callback(err, response);
        }
    });
}

// export the function
module.exports = { getFen };

function main(){

    var argv = parseArgs(process.argv.slice(2), {
        string: 'target'
      });
      var target;
    if (argv.target) {
        target = argv.target;
    } else {
        target = 'localhost:8080';
    }
    var client = new fen_proto.FENService(target, grpc.credentials.createInsecure());

    //req is the request
    var reason='MOVE';
    var req;
    var msg;
    if (argv._.length > 0) {
        req = argv._[0];
    } else if (reason=='FEN') {
        req = 'FEN';
        msg = '';
    } else {    //HARD CODED TEST TODO: MAKE DYNAMIC
        req = 'MOVE';
        msg = 'D2-D3';
    }
    
    client.getFen({request: req, message: msg}, function(err, response) {
        if (err) {
            // process error
            console.log(err);
        } else {
            console.log('Answer:\n', response.answer);
        }
    });
}

main();