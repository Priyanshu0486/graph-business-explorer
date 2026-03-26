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

const TYPE_LABELS = {
  business_partner: 'Business Partners',
  product: 'Products',
  sales_order_header: 'Sales Orders',
  sales_order_item: 'Sales Order Items',
  billing_document_header: 'Billing Documents',
  billing_document_item: 'Billing Document Items',
  outbound_delivery_header: 'Outbound Deliveries',
  outbound_delivery_item: 'Outbound Delivery Items',
  payment_accounts_receivable: 'Payments',
};

export default function EntityDetailPanel({ entityType, onClose }) {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);

  useEffect(() => {
    if (!entityType) return;
    setLoading(true);
    const apiType = TYPE_MAP[entityType] || entityType;
    fetchEntities(apiType, page, 15)
      .then((res) => {
        setData(res);
        setLoading(false);
      })
      .catch((err) => {
        console.error('Failed to fetch entities:', err);
        setLoading(false);
      });
  }, [entityType, page]);

  if (!entityType) return null;

  const records = data?.content || [];
  const totalPages = data?.totalPages || 0;
  const totalElements = data?.totalElements || 0;

  // Get column keys from the first record
  const columns = records.length > 0 ? Object.keys(records[0]) : [];

  return (
    <div className="entity-detail-panel">
      <div className="entity-detail-header">
        <div>
          <h3>{TYPE_LABELS[entityType] || entityType}</h3>
          <span className="entity-count">{totalElements.toLocaleString()} records</span>
        </div>
        <button className="close-btn" onClick={onClose}>✕</button>
      </div>

      {loading ? (
        <div className="entity-loading">
          <div className="spinner"></div>
          <p>Loading records...</p>
        </div>
      ) : records.length === 0 ? (
        <div className="entity-empty">
          <p>No records found</p>
        </div>
      ) : (
        <>
          <div className="entity-table-wrapper">
            <table className="entity-table">
              <thead>
                <tr>
                  {columns.map((col) => (
                    <th key={col}>{formatColumnName(col)}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {records.map((row, i) => (
                  <tr key={i}>
                    {columns.map((col) => (
                      <td key={col}>{row[col] != null ? String(row[col]) : '-'}</td>
                    ))}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          <div className="pagination">
            <button
              disabled={page === 0}
              onClick={() => setPage((p) => Math.max(0, p - 1))}
            >
              ← Prev
            </button>
            <span>
              Page {page + 1} of {totalPages}
            </span>
            <button
              disabled={page >= totalPages - 1}
              onClick={() => setPage((p) => p + 1)}
            >
              Next →
            </button>
          </div>
        </>
      )}
    </div>
  );
}

function formatColumnName(name) {
  return name
    .replace(/([A-Z])/g, ' $1')
    .replace(/^./, (s) => s.toUpperCase())
    .trim();
}
