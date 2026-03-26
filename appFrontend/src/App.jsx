import React, { useState, useCallback } from 'react';
import GraphVisualization from './components/GraphVisualization';
import ChatPanel from './components/ChatPanel';
import Sidebar from './components/Sidebar';
import EntityDetailPanel from './components/EntityDetailPanel';

function App() {
  const [selectedEntityType, setSelectedEntityType] = useState(null);
  const [activeView, setActiveView] = useState('graph'); // 'graph' or 'entities'

  const handleEntitySelect = useCallback((type) => {
    setSelectedEntityType(type);
    setActiveView('entities');
  }, []);

  const handleNodeClick = useCallback((nodeData) => {
    setSelectedEntityType(nodeData.type);
    setActiveView('entities');
  }, []);

  const handleCloseDetail = useCallback(() => {
    setSelectedEntityType(null);
    setActiveView('graph');
  }, []);

  return (
    <div className="app">
      <Sidebar
        onEntitySelect={handleEntitySelect}
        selectedType={selectedEntityType}
      />

      <main className="main-content">
        <header className="top-bar">
          <div className="top-bar-left">
            <button
              className={`view-tab ${activeView === 'graph' ? 'active' : ''}`}
              onClick={() => { setActiveView('graph'); setSelectedEntityType(null); }}
            >
              🔗 Graph View
            </button>
            {selectedEntityType && (
              <button
                className={`view-tab ${activeView === 'entities' ? 'active' : ''}`}
                onClick={() => setActiveView('entities')}
              >
                📋 Entity Data
              </button>
            )}
          </div>
          <div className="top-bar-right">
            <span className="status-badge">
              <span className="status-dot"></span>
              Connected
            </span>
          </div>
        </header>

        <div className="content-area">
          <div className="center-panel">
            {activeView === 'graph' ? (
              <GraphVisualization onNodeClick={handleNodeClick} />
            ) : (
              <EntityDetailPanel
                entityType={selectedEntityType}
                onClose={handleCloseDetail}
              />
            )}
          </div>

          <div className="right-panel">
            <ChatPanel />
          </div>
        </div>
      </main>
    </div>
  );
}

export default App;
