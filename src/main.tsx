import React from 'react';
import ReactDOM from 'react-dom/client';
import { AuraProvider } from './context/AuraContext';
import { ErrorBoundary } from './components/ErrorBoundary';
import App from './App';
import './index.css';

ReactDOM.createRoot(document.getElementById('root') as HTMLElement).render(
  <React.StrictMode>
    <ErrorBoundary>
      <AuraProvider>
        <App />
      </AuraProvider>
    </ErrorBoundary>
  </React.StrictMode>
);
