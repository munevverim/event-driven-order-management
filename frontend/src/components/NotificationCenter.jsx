import React from 'react';
import { Bell, Info, CheckCircle, AlertTriangle, XCircle } from 'lucide-react';

export default function NotificationCenter({ notifications, loading }) {
  const getNotificationIcon = (type) => {
    switch (type) {
      case 'SUCCESS':
        return <CheckCircle className="w-5 h-5 text-emerald-500" />;
      case 'WARNING':
        return <AlertTriangle className="w-5 h-5 text-amber-500" />;
      case 'ERROR':
        return <XCircle className="w-5 h-5 text-rose-500" />;
      case 'INFO':
      default:
        return <Info className="w-5 h-5 text-sky-500" />;
    }
  };

  const formatTime = (timeStr) => {
    try {
      // Input might be array [yyyy, MM, dd, HH, mm, ss] or string
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
    <div className="glass-panel p-6 flex flex-col flex-1">
      <h2 className="text-xl font-bold text-white mb-6 flex items-center gap-2">
        <Bell className="w-5 h-5 text-indigo-500" /> Sistem Bildirimleri
      </h2>

      <div className="overflow-y-auto pr-1 flex-1 max-h-[600px] flex flex-col gap-3">
        {notifications.length === 0 && !loading && (
          <div className="text-center py-12 text-slate-500">
            Henüz bir sistem bildirimi alınmadı.
          </div>
        )}

        {notifications.map((notif) => (
          <div 
            key={notif.id}
            className={`flex gap-4 p-4 rounded-2xl bg-slate-900/30 border border-white/5 transition-all duration-300 hover:bg-white/5 ${
              notif.type === 'SUCCESS' ? 'hover:border-emerald-500/20' : 
              notif.type === 'ERROR' ? 'hover:border-rose-500/20' : 
              notif.type === 'WARNING' ? 'hover:border-amber-500/20' : 
              'hover:border-sky-500/20'
            }`}
          >
            <div className="flex-shrink-0 mt-0.5">
              {getNotificationIcon(notif.type)}
            </div>
            <div className="flex-1 min-w-0">
              <p className="text-sm text-white font-medium break-words leading-relaxed">
                {notif.message}
              </p>
              <div className="flex items-center gap-2 mt-2">
                <span className="text-[10px] font-semibold text-slate-500 tracking-wider uppercase bg-white/5 px-2 py-0.5 rounded-md">
                  Sipariş #{notif.orderId}
                </span>
                <span className="text-[10px] text-slate-500">
                  {formatTime(notif.createdAt)}
                </span>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
