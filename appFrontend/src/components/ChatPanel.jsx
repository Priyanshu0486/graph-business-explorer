import React, { useState, useRef, useEffect } from 'react';
import { sendChatQuery } from '../api';

export default function ChatPanel() {
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const messagesEndRef = useRef(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!input.trim() || loading) return;

    const question = input.trim();
    setInput('');
    setMessages((prev) => [...prev, { role: 'user', content: question }]);
    setLoading(true);

    try {
      const response = await sendChatQuery(question);
      setMessages((prev) => [
        ...prev,
        {
          role: 'assistant',
          content: response.answer || 'No answer available.',
          sql: response.sql,
          results: response.results,
          error: response.error,
        },
      ]);
    } catch (err) {
      setMessages((prev) => [
        ...prev,
        {
          role: 'assistant',
          content: 'Sorry, an error occurred. Please check if the backend is running.',
          error: err.message,
        },
      ]);
    } finally {
      setLoading(false);
    }
  };

  const [expandedSql, setExpandedSql] = useState({});

  const toggleSql = (index) => {
    setExpandedSql((prev) => ({ ...prev, [index]: !prev[index] }));
  };

  return (
    <div className="chat-panel">
      <div className="chat-header">
        <div className="chat-header-icon">💬</div>
        <h3>Query Assistant</h3>
        <span className="chat-badge">AI-Powered</span>
      </div>

      <div className="chat-messages">
        {messages.length === 0 && (
          <div className="chat-empty">
            <div className="chat-empty-icon">🔍</div>
            <p>Ask questions about your business data</p>
            <div className="chat-suggestions">
              <button onClick={() => setInput('How many sales orders are there?')}>
                📊 Count sales orders
              </button>
              <button onClick={() => setInput('What is the total revenue?')}>
                💰 Total revenue
              </button>
              <button onClick={() => setInput('Show top 5 customers by order value')}>
                👥 Top customers
              </button>
            </div>
          </div>
        )}

        {messages.map((msg, i) => (
          <div key={i} className={`chat-message ${msg.role}`}>
            <div className="message-avatar">
              {msg.role === 'user' ? '👤' : '🤖'}
            </div>
            <div className="message-content">
              <p>{msg.content}</p>

              {msg.sql && (
                <div className="sql-block">
                  <button className="sql-toggle" onClick={() => toggleSql(i)}>
                    {expandedSql[i] ? '▼' : '▶'} SQL Query
                  </button>
                  {expandedSql[i] && (
                    <pre className="sql-code">{msg.sql}</pre>
                  )}
                </div>
              )}

              {msg.results && msg.results.length > 0 && (
                <div className="results-table-wrapper">
                  <table className="results-table">
                    <thead>
                      <tr>
                        {Object.keys(msg.results[0]).map((key) => (
                          <th key={key}>{key}</th>
                        ))}
                      </tr>
                    </thead>
                    <tbody>
                      {msg.results.slice(0, 20).map((row, ri) => (
                        <tr key={ri}>
                          {Object.values(row).map((val, vi) => (
                            <td key={vi}>{val != null ? String(val) : '-'}</td>
                          ))}
                        </tr>
                      ))}
                    </tbody>
                  </table>
                  {msg.results.length > 20 && (
                    <p className="results-truncated">
                      Showing 20 of {msg.results.length} results
                    </p>
                  )}
                </div>
              )}

              {msg.error && (
                <div className="error-block">
                  ⚠️ {msg.error}
                </div>
              )}
            </div>
          </div>
        ))}

        {loading && (
          <div className="chat-message assistant">
            <div className="message-avatar">🤖</div>
            <div className="message-content">
              <div className="typing-indicator">
                <span></span><span></span><span></span>
              </div>
            </div>
          </div>
        )}

        <div ref={messagesEndRef} />
      </div>

      <form className="chat-input-form" onSubmit={handleSubmit}>
        <input
          type="text"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          placeholder="Ask about your business data..."
          disabled={loading}
          className="chat-input"
        />
        <button type="submit" disabled={loading || !input.trim()} className="chat-send-btn">
          {loading ? '⏳' : '➤'}
        </button>
      </form>
    </div>
  );
}
