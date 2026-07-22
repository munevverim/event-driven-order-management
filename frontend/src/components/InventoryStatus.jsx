import React from 'react';
import { useSelector } from 'react-redux';
import { Package, AlertCircle } from 'lucide-react';

export default function InventoryStatus() {
  const { items: inventory, loading } = useSelector((state) => state.inventory);

  // Ürünlerin adlarını mock eşleştirelim
  const getProductName = (productId) => {
    switch (productId) {
      case 101: return 'Developer Laptop (Pro)';
      case 102: return 'Mechanical Keyboard (RGB)';
      case 103: return 'Studio Headset (Hi-Fi)';
      default: return `Bilinmeyen Ürün (ID: ${productId})`;
    }
  };

  return (
    <div className="grid-3-col">
      {inventory.length === 0 && !loading && (
        <div style={{ textAlign: 'center', padding: '2rem 0', color: 'var(--text-secondary)' }}>
          Envanter bilgisi bulunamadı.
        </div>
      )}
      {inventory.map((item) => {
        const isLowStock = item.stockQuantity < 3;
        return (
          <div key={item.id} className="glass-panel inventory-card">
            <div className="inventory-card-header">
              <div className="inventory-card-icon">
                <Package className="w-6 h-6" />
              </div>
              {isLowStock ? (
                <span className="badge badge-pending">
                  <AlertCircle className="w-3.5 h-3.5" /> Kritik Stok
                </span>
              ) : (
                <span className="badge badge-success">Yeterli Stok</span>
              )}
            </div>

            <h3>{getProductName(item.productId)}</h3>
            <p className="inventory-card-subtitle">Ürün Kodu: #{item.productId}</p>

            <div className="inventory-card-stock">
              <span className="label">Mevcut Stok</span>
              <span className="value">
                {item.stockQuantity} <span>adet</span>
              </span>
            </div>
          </div>
        );
      })}
    </div>
  );
}
