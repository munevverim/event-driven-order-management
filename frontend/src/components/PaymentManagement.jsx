import React from 'react';
import { CreditCard, History, User, CheckCircle2, XCircle } from 'lucide-react';

export default function PaymentManagement({ balances, history, loading }) {
  const getProductName = (productId) => {
    switch (productId) {
      case 101: return 'Developer Laptop (Pro)';
      case 102: return 'Mechanical Keyboard (RGB)';
      case 103: return 'Studio Headset (Hi-Fi)';
      default: return `Ürün #${productId}`;
    }
  };

  const getUserName = (userId) => {
    switch (userId) {
      case 1: return 'Munevver (Müşteri #1)';
      case 2: return 'Kemal (Müşteri #2)';
      case 3: return 'Elif (Müşteri #3)';
      default: return `Müşteri #${userId}`;
    }
  };

  const formatTime = (timeStr) => {
    try {
      if (Array.isArray(timeStr)) {
        const [year, month, day, hour, minute, second] = timeStr;
        return `${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}:${second.toString().padStart(2, '0')}`;
      }
      const date = new Date(timeStr);
      return date.toLocaleTimeString();
    } catch (e) {
      return timeStr;
    }
  };

  return (
    <div className="flex flex-col gap-6 w-full">
      {/* Üst Kartlar: Kullanıcı Bakiyeleri */}
      <div>
        <h2 style={{ fontSize: '1.25rem', fontWeight: 700, marginBottom: '1.25rem', color: 'white' }} className="flex items-center gap-2">
          <CreditCard className="w-5 h-5 text-indigo-500" /> Hesap Bakiyeleri
        </h2>
        <div className="grid-3-col">
          {balances.map((user) => (
            <div key={user.userId} className="glass-panel p-5 flex items-center justify-between">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-xl bg-indigo-500/10 border border-indigo-500/20 flex items-center justify-center text-indigo-400">
                  <User className="w-5 h-5" />
                </div>
                <div>
                  <h3 className="text-sm font-semibold text-white">{getUserName(user.userId)}</h3>
                  <p className="text-xs text-slate-500">Kullanıcı Kimliği: #{user.userId}</p>
                </div>
              </div>
              <div className="text-right">
                <p className="text-[10px] font-bold text-slate-500 uppercase tracking-wide">Mevcut Bakiye</p>
                <p className="text-xl font-black text-indigo-400">${user.balance.toFixed(2)}</p>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Alt Kart: Ödeme İşlem Geçmişi */}
      <div className="glass-panel p-6 flex flex-col flex-1">
        <h2 className="text-xl font-bold text-white mb-6 flex items-center gap-2">
          <History className="w-5 h-5 text-indigo-500" /> Ödeme İşlem Kayıtları
        </h2>

        <div className="overflow-x-auto flex-1">
          <table className="glass-table text-left">
            <thead>
              <tr>
                <th className="py-3 px-4">İşlem ID</th>
                <th className="py-3 px-4">Sipariş ID</th>
                <th className="py-3 px-4">Müşteri</th>
                <th className="py-3 px-4">Ürün</th>
                <th className="py-3 px-4 text-right">Tutar</th>
                <th className="py-3 px-4 text-center">Tarih</th>
                <th className="py-3 px-4 text-center">Ödeme Durumu</th>
              </tr>
            </thead>
            <tbody>
              {history.length === 0 && !loading && (
                <tr>
                  <td colSpan="7" className="text-center py-10 text-slate-500">
                    Henüz bir ödeme işlemi gerçekleştirilmedi.
                  </td>
                </tr>
              )}
              {history.map((record) => (
                <tr key={record.id}>
                  <td className="font-mono text-xs text-slate-400">#{record.id}</td>
                  <td className="font-mono text-xs text-slate-400">#{record.orderId}</td>
                  <td>{getUserName(record.userId)}</td>
                  <td className="font-medium text-white">{getProductName(record.productId)} (x{record.quantity})</td>
                  <td className="text-right font-bold text-indigo-400">${record.amount.toFixed(2)}</td>
                  <td className="text-center text-xs text-slate-400">{formatTime(record.createdAt)}</td>
                  <td className="text-center">
                    {record.status === 'SUCCESS' ? (
                      <span className="badge badge-success">
                        <CheckCircle2 className="w-3.5 h-3.5" /> BAŞARILI
                      </span>
                    ) : (
                      <div className="flex flex-col items-center gap-1">
                        <span className="badge badge-danger">
                          <XCircle className="w-3.5 h-3.5" /> BAŞARISIZ
                        </span>
                        <span className="text-[9px] text-rose-400 font-semibold max-w-[150px] truncate" title={record.reason}>
                          {record.reason}
                        </span>
                      </div>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
