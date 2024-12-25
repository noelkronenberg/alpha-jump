"use client"; // mark as client component (allows for use of hooks like useEffect and useState)

import React, { useEffect, useState } from 'react';
import { Chessboard } from 'react-chessboard';

// import RPC function
const { getFen } = require('connection/client.js');

const Page = () => {
  const [fen, setFen] = useState('start'); // default FEN

  // fetch FEN from server
  useEffect(() => {
    const fetchFen = async () => {
      try {
        const response = await fetch('http://localhost:8080/api/hello'); // TODO: replace with endpoint
        const data = await response.json();
        const receivedFEN = data.message; 
        setFen(receivedFEN);
      } catch (error) {
        console.error('Error fetching FEN:', error);
      }
    };

    fetchFen();
  }, []);

  // handle move
  const handleMove = (from: string, to: string) => {

    const moveFEN = from + "-" + to;
    var isValid = false;

    // RPC call
    getFen(moveFEN, function(err: any, response: any) {
      if (err) {
        console.error('Error:', err);
        isValid = false;  
      } else {
        console.log('Response:', response.answer);
        setFen(response.answer); // update FEN
        isValid = true;
      }
    });

    return isValid;

  };

  return (
    <div className="flex justify-center items-center min-h-screen p-5">
      <div className="w-full max-w-md mx-auto">
        <p className="text-center mb-4">Current FEN: {fen}</p>
        <div className="flex justify-center">
          <Chessboard key={fen} position={fen} onPieceDrop={handleMove} />
        </div>
      </div>
    </div>
  );
};

export default Page;