import React from 'react';
import { ShoppingCart, Package, RefreshCw, BarChart2 } from 'lucide-react';

export default function Sidebar({ activeTab, setActiveTab }) {
  return (
    <aside className="sidebar">
      <div>
        <div className="brand-logo">
          <div className="logo-box">
            <ShoppingCart className="text-white w-6 h-6" />
          </div>
          <div className="brand-text">
            <h2>EventFlow</h2>
            <p>Order Management</p>
          </div>
        </div>

        <nav className="nav-menu">
          <button
            onClick={() => setActiveTab('dashboard')}
            className={`nav-item ${activeTab === 'dashboard' ? 'nav-item-active' : ''}`}
          >
            <BarChart2 className="w-5 h-5" />
            Kontrol Paneli
          </button>
        </nav>
      </div>

      <div className="glass-panel sidebar-monitor-card">
        <div className="monitor-status">
          <span className="monitor-dot"></span>
          Canlı İzleme Aktif
        </div>
        <p>CDC & Saga akışları her 3 saniyede bir otomatik sorgulanmaktadır.</p>
      </div>
    </aside>
  );
}
