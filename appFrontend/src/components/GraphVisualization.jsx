import React, { useCallback, useEffect, useState } from 'react';
import {
  ReactFlow,
  addEdge,
  useNodesState,
  useEdgesState,
  Controls,
  MiniMap,
  Background,
  BackgroundVariant,
} from '@xyflow/react';
import '@xyflow/react/dist/style.css';
import { fetchGraphOverview } from '../api';

const nodeDefaults = {
  style: {
    borderRadius: '12px',
    padding: '16px 20px',
    fontSize: '13px',
    fontWeight: 600,
    fontFamily: "'Inter', sans-serif",
    border: '1px solid rgba(255,255,255,0.08)',
    backdropFilter: 'blur(12px)',
    cursor: 'pointer',
    minWidth: '160px',
    textAlign: 'center',
  },
};

const layoutNodes = (apiNodes) => {
  const positions = [
    { x: 400, y: 0 },     // BP
    { x: 800, y: 0 },     // Product
    { x: 250, y: 180 },   // Sales Orders
    { x: 550, y: 180 },   // SO Items
    { x: 100, y: 360 },   // Billing Docs
    { x: 400, y: 360 },   // Billing Items
    { x: 700, y: 360 },   // Deliveries
    { x: 950, y: 360 },   // Delivery Items
    { x: 250, y: 520 },   // Payments
  ];

  return apiNodes.map((node, i) => ({
    id: node.id,
    position: positions[i] || { x: i * 200, y: 0 },
    data: {
      label: (
        <div>
          <div style={{ marginBottom: '4px' }}>{node.label}</div>
          <div style={{
            fontSize: '11px',
            opacity: 0.7,
            fontWeight: 400,
          }}>
            {node.count.toLocaleString()} records
          </div>
        </div>
      ),
      ...node,
    },
    ...nodeDefaults,
    style: {
      ...nodeDefaults.style,
      background: `${node.data?.color || '#6366f1'}22`,
      color: node.data?.color || '#6366f1',
      boxShadow: `0 0 20px ${node.data?.color || '#6366f1'}15`,
    },
  }));
};

const layoutEdges = (apiEdges) => {
  return apiEdges.map((edge) => ({
    id: edge.id,
    source: edge.source,
    target: edge.target,
    label: edge.label,
    animated: true,
    style: { stroke: 'rgba(148, 163, 184, 0.4)', strokeWidth: 1.5 },
    labelStyle: {
      fill: 'rgba(148, 163, 184, 0.8)',
      fontSize: '10px',
      fontWeight: 500,
      fontFamily: "'Inter', sans-serif",
    },
    labelBgStyle: {
      fill: '#0f172a',
      fillOpacity: 0.8,
    },
    labelBgPadding: [6, 4],
    labelBgBorderRadius: 4,
  }));
};

export default function GraphVisualization({ onNodeClick }) {
  const [nodes, setNodes, onNodesChange] = useNodesState([]);
  const [edges, setEdges, onEdgesChange] = useEdgesState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchGraphOverview()
      .then((data) => {
        setNodes(layoutNodes(data.nodes));
        setEdges(layoutEdges(data.edges));
        setLoading(false);
      })
      .catch((err) => {
        console.error('Failed to fetch graph:', err);
        setLoading(false);
      });
  }, []);

  const handleNodeClick = useCallback((event, node) => {
    if (onNodeClick) {
      onNodeClick(node.data);
    }
  }, [onNodeClick]);

  if (loading) {
    return (
      <div className="graph-loading">
        <div className="spinner"></div>
        <p>Loading graph data...</p>
      </div>
    );
  }

  return (
    <div style={{ width: '100%', height: '100%' }}>
      <ReactFlow
        nodes={nodes}
        edges={edges}
        onNodesChange={onNodesChange}
        onEdgesChange={onEdgesChange}
        onNodeClick={handleNodeClick}
        fitView
        attributionPosition="bottom-left"
        style={{ background: 'transparent' }}
      >
        <Controls
          style={{
            background: 'rgba(15, 23, 42, 0.9)',
            border: '1px solid rgba(255,255,255,0.08)',
            borderRadius: '8px',
          }}
        />
        <MiniMap
          style={{
            background: 'rgba(15, 23, 42, 0.9)',
            border: '1px solid rgba(255,255,255,0.08)',
            borderRadius: '8px',
          }}
          maskColor="rgba(15, 23, 42, 0.7)"
          nodeColor={(n) => n.data?.data?.color || '#6366f1'}
        />
        <Background variant={BackgroundVariant.Dots} gap={20} size={1} color="rgba(148, 163, 184, 0.08)" />
      </ReactFlow>
    </div>
  );
}
