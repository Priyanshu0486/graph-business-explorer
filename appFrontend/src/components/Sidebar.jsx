import React, { useState, useEffect } from 'react';
import { fetchEntities } from '../api';

const TYPE_MAP = {
  business_partner: 'business-partners',
  product: 'products',
  sales_order_header: 'sales-orders',
  sales_order_item: 'sales-order-items',
  billing_document_header: 'billing-documents',
  billing_document_item: 'billing-document-items',
  outbound_delivery_header: 'deliveries',
  outbound_delivery_item: 'delivery-items',
  payment_accounts_receivable: 'payments',
};

export default function Sidebar({ onEntitySelect, selectedType }) {
  const entityTypes = [
    { id: 'business_partner', label: 'Business Partners', icon: '🏢', color: '#6366f1' },
    { id: 'product', label: 'Products', icon: '📦', color: '#8b5cf6' },
    { id: 'sales_order_header', label: 'Sales Orders', icon: '📋', color: '#3b82f6' },
    { id: 'sales_order_item', label: 'Sales Order Items', icon: '📝', color: '#06b6d4' },
    { id: 'billing_document_header', label: 'Billing Documents', icon: '💵', color: '#f59e0b' },
    { id: 'billing_document_item', label: 'Billing Doc Items', icon: '📄', color: '#f97316' },
    { id: 'outbound_delivery_header', label: 'Deliveries', icon: '🚚', color: '#22c55e' },
    { id: 'outbound_delivery_item', label: 'Delivery Items', icon: '📬', color: '#10b981' },
    { id: 'payment_accounts_receivable', label: 'Payments', icon: '💳', color: '#ef4444' },
  ];

  return (
    <div className="sidebar">
      <div className="sidebar-header">
        <h2>📊 O2C Explorer</h2>
        <p className="sidebar-subtitle">SAP Order-to-Cash</p>
      </div>

      <nav className="sidebar-nav">
        <div className="nav-section-label">Entity Types</div>
        {entityTypes.map((et) => (
          <button
            key={et.id}
            className={`sidebar-item ${selectedType === et.id ? 'active' : ''}`}
            onClick={() => onEntitySelect(et.id)}
            style={{
              '--accent-color': et.color,
            }}
          >
            <span className="sidebar-icon">{et.icon}</span>
            <span className="sidebar-label">{et.label}</span>
          </button>
        ))}
      </nav>

      <div className="sidebar-footer">
        <div className="sidebar-footer-info">
          <span>🔗 Graph-Based Data</span>
          <span>Modeling & Query System</span>
        </div>
      </div>
    </div>
  );
}
