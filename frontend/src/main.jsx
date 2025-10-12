import React from "react";
import { createRoot } from "react-dom/client";

function App() {
    return(
        <main style={{fontFamily:"system-ui, sans-serif", padding:"2rem"}}>
            <h1>AgroConnectWorld</h1>
            <p>Vite + React build is working</p>
        </main>
    );
}

createRoot(document.getElementById("root")).render(<App />);