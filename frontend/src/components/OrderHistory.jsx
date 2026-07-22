import React from 'react';
import { useSelector } from 'react-redux';
import { Clock, RefreshCw, CheckCircle, XCircle } from 'lucide-react';

export default function OrderHistory() {
  const { items: orders, loading } = useSelector((state) => state.orders);

  const getStatusBadge = (status) => {
    switch (status) {
      case 'CREATED':
        return (
          <span className="badge badge-info">
            <RefreshCw className="w-3 h-3 animate-spin" /> İŞLENİYOR
          </span>
        );
      case 'PAID':
        return (
          <span className="badge badge-success">
            <CheckCircle className="w-3 h-3" /> ONAYLANDI (SAGA SUCCESS)
          </span>
        );
      case 'REJECTED':
        return (
          <span className="badge badge-danger">
            <XCircle className="w-3 h-3" /> İPTAL (SAGA REJECTED)
          </span>
        );
      default:
        return <span className="badge badge-pending">{status}</span>;
    }
  };

  const getProductName = (productId) => {
    switch (productId) {
      case 101: return 'Developer Laptop (Pro)';
      case 102: return 'Mechanical Keyboard (RGB)';
      case 103: return 'Studio Headset (Hi-Fi)';
      default: return `Bilinmeyen Ürün (ID: ${productId})`;
    }
  };

  return (
    <div className="glass-panel p-6 flex flex-col flex-1">
      <h2 className="text-xl font-bold text-white mb-6 flex items-center gap-2">
        <Clock className="w-5 h-5 text-indigo-500" /> Saga Sipariş Takibi
      </h2>

      <div className="overflow-x-auto flex-1">
        <table className="glass-table text-left">
          <thead>
            <tr>
              <th className="py-3 px-4">Sipariş ID</th>
              <th className="py-3 px-4">Müşteri ID</th>
              <th className="py-3 px-4">Ürün Adı</th>
              <th className="py-3 px-4 text-center">Adet</th>
              <th className="py-3 px-4 text-right">Tutar</th>
              <th className="py-3 px-4 text-center">Saga Durumu</th>
            </tr>
          </thead>
          <tbody>
            {orders.length === 0 && !loading && (
              <tr>
                <td colSpan="6" className="text-center py-10 text-slate-500">
                  Henüz bir sipariş bulunmuyor.
                </td>
              </tr>
            )}
            {orders.map((order) => (
              <tr key={order.id}>
                <td className="font-mono text-xs text-slate-400">#{order.id}</td>
                <td>User #{order.userId}</td>
                <td className="font-medium text-white">{getProductName(order.productId)}</td>
                <td className="text-center">{order.quantity}</td>
                <td className="text-right font-bold text-indigo-400">${order.totalPrice.toFixed(2)}</td>
                <td className="text-center">{getStatusBadge(order.status)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
