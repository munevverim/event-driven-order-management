import React, { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { createOrder, clearCreateError } from '../redux/slices/orderSlice';
import { ShoppingBag, ArrowRight, Loader } from 'lucide-react';

const products = [
  { id: 101, name: 'Developer Laptop (Pro)', price: 75.0 },
  { id: 102, name: 'Mechanical Keyboard (RGB)', price: 15.0 },
  { id: 103, name: 'Studio Headset (Hi-Fi)', price: 30.0 }
];

export default function ProductCatalog() {
  const dispatch = useDispatch();
  const { creating, createError } = useSelector((state) => state.orders);
  
  const [selectedProductId, setSelectedProductId] = useState(101);
  const [quantity, setQuantity] = useState(1);
  const [userId, setUserId] = useState(1);
  const [showSuccessGlow, setShowSuccessGlow] = useState(false);

  const selectedProduct = products.find(p => p.id === selectedProductId);
  const totalPrice = selectedProduct ? (selectedProduct.price * quantity).toFixed(2) : '0.00';

  const handleSubmit = (e) => {
    e.preventDefault();
    dispatch(clearCreateError());
    
    const orderData = {
      userId,
      productId: selectedProductId,
      quantity,
      totalPrice: parseFloat(totalPrice)
    };

    dispatch(createOrder(orderData)).then((res) => {
      if (res.meta.requestStatus === 'fulfilled') {
        setShowSuccessGlow(true);
        setTimeout(() => setShowSuccessGlow(false), 2000);
      }
    });
  };

  return (
    <div className={`glass-panel p-6 relative transition-all duration-500 ${
      showSuccessGlow ? 'shadow-[0_0_40px_rgba(16,185,129,0.2)] border-emerald-500/40' : ''
    }`}>
      <h2 className="text-xl font-bold text-white mb-6 flex items-center gap-2">
        <ShoppingBag className="w-5 h-5 text-indigo-500" /> Hızlı Sipariş Oluştur
      </h2>

      <form onSubmit={handleSubmit} className="flex flex-col gap-5">
        <div>
          <label className="block text-xs font-semibold text-slate-400 mb-2 uppercase tracking-wide">
            Kullanıcı Seçimi (User ID)
          </label>
          <select 
            value={userId} 
            onChange={(e) => setUserId(parseInt(e.target.value))}
            className="form-input"
          >
            <option value={1}>Müşteri #1 (Munevver)</option>
            <option value={2}>Müşteri #2 (Kemal)</option>
            <option value={3}>Müşteri #3 (Elif)</option>
          </select>
        </div>

        <div>
          <label className="block text-xs font-semibold text-slate-400 mb-2 uppercase tracking-wide">
            Ürün Kataloğu
          </label>
          <div className="flex flex-col gap-2.5">
            {products.map((product) => (
              <div 
                key={product.id}
                onClick={() => setSelectedProductId(product.id)}
                className={`catalog-card flex justify-between items-center ${
                  selectedProductId === product.id ? 'catalog-card-selected' : ''
                }`}
              >
                <div>
                  <p className="text-sm font-semibold">{product.name}</p>
                  <p className="text-xs text-slate-500">Ürün Kodu: #{product.id}</p>
                </div>
                <p className="text-sm font-bold text-indigo-400">${product.price.toFixed(2)}</p>
              </div>
            ))}
          </div>
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-xs font-semibold text-slate-400 mb-2 uppercase tracking-wide">
              Miktar (Adet)
            </label>
            <input 
              type="number" 
              min={1} 
              max={50}
              value={quantity}
              onChange={(e) => setQuantity(Math.max(1, parseInt(e.target.value) || 1))}
              className="form-input"
            />
          </div>
          <div>
            <label className="block text-xs font-semibold text-slate-400 mb-2 uppercase tracking-wide">
              Toplam Fiyat
            </label>
            <div className="form-input text-indigo-400 font-bold flex items-center bg-opacity-40">
              ${totalPrice}
            </div>
          </div>
        </div>

        {createError && (
          <div className="p-3 rounded-xl bg-rose-500/10 border border-rose-500/20 text-rose-400 text-xs font-medium">
            Hata: {createError}
          </div>
        )}

        <button
          type="submit"
          disabled={creating}
          className="btn-primary"
        >
          {creating ? (
            <Loader className="w-4 h-4 animate-spin" />
          ) : (
            <>
              Sipariş Gönder (Saga Akışını Tetikle) <ArrowRight className="w-4 h-4" />
            </>
          )}
        </button>
      </form>
    </div>
  );
}
