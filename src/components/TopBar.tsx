import React from 'react';
import { RefreshCw, Wifi, WifiOff, ShieldCheck } from 'lucide-react';
import { UserProfile } from '../types';

interface TopBarProps {
  user: UserProfile;
  isSyncing: boolean;
  onSync: () => void;
}

export const TopBar: React.FC<TopBarProps> = ({ user, isSyncing, onSync }) => {
  return (
    <header className="sticky top-0 z-50 backdrop-blur-md bg-[#0e1117]/85 border-b border-[#262f42] px-4 py-3">
      <div className="max-w-[680px] mx-auto flex items-center justify-between">
        {/* Brand & ID */}
        <div className="flex items-center gap-3">
          <div className="w-9 h-9 rounded-xl bg-gradient-to-br from-[#00e5ff] to-[#00e676] flex items-center justify-center font-bold text-[#0e1117] text-lg shadow-lg shadow-[#00e5ff]/20">
            M
          </div>
          <div>
            <div className="flex items-center gap-1.5">
              <span className="text-xs font-bold tracking-wider text-[#94a3b8] uppercase">
                MONETA CHAIN
              </span>
              <span className="w-1.5 h-1.5 rounded-full bg-[#00e676] animate-pulse"></span>
            </div>
            <div className="text-sm font-mono font-semibold text-[#f1f5f9] tracking-tight">
              {user.monetaId}
            </div>
          </div>
        </div>

        {/* Balance & Sync */}
        <div className="flex items-center gap-3">
          <div className="text-right">
            <span className="text-[10px] uppercase font-bold tracking-wider text-[#94a3b8] block">
              MINT BALANCE
            </span>
            <span className="text-base font-mono font-bold text-gradient-moneta">
              {user.walletBalance.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })} MINT
            </span>
          </div>

          <button
            onClick={onSync}
            disabled={isSyncing}
            title="Sync with Moneta Consensus"
            className="w-9 h-9 rounded-xl bg-[#1c2230] hover:bg-[#262f42] border border-[#262f42] flex items-center justify-center text-[#94a3b8] hover:text-[#00e5ff] transition-all disabled:opacity-50"
          >
            <RefreshCw className={`w-4 h-4 ${isSyncing ? 'animate-spin text-[#00e5ff]' : ''}`} />
          </button>
        </div>
      </div>
    </header>
  );
};
