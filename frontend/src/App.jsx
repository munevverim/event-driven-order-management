import React, { useState, useEffect } from 'react';
import { useDispatch } from 'react-redux';
import { fetchInventory } from './redux/slices/inventorySlice';
import { fetchOrders } from './redux/slices/orderSlice';
import Sidebar from './components/Sidebar';
import InventoryStatus from './components/InventoryStatus';
import ProductCatalog from './components/ProductCatalog';
import OrderHistory from './components/OrderHistory';
import { RefreshCw } from 'lucide-react';
import './App.css';

function App() {
  const dispatch = useDispatch();
  const [activeTab, setActiveTab] = useState('dashboard');
  const [lastRefreshed, setLastRefreshed] = useState(new Date().toLocaleTimeString());

  // Periyodik olarak verileri Kafka CDC & Saga akışından çek
  useEffect(() => {
    const loadData = () => {
      dispatch(fetchInventory());
      dispatch(fetchOrders());
      setLastRefreshed(new Date().toLocaleTimeString());
    };

    loadData();
    const interval = setInterval(loadData, 3000); // 3 saniyede bir poll et

    return () => clearInterval(interval);
  }, [dispatch]);

  const handleManualRefresh = () => {
    dispatch(fetchInventory());
    dispatch(fetchOrders());
    setLastRefreshed(new Date().toLocaleTimeString());
  };

  return (
    <div className="app-layout">
      {/* Background Glowing Decorative Orbs */}
      <div className="bg-orb bg-orb-1"></div>
      <div className="bg-orb bg-orb-2"></div>

      {/* Sol Sidebar */}
      <Sidebar activeTab={activeTab} setActiveTab={setActiveTab} />

      {/* Ana Ekran */}
      <main className="content-view">
        {/* Üst Header */}
        <header className="header-bar">
          <div className="header-title">
            <h1>Saga Kontrol Paneli</h1>
            <p>Envanter & Sipariş Dağıtık Akış Yönetimi</p>
          </div>

          <div className="header-actions">
            <span>
              Son Güncelleme: {lastRefreshed}
            </span>
            <button
              onClick={handleManualRefresh}
              className="btn-secondary"
            >
              <RefreshCw className="w-4 h-4" />
              Yenile
            </button>
          </div>
        </header>

        {/* Canlı Envanter Durumu */}
        <section>
          <h2 style={{ fontSize: '1.25rem', fontWeight: 700, marginBottom: '1.25rem', color: 'white' }}>
            Canlı Envanter Seviyeleri
          </h2>
          <InventoryStatus />
        </section>

        {/* Sipariş Formu ve Sipariş Takip Paneli */}
        <div className="grid-main-layout">
          <section>
            <ProductCatalog />
          </section>

          <section style={{ display: 'flex', flexDirection: 'column', height: '100%', minHeight: '500px' }}>
            <OrderHistory />
          </section>
        </div>
      </main>
    </div>
  );
}

export default App;
