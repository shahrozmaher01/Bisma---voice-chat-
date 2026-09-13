import React from 'react';
import ReactDOM from 'react-dom/client';
import { AuraProvider } from './context/AuraContext';
import App from './App';
import './index.css';

ReactDOM.createRoot(document.getElementById('root') as HTMLElement).render(
  <React.StrictMode>
    <AuraProvider>
      <App />
    </AuraProvider>
  </React.StrictMode>
);
